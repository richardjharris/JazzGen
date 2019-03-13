import java.awt.*;
import java.awt.event.*;
import javax.swing.*;      
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;

/**
 * Lists all possible mutation methods and allows the user to enable or
 * disable them individually. While most methods have lots of parameters
 * (e.g. probability of pitch shift per note, how much to shift...) they
 * are NOT opened up to the user, as this would simply create too many
 * choices!
 */
public class MutationOptionsUI extends JPanel {
    MutationFunction mf;
	JCheckBox[] enabledCbx;
	int fnCount;
    
    public MutationOptionsUI(MutationFunction mf) {
        this.mf = mf;
		this.fnCount = mf.fnNames.length;
		enabledCbx = new JCheckBox[fnCount];
        makeMainPanel();
    }
    
    /**
     * Save the list of enabled functions to Config.
     */
    public void save(Config c) {
        commit();
        c.put("mutopt", mf.fnEnabled);
	}
    
    /**
     * Notify MutationFunction to use the mutation functions we have selected.
     */
    public void commit() {
		for(int i = 0; i < fnCount; i++) {
			mf.fnEnabled[i] = enabledCbx[i].isSelected();
		}
    }
    
    /**
     * Updates all UI elements to reflect the contents of the Config object.
     */
    public void load(Config c) {
		boolean[] enabled = new boolean[fnCount];
        if(c.get("mutopt") == null) {
			Arrays.fill(enabled, true); // def: all mutations enabled
            c.put("mutopt", enabled);
        }
		else {
			enabled = (boolean[])c.get("mutopt");
		}
        for(int i = 0; i < fnCount; i++) {
			enabledCbx[i].setSelected(enabled[i]);
		}
    }
    
    // UI building    
    private void makeMainPanel() {
		setLayout(new BorderLayout());
		JPanel pnl = new JPanel(new SpringLayout());
		for(int i = 0; i < fnCount; i++) {
			enabledCbx[i] = new JCheckBox();
			enabledCbx[i].setHorizontalAlignment(SwingConstants.CENTER);
			JLabel l = new JLabel(mf.fnNames[i]);
			l.setLabelFor(enabledCbx[i]);
			pnl.add(l);
			pnl.add(enabledCbx[i]);
		}
		// Make grid with init (5, 5); hgap 30; vgap 5
		SpringUtilities.makeCompactGrid(pnl, (int)Math.ceil(fnCount/2), 4, 5, 5, 30, 5);
		add(new JScrollPane(pnl, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
    }
}
