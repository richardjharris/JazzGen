/**
 * Awards a bonus if notes occur in the currently playing chord.
 */
public class FmChord extends FitnessModule {
    public String getID() { return "Chord"; }
    public String getDescription() { return "Promotes notes that appear in the current chord."; }
    public boolean sendOnlyLastBlock() { return true; }
    
    public FmChord(Config c) { super(c, 100); }
    
    public float evaluate(PhraseInfo p) {
        int score = 0;
		// Only consider last phrase
		for(int i = 0; i < phraseLen; i++) {
			if(p.phrase[i] != Rest && p.getChordAt(i).hasNote(p.phrase[i])) {
                score++;
			}
		}
        if(p.noteCount == 0) return 0.0f; // no basis for evaluation
        // Encourage 50% chord notes
		float fit = 1.0f - Math.abs(0.5f - ((float)score/p.noteCount))*2.0f;
		return fit;
    }

    public void newWeight() {
        setWeightNormal(baseWeight, 10.0);
    }
}