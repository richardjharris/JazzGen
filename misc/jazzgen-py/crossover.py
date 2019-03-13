# Contains algorithms for genetic crossover.
import random

def SinglePoint(c1, c2):
    "Beginning of offspring is taken from first parent, and the rest from the second"
    cut = random.randrange(len(c1))
    return [i <= cut and c1[i] or c2[i] for i in range(len(c1))] # assumes c1[i] != 0
    
def DoublePoint(c1, c2):
    "The beginning and end of the offspring is taken from the first parent, and the rest from the second"
    assert(len(c1) == len(c2))
    cut1 = random.randrange(len(c1))
    cut2 = random.randrange(len(c1))
    if cut1 > cut2: cut1, cut2 = cut2, cut1
    return [i <= cut1 and c1[i] or (i > cut2 and c1[i] or c2[i]) for i in range(len(c1))]
    
def Uniform(c1, c2):
    "Notes are randomly copied from the first or from the second parent"
    return [random.randint(0, 1) and c1[i] or c2[i] for i in range(len(c1))]

def Average(c1, c2):
    "The average of the two notes' pitches is chosen."
    return [(c1[i] + c2[i]) // 2 for i in range(len(c1))]