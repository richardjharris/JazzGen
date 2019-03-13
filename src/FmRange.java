import javax.swing.*;   
import java.awt.*;   

/**
 * Awards a bonus if notes stay inside a reasonable piano range. Very
 * important!
 */
public class FmRange extends FitnessModule {
    public String getID() { return "Range"; }
    public String getDescription() { return "Enforces a reasonable pitch range."; }
    public boolean sendOnlyLastBlock() { return true; }
	
	int minOctave, maxOctave;
	JTextField minOctField, maxOctField;
    
    public FmRange(Config c) {
		super(c, 200, true);
		if(c.get("fm.range") == null) {
			c.put("fm.range", new int[] { 3, 6 });
		}
		int[] args = (int[])c.get("fm.range");
		minOctave = args[0]; maxOctave = args[1];
		makeOptionsPanel();
	}
    
    public float evaluate(PhraseInfo p) {
        float score = 0;
		int LO = (int)Math.max(0, (minOctave+1) * 12); // note 0 = C-1
		int HI = (int)Math.min(127, (maxOctave+1) * 12);
        for(byte pitch: p.pitches) {
            if(pitch < LO || pitch > HI) {
                return 0.0f;
            }
            
            // Old implementation follows
            /*
            if(pitch < LO) {
				score += ((float)pitch/LO);
			}
			else if(pitch > HI) {
				score += 1.0 - ((float)(pitch - HI)/(127 - HI));
			}
			else {
				score += 1.0; // acceptable pitch
			}
            */
        }
        return 1.0f;
        //return ((float)score/p.noteCount);
    }

    public void newWeight() {
        setWeightNormal(baseWeight, 10.0);
    }
	
	public void makeOptionsPanel() {
		pnl.setLayout(new GridLayout(2, 2));
		pnl.add(new JLabel("Min octave"));
		minOctField = new JTextField(Integer.toString(minOctave), 2);
		pnl.add(minOctField);
		pnl.add(new JLabel("Max octave"));
		maxOctField = new JTextField(Integer.toString(maxOctave), 2);
		pnl.add(maxOctField);
	}
	
	public void save(Config c) {
		commit();
		super.save(c);
		c.put("fm.range", new int[] { minOctave, maxOctave });
	}
	
	public void commit() {
		super.commit();
		minOctave = Integer.parseInt(minOctField.getText());
		maxOctave = Integer.parseInt(maxOctField.getText());
	}
}