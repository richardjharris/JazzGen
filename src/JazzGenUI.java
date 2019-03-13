import java.awt.*;
import java.awt.event.*;
import javax.swing.*;      
import javax.swing.event.*;
import java.io.*;
import java.util.*;
import javax.swing.filechooser.*;

/**
 * User interface class for JazzGen. Controls fitness function, chord progression
 * etc. and invokes JazzGen via the SwingWorker interface.
 */
public class JazzGenUI extends JFrame {
    JazzGen jg;
    JPanel crossoverPanel, initPanel;
    JComboBox crossoverCbox, initCbox, meterList;
    JTextArea chordProgField;
    JTextField repeatCountField, tempoField;
    JProgressBar genProgressBar;
    FitnessFunctionUI ffui;
    GenAlgOptionsUI gaui;
	MutationOptionsUI mfui;
    
    Config config;
    
    public JazzGenUI() {
        super("JazzGen");
        config = Config.loadDefault();
        jg = new JazzGen();
        ffui = new FitnessFunctionUI(jg.gen.fitFunc);
        gaui = new GenAlgOptionsUI(jg.gen);
		mfui = new MutationOptionsUI(jg.gen.mutFunc);
        makeMainFrame();
        loadAll();
    }
    
    /**
     * Save configuration so it is restored on the next load, and exit cleanly.
     */
    public void saveAndClose() {
        try {
            saveAll();
            config.saveDefault();
            System.exit(0);
        }
        catch(Exception ex) {
            System.out.println(ex);
            System.exit(1);
        }
    }
    
    // Action listeners
    private class CloseAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            saveAndClose();
        }
    }
    
    private class LoadAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser();
            int retVal = fc.showOpenDialog(JazzGenUI.this);
            if(retVal == JFileChooser.APPROVE_OPTION) {
                try {
                    config = Config.loadFromFile(fc.getSelectedFile());  
                    // Force reload of all UI components
                    loadAll();
                }
                catch(Exception ex) {
                    JOptionPane.showMessageDialog(JazzGenUI.this,
                        "Error loading options:\n" + ex.getMessage(),
                        "Error loading",
                        JOptionPane.ERROR_MESSAGE);
                }
            }            
        }
    }
    
    private class SaveAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Call save() method on all UI components, then write Config to disk
            JFileChooser fc = new JFileChooser();
            int retVal = fc.showSaveDialog(JazzGenUI.this);
            if(retVal == JFileChooser.APPROVE_OPTION) {
                try {
                    String fn = fc.getSelectedFile().getAbsolutePath();
                    // Add .opt extension if none present
                    if(fn.lastIndexOf(".") == -1) fn += ".opt";
                    saveAll();
                    config.saveToFile(new File(fn));           
                }
                catch(Exception ex) {
                    JOptionPane.showMessageDialog(JazzGenUI.this,
                        "Error saving options:\n" + ex.getMessage(),
                        "Error saving",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }    
    
    private class PlayAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            jg.play();
        }
    }    
    
    private class GenerateAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Parse chord progression
            try {
                jg.prog = new ChordProgression(chordProgField.getText(),
                    Integer.parseInt(repeatCountField.getText()));
            }
            catch(Exception ex) {
                JOptionPane.showMessageDialog(JazzGenUI.this,
                    "Error parsing chord progression: " + ex.getMessage(),
                    "Invalid chord progression",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            jg.tempo = Double.parseDouble(tempoField.getText());
        
            // Update JazzGen object with UI parameters
            gaui.commit();
            ffui.commit();
			mfui.commit();
            jg.gen.crossoverMethod.commit();
            jg.gen.initMethod.commit();
            // Run JazzGen in a background process
            JazzGenTask task = new JazzGenTask(jg, genProgressBar);
            task.execute();
        }
    }
    
    private class WriteMidiAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Open file chooser
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter(
        "MIDI file (*.mid)", "mid"));

            int retVal = fc.showSaveDialog(JazzGenUI.this);
            if(retVal == JFileChooser.APPROVE_OPTION) {
                try {
                    String fn = fc.getSelectedFile().getAbsolutePath();
                    // Add .mid extension if none present
                    if(fn.lastIndexOf(".") == -1) fn += ".mid";
                    OutputStream out = new FileOutputStream(new File(fn));
                    jg.writeMidi(out);
                    out.close();                
                }
                catch(Exception ex) {
                    JOptionPane.showMessageDialog(JazzGenUI.this,
                        "Error saving MIDI to file:\n" + ex.getMessage(),
                        "Error saving",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
       
    private class WriteLilyPondAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Open file chooser
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter(
        "GNU Lilypond file (*.ly)", "ly"));

            int retVal = fc.showSaveDialog(JazzGenUI.this);
            if(retVal == JFileChooser.APPROVE_OPTION) {
                try {
                    String fn = fc.getSelectedFile().getAbsolutePath();
                    // Add .ly extension if none present
                    if(fn.lastIndexOf(".") == -1) fn += ".ly";
                    jg.writeLilyPond(new File(fn));              
                }
                catch(Exception ex) {
                    JOptionPane.showMessageDialog(JazzGenUI.this,
                        "Error saving Lilypond file:\n" + ex.getMessage(),
                        "Error saving",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }    
    
    private abstract class MethodChangeAction {
        protected String getSelectedItem(ActionEvent e) {
            JComboBox source = (JComboBox)e.getSource();
            return (String)source.getModel().getSelectedItem();
        }

        protected void updateMethodUI(JPanel parent, GeneticMethod method) {
            parent.remove(1);
            JPanel ui = method.getOptionsUI();
            parent.add(ui);
            ui.revalidate();
        }            
    }
    
    private class CrossoverMethodChangeAction extends MethodChangeAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String methodName = getSelectedItem(e);        
            // save before replacing     
            if(jg.gen.crossoverMethod != null) jg.gen.crossoverMethod.save(config); 
            CrossoverMethod method = CrossoverMethodFactory.getMethod(methodName, config);
            jg.gen.crossoverMethod = method;
            updateMethodUI(crossoverPanel, method);
        }
    }
    
    private class InitMethodChangeAction extends MethodChangeAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String methodName = getSelectedItem(e);  
            if(jg.gen.initMethod != null) jg.gen.initMethod.save(config);
            InitMethod method = InitMethodFactory.getMethod(methodName, config);
            jg.gen.initMethod = method;
            updateMethodUI(initPanel, method);
        }
    }
    
    /**
     * Forces all genetic methods and other UI elements to save their parameters to the
     * Config object, so it can be written to disk.
     */
    private void saveAll() {
        // JazzGen general parameters
        config.put("general", new Object[] {
            chordProgField.getText(),
            new Integer(repeatCountField.getText()),
            new Integer(tempoField.getText()),
            meterList.getSelectedItem()
        });
        // Individual methods + method selections
        jg.gen.initMethod.save(config);
        jg.gen.crossoverMethod.save(config);
        config.put("msel", new String[] { (String)initCbox.getSelectedItem(),
            (String)crossoverCbox.getSelectedItem() });
        // Fitness function
        gaui.save(config);
        ffui.save(config);
		mfui.save(config);
    }
    
    /**
     * Updates all UI elements to reflect the contents of the Config object.
     */
    private void loadAll() {
        // JazzGen general parameters
        if(config.get("general") == null) {
            config.put("general", new Object[] {
                "C7 / C7 / F7 / C7\nG7 / F7 / C7 / C7",
                new Integer(1),
                new Integer(105),
                "4/4"
            });
        }
        Object[] genArgs = (Object[])config.get("general");
        chordProgField.setText((String)genArgs[0]);
        repeatCountField.setText(((Integer)genArgs[1]).toString());
        tempoField.setText(((Integer)genArgs[2]).toString());
        meterList.setSelectedItem((String)genArgs[3]);
        // Set selected methods
        if(config.get("msel") == null) {
            config.put("msel", new String[] {"random scale", "uniform"});
        }
        String[] msel = (String[])config.get("msel");
        jg.gen.initMethod = null;
        initCbox.setSelectedItem(msel[0]);
        jg.gen.crossoverMethod = null;
        crossoverCbox.setSelectedItem(msel[1]);
        // Fitness function
        gaui.load(config);
        ffui.load(config);
		mfui.load(config);
    }
    
    // UI building    
    private void makeMainFrame() {
        JPanel pane = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = 0;
        pane.add(new JLabel("Chord progression"), c);
        pane.add(new JLabel("Repeat"), c);
        pane.add(new JLabel("Tempo"), c);
        pane.add(new JLabel("Meter"), c);
        
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.7; c.weighty = 0.1;
        c.ipady = 20;
        chordProgField = new JTextArea(1, 40);
        chordProgField.setLineWrap(true);
        chordProgField.setWrapStyleWord(true);
        JScrollPane cpScrollPane = new JScrollPane(chordProgField);
        cpScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        cpScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.add(cpScrollPane, c);
        
        c.fill = GridBagConstraints.NONE;
        c.weightx = c.weighty = 0.0;
        c.ipady = 0;
        JPanel repeatPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        repeatPane.add(new JLabel("x"));
        repeatCountField = new JTextField(2);
        repeatPane.add(repeatCountField);
        pane.add(repeatPane, c);
        
        tempoField = new JTextField(10);
        pane.add(tempoField, c);
        
        String [] meters = { "3/4", "4/4", "5/4", "7/4", "11/8" };
        meterList = new JComboBox(meters);
        meterList.setEditable(true);
        pane.add(meterList, c);
        
        c.gridy = 2;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0; c.weighty = 0.8;
        JTabbedPane tabs = makeTabs();
        pane.add(tabs, c);
        
        c.gridy = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = c.weighty = 0.0;        
        BorderLayout bl = new BorderLayout();
        JPanel buttonBar = new JPanel(bl);
        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton btnGenerate = new JButton("Generate");
        btnGenerate.addActionListener(new GenerateAction());
        actionButtons.add(btnGenerate);
        JButton btnPlay = new JButton("Play");
        btnPlay.addActionListener(new PlayAction());
        actionButtons.add(btnPlay);
        JButton btnWriteMidi = new JButton("To MIDI");
        btnWriteMidi.addActionListener(new WriteMidiAction());
        actionButtons.add(btnWriteMidi);        
        JButton btnWriteLilyPond = new JButton("To LilyPond");
        btnWriteLilyPond.addActionListener(new WriteLilyPondAction());
        actionButtons.add(btnWriteLilyPond);
        buttonBar.add(actionButtons, BorderLayout.LINE_START);
        
        JPanel fileButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JButton btnLoad = new JButton("Load Opts");
        btnLoad.addActionListener(new LoadAction());
        fileButtons.add(btnLoad);
        JButton btnSave = new JButton("Save Opts");
        btnSave.addActionListener(new SaveAction());
        fileButtons.add(btnSave);
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(new CloseAction());
        fileButtons.add(btnClose);
        buttonBar.add(fileButtons, BorderLayout.LINE_END);
        pane.add(buttonBar, c);
        
        c.gridy = 4;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        genProgressBar = new JProgressBar();
        pane.add(genProgressBar, c);
        
        add(pane);
        pack();
        center();
        WindowListener closeHandler = new WindowAdapter() {
            public void windowClosing(WindowEvent e) { saveAndClose(); }
        };
        addWindowListener(closeHandler); 
        setVisible(true);
    }
    
    private JTabbedPane makeTabs() {
        JTabbedPane tabs;
        JPanel tFitness, tSelection, tMutation, tInit, tCrossover;
        JPanel tGeneticAlgorithm;
        
        Object[] args = makeMultiOptionTab("Initialisation method",
            InitMethodFactory.methods(), new InitMethodChangeAction(),
            jg.gen.initMethod);
        initPanel = (JPanel)args[0]; initCbox = (JComboBox)args[1];
        args = makeMultiOptionTab("Crossover method",
            CrossoverMethodFactory.methods(), new CrossoverMethodChangeAction(),
            jg.gen.crossoverMethod);
        crossoverPanel = (JPanel)args[0]; crossoverCbox = (JComboBox)args[1];
        
        tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabs.addTab("Fitness function", ffui);
        tabs.addTab("Genetic algorithm", gaui);
        tabs.addTab("Mutation", mfui);
        tabs.addTab("Initialisation", initPanel);
        tabs.addTab("Crossover", crossoverPanel);

        return tabs;
    }
       
    /**
     * Build and return a tab that contains a series of options, each with their own UI.
     * @return An Object array containing the tab's JPanel and method JComboBox.
     */
    private Object[] makeMultiOptionTab(String label, String[] methodList, ActionListener handler, GeneticMethod initMethod) {
        JPanel panel = new JPanel();
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        BoxLayout bl = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
        panel.setLayout(bl);
        JPanel opts = new JPanel();
        opts.add(new JLabel(label + ": "));
        
        JComboBox cBox = new JComboBox(methodList);
        cBox.addActionListener(handler);
        opts.add(cBox);
        panel.add(opts);
        panel.add(new JPanel()); // dummy: will be replaced on config load
        
        return new Object[] { panel, cBox };
    }
    
    public void center() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point center = ge.getCenterPoint();
        Rectangle bounds = ge.getMaximumWindowBounds();
        int w = Math.max(bounds.width/2, Math.min(getWidth(), bounds.width));
        int h = Math.max(bounds.height/2, Math.min(getHeight(), bounds.height));
        int x = center.x - w/2, y = center.y - h/2;
        setBounds(x, y, w, h);
        if (w == bounds.width && h == bounds.height)
            setExtendedState(Frame.MAXIMIZED_BOTH);
        validate();
    }


    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
        /* Invoke from event-dispatch thread. */
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new JazzGenUI();
            }
        });
    }
}
