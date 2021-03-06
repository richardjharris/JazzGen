/**
 * Awards a bonus if notes fit a common key, particularly one which
 * matches the given chord progression.
 */
public class FmKey extends FitnessModule {
    public String getID() { return "Key"; }
    public String getDescription() { return "Evolves notes to fit the current scale."; }
    public boolean sendOnlyLastBlock() { return true; }
    
    public FmKey(Config c) { super(c, 250); }
    
    public float evaluate(PhraseInfo p) {
        int score = 0;
		// Only consider last phrase
		for(int i = 0; i < phraseLen; i++) {
			if(p.phrase[i] != Rest && p.getChordAt(i).scaleIndex(p.phrase[i]) >= 0) {
                score++;
			}
		}
        if(p.noteCount == 0) return 0.0f; // no basis for evaluation
		return ((float)score/p.noteCount);
    }

    public void newWeight() {
        setWeightNormal(baseWeight, 10.0);
    }
}