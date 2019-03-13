/**
 * Notes are randomly copied from the first or second parent.
 */
public class CrossoverUniform extends CrossoverMethod {
    public void crossover(byte[] mom, byte[] pop, byte[] child) {
        for(int i = 0; i < child.length; i++) {
            child[i] = (rand.nextInt(1) == 0) ? mom[i] : pop[i];
        }
    }
}