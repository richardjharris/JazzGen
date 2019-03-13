/**
 * Generate one sequence of pitches via a uniform random distribution, to the current scale.
 */
public class InitRandomScale extends InitMethod {    
    public byte[] generate() {	
        byte[] seed = new byte[curLength];
        for(int i = 0; i < seed.length; i++) {
			byte[] scale = getScale(i);
            if(rand.nextDouble() > 0.5) {
                seed[i] = Rest;
            }
            else {
                seed[i] = (byte)(scale[rand.nextInt(scale.length)] + (rand.nextInt(3)+4)*12); // ~ C3-C6
            }
        }
        return seed;
    }
}