/**
 * Encourage note repetition within a phrase.
 */
public class FmRepetition extends FitnessModule {
    public String getID() { return "Repetition"; }
    public String getDescription() { return "Encourages repetition of two or three note sequences."; }
    public boolean showOnlyLastBlock() { return true; }
    
    public FmRepetition(Config c) { super(c, 75); }
    
    public float evaluate(PhraseInfo p) {
        int score = 0;
		
		if(p.noteCount < 6) return 0.0f; // no basis for repetition
		
		// Two note repetition
        for(int i = 0; i < p.noteCount - 2; i++) {
            for(int j = i+2; j < p.noteCount - 1; j++) {
                if(p.pitches[i] == p.pitches[j] &&
                    p.pitches[i + 1] == p.pitches[j + 1]
                    && p.pitches[i] != p.pitches[i + 1]) {
                    score += 3;
                }
            }
        }
		
		// Three note repetition
        for(int i = 0; i < p.noteCount - 3; i++) {
            for(int j = i+3; j < p.noteCount - 2; j++) {
                if(p.pitches[i] == p.pitches[j] &&
                    p.pitches[i + 1] == p.pitches[j + 1] &&
                    p.pitches[i + 2] == p.pitches[j + 2] &&
                    (p.pitches[i] != p.pitches[i + 1] ||
					 p.pitches[i + 1] != p.pitches[i + 2])) {
                    score += 10;
                }
            }
        }
		
		int maxScore = ((p.noteCount - 2)*(p.noteCount - 3)/2) * 3
			+ ((p.noteCount - 4)*(p.noteCount - 5)/2) * 10;
		// We only want 20% repetition, so assign a score based on distance between
        // actual (scaled) score and 0.2.
        // Map 0.0-0.2 as 0.0-0.1, 0.2-.8 to .1-.0
        float fScore = (float)score/maxScore;
        if(fScore < 0.2) return 5.0f*fScore;
        else return 1.0f - ((5.0f/3.0f)*(fScore - 0.2f));
    }

    public void newWeight() {
        setWeightWalk(baseWeight,
			(int)Math.max(0, baseWeight - 100), baseWeight + 100,
			50, 0.2);
    }
}