# Contains genetic selection algorithms, which are implemented as generators.
import random

class TournamentSelection:
    "Pick _tourSize_ individuals from the population and return the best."
    def __init__(self, gen, tourSize):
        self.gen = gen
        self.tourSize = tourSize
    def selector(self, gen):
        while True:
            tour = random.sample(gen.population, self.tourSize)
            tourMax, tourMaxInd = None, []
            for i in tour:
                fitness = gen.fitness[tuple(i)]
                if tourMax is None or fitness >= tourMax:
                    tourMax, tourMaxInd = fitness, i
            yield tourMaxInd
       