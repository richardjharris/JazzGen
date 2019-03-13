import jm.util.*;
import jm.music.data.*;
import jm.music.tools.*;
import jm.midi.MidiSynth;
import java.util.*;
import java.util.regex.*;

/**
 * Represents the solo's chord progression, handles user input parsing
 * and encapsulates information about scale and chord note values.
 */
public class ChordProgression implements JGConstants {
	List<List<Chord>> chords;
	
	/* Map user-input strings to chord types. Many-to-one. */
	static Map<String, ChordType> cmap = new HashMap<String, ChordType>();

	/**
	 * Parses a chord progression in the form C7 D7 / Fmaj7 / Ealt ...
	 * The chord progression will be repeated the specified number
	 * of times.
	 */
	public ChordProgression(String progText, int repeat) throws Exception {
		String parseProg = "";
		while(repeat-- > 0) parseProg += progText + "\n";
		parseProgText(parseProg);
		calculateInversions();
	}
	
	public ChordProgression(String progText) throws Exception {
		this(progText, 0);
	}
	
	/**
	 * Return the exact chord at phrase _phrase_, position _pos_.
	 */
	public Chord getChordAt(int phrase, int pos) {
		assert pos < phraseLen;
		assert phrase < chords.size();
        try {
            List<Chord> bar = chords.get(phrase);
    		int[] durs = getDurations(phrase);
    		int i = 0;
    		while(pos >= durs[i]) pos -= durs[i++];
            return bar.get(i);
        }
        catch(Exception e) {
            System.out.println("getChordAt:" + phrase + "/" + pos);
            // trigger error again
            List<Chord> bar = chords.get(phrase);
            System.exit(1);
        }
		return null;
	}
	
	/**
     * Returns a list of all chord lengths (in units) for position pos.
	 */
	public int[] getDurations(int pos) {
		List<Chord> bar = chords.get(pos);
		int size = bar.size();
		int[] durs = new int[size];
		int pow2 = 1; while(pow2 < size) pow2 *= 2;  // Round size up to nearest power of two
		int unitSize = phraseLen/pow2;
		
		// Now assign each chord a size unitSize, assigning the last chord any
		// remaining space
		for(int i = 0; i < size - 1; i++) durs[i] = unitSize;
		durs[size - 1] = phraseLen - (size - 1)*unitSize;
		
		return durs;
	}
	
	/* Parse a String chord progression */
	private void parseProgText(String progText) throws Exception {
		Pattern chordPat = Pattern.compile("(?i)^([a-g](?:#|b)?)(.*)$");
		chords = new ArrayList<List<Chord>>();
		List<Chord> curList = new ArrayList<Chord>();
		chords.add(curList);
		
		StringTokenizer toks = new StringTokenizer(progText, " \t\n\r\f/", true);
		while(toks.hasMoreTokens()) {
			String tok = toks.nextToken().toLowerCase();
			if(tok.equals("/") || tok.equals("|") || tok.equals("\n")) {  // Bar delimiter
				if(!curList.isEmpty()) {   // possible that someone would type /, \n
					curList = new ArrayList<Chord>();
					chords.add(curList);
				}
			}
			else {
				tok = tok.trim();
				if(tok.length() > 0) {  // Chord symbol
					Matcher m = chordPat.matcher(tok);
					if(!m.matches()) throw new Exception("Invalid chord: " + tok);
				
					// Get pitch class
					byte pitch = -1;
					String note = m.group(1);
					for(int i = 0; i < pitchNameFull.length; i++) {
						if(note.equals(pitchNameFull[i]))
							pitch = pitchNameIndexes[i];
					}
					if(pitch == -1) throw new Exception("Invalid pitch class for chord: " + tok);
					
					// Get chord type
					ChordType ct = cmap.get(m.group(2));
					if(ct == null) throw new Exception("Chord " + tok + " is not in dictionary.");
					
					curList.add(new Chord(pitch, ct));
				}
			}
		}
		
		// Finally, strip out empty chord lists
		for(int i = 0; i < chords.size(); i++) {
			if(chords.get(i).isEmpty()) {
				chords.remove(i);
			}
		}
	}
	
	/**
     * Sets chord inversions such that voice leading is optimal i.e.
	 * notes between chords remain in close proximity.
	 */
	private void calculateInversions() {
		Chord prev = null;
		// First chord is played in root position
		for(int i = 0; i < chords.size(); i++) {
			List<Chord> bar = chords.get(i);
			for(Chord chord: bar) {	
				if(prev != null) {
					int bestInv = getBestInversion(chord, prev);
					chord.setInversion(bestInv);
				}
				prev = chord;
			}
		}
	}
	
	/**
	 * Return the best inversion to use for a chord, given a previous chord.
	 * Takes into account adjacency of notes and octave range.
	 */
	private int getBestInversion(Chord cur, Chord prev) {
		cur = cur.clone();  // we're modifying this
		int[] prevNotes = prev.notes(3);
		int bestInv = 0;  // if all else fails, don't invert
		int bestInvScore = 0;
		for(int inv = -4; inv < 5; inv++) {
			boolean outOfRange = false;
			cur.setInversion(inv);
			int[] curNotes = cur.notes(3);
			int score = 0;
			// Check each pair of notes
			for(int curNote: curNotes) {
				for(int prevNote: prevNotes) {
					if(curNote <= C2 || curNote >= G3) outOfRange = true;
					int diff = Math.abs(curNote - prevNote);
					score += (30/(diff+1));  // encourage closer notes
				}
			}
			if(outOfRange) score /= 2;  // discourage chords muddying solo lines
			// If score is equal to current best, randomly decide whether to favour it
			if(score > bestInvScore || (score == bestInvScore && Math.random() <= 0.5)) {
				bestInvScore = score;
				bestInv = inv;
			}
		}
		return bestInv;
	}
	
	/* Returns the length of the chord progression, in bars */
	public int length() { return chords.size();	}
	public List<Chord> get(int i) { return chords.get(i); }
    
    /* Returns the very last chord in the progression. */
    public Chord getLast() {
        List<Chord> lastBlock = chords.get(chords.size() - 1);
        return lastBlock.get(lastBlock.size() - 1);
    }
	
	public String toString() {
		String out = "";
		int i = 0;
		for(List<Chord> bar: chords) {
			out += "Bar " + (++i) + ": ";
			int j = 0;
			for(Chord c: bar) {
				out += c.toString();
				out += " [" + c.inversion + "]";
				if(++j < bar.size()) {
					out += ", ";
				}
				else out += "\n";
			}
		}
		return out;
	}
	
	/* Test */
	public static void main(String[] args) {
		try {
		ChordProgression cp = new ChordProgression(
			"C7 Ebmaj7 / C#7 D#7 | E7\nF7 | C C7 / C7 E7aug D7dim\nC7/E7+/C7+9 Dmaj7s11\nF C F C G", 1
		);
		System.out.println(cp);
		// Play the progression
		Score s = new Score(130.0); Part p = new Part(0, 0);
		CPhrase pp = new CPhrase(0.0);
		for(int i = 0; i < cp.chords.size(); i++) {
			List<Chord> bar = cp.chords.get(i);
			for(Chord chord: bar) {	
				pp.addChord(chord.notes(4), HN, MF);
			}
		}
		p.addCPhrase(pp);
		s.addPart(p);
		//Play.midi(s);
		// Test duration form
		for(int i = 0; i < cp.chords.size(); i++) {
			int[] durs = cp.getDurations(i);
			System.out.println("Bar " + i + ": " + Arrays.toString(durs));
		}
		// Print Lilypond
		System.out.println(LilyPondOutput.toLy(cp));
		
		} catch(Exception e) { System.out.println(e); }
	}
	
	/* Initialise the String -> ChordType map */
	static {
		cmap.put("", 		ChordType.Cmaj);
		cmap.put("maj", 	ChordType.Cmaj);
		cmap.put("maj7", 	ChordType.Cmaj7);
		cmap.put("7", 		ChordType.C7);
		cmap.put("dom7", 	ChordType.C7);
		cmap.put("m", 		ChordType.Cm);
		cmap.put("m7", 		ChordType.Cm7);
		cmap.put("m7b5", 	ChordType.Cm7b5);
		cmap.put("dim", 	ChordType.Cdim);
		cmap.put("7dim", 	ChordType.Cdim);
		cmap.put("aug", 	ChordType.Caug);
		cmap.put("+", 		ChordType.Caug);
		cmap.put("7aug", 	ChordType.C7aug);
		cmap.put("7+", 		ChordType.C7aug);
		cmap.put("7s11", 	ChordType.C7s11);
		cmap.put("7s9", 	ChordType.C7s9);
		cmap.put("7+9", 	ChordType.C7s9);
		cmap.put("7b9", 	ChordType.C7b9);
		cmap.put("m7b9", 	ChordType.Cm7b9);
		cmap.put("maj7#11", ChordType.Cmaj7s11);
		cmap.put("maj7+11", ChordType.Cmaj7s11);
        cmap.put("sus",     ChordType.Csus4);
        cmap.put("sus4",    ChordType.Csus4);
        cmap.put("6",       ChordType.C6);
        cmap.put("m6",      ChordType.Cm6);
	}
}