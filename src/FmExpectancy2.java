import javax.swing.*;      
import java.awt.*;

/**
 * Calculates melodic expectancy according to Schellenberg's simplification
 * of Narmour's model. Uses only three of the tests, and employs Schellenberg's
 * own scores for each implication.
 * The expectancy is framed into a percentage of the maximum possible
 * value; the fitness returned is based on the deviation from a user-
 * specified expectancy (e.g. 75%)
 */
public class FmExpectancy2 extends FitnessModule {
    public String getID() { return "Expectancy2"; }
    public String getDescription() { return "Encourages expectancy ~ 75%. Based on Schellenberg."; }
    
    int targetExp;
	double punishFactor;
        
    // Co-efficients for each metric
    float regDirCE, regRetCE, proxCE;
    
    JTextField targetExpField, punishFactorField;
    JTextField regDirField, regRetField, proxField;
    
    public FmExpectancy2(Config c) {
        super(c);
        if(c.get("fm.exp2") == null) {
            c.put("fm.exp2", new Object[] {
				new Integer(75), new Double(2.0), new Float(3.0), new Float(6.0), new Float(0.5)
			});
        }
		Object[] args = (Object[])c.get("fm.exp2");
        targetExp = ((Integer)args[0]).intValue();
		punishFactor = ((Double)args[1]).doubleValue();
        regDirCE = ((Float)args[2]).floatValue();
        regRetCE = ((Float)args[3]).floatValue();
        proxCE = ((Float)args[4]).floatValue();
        makeOptionsPanel();
    }
    
    public float evaluate(PhraseInfo p) {
        byte[] pitch = p.pitches;
        float score = 0;
        
        if(pitch.length < 3) return 0.5f; // not enough notes to work with
                
        // Operate over groups of three items
        for(int i = 2; i < pitch.length; i++) {
            int implInt = pitch[i - 2] - pitch[i - 1]; // implicative interval
            int realInt = pitch[i] - pitch[i - 1]; // realised interval
            int implSgn = (int)Math.signum(implInt);
            int realSgn = (int)Math.signum(realInt);
            implInt = Math.abs(implInt);
            realInt = Math.abs(realInt);
            
            // Registral direction
            // If the impl. int is large, assign 1 if different dir, -1 if same dir
            if(implInt > 6) {
                score += regDirCE * ((realSgn != implSgn) ? 2 : 0);
            }
            else score += regDirCE;   
            
            // Registral return
            // 1. Realised interval must be in different direction
            // 2. Real. interval must be within 2 semitones of implicative interval
            if(realSgn != implSgn && realInt != 0 && implInt != 0) {
                if(Math.abs(implInt - realInt) <= 1) score += regRetCE;
            }
            
            // Proximity
            if(realInt > 0 && realInt < 12)
                score += proxCE * (12 - realInt);
            else if(realInt == 0) {
                score += proxCE * 5;
            }
        }

        // Max possible score
        float maxScore = (regDirCE * 2 + proxCE * 12 + regRetCE) * (p.noteCount - 2);
            
		// Normalisation
        // Return a fitness value based on proximity of exp. to user-defined target value
        float tExp = targetExp / 100.0f;
		return (float)(1.0 - Math.pow(Math.abs((score/maxScore) - tExp)/tExp, punishFactor));
    }
    
    public void newWeight() {
        setWeightNormal(baseWeight, 5.0);
    }
    
    public void makeOptionsPanel() {
		pnl.setLayout(new BorderLayout());
		JPanel j = new JPanel(new SpringLayout());
        j.add(new JLabel("Target expectancy (%): "));
        targetExpField = new JTextField(Integer.toString(targetExp), 2);
        j.add(targetExpField);
		j.add(new JLabel("Punishment factor: "));
		punishFactorField = new JTextField(Double.toString(punishFactor), 4);
		j.add(punishFactorField);
        j.add(new JLabel("Registral direction weight"));
        regDirField = new JTextField(Float.toString(regDirCE), 3);
        j.add(regDirField);
        j.add(new JLabel("Registral return weight"));
        regRetField = new JTextField(Float.toString(regRetCE), 3);
        j.add(regRetField);
        j.add(new JLabel("Proximity weight"));
        proxField = new JTextField(Float.toString(proxCE), 3);
        j.add(proxField);
		
		SpringUtilities.makeCompactGrid(j, 5, 2, 5, 5, 30, 5);
		pnl.add(new JScrollPane(j, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
    }
    
    public void save(Config c) {
        commit();
        super.save(c);
        c.put("fm.exp2", new Object[] {
			new Integer(targetExp), new Double(punishFactor), new Float(regDirCE), new Float(regRetCE), new Float(proxCE)
		});
    }
    
    public void commit() {
        super.commit();
        targetExp = Integer.parseInt(targetExpField.getText());
		punishFactor = Double.parseDouble(punishFactorField.getText());
        regDirCE = Float.parseFloat(regDirField.getText());
        regRetCE = Float.parseFloat(regRetField.getText());
        proxCE = Float.parseFloat(proxField.getText());
    }
}
