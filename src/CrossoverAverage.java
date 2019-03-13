import java.awt.event.*;
import javax.swing.*;      

/**
 * The child is formed from the average of the parents' pitches. If one
 * note is a rest and one a normal note, rests are picked with the supplied
 * probability.
 */
public class CrossoverAverage extends CrossoverMethod {
    double restProb;
    
    JTextField restProbField;

    public CrossoverAverage(Config c) {
        if(c.get("c.avg") == null) {
            c.put("c.avg", new Double(0.5));
        }
        restProb = ((Double)c.get("c.avg")).doubleValue();
        makeOptionsPanel();
    }
    
    public void crossover(byte[] mom, byte[] pop, byte[] child) {
        for(int i = 0; i < child.length; i++) {
            if(mom[i] == Rest && pop[i] == Rest)
                child[i] = Rest;
            else if(mom[i] == Rest)
                child[i] = rand.nextDouble() <= restProb ? Rest : pop[i];
            else if(pop[i] == Rest)
                child[i] = rand.nextDouble() <= restProb ? Rest : mom[i];
            else
                child[i] = (byte)Math.round((mom[i] + pop[i]) / 2);
        }
    }
    
    public void makeOptionsPanel() {
        pnl.add(new JLabel("Probability of choosing rest over note:"));
        restProbField = new JTextField(Double.toString(restProb), 3);
        pnl.add(restProbField);
    }
    
    // Save class instance vars to Config object
    public void save(Config c) {
        commit();
        c.put("c.avg", new Double(restProb));
    }
    
    // Update class instance vars to reflect UI
    public void commit() {
        restProb = Double.parseDouble(restProbField.getText());
    }        
}    