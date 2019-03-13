import javax.swing.*;      
import java.awt.*;

/**
 * Calculates melodic expectancy according to Narmour's model.
 * The expectancy is framed into a percentage of the maximum possible
 * value; the fitness returned is based on the deviation from a user-
 * specified expectancy (e.g. 75%)
 *
 * This module has not been properly normalised or tested, and has been
 * obsoleted by FmExpectancy2.
 */
public class FmExpectancy extends FitnessModule {
    public String getID() { return "Expectancy"; }
    public String getDescription() { return "Encourages expectancy ~ 75%. Not consistent."; }
    public boolean sendOnlyLastBlock() { return true; }
    
    static int small = 6;  // semitones
    int targetExp;
	double punishFactor;
    
    JTextField targetExpField, punishFactorField;
    
    public FmExpectancy(Config c) {
        super(c);
        if(c.get("fm.exp") == null) {
            c.put("fm.exp", new Object[] {
				new Integer(75), new Double(2.0)
			});
        }
		Object[] args = (Object[])c.get("fm.exp");
        targetExp = ((Integer)args[0]).intValue();
		punishFactor = ((Double)args[1]).doubleValue();
        makeOptionsPanel();
    }
    
    public float evaluate(PhraseInfo p) {
        float te = 0;  // total expectancy
        byte[] pitch = p.pitches;
        byte[] length = p.lengths;
        
        // Operate over groups of three items
        for(int i = 2; i < pitch.length; i++) {
			float e = 0;
            int int1 = pitch[i - 2] - pitch[i - 1];
            int int2 = pitch[i] - pitch[i - 1];
            int sig1 = (int)Math.signum(int1);
            int sig2 = (int)Math.signum(int2);
            int1 = Math.abs(int1); int2 = Math.abs(int2);
            // Registral direction
            if(int1 <= small && sig1 == sig2) e += small - int1; // small->same dir
                                              // smaller intervals -> stronger impl.
            else if(sig1 != sig2) e++;                           // big->change dir
            // Intervallic difference
            if(int1 <= small)
                e += (2*(int1+1))/(1+Math.abs(int1 - int2)); // Imply similar-sized intervals
            else if(int1 - int2 > 0) e += int1; // Imply smaller intervals

            // Closure increases expectancy
            if(sig1 != sig2) e *= 1.05;
            if(int1 > small && int2 <= small) e *= 1.1;
			// Ignore last length, as it might be extended by next block
			if(i != pitch.length - 1) {
				if(length[i] > length[i - 1]) e *= 1.2;
			}
			te += e;
        }
        // Registral return
        for(int i = 0; i < pitch.length - 2; i++) {
            if(Math.abs(pitch[i] - pitch[i + 2]) <= 2 &&
                pitch[i+1] != pitch[i] && pitch[i+1] != pitch[i+2])
                te += 5;
        }

		// Normalisation
		te = (int)Math.min(100, te / (3.5*p.phraseCount)); // ~0-350 per phrase -> 0-100
        // Return a fitness value based on proximity of e to user-defined target value
		return (float)(1.0 - Math.pow(Math.abs(te - targetExp)/targetExp, punishFactor));
    }
    
    public void newWeight() {
        setWeightNormal(baseWeight, 5.0);
    }
    
    public void makeOptionsPanel() {
		pnl.setLayout(new GridLayout(2, 1));
		JPanel j = new JPanel();
        j.add(new JLabel("Target expectancy: "));
        targetExpField = new JTextField(Integer.toString(targetExp), 2);
        j.add(targetExpField);
        j.add(new JLabel("%"));
		pnl.add(j);
		j = new JPanel();
		j.add(new JLabel("Punishment factor: "));
		punishFactorField = new JTextField(Double.toString(punishFactor), 4);
		j.add(punishFactorField);
		pnl.add(j);
    }
    
    public void save(Config c) {
        commit();
        super.save(c);
        c.put("fm.exp", new Object[] {
			new Integer(targetExp), new Double(punishFactor)
		});
    }
    
    public void commit() {
        super.commit();
        targetExp = Integer.parseInt(targetExpField.getText());
		punishFactor = Double.parseDouble(punishFactorField.getText());
    }
}
