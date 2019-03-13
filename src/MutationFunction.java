import java.util.*;

/**
 * Randomly mutates a musical phrase.
 */
public class MutationFunction implements JGConstants {
	Random rand;
    
	// List of available mutations
    static final String[] fnNames = {
		"Sort pitches ascending",
		"Sort pitches descending",
		"Reverse pitches",
		"Reverse phrase",
		"Rotate pitches",
		"Rotate phrase",
		"Transpose",
		"Octave transpose",
		"Restify",
		"Derestify",
		"Random pitch change",
		"Normal dist. pitch change"
	};
	
	// Records which mutations have been disabled by the user
	boolean[] fnEnabled = new boolean[fnNames.length];
	
	public MutationFunction(Random r) {	this.rand = r; }

	/**
	 * Apply a random mutation to the musical phrase.
	 */
	public void mutate(byte[] p) {
		int numChoices = 0, fnIndex;
		for(boolean b: fnEnabled) {
			if(b) numChoices++;
		}
		if(numChoices == 0) return; // no mutations enabled

		do {
			fnIndex = rand.nextInt(fnNames.length);
		} while(!fnEnabled[fnIndex]);
		
		int noteCount;
		byte[] pitches, lengths;
		int irl;
		
		switch(fnIndex) {
			case 0: // Sort pitches ascending
				sort(p, true);
				break;
			case 1: // Sort pitches descending
	 			sort(p, false);
				break;
			case 2: // Reverse pitches
				noteCount = getNoteCount(p);
                if(noteCount > 1) {
                    pitches = new byte[noteCount];
                    lengths = new byte[noteCount];
                    irl = PhraseInfo.rhythmTransform(p, pitches, lengths);
                    reverse(pitches);
                    reassemble(p, pitches, lengths, irl);
                }
				break;
			case 3: // Reverse phrase - including rests
				reverse(p);
				break;
			case 4: // Rotate pitches
				noteCount = getNoteCount(p);
                if(noteCount > 1) {
                    pitches = new byte[noteCount];
                    lengths = new byte[noteCount];
                    irl = PhraseInfo.rhythmTransform(p, pitches, lengths);
                    rotate(pitches, noteCount == 2 ? 1 : (rand.nextInt(pitches.length - 2) + 1));
                    reassemble(p, pitches, lengths, irl);
                }
				break;
			case 5: // Rotate phrase
				rotate(p, rand.nextInt(p.length - 2) + 1);
				break;
			case 6: // Transpose
				transpose(p, -3 + rand.nextInt(7));
				break;
			case 7: // Octave transpose
				transpose(p, 12 * (rand.nextBoolean() ? -1 : 1));
				break;
			case 8: // Restify - randomly replaces notes with rests
				for(int i = 0; i < p.length; i++) {
					if(p[i] != Rest && rand.nextDouble() < 0.2) {
						p[i] = Rest;
					}
				}
				break;
			case 9: // Derestify - randomly replace rests with notes
                noteCount = getNoteCount(p);
                byte[] minMax = noteCount > 0 ? getMinMax(p) : new byte[] { 48, 72 };
				for(int i = 0; i < p.length; i++) {
					if(p[i] == Rest && rand.nextDouble() < 0.2) {
						// Random note is within range of min/max pitch in entire phrase
						p[i] = (byte)(minMax[0] + rand.nextInt((minMax[1] - minMax[0]) + 1));
					}
				}
				break;
			case 10: // Random pitch change
				for(int i = 0; i < p.length; i++) {
					if(p[i] != Rest && rand.nextDouble() < 0.2) {
						// Uniformly shifts pitch up or down 
						p[i] = (byte)Math.min(127, Math.max(0, p[i] + (rand.nextInt(11) - 5)));
					}
				}
                break;
			case 11: // Normal dist. pitch change
				for(int i = 0; i < p.length; i++) {
					if(p[i] != Rest && rand.nextDouble() < 0.2) {
						// Shifts pitch up or down according to normal distribution
						p[i] = (byte)Math.min(127, Math.max(0, p[i] + (int)Math.round(rand.nextGaussian()*2)));
					}
				}
				break;
		}
	}
	
	/**
	 * Tranpose all pitches the given number of semitones.
	 */
	public static void transpose(byte[] p, int shift) {
		for(int i = 0; i < p.length; i++) {
			if(p[i] != Rest) p[i] = (byte)Math.min(127, Math.max(0, p[i] + shift));
		}
	}
	
	/**
	 * Reverse an array.
	 */
	private static void reverse(byte[] p) {
		for(int left = 0, right = p.length - 1; left < right; left++, right--) {
		    byte t = p[left]; p[left] = p[right]; p[right] = t;  // swap
		}
	}
	
	/**
	 * Sort the phrase's notes, keeping rhythm intact.
	 */
	private static void sort(byte[] p, boolean ascending) {
		int noteCount = getNoteCount(p);
        if(noteCount > 1) {
    		byte[] pitches = new byte[noteCount];
    		byte[] lengths = new byte[noteCount];
    		int irl = PhraseInfo.rhythmTransform(p, pitches, lengths);
    		Arrays.sort(pitches);
    		if(!ascending) reverse(pitches);
    		reassemble(p, pitches, lengths, irl);
        }
	}
	
	/**
	 * Rotate an array to the right by a given number of place.
	 * rotleft(x, n) == rotright(x, |x| - n)
	 *   0 < amt < |p|
	 */
	private static void rotate(byte[] p, int amt) {
		byte[] chunk = new byte[amt];
		// Copy last amt bytes to chunk, shift p, copy chunk to head
		System.arraycopy(p, p.length - amt, chunk, 0, amt);
		System.arraycopy(p, 0, p, amt, p.length - amt);
		System.arraycopy(chunk, 0, p, 0, amt);
	}
	
	/**
	 * Reform a phrase from (pitch, length) tuples.
	 */
	private static void reassemble(byte[] p, byte[] pitches, byte[] lengths, int initRestLen) {
		int pos = 0; // position in phrase
		while(initRestLen-- > 0) p[pos++] = Rest;
		for(int i = 0; i < pitches.length; i++) {
			p[pos++] = pitches[i];
			for(int j = 0; j < lengths[i] - 1; j++) p[pos++] = Rest;
		}
	}
	
	/**
	 * Return the number of (non-rest) notes in the phrase.
	 */
	private static int getNoteCount(byte[] p) {
		int noteCount = 0;
		for(byte pitch: p) {
			if(pitch != Rest) noteCount++;
		}
		return noteCount;
	}
	
	/**
	 * Returns the minimum and maximum pitch values in the phrase.
	 */
	private static byte[] getMinMax(byte[] phrase) {
		byte min = 127;
		byte max = 0;
		for(byte p: phrase) {
			if(p != Rest) {
				if(p > max) max = p;
				if(p < min) min = p;
			}
		}
		return new byte[] { min, max };
	}
}