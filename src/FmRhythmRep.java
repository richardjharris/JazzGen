/**
 * Encourage rhythm repetition within a phrase.
 */
public class FmRhythmRep extends FitnessModule {
    public String getID() { return "RhythmRep"; }
    public String getDescription() { return "Encourages two or three-note rhythm repetition."; }
    public boolean sendOnlyLastBlock() { return true; }
    
    public FmRhythmRep(Config c) { super(c, 100); }
    
    public float evaluate(PhraseInfo p) {
        int score = 0;
		
		if(p.noteCount < 7) return 0.5f; // no basis for repetition
		
		// Two note repetition
        for(int i = 0; i < p.noteCount - 3; i++) {
            for(int j = i+2; j < p.noteCount - 2; j++) {
                if(p.lengths[i] == p.lengths[j] &&
                    p.lengths[i + 1] == p.lengths[j + 1]) {
                    score += 3;
                }
            }
        }
		
		// Three note repetition
        for(int i = 0; i < p.noteCount - 4; i++) {
            for(int j = i+3; j < p.noteCount - 3; j++) {
                if(p.lengths[i] == p.lengths[j] &&
                    p.lengths[i + 1] == p.lengths[j + 1] &&
                    p.lengths[i + 2] == p.lengths[j + 2]) {
                    score += 10;
                }
            }
        }
		
		int maxScore = ((p.noteCount - 4)*(p.noteCount - 3)/2) * 3
			+ ((p.noteCount - 5)*(p.noteCount - 6)/2) * 10;
		
		// The maximum score is somewhat difficult to obtain, so we multiply
		// our score by two to make it easier.
		return (float)Math.min(1.0, 2.0*((float)score/maxScore));
    }

    public void newWeight() {
        setWeightWalk(baseWeight,
			(int)Math.max(0, baseWeight - 100), baseWeight + 100,
			50, 0.2);
    }
}