/**
 * The beginning and end of the child are taken from the first parent, and the rest
 * from the second.
 */
public class CrossoverDoublePoint extends CrossoverMethod {
    public void crossover(byte[] mom, byte[] pop, byte[] child) {
        int cut1 = rand.nextInt(child.length);
        int cut2 = rand.nextInt(child.length);
        if(cut1 > cut2) { int t = cut1; cut1 = cut2; cut2 = t; }
        for(int i = 0; i < child.length; i++) {
            child[i] = (i <= cut1) ? mom[i] : (i > cut2 ? mom[i] : pop[i]);
        }
    }
}
