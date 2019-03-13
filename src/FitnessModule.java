import java.awt.*;
import java.awt.event.*;
import javax.swing.*;      
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;

/**
 * Represents a single fitness module. Contains methods for handling storage
 * and UI for parameters common to all modules.
 */
abstract public class FitnessModule extends GeneticMethod {
    int weight;         // Current weight factor
    int baseWeight;     // User-specified initial weight
    boolean wgtFixed;   // Is weight mutable at runtime?
    boolean enabled;    // Is module enabled?
    
    JPanel genPnl; // general options
    JCheckBox enabledCbx, fixedCbx;
    JTextField importanceField;
    
    /**
     * Return string identifier for this fitness module.
     */
    abstract public String getID();
    
    /**
     * Returns single-line description of the module's constraints.
     */
    abstract public String getDescription();
    
    /** Returns true if only the most recent block should be sent to this module for analysis. */
    public boolean sendOnlyLastBlock() { return false; }
    
    /**
     * Evaluate a musical phrase and return a suitable score. The method is passed
     * a standard pitch array representation, plus a representation of pitches and
     * durations. Evaluation is returned as some multiple of maximum fitness (1.0)
     */
    abstract public float evaluate(PhraseInfo p);
    
    /**
     * Handles options common across all genetic methods: base weight, weight fixed, enabled
     * Loads/sets defaults (specified by sub-class) and builds UI
     */
    public FitnessModule(Config c, int defImportance, boolean defFixed, boolean defEnabled) {
        String key = "fmg." + getID();
        if(c.get(key) == null) {
            c.put(key, new int[] { defImportance, defFixed?1:0, defEnabled?1:0 });
        }
        int[] args = (int[])c.get(key);
        baseWeight = weight = args[0];
        wgtFixed = (args[1] == 1);
        enabled = (args[2] == 1);
        makeGeneralOptionsPanel();
    }
    
    public FitnessModule(Config c, int defImportance, boolean defFixed) {
        this(c, defImportance, defFixed, true);
    }
	
	public FitnessModule(Config c, int defImportance) {
		this(c, defImportance, true, true);
	}
    
    public FitnessModule(Config c, boolean defFixed) {
        this(c, 100, defFixed, true);
    }
    
    public FitnessModule(Config c) {
        this(c, 100, true, true);
    }
    
    public void save(Config c) {
        c.put("fmg." + getID(), new int[] { baseWeight, wgtFixed?1:0, enabled?1:0 });
    }
    
    /**
     * Update object state to reflect UI values.
     */
    public void commit() {
        weight = baseWeight = Integer.parseInt(importanceField.getText());
        wgtFixed = fixedCbx.isSelected();
        enabled = enabledCbx.isSelected();
    }
    
    public JPanel getOptionsUI() {
        return genPnl;
    }
    
    public void makeGeneralOptionsPanel() {
        genPnl = new JPanel(new BorderLayout());
        genPnl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(getID()),
            BorderFactory.createEmptyBorder(5,5,5,5)));
    
        JPanel genCfg = new JPanel(new FlowLayout(FlowLayout.LEADING));
        enabledCbx = new JCheckBox("Enabled", enabled);
        genCfg.add(enabledCbx);
        genCfg.add(Box.createRigidArea(new Dimension(10, 0)));
        genCfg.add(new JLabel("Importance"));
        importanceField = new JTextField(Integer.toString(baseWeight), 4);
        genCfg.add(importanceField);
        genCfg.add(Box.createRigidArea(new Dimension(3, 0)));
        fixedCbx = new JCheckBox("Fixed", wgtFixed);
        genCfg.add(fixedCbx);
        
        JPanel hdrBox = new JPanel();
        hdrBox.setLayout(new BoxLayout(hdrBox, BoxLayout.PAGE_AXIS));
        JLabel descLbl = new JLabel(getDescription());
        descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        hdrBox.add(descLbl);
        genCfg.setAlignmentX(Component.LEFT_ALIGNMENT);
        hdrBox.add(genCfg);
        
        genPnl.add(hdrBox, BorderLayout.PAGE_START);
        genPnl.add(new JScrollPane(pnl, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
    }
    
    /**
     * Modify weight to a new, possibly random value.
     */
    abstract public void newWeight();
    
    // Helper methods to call from newWeight()
    
    /**
     * Weight is randomly set between the two provided values
     * (inclusive)
     */
    public void setWeightRandom(int low, int high) {
        weight = low + rand.nextInt(high - low + 1);
    }
    
    /**
     * Weight is increased or decreased a random amount within
     * a certain limit, with direction changing a certain amount of
     * the time.
     */
    static int dir = 1;
	static boolean weightSet = false;
    public void setWeightWalk(int init, int low, int high, int maxDist, double dirChangeProb) {
		if(!weightSet) { weightSet = true; weight = init; }
        dir = (rand.nextDouble() < dirChangeProb ? -dir : dir);
        weight += dir * rand.nextInt(maxDist + 1);
        if(weight > high) weight = high;
        else if(weight < low) weight = low;
    }
    
    /**
     * Weight is set according to the normal distribution around
     * the given weight and with the given standard deviation.
     */
    public void setWeightNormal(int base, double stdDev) {
        weight = (int)Math.round(rand.nextGaussian()*stdDev) + base;
    }
    public void setWeightNormal(int base) { setWeightNormal(base, 1.0); }
     
    /**
     * Weight is set to a non-zero value a percentage of times
     * newWeight is called, otherwise zero.
     */
    public void setWeightJump(int newVal, double percent) {
        weight = (rand.nextDouble() < percent) ? newVal : 0;
    }
    
    public boolean equals(FitnessModule m) {
        return getID().equals(m.getID());
    }
    
    public String toString() {
        return "[Fm" + getID() + " wgt=" + weight + " base=" + baseWeight + " fixed=" + wgtFixed + " enabled=" + enabled + "]";
    }    
}