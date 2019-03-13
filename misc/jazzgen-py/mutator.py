 # The genetic mutation algorithm
import random

def RandomPitchShift(p):
    "Mutates phrase by randomly shifting it up or down."
    shift = 10 #random.randint(-5, 5)
    return [min(127, max(0, n + shift)) for n in p]