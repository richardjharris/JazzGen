import java.awt.*;
import java.awt.event.*;
import javax.swing.*;      
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;

/**
 * User interface class for the modular fitness function. Tied to a FitnessFunction, it is
 * responsible for allowing the user to alter fitness function parameters, enable/disable
 * modules, change module weights and save/load configurations.
 */
public class FitnessFunctionUI extends JPanel {
    FitnessFunction ff;
    
    JList moduleList;
    
    public FitnessFunctionUI(FitnessFunction ff) {
        this.ff = ff;
        makeMainPanel();
    }
    
    /**
     * Forces all fitness modules to save to the Config object, saves general 
     * parameters and enabled modules.
     */
    public void save(Config c) {
        // Save individual module settings
        for(FitnessModule m: ff.modules) {
            m.save(c);
        }
    }
    
    /**
     * Ensures that all fitness modules have parameters reflecting the current
     * user interface inputs.
     */
    public void commit() {
        int enabledMods = 0;
        for(FitnessModule m: ff.modules) {
            m.commit();
            if(m.enabled) enabledMods++;
        }
        ff.enabled = new FitnessModule[enabledMods];
        int idx = 0;
        for(FitnessModule m: ff.modules) {
            if(m.enabled) ff.enabled[idx++] = m;
        }
    }
    
    /**
     * Updates all UI elements to reflect the contents of the Config object.
     */
    public void load(Config c) {
        // Load fitness modules with Config object
        ff.modules = FitnessModuleFactory.getAllModules(c);
        
        // Construct module list UI
        String[] modNames = new String[ff.modules.length];
        for(int i = 0; i < modNames.length; i++) {
            modNames[i] = ff.modules[i].getID();
        }
        moduleList.setListData(modNames);
        moduleList.setSelectedIndex(0); // select first
        
        // Set JList width to be large enough to show all module names,
        // plus padding
        int maxIndex = 0;
        for(int i = 0; i < modNames.length; i++) {
            if(modNames[i].length() > modNames[maxIndex].length()) {
                maxIndex = i;
            }
        }
        moduleList.setPrototypeCellValue(modNames[maxIndex] + "     ");
    }
    
    static int lastSelection = -1;
    
    // Value change handlers
    private class ModuleSelectionAction implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            int sel = moduleList.getSelectedIndex();
            if(sel == lastSelection) return; // avoid duplicate events
            // Update UI
            FitnessModule m = ff.modules[sel];

            FitnessFunctionUI.this.remove(1);
            JPanel moduleConfigPanel = m.getOptionsUI();
            FitnessFunctionUI.this.add(moduleConfigPanel);
            FitnessFunctionUI.this.revalidate();
            FitnessFunctionUI.this.repaint();

            lastSelection = sel;
        }
    }
    
    // UI building    
    private void makeMainPanel() {
        setLayout(new BorderLayout());
        
        // Module list
        moduleList = new JList();
        moduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        moduleList.addListSelectionListener(new ModuleSelectionAction());
        JScrollPane moduleListScrollPane = new JScrollPane(moduleList);
        
        JPanel moduleListPanel = new JPanel(new BorderLayout());
        moduleListPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Modules"),
            BorderFactory.createEmptyBorder(5,5,5,5)));
        moduleListPanel.add(moduleListScrollPane, BorderLayout.CENTER);
        add(moduleListPanel, BorderLayout.LINE_START);
        add(new JPanel(), BorderLayout.CENTER); // module config panel placeholder
    }
}
