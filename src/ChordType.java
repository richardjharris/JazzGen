/**
 * A collection of chord types: offsets from a root note plus
 * the recommended scale to play over it.
 * The string representation is also stored for parsing purposes.
 */
public enum ChordType {
	Cmaj(new byte[] {0, 4, 7}, Scale.Major),
	Cmaj7(new byte[] {0, 4, 7, 11}, Scale.Major),
	C7(new byte[] {0, 4, 7, 10}, Scale.Mixolydian),
	Cm7(new byte[] {0, 3, 7, 10}, Scale.Minor),
    Cm(new byte[] {0, 3, 7}, Scale.Minor),
	Cm7b5(new byte[] {0, 3, 6, 10}, Scale.Locrian),
	Cdim(new byte[] {0, 3, 6, 9}, Scale.WholeToneDim),
	Caug(new byte[] {0, 4, 8}, Scale.LydianAug),
	C7aug(new byte[] {0, 4, 8, 10}, Scale.WholeTone),
	C7s11(new byte[] {0, 4, 6, 7, 10}, Scale.LydianDom),
	C7s9(new byte[] {0, 3, 4, 10}, Scale.Altered), // or HalftoneWhole, Blues
	C7b9(new byte[] {0, 4, 7, 10, 13}, Scale.HalftoneWholeDim),
	Cm7b9(new byte[] {0, 3, 7, 10, 13}, Scale.Phrygian),
	Cmaj7s11(new byte[] {0, 4, 6, 7, 11}, Scale.Lydian),
    Csus4(new byte[] {0, 4, 6, 11}, Scale.Major),
    C6(new byte[] {0, 4, 7, 9}, Scale.Lydian),
    Cm6(new byte[] {0, 3, 7, 9}, Scale.Dorian);
	
	public final byte[] offsets;
	public final Scale scale;
	
	ChordType(byte[] offsets, Scale scale) {
		this.offsets = offsets;
		this.scale = scale;
	}
}