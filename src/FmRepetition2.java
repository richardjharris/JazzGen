/**
 * Encourage note repetition within a phrase, part deux.
 */
public class FmRepetition2 extends FitnessModule {
    public String getID() { return "Repetition2"; }
    public String getDescription() { return "Encourages near or exact repetition of melodic fragments."; }
    public boolean showOnlyLastBlock() { return true; }
    
    public FmRepetition2(Config c) { super(c, 100); }
    
    public float evaluate(PhraseInfo p) {	
		if(p.noteCount < 6) return 0.0f; // no basis for repetition
        int maxScore = 0;
        try{
		
        // Pick two starting points in the phrase
        //   Repetition is realistically at least three notes
        for(int a = 0; a < p.noteCount - 6; a++) {
            for(int b = a + 3; b < p.noteCount; b++) {
                // for(scale transpose?) TODO
                int pos = 0;
                int score = 0;
                // Keep increasing pos til we don't see a match
                while(a + pos < p.noteCount && b + pos < p.noteCount
                    && Math.abs(p.pitches[a + pos] - p.pitches[b + pos]) <= 3) {
                    score += 3 - Math.abs(p.pitches[a + pos] - p.pitches[b + pos]);
                    if(p.pitches[a + pos] == p.pitches[b + pos])
                        score += 2;
                    
                    pos++;
                }
                if(score > 25) score = 12; // punish > 5 note repetition
                maxScore = Math.max(maxScore, score);
            }
        }
        } catch(Exception e) { System.out.println(e.getMessage()); e.printStackTrace(); }
        return (float)maxScore/25.0f;
    }

    public void newWeight() {
        setWeightWalk(baseWeight,
			(int)Math.max(0, baseWeight - 100), baseWeight + 100,
			50, 0.2);
    }
}