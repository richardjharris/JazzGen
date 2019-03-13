/**
 * Represents a genetic crossover method.
 */
public abstract class CrossoverMethod extends GeneticMethod {
    public abstract void crossover(byte[] mom, byte[] pop, byte[] child);
}
