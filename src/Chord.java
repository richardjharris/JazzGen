import jm.util.*;
import jm.music.data.*;
import java.util.Arrays;
import jm.music.tools.*;
import jm.midi.MidiSynth;

/**
 * Represents a chord (pair of chord type - dom7, m, aug, etc. and root note)
 * The inversion is also stored and can be changed at any time.
 */
public class Chord implements JGConstants {
	ChordType ct;
	byte rootNote; /* 0 to 11 */
	int inversion;
	
	public Chord(byte rootNote, ChordType type, int inversion) {
		assert rootNote >= 0 && rootNote < 12;
		this.rootNote = rootNote;
		this.ct = type;
		this.inversion = inversion;
	}
	
	public Chord(byte rootNote, ChordType type) {
		this(rootNote, type, 0);
	}
	
	public Chord clone() {
		Chord copy = new Chord(rootNote, ct, inversion);
		return copy;
	}
	
	/* Returns the number of notes in this chord. */
	public void setInversion(int inversion) { this.inversion = inversion; }
	
	/**
	 * Returns the chord played with the current inversion, in the
	 * specified octave (typically 3 for bass, 4 for piano)
	 */
	public int[] notes(int baseOctave) {
		int[] chord = new int[ct.offsets.length];
		for(int i = 0; i < ct.offsets.length; i++) {
			chord[i] = ((ct.offsets[i] + rootNote) % 12) + 12*baseOctave;
		}
		if(inversion > 0) {
			// Add 12 semitones (an octave) to the first _inversion_
			// notes of the chord
			for(int i = 0; i < inversion; i++)
				chord[i % chord.length] += 12;
		}
		else if(inversion < 0) {
			// Substract 12 semitones from the last _inversion_ notes
			for(int i = 0; i < -inversion; i++)
				chord[chord.length - (i % chord.length) - 1] -= 12;
		}
		return chord;
	}
	
	/**
	 * Return the index of the given note in this chord's preferred
	 * scale, or an arbitrary negative number if not present in the scale.
	 */
	public int scaleIndex(byte pitch) {
		return Arrays.binarySearch(getScale(), (byte)(pitch % 12));
	}
	
	/**
	 * Return the appropriate scale for this chord.
	 */
	public byte[] getScale() {
		return ct.scale.notes(rootNote);
	}
    
    /**
     * Checks to see if supplied pitch is in the chord.
     */
    public boolean hasNote(byte pitch) {
        int aPitch = pitch % 12;
        for(int i = 0; i < ct.offsets.length; i++) {
            if((ct.offsets[i] + rootNote) % 12 == aPitch)
                return true;
        }
        return false;
    }
    
    /**
     * Return this chord in root position, played over several octaves.
     */
    public int[] unison(int[] octaves) {
        int chordLen = ct.offsets.length;
        int[] uNotes = new int[octaves.length * chordLen];
        for(int i = 0; i < octaves.length; i++) {
            System.arraycopy(this.notes(octaves[i]), 0, uNotes, i*chordLen, chordLen);
        }
        return uNotes;
    }
	
	public String toString() {
		return pitchName[rootNote] + ct.toString().substring(1);
	}
}