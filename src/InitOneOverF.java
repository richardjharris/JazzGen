import java.awt.GridLayout;
import java.awt.event.*; 
import javax.swing.*;

/**
 * Generate one sequence of pitches via a purely random distribution.
 */
public class InitOneOverF extends InitMethod {    
    int minOctave;
    int maxOctave;
    
    JTextField minOctaveField, maxOctaveField;
    
    public InitOneOverF(Config c) {
        if(c.get("i.1/f") == null) {
            c.put("i.1/f", new int[] { 3, 6 });
        }
        int[] params = (int[])c.get("i.1/f");
        minOctave = params[0];
        maxOctave = params[1];
        makeOptionsPanel();
    }
    
    public byte[] generate() {
        byte[] seed = new byte[curLength];
        double[] fpicks = getOneOverF(curLength);
        byte fIndex = 0; // next unused 1/f pick
        
        for(int i = 0; i < seed.length; i++) {
			byte[] scale = getScale(i);
            if(rand.nextDouble() > 0.5) {
                seed[i] = Rest;
            }
            else {
                byte pitchIdx = (byte)Math.floor(fpicks[fIndex++]*scale.length*(maxOctave - minOctave + 1)); // C3-6 is actually two octaves
                seed[i] = (byte)((minOctave+1)*12 + Math.floor(pitchIdx / scale.length)*12 + scale[pitchIdx % scale.length]);
            }
        }
        return seed;
    }
    
    /**
     * Generate _size_ 1/f-distributed values between 0 and 1.
     * Algorithm by R. F. Voss (European Broadcasting Union)
     * Taken from Computer Music (Dodge & Jerse, 1997),
     *     Second Edition p.369-70.
     */
    public double[] getOneOverF(int npts) {
        double seqout[] = new double[npts];
        double sum;
        double rg[] = new double[16];
        int k, kg, ng, threshold, np, nbits;
        nbits = 1;
        np = 1;
        double nr = npts;
        nr = nr / 2;
        while(nr > 1) {
            nbits++;
            np = np * 2;
            nr = nr / 2;
        }
        for(kg = 0; kg < nbits; kg++)
            rg[kg] = rand.nextDouble();
        for(k = 0; k < npts; k++) {
            threshold = np;
            ng = nbits;
            while(k % threshold != 0) {
                ng--;
                threshold = threshold / 2;
            }
            sum = 0;
            for(kg = 0; kg < nbits; kg++) {
                if(kg < ng) rg[kg] = rand.nextDouble();
                sum += rg[kg];
            }
            seqout[k] = sum/nbits;
        }
        return seqout;
    }
    
    public void makeOptionsPanel() {
        pnl.setLayout(new GridLayout(2, 2));
        pnl.add(new JLabel("Min octave"));
        minOctaveField = new JTextField(Integer.toString(minOctave), 2);
        pnl.add(minOctaveField);
        
        pnl.add(new JLabel("Max octave"));
        maxOctaveField = new JTextField(Integer.toString(maxOctave), 2);
        pnl.add(maxOctaveField);
    }
    
    public void save(Config c) {
        commit();
        c.put("i.1/f", new int[] { minOctave, maxOctave });
    }
    
    public void commit() {
        minOctave = Integer.parseInt(minOctaveField.getText());
        maxOctave = Integer.parseInt(maxOctaveField.getText());
    }
}