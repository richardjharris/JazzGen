import java.util.*;
import java.io.*;

/**
 * Evaluates the fitness of a musical phrase (array of pitches and rests).
 */
public class FitnessFunction implements JGConstants {
    GeneticGenerator gen;
    FitnessModule[] modules = new FitnessModule[0];
    FitnessModule[] enabled = new FitnessModule[0]; // only contains enabled modules
    PrintWriter log;
    
    public FitnessFunction(GeneticGenerator gen) {
        this.gen = gen;
    }   

    /**
     * Invoke newWeight() on all fitness modules unless they are marked as fixed.
     */
    public void newWeights() {
        for(FitnessModule m: enabled) {
            if(!m.wgtFixed) m.newWeight();
        }
    }

    /**
     * Calculate the fitness of a single musical phrase.
     */
    public int evaluate(byte[] lastBlock, byte[] histBlocks, int hbNumPhrases, float[] scores) {
        int fitnessTotal = 0;
        PhraseInfo histPi = new PhraseInfo(histBlocks, gen, hbNumPhrases);
        PhraseInfo lastPi = new PhraseInfo(lastBlock, gen, 1);
        for(int i = 0; i < enabled.length; i++) {
            float score = enabled[i].evaluate(enabled[i].sendOnlyLastBlock() ? lastPi: histPi);
            scores[i] += score; // module score totals
            fitnessTotal += score * enabled[i].weight;
        }
        return Math.round(fitnessTotal);
    }
    
    final static String FFLOG = "fitness.csv";
    
    /**
     * Manage the fitness log file.
     */
    public void openLog() {
        try {
            log = new PrintWriter(new BufferedOutputStream(
                new FileOutputStream(FFLOG), 2048));
            if(log.checkError())
                System.out.println("Warning: error occured when opening fitness log " + FFLOG);
        
            // Write header
            log.print("Generation,");
            for(FitnessModule m: enabled) {
                log.print(m.getID() + ",");
            }
            log.println("Highest fitness,Average fitness");
        }
        catch(IOException e) {
            System.out.println("Warning: error occured when opening fitness log " + FFLOG + ":" + e.getMessage());
        }
    }
    
    public void writeLog(int gen, float[] scores, int totalFit, int bestFit, int count) {
        log.print(gen + ",");
        for(float scoreTotal: scores) {
            float avgScore = scoreTotal/count;
            log.print(avgScore + ",");
        }
        float averageFit = (float)totalFit/count;
        log.println(bestFit + "," + averageFit);
    }
    
    public void closeLog() {
        if(log.checkError())
            System.out.println("Warning: error occured while writing fitness log " + FFLOG);
        log.close();
        log = null;
    }
}