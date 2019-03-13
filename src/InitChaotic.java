/**
 * Generate one sequence of pitches randomly, using the Chirikov
 * standard map mapped to pitches and durations.
 */
public class InitChaotic extends InitMethod {  
    double I, T, K;
    static final double TWO_PI = 2*Math.PI;
    // Durations that can be randomly selected based on chaotic function (1 = 16th)
    static byte[] durations = { 1, 2, 3, 4, 6, 8, 16 };
    // Chance, out of 16, that dur will be selected
    static int[] durWeights = { 2, 8, 0, 4, 1, 1, 0 };
    static int weightSum = 16;

    public byte[] generate() {
        byte[] seed = new byte[curLength];
        int pos = 0;
		byte[] scale = getScale(0);
        byte pitch = (byte)(scale[0] + scale.length*5);
        K = 0.5 + (rand.nextDouble() % 2.0);
        I = 2; T = 2;
        
        while(pos < curLength) {
            standardMap();
            pitch = (byte)Math.max(0, Math.min(255, pitch - 3 + (int)Math.floor(T / (TWO_PI/(7))))); // -3..3
            // Convert pitch number to MIDI pitch via scale
			scale = getScale(pos);
            seed[pos++] = (byte)(Math.floor(pitch / scale.length)*12 + scale[pitch % scale.length]);
            double Ip = (Math.min(3, Math.max(1.5, I)) - 1.5) / 1.5;
            byte rhythmSel = (byte)Math.floor(Ip * 15);
            int i = 0;
            // Select duration index i according to weights
            while(rhythmSel >= durWeights[i]) {
                rhythmSel -= durWeights[i];
                i++;
            }
            for(int j = 0; j < Math.min(curLength - pos, durations[i]); j++) {
                seed[pos++] = Rest;
            }
        }
        return seed;
    }
    
    private void standardMap() {
        I += K*Math.sin(T);
        T += I;
        
        I = Math.abs(I % TWO_PI);
        T = Math.abs(T % TWO_PI);
        
    }
}