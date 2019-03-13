/**
 * Represents a genetic candidate initialisation method. These methods
 * generate seeds for the genetic algorithm to refine.
 */
public abstract class InitMethod extends GeneticMethod implements JGConstants {
    int curLength;
    GeneticGenerator gen;
    /* Announce that we're generating a new phrase. */
    public void newPhrase(GeneticGenerator gen, int length) {
        this.gen = gen;
        this.curLength = length;
    }
    /* Generate a candidate phrase. */
    public abstract byte[] generate();
	
	/* Returns the chord for this phrase at position p. */
	protected Chord getChord(int p) { return gen.prog.getChordAt(gen.progIndex, p); }
	/* Returns the scale offsets for this phrase at position p. */
	protected byte[] getScale(int p) {
        return gen.prog.getChordAt(gen.progIndex, p).getScale();
    }
}

