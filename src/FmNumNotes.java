import javax.swing.*;   
import java.awt.*;   

/**
 * Awards a bonus corresponding to the number of notes in the phrase.
 */
public class FmNumNotes extends FitnessModule {
    public String getID() { return "NumNotes"; }
    public String getDescription() { return "Encourages a sane number of notes per block."; }
    public boolean sendOnlyLastBlock() { return true; }
    
    int targetNotes;
    JTextField targetField;
    
    public FmNumNotes(Config c) {
        super(c, 100);
        if(c.get("fm.nn") == null)
            c.put("fm.nn", new Integer(12));
        targetNotes = ((Integer)c.get("fm.nn")).intValue();
        makeOptionsPanel();
    }
    
    public float evaluate(PhraseInfo p) {
        // Value assumes 2 chords per block. If more or less, adjust
        int numChords = p.gen.prog.get(p.gen.progIndex).size();
        int tn = Math.min(phraseLen, (int)Math.round(targetNotes * ((numChords + 1) / 3.0))); // not directly proportional.
        // scores: 50, 49, 46, 41, 34, 25 etc.
        int score = Math.max(0, 50 - (int)Math.round(Math.pow(Math.abs(p.noteCount - tn), 2)));
        return (float)Math.min(1.0f, score/50.0f);
    }

    public void newWeight() {
        setWeightNormal(baseWeight, 10.0);
    }
    
    public void makeOptionsPanel() {
        pnl.add(new JLabel("Target notes: "));
        targetField = new JTextField(Integer.toString(targetNotes), 2);
        pnl.add(targetField);
    }
    public void save(Config c) { commit(); super.save(c); c.put("fm.nn", new Integer(targetNotes)); }
    public void commit() { super.commit(); targetNotes = Integer.parseInt(targetField.getText()); }
}