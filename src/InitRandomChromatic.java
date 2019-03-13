/**
 * Generate one sequence of pitches via a random uniform distribution, over the chromatic (all pitches) scale.
 * Pitches are limited to the octaves C3 to C6.
 */
public class InitRandomChromatic extends InitMethod {
    public byte[] generate() {
        byte[] seed = new byte[curLength];
        for(int i = 0; i < seed.length; i++) {
            if(rand.nextDouble() > 0.5) {
                seed[i] = Rest;
            }
            else {
                seed[i] = (byte)(rand.nextInt(12) + (rand.nextInt(3)+4)*12);
            }
        }
        return seed;
    }
}