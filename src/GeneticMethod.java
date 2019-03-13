import javax.swing.JPanel;
import java.awt.FlowLayout;

public abstract class GeneticMethod implements JGConstants {
    java.util.Random rand = new java.util.Random();
    JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
    
    /**
     * Returns the options UI panel for this method.
     */
    public JPanel getOptionsUI() {
        return pnl; // no options
    }
    
    /**
     * Update method's parameters to reflect UI values.
     */
    public void commit() { }
    
    /**
     * Save method's parameters to a Config object. This method should
     * also call commit() before saving.
     */
    public void save(Config c) { }
}