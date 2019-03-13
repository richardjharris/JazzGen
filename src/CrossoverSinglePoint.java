/**
 * Beginning of child is taken from first parent, and the rest from the second.
 */
public class CrossoverSinglePoint extends CrossoverMethod {
    public void crossover(byte[] mom, byte[] pop, byte[] child) {
        int cut = rand.nextInt(child.length);
        for(int i = 0; i < child.length; i++) {
            child[i] = (i <= cut) ? mom[i] : pop[i];
        }
    }
}