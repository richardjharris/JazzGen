import javax.swing.*;      
import java.awt.*;

/**
 * Awards a bonus for adj. notes that are similar in pitch. Punishes large
 * intervals. Similar to expectancy, but more of a simple 'ground rule'
 * that prevents huge intervals that couldn't be physically played.
 */
public class FmPitch extends FitnessModule {
    public String getID() { return "Pitch"; }
    public String getDescription() { return "Encourages certain intervals, and starting with the root note."; }
    
    int[] intBonus;
    JTextField bonusField[];
    int maxBonus;
    
    public FmPitch(Config c) {
        super(c, 100);
        if(c.get("fm.pitch.bonus") == null) {
            c.put("fm.pitch.bonus", new int[] {
                40, 30, 50, 50, 50, 40, 30, 10, 5, 0, 0, 0, 15
            });
        }
        intBonus = (int[])c.get("fm.pitch.bonus");
        makeOptionsPanel();
    }
	
    public float evaluate(PhraseInfo p) {
		int score = 0;
        for(int i = 1; i < p.noteCount; i++) {
			int diff = (int)Math.abs((double)(p.pitches[i] - p.pitches[i-1]));
			if(diff <= 12) score += intBonus[diff];
		}
		
		// Bonus if root note is the first note
        // TODO: reward root (or any in chord?) notes for ALL chords in block
        // Scale reward to number of notes in phrase
		int newestRootIdx = phraseLen * (p.phraseCount - 1);
		if(p.noteCount > 0 && p.initRestLen == 0 && p.getChordAt(newestRootIdx).scaleIndex(p.pitches[0]) == 0
		  && score >= (0.6 * maxBonus * p.noteCount)) {
			score += maxBonus;
		}
		
		int maxScore = (maxBonus * (p.noteCount + 1)); // +1 for root
 		return (float)Math.min(1.0, score/maxScore);
    }

    public void newWeight() {
        setWeightNormal(baseWeight, 3.0); // don't deviate much
    }
    
    public void makeOptionsPanel() {
        bonusField = new JTextField[intBonus.length];
        pnl.setLayout(new BorderLayout());
        JPanel j = new JPanel(new SpringLayout());
        j.add(new JLabel("Difference"));
        for(int i = 0; i < intBonus.length; i++) {
            j.add(new JLabel(Integer.toString(i)));
        }
        j.add(new JLabel("Score"));
        for(int i = 0; i < intBonus.length; i++) {
            JTextField tf = new JTextField(Integer.toString(intBonus[i]), 2);
            j.add(tf);
            bonusField[i] = tf;
        }
        SpringUtilities.makeCompactGrid(j, 2, intBonus.length + 1, 5, 5, 5, 5);
		pnl.add(new JScrollPane(j, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
   
    }
    
    public void save(Config c) {
        commit();
        super.save(c);
        c.put("fm.pitch.bonus", intBonus);
    }
    
    public void commit() {
        super.commit();
        maxBonus = 0;
        for(int i = 0; i < intBonus.length; i++) {
            intBonus[i] = Integer.parseInt(bonusField[i].getText());
            if(maxBonus < intBonus[i]) maxBonus = intBonus[i];
        }
    }
}