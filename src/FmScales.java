/**
 * Encourage playing up and down scales. It is recommended that this
 * module have a low influence or is set to Fixed so that it only 
 * influences every 8 bars or so.
 */
public class FmScales extends FitnessModule {
    public String getID() { return "Scales"; }
    public String getDescription() { return "Encourages ascending or descending passages (smooth contour)"; }
    
    public FmScales(Config c) { super(c, 300, true); }
    
    public float evaluate(PhraseInfo p) {
		if(p.noteCount < 4) return 0.5f; // no basis for repetition
		
        // We look for long passages going in the same registral direction.
        // Scale index changes of one note are given the best score.
        int lastIndex = 0;
        int curDir = 0;
        int runLength = 0;
        int runBonus = 0;
        int longestRun = 0;
        for(int i = 0; i < p.phrase.length; i++) {
            byte pitch = p.phrase[i];
            if(pitch != Rest) {
                int scaleLen = p.getChordAt(i).getScale().length;
                int index = p.getChordAt(i).scaleIndex(pitch) + scaleLen*(pitch / 12);
                int diff = index - lastIndex;
                if(diff > 0) {
                    // Up in pitch by 1
                    if(curDir != -1) {
                        curDir = 1;
                        runLength++;
                        if(diff == 1) runBonus++;
                    }
                    else {
                        if(runLength > longestRun) longestRun = runLength;
                        runLength = 0; runBonus = 0;
                    }
                }
                else if(diff < 0) {
                    // Down in pitch by 1
                    if(curDir != 1) {
                        curDir = -1;
                        runLength++;
                        if(diff == -1) runBonus++;
                    }
                    else {
                        if(runLength > longestRun) longestRun = runLength;
                        runLength = 0; runBonus = 0;
                    }
                }
                lastIndex = index;
            }
        }
        if(runLength > longestRun) longestRun = runLength;
        
        longestRun += runBonus * 2; // award 3 points for single interval
		
		return ((float)longestRun/(3*p.noteCount));
    }

    // Come into effect every eight bars
    public void newWeight() {
        setWeightJump(baseWeight, 0.25);
    }
}