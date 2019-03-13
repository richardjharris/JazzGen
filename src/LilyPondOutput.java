import jm.util.*;
import jm.music.data.*;
import jm.music.tools.*;
import java.io.*;
import java.util.Date;

/**
 * Converts a JMusic Score object to GNU Lilypond source suitable
 * for conversion to PDF.
 */
public class LilyPondOutput implements JGConstants {
    public static void convert(Score score, ChordProgression prog, File output) throws IOException {
        convert(score, prog, output, "JazzGen Composition");
    }
    public static void convert(Score score, ChordProgression prog, File output, String title) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(output)));
        String format = getContents("lilypond.template");
        Part piano = score.getPart("Piano");
        Part bass  = score.getPart("Bass");
        Part drums = score.getPart("Drums");
        try {
            out.format(
                format, title, new Date(),
                (int)Math.floor(score.getTempo()),
                toLy(piano),
                toLy(prog),
                toLy(bass),
                repeat("\\DrumsHH ", prog.length() * 2),
                repeat("\\DrumsSNBD ", prog.length() * 4)
            );
        }
        catch(Exception e) { System.out.println(e); }
        out.close();
    }
    
    // Repeat a string _repeat_ times.
    public static String repeat(String s, int repeat) {
        StringBuffer buf = new StringBuffer();
        for(int i = 0; i < repeat; i++) {
            buf.append(s);
        }
        return buf.toString();
    }
    
    /**
     * Convert a Phrase to a Lilypond Staff block.
     */
    public static String toLy(Phrase p) {
        StringBuffer buf = new StringBuffer();
        Mod.tieRests(p); // join adjacent rests into one note
        for(Note n: p.getNoteArray()) {
            buf.append(toLy(n));
            buf.append(' ');
        }
        return buf.toString();
    }
    
    /**
     * Convert a chord progression to a Lilypond Staff block
     * (used for the piano chords)
     */
    public static String toLy(ChordProgression prog) {
        StringBuffer buf = new StringBuffer();
        for(int i = 0; i < prog.length(); i++) {
			int[] durs = prog.getDurations(i);
			int j = 0;
			for(Chord c: prog.get(i)) {
				StringBuffer chord = new StringBuffer();
				chord.append('<');
				for(int note: c.notes(3)) {
	                chord.append(noteNames[note % 12]);
	                chord.append(octModifier[note / 12]);
	                chord.append(' ');
				}
				chord.append(">");
				
				buf.append(chord);
				if(durs[j] == phraseLen) {
					// Covers two bars
					buf.append("1 ");
					buf.append(chord);
					buf.append("1 ");
				}
				else {
					buf.append(phraseLen / (2 * durs[j]));
					buf.append(" ");
				}
				j++;
            }
        }
        return buf.toString();
    }
    
    static String[] noteNames = {
        "c", "cs", "d", "ds", "e", "f",
        "fs", "g", "gs", "a", "as", "b"
    };
    
    static String[] octModifier = {
        ",,,,", /* oct -1 */
        ",,,", ",,", ",", "",
        "'", /* oct 4 (middle) */
        "''", "'''", "''''", "'''''", "''''''" /* oct 9 */
    };
    
    /**
     * Convert a Note to Lilypond notation.
     */
    public static String toLy(Note n) {
        StringBuffer buf = new StringBuffer();
        if(n.isRest()) {
            buf.append('r');
        }
        else {
            int pitch = n.getPitch();
            int octave = pitch / 12;
            pitch %= 12;
            buf.append(noteNames[pitch]);
            buf.append(octModifier[octave]); // @todo: +1
        }
        
        // If res. is 16ths, we're only going to get a max of:
        // 0.5 8th, 0.25 16th.
        // If not found, repeat.
        // rv = 1.0 for 4th, 2.0 for half, 0.5 for 8th etc.
        // Depending on post-pass may not be able to handle certain durations -- fix manually.
        double rv = n.getRhythmValue();
        int dots = 0;
        int lyRv = 0;
        while(lyRv == 0 && dots < 3) {
            if(rv == 0.25)     lyRv = 16;
            else if(rv == 0.5) lyRv = 8;
            else if(rv == 1.0) lyRv = 4;
            else if(rv == 2.0) lyRv = 2;
            else if(rv == 4.0) lyRv = 1;
            if(lyRv == 0) {
                // e.g. 1.5 is dotted 4th
                rv *= 2;
                rv /= 3;
                dots++;
            }
        }
        if(dots == 3) {
            System.out.println("Unusual duration: " + n.getRhythmValue());
            buf.append("*");
        }
        buf.append((int)(4/rv));
        while(dots-- > 0) buf.append('.');
        
        return buf.toString();
    }

    public static String toLy(Part p) {
        return toLy(p.getPhrase(0));
    }        
    
    public static String getContents(String file) throws IOException {
        StringBuffer c = new StringBuffer();
        BufferedReader input = new BufferedReader(new FileReader(file));
        try {
            String line;
            while((line = input.readLine()) != null) {
                c.append(line);
                c.append(System.getProperty("line.separator"));
            }
        }
        finally { input.close(); }
        
        return c.toString();
    }
}
