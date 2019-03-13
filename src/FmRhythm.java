import javax.swing.*;      

/**
 * Develops a consistent, acceptable rhythm for the phrase.
 */
public class FmRhythm extends FitnessModule {
    public String getID() { return "Rhythm"; }
    public String getDescription() { return "Encourages good note position, duration, adjacent durations."; }
    
	// Rhythm does more tests, so should have a higher importance
    public FmRhythm(Config c) { super(c, 200); }
    
    public float evaluate(PhraseInfo p) {
		int score = 0;
        
        // Assuming phraseLen = 32: reward on-beat notes
        if(p.phrase[0] != Rest) score += 100;
        if(p.phrase[16] != Rest) score += 100;
        if(p.phrase[8] != Rest) score += 50;
        if(p.phrase[24] != Rest) score += 50;
        
        for(int i = 0; i < p.noteCount; i++) {
			// Length tests ignore last length as it may be extended by
			// next block
			if(i != p.noteCount - 1) {
				if(i != 0) {
					// Encourage adjacent notes of similar length
					int sqrDiff = (int)Math.pow((double)(p.lengths[i] - p.lengths[i - 1]), 2);
					score += (int)Math.floor(30/(sqrDiff+1));
				}
			
				// Encourage note lengths that are powers of two
				if(p.isPowerOfTwo(p.lengths[i])) score += 20;
				
                // Center around 8th notes
				if(p.lengths[i] == 2) score += 5;
			}
		}
        int maxScore = (p.noteCount - 1) * 25 + (p.noteCount - 2) * 30 + 300;
		
		return Math.min(1.0f, ((float)score/maxScore));
    }

    public void newWeight() {
        setWeightNormal(baseWeight, 10.0);
    }
}