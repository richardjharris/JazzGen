import java.awt.event.*;
import javax.swing.*;      

/**
 * The maskChangeProb parameter determines the probability of each mask bit being
 * different to the preceeding one.
 * prob = 1/2: equivalent to uniform crossover
 * prob > 1/2: more crossover points
 * prob < 1/2: fewer crossover points
 */
public class CrossoverRandomMask extends CrossoverMethod {
    double maskChangeProb;
    
    JTextField maskChangeProbField;
    
    public String getDescription() {
        return "Generates a mask, e.g. 00110101, where 0 notes come from one parent and 1 notes from the other. Each mask bit has a probabilty of being different to the preceeding one (0.5 = equivalent to uniform crossover, < 0.5 leads to fewer crossover points etc.)";
    }

    public CrossoverRandomMask(Config c) {
        if(c.get("c.rm") == null) {
            c.put("c.rm", new Double(0.3));
        }
        maskChangeProb = ((Double)c.get("c.rm")).doubleValue();
        makeOptionsPanel();
    }
    
    public void crossover(byte[] mom, byte[] pop, byte[] child) {
        int maskBit = 0;
        for(int i = 0; i < child.length; i++) {
            if(rand.nextDouble() <= maskChangeProb) {
                maskBit = (maskBit == 1) ? 0 : 1;
            }
            child[i] = (maskBit == 0) ? mom[i] : pop[i];
        }
    }
    
    public void makeOptionsPanel() {
        pnl.add(new JLabel("Probability of mask bit changing:"));
        maskChangeProbField = new JTextField(Double.toString(maskChangeProb), 3);
        pnl.add(maskChangeProbField);
    }
    
    public void commit() {
        maskChangeProb = Double.parseDouble(maskChangeProbField.getText());
    }
    
    public void save(Config c) {
        commit();
        c.put("c.rm", new Double(maskChangeProb));
    }
}