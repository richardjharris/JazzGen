import java.awt.*;
import java.awt.event.*;
import javax.swing.*;      
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;

/**
 * User interface class for general genetic algorithm options, outside
 * of the fitness function, mutation, initialisation and crossover.
 */
public class GenAlgOptionsUI extends JPanel {
    GeneticGenerator gen;
    JTextField popSizeField, tourSizeField, maxGenField;
	JTextField mutationProbField, histSizeField, maxPrevPhrasesField;
	JTextField repeatPhraseProbField, repeatPhraseSkewField;
    JTextField elitismSelField;
    
    public GenAlgOptionsUI(GeneticGenerator gen) {
        this.gen = gen;
        makeMainPanel();
    }
    
    /**
     * Saves general parameters to the config object.
     */
    public void save(Config c) {
        commit();
        c.put("genopt.int", new int[] { gen.populationSize, gen.tourSize, gen.maxGenerations, gen.histSize, gen.maxPrevPhrases });
		c.put("genopt.double", new double[] { gen.mutationProb, gen.repeatPhraseProb, gen.repeatPhraseSkew });
        c.put("genopt.elitsel", new Integer(gen.elitismSel));
    }
    
    /**
     * Ensures that the GeneticGenerator object's values reflect those of
     * the user interface.
     */
    public void commit() {
        gen.populationSize = Integer.parseInt(popSizeField.getText());
        gen.tourSize = Integer.parseInt(tourSizeField.getText());
        gen.maxGenerations = Integer.parseInt(maxGenField.getText());
		gen.histSize = Integer.parseInt(histSizeField.getText());
        gen.maxPrevPhrases = Integer.parseInt(maxPrevPhrasesField.getText());
		
		gen.mutationProb = Double.parseDouble(mutationProbField.getText());
		gen.repeatPhraseProb = Double.parseDouble(repeatPhraseProbField.getText());
		gen.repeatPhraseSkew = Double.parseDouble(repeatPhraseSkewField.getText());
        
        gen.elitismSel = Integer.parseInt(elitismSelField.getText());
    }
    
    /**
     * Updates all UI elements to reflect the contents of the Config object.
     */
    public void load(Config c) {
        if(c.get("genopt.int") == null)
            c.put("genopt.int", new int[] { 32, 4, 1000, 4, 5 });
		if(c.get("genopt.double") == null)
			c.put("genopt.double", new double[] { 0.5, 0.3, 0.5 });
        if(c.get("genopt.elitsel") == null)
            c.put("genopt.elitsel", new Integer(4));
			
        int[] iargs = (int[])c.get("genopt.int");
        popSizeField.setText(Integer.toString(iargs[0]));
        tourSizeField.setText(Integer.toString(iargs[1]));
        maxGenField.setText(Integer.toString(iargs[2]));
		histSizeField.setText(Integer.toString(iargs[3]));
        maxPrevPhrasesField.setText(Integer.toString(iargs[4]));
		
		double[] dargs = (double[])c.get("genopt.double");
		mutationProbField.setText(Double.toString(dargs[0]));
		repeatPhraseProbField.setText(Double.toString(dargs[1]));
		repeatPhraseSkewField.setText(Double.toString(dargs[2]));
        
        elitismSelField.setText(Integer.toString((Integer)c.get("genopt.elitsel")));
    }
    
    // UI building    
    private void makeMainPanel() {
		setLayout(new BorderLayout());
		JPanel pnl = new JPanel(new SpringLayout());
        pnl.add(new JLabel("Population size"));
        popSizeField = new JTextField(4);
        pnl.add(popSizeField);
        pnl.add(new JLabel("Maximum generations per block"));
        maxGenField = new JTextField(8);
        pnl.add(maxGenField);
        pnl.add(new JLabel("Tournament selection pool size"));
        tourSizeField = new JTextField(4);
        pnl.add(tourSizeField);
		pnl.add(new JLabel("% chance of mutation on child"));
        mutationProbField = new JTextField(4);
        pnl.add(mutationProbField);
		pnl.add(new JLabel("Max. previous bars to remember"));
        histSizeField = new JTextField(4);
        pnl.add(histSizeField);
		pnl.add(new JLabel("Chance of repeating a bar from history"));
        repeatPhraseProbField = new JTextField(4);
        pnl.add(repeatPhraseProbField);
		pnl.add(new JLabel("Tendency to pick more recent bars"));
        repeatPhraseSkewField = new JTextField(4);
        pnl.add(repeatPhraseSkewField);
        pnl.add(new JLabel("Max. previous bars to use in fitness evaluation"));
        maxPrevPhrasesField = new JTextField(4);
        pnl.add(maxPrevPhrasesField);
        pnl.add(new JLabel("Number of elitism candidates"));
        elitismSelField = new JTextField(4);
        pnl.add(elitismSelField);
		
		// Make grid with init (5, 5); hgap 30; vgap 5
		SpringUtilities.makeCompactGrid(pnl, 9, 2, 5, 5, 10, 5);
		add(new JScrollPane(pnl, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
    }
}
