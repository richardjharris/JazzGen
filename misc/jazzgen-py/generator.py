import random
import note, chord, scale
import selector, crossover, mutator

class Generator:
    "Generate notes and pass to supplied collector."
    
    # Initialisation
    def __init__(self):
        self.notes = []
        self.chords = []
        self.tempo = 120
        self.setMeter(4, 4)
        
        # Population size, per generation (20-30 or 50-100; recommend the same size as chromosome size)
        self.popSize = 32
        # Chance crossover is performed - else offspring are exact copy of parents
        # 0.80-0.95 recommended; 0.6 good for 'some' problems
        self.crossoverP = 0.8
        # Chance offspring are mutated (per 'bit') after crossover/copy
        # 0.005-0.01 recommended
        self.mutationP = 0.01
        # Number of 'most fit' parents to transfer over to new generation unchanged ('elitism')
        self.elitism = 4
        
        self.selectionMethod = selector.TournamentSelection(self, 4)
        self.crossoverMethod = crossover.DoublePoint
        self.mutationMethod  = mutator.RandomPitchShift
        self.fitnessEvaluators = []
        
    def setMeter(self, *meter):
        self.meter = meter
        self.barLength = meter[0]*4
            
    # Generation
    def run(self):
        assert(len(self.chords))
        self.pos = 0
        for c in self.chords:
            self.playChord(c)
            scaleNotes = random.choice(scale.getScaleFromChord(*c).values())
            self.scale = []
            for oct in range(2,7):
                self.scale.extend([n + oct*12 for n in scaleNotes])
            
            # Get and blindly play genetic output as 8ths
            phrase = self.genPhrase()
            for n in phrase:
                self.notes.append(note.Note(note.Piano, n, self.pos, 1))
                self.pos += 2
        return note.Phrase(self.notes, self.tempo, self.meter)
        
    def genPhrase(self):
        "Generates eight notes of music using a genetic algorithm"
        # Initialise population
        self.population = [
            [random.choice(self.scale) for j in range(self.barLength)]
            for i in range(self.popSize)
        ]
        self.fitness = {}
        self.evaluateFitness(self.population, fitTable=self.fitness)
        selector = self.selectionMethod.selector(self)
        # Sort by fitness
        self.population.sort(key=lambda i:self.fitness[tuple(i)], reverse=True)
        for generation in xrange(1, 3000):
            if self.elitism:
                parents = self.population[:self.elitism]
            while len(parents) < self.popSize:
                parents.append(selector.next())
            # Generate offspring via random parent crossovers
            children = [self.crossoverMethod(random.choice(parents), random.choice(parents)) for i in range(self.popSize)]
            # Mutate the children
            children = map(self.mutationMethod, children)
            childFitness = {}
            self.evaluateFitness(children, fitTable=childFitness)
            # Replace worst popSize/2 in population with best popSize/2 in offspring
            children.sort(key=lambda i:childFitness[tuple(i)], reverse=True)
            ps2 = self.popSize//2
            for i in range(ps2):
                #del self.fitness[tuple(self.population[i + ps2])] --- LOOK AT THIS
                self.population[i + ps2] = children[i]
                key = tuple(children[i])
                self.fitness[key] = childFitness[key]
        return self.population[0]
    
    def evaluateFitness(self, indivs, fitTable):
        for i in indivs:
            if tuple(i) not in fitTable:
                # Simple fitness function: favour lower notes
                fitTable[tuple(i)] = sum(i)
        
    def playChord(self, chrd):
        "Play the provided chord with an appropriate velocity, duration and inversion."
        for n in chord.getChord(chrd[0] + 48, chrd[1]):
            self.notes.append(note.Note(note.Piano, n, self.pos, self.barLength - 1, 75))
            