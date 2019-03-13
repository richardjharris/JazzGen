import javax.swing.*;   
import java.awt.*;   

/**
 * Test influence of fitness modules and their interaction with other modules.
 * FmInfluenceTest encourages the appearance of a C note and no rests.
 * Additionally, it awards higher scores for C notes closer to C4.
 */
public class FmInfluenceTest extends FitnessModule {
    public String getID() { return "InfluenceTest"; }
    public String getDescription() { return "Encourages C notes."; }
    
    public FmInfluenceTest(Config c) { super(c, 100); }   
	
    public float evaluate(PhraseInfo p) {
        int score = 0;
		for(byte pt: p.pitches) {
            score += Math.max(0, 20 - Math.abs(pt - C4));
            if(pt == C4) score += 20;
            
            //if((pt % 12) == 0) score+=5;
            /*
            int pc = (pt % 12);
            if(pc == 0) score += 5;
            else if(pc == 11 || pc == 1) score += 4;
            else if(pc == 10 || pc == 2) score += 3;
            else if(pc == 9 || pc == 3) score += 2;
            else if(pc == 8 || pc == 4) score += 1;
            */
        }
        return ((float)score/(phraseLen*40));
    }

    public void newWeight() {
        weight += baseWeight; // gain more influence
    }
}