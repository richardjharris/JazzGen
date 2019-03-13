import jm.util.*;
import jm.music.data.*;
import jm.music.tools.*;
import java.util.*;

/**
 * Runs a genetic algorithm to generate a jazz solo over a given chord
 * progression.
 *
 * Coding scheme: 0..127 indicates MIDI notes; -1..-128 is a rest (-1 'canonical')
 * @author Richard Harris
 */
public final class GeneticGenerator implements JGConstants {
    /* Population size (default: same as chromosone length) */
    int populationSize;
    /* Chance of mutation, per child */
    double mutationProb;
    /* Number of 'most fit' parents to transfer to new generation unmodified */
    int elitismSel;
    /* Tournament size for tournament selection. */
    int tourSize;
    /* Max. number of generations to run per block. */
    int maxGenerations;
	/* Max. history of successful phrases to remember. */
	int histSize;
	/* Max. previous phrases to include in fitness evaluation ( < histSize) */
	int maxPrevPhrases;
	/* % chance of an initialised phrase being one from the history. */
	double repeatPhraseProb;
	/* Tendency to choose more recent phrases when repeating (1=even, >1=skew) */
	double repeatPhraseSkew;
	
	/* Genetic methods */
    FitnessFunction fitFunc;
	MutationFunction mutFunc;
	CrossoverMethod crossoverMethod;
    InitMethod initMethod;

	ChordProgression prog;
    Random rand;
    int[] fitness;
    GeneticObserver obs;
    JazzGen jg;
	int progIndex;
    int gen; // generation
	byte[] history;
    
    public GeneticGenerator(JazzGen jg) {
        this.jg = jg;
        this.rand = jg.rand;
		
        fitFunc = new FitnessFunction(this);
		mutFunc = new MutationFunction(rand);
    }
    
    /**
     * Generates a piano solo for the given chord progression.
     */
    public void run(Phrase out) throws Exception {
		prog = jg.prog;  // copy over chord progression
        // Generate one block at a time
		history = new byte[phraseLen * histSize];
        fitness = new int[populationSize];
        fitFunc.openLog();
        for(progIndex = 0; progIndex < prog.length(); progIndex++) {		
            byte[][] population = generateSeeds(history);
            Arrays.fill(fitness, NoFitness);
            fitFunc.newWeights();
            int[] weights = new int[fitFunc.enabled.length];
            for(int i = 0; i < fitFunc.enabled.length; i++) {
                weights[i] = fitFunc.enabled[i].weight;
            }
            
            for(gen = 0; gen <= maxGenerations; gen++) {
                calculateFitness(population);
                int[] rankedFitness = sortFitness();
                byte[][] newgen = new byte[populationSize][phraseLen];
                
                // If elitism is enabled, copy over some candidates untouched
                for(int i = 0; i < elitismSel; i++) {
                    newgen[i] = population[ rankedFitness[rankedFitness.length - 1 - i] ];
                    fitness[i] = fitness[ rankedFitness[rankedFitness.length - 1 - i] ];
                }
                
                // Generate offspring via random parent crossovers + mutate
                for(int i = elitismSel; i < populationSize; i++) {
                    byte[] mom = population[select()];
                    byte[] pop = population[select()];
                    byte[] child = new byte[phraseLen];
					crossoverMethod.crossover(mom, pop, child);
					if(rand.nextDouble() < mutationProb) mutFunc.mutate(child);
                    newgen[i] = child;
                }
                
                // Replace old gen with new gen
                population = newgen;
                // Unset fitness for all but elitism (-> popSize - 1)
                Arrays.fill(fitness, elitismSel, populationSize, NoFitness);
            }
            
            // Pick the fittest phrase and add it
            int bestIdx = getFittest(population, out);
            addBlock(out, population[bestIdx]);

			// Rotate history
			System.arraycopy(history, phraseLen, history, 0, phraseLen * (histSize - 1));
			System.arraycopy(population[bestIdx], 0, history, phraseLen * (histSize - 1), phraseLen);
            if(obs != null) obs.updateProgress(progIndex + 1);
        }
        fitFunc.closeLog();
    }
    
    /**
     * Evaluate the fitness of all members, and return the index of the
	 * candidate with the highest score.
     */
    private int getFittest(byte[][] pop, Phrase out) {
        calculateFitness(pop);
        int bestFit = NoFitness;
        int bestIdx = 0;
        for(int i = 0; i < populationSize; i++) {
            if(fitness[i] > bestFit) {
                bestFit = fitness[i];
                bestIdx = i;
            }
        }
        return bestIdx;
    }
    
    /**
     * Return a selection from the fitness table via Tournament Selection. A random
     * sample of the population is selected, and the highest fitness member is
     * returned.
     */
    private int select() {
        int maxFitness = NoFitness;
        int maxIndex = 0;
        for(int i = 0; i < tourSize; i++) {
            int idx = rand.nextInt(populationSize);
            if(fitness[idx] > maxFitness) {
                maxFitness = fitness[idx];
                maxIndex = idx;
            }
        }
        return maxIndex;
    }
    
    /**
     * Calculate the fitness of the entire population, if it
     * has not already been calculated.
     */
    private void calculateFitness(byte[][] population) {
		// e.g. if progIndex = 2, we can only use 2 previous phrases
		int prevPhrases = Math.min(progIndex, Math.min(histSize, maxPrevPhrases));
		byte[] fitSubject = new byte[phraseLen * (prevPhrases + 1)];
        try{
            System.arraycopy(history, phraseLen * (histSize - prevPhrases), fitSubject, 0, phraseLen * prevPhrases);
        }catch(ArrayIndexOutOfBoundsException e) {
            System.out.println("Fitness subject copy error: " + Arrays.toString(new int[]{progIndex, maxPrevPhrases, prevPhrases, fitSubject.length, histSize, phraseLen*(histSize-prevPhrases)}));
            System.exit(1);
        }
        int totalFitness = 0;
        int bestFitness = 0;
        int tfSize = 0;
        // Also store totals of each module score
        float scores[] = new float[fitFunc.enabled.length];
        
        for(int i = 0; i < population.length; i++) {
            if(fitness[i] == NoFitness) {
                // Fitness is unset, so calculate it
				System.arraycopy(population[i], 0, fitSubject, phraseLen * prevPhrases, phraseLen);
                fitness[i] = fitFunc.evaluate(population[i], fitSubject, prevPhrases + 1, scores);
                totalFitness += fitness[i];
                tfSize++;
            if(fitness[i] > bestFitness) bestFitness = fitness[i];
        }
        fitFunc.writeLog(gen, scores, totalFitness, bestFitness, tfSize);
    }
    
    /**
     * Return an array of population indices, ranked by
     * fitness ascending.
     */
    private int[] sortFitness() {
        int[] ordering = new int[fitness.length];
        for(int i = 0; i < ordering.length; i++) {
            ordering[i] = i;
        }
        // Perform quicksort on fitness; their associated indices
        // (population members) are also sorted.
        TwoArrayQS.quicksort(fitness.clone(), ordering, 0, fitness.length - 1);
        return ordering;
    }
    
    /**
     * Generates the initial candidates for the genetic algorithm. These candidates
     * should at least resemble acceptable outputs, and be as varied as possible.
     */
    private byte[][] generateSeeds(byte[] history) {
        byte[][] pop = new byte[populationSize][phraseLen];
        initMethod.newPhrase(this, phraseLen);
        for(int i = 0; i < populationSize; i++) {
			if(progIndex > 0 && rand.nextDouble() < repeatPhraseProb) {
				// Copy a phrase from the history
				pop[i] = new byte[phraseLen];
				int maxHist = Math.min(progIndex, histSize); // available history
				double x = Math.random();
				x = Math.pow(x, repeatPhraseSkew); // prefer more recent melodies
				int offset = maxHist - (int)Math.floor(x*maxHist) - 1; // history offset (0 = most recent)
                int histIndex = histSize - offset - 1; // convert so 0 = first block, histSize - 1 most recent

                try {
				System.arraycopy(history, phraseLen*histIndex, pop[i], 0, phraseLen);
                } catch(IndexOutOfBoundsException e) {
                    System.out.println("Problem using history as seed!");
                    System.out.println("maxHist = " + maxHist + "; x = " + x + "; offset = " + offset + "; histIndex = " + histIndex + "; history array size = " + history.length);
                    System.exit(1);
                }
				
				if(rand.nextBoolean() == true) {
					// Transpose candidate to fit new chord
                    for(int j = 0; j < phraseLen; j++) {
                        if(pop[i][j] != Rest) {
                            byte oldRoot = prog.getChordAt(progIndex - offset - 1, j).rootNote;
                            byte newRoot = prog.getChordAt(progIndex, j).rootNote;
                            int shift = newRoot - oldRoot;
                            if(shift <= -7) shift += 12;
                            if(shift >= 7) shift -= 12;
                            pop[i][j] = (byte)Math.min(127, Math.max(0, pop[i][j] + shift));
                        }
                    }
				}
			}
			else {
				// Generate one according to the selected initialisation method
				pop[i] = initMethod.generate();
			}
        }
        return pop;
    }
    
    /**
     * Add the supplied block to the output. Pitch values are converted to
     * jMusic equivalents, and a swing rhythm is applied to note durations.
     */
    public void addBlock(Phrase p, byte[] block) {
        // Represents a 16th note
        double twoEightsDur = (EN*16/phraseLen)*4;
        double[] share = new double[] { 0.3, 0.25, 0.25, 0.2 };
    
        for(int i = 0; i < block.length; i++) {
            int pitch = (block[i] == Rest) ? REST : (int)block[i];
            
            // Set duration to a share (4/10..1/10) of 4 16ths (2 8ths)
            double duration = twoEightsDur*share[i % 4];
            
            p.addNote(pitch, duration);
        }
    }
}