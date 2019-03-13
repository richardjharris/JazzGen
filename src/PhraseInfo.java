import java.util.Arrays;

/**
 * Pre-computes various information about a musical block (phrase)
 * so that it can be used by the fitness modules.
 *
 * Contains useful utility methods for analysing musical phrases
 * that are likely to be used by more than one fitness method.
 */
public class PhraseInfo implements JGConstants {
    byte[] phrase;
    byte[] pitches;
    byte[] lengths;
    int noteCount;
	int phraseCount;
	int initRestLen;
    GeneticGenerator gen;

    public PhraseInfo(byte[] phrase, GeneticGenerator gen, int numPhrases) {
        this.phrase = phrase;
        this.gen = gen;
		this.phraseCount = numPhrases;
        countNotes();
		pitches = new byte[noteCount];
        lengths = new byte[noteCount];
        if(noteCount > 0) {
            initRestLen = rhythmTransform(phrase, pitches, lengths);
        }
        else {
            initRestLen = phraseLen;
        }
    }
	
	/**
	 * Returns the Chord for this phrase, at position pos.
	 */
	public Chord getChordAt(int pos) {
		int phraseIndex = gen.progIndex - phraseCount + (pos / phraseLen) + 1;
		return gen.prog.getChordAt(phraseIndex, pos % phraseLen);
	}

    /**
     * Returns true if number if a power of two.
     */
    public static boolean isPowerOfTwo(byte num) {
        byte s = 1;
        while(num > s) s <<= 1;
        return (num ^ s) == 0 ? true : false;
    }   

    /**
     * Collects the total number of notes in the phrase.
     */
    private void countNotes() {
        noteCount = 0;
        for(byte pitch: phrase) if(pitch != Rest) noteCount++;
    }

    /** 
     * Transform phrase into (note, length) tuples.
     */
    public static int rhythmTransform(byte[] phrase, byte[] pitches, byte[] lengths) {
        byte lastPitch = Rest;
        byte pCount = 0;
		int initRestLen = 0;
        int i = 0;
		
		// Find initial rest
		while(initRestLen < phraseLen && phrase[initRestLen] == Rest) initRestLen++;
		
        for(int p = initRestLen; p < phrase.length; p++) {
            if(phrase[p] == Rest) {
                pCount++;
            }
            else {
                if(pCount > 0) {
                    pitches[i] = lastPitch;
                    lengths[i] = pCount;
                    i++;
                }
                lastPitch = phrase[p];
                pCount = 1;
            }
        }
        if(pCount > 0) {
            pitches[i] = lastPitch;
            lengths[i] = pCount;
        }
		
		return initRestLen;
    }
    
    public String toString() {
        String buf = "[";
        for(int i = 0; i < noteCount; i++) {
            buf += pitchName[pitches[i] % 12] + lengths[i];
            if(i != noteCount - 1) buf += " ";
        }
        return buf + "]";
    }
}