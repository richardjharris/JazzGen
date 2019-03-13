import jm.JMC;

/**
 * Global constants.
 */
interface JGConstants extends JMC {
	// Note resolution of a single 2-bar block
	final static int phraseLen = 32;

	final static byte Rest = -1;
	final static int NoFitness = -2147483647;
	final static String[] pitchName = new String[] {
		"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
	};
    final static String[] pitchNameFull = {
        "c", "c#", "db", "d", "d#", "eb", "e",
        "f", "f#", "gb", "g", "g#", "ab", "a",
        "a#", "bb", "b"
    };
    final static byte[] pitchNameIndexes = {
		0, 1, 1, 2, 3, 3, 4, 5, 6, 6, 7, 8, 8, 9, 10, 10, 11
    };
}