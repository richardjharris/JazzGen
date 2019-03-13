import java.util.Arrays;

/**
 * A collection of scales: interval offsets from a root note.
 */
public enum Scale {
	Major(new byte[] {0, 2, 4, 7, 9, 11}),
	Blues(new byte[] {0, 3, 5, 6, 7, 10}),
	Mixolydian(new byte[] {0, 2, 4, 7, 9, 10}),
	Minor(new byte[] {0, 2, 3, 5, 7, 10}),
	Locrian(new byte[] {0, 3, 5, 6, 8, 10}),
	WholeTone(new byte[] {0, 2, 4, 6, 8, 10}),
	WholeToneDim(new byte[] {0, 2, 3, 5, 6, 8, 9, 11}),
	HalftoneWholeDim(new byte[] {0, 1, 3, 4, 6, 7, 9, 10}),
	LydianAug(new byte[] {0, 2, 4, 6, 8, 9, 11}),
	LydianDom(new byte[] {0, 2, 4, 6, 7, 9, 10}),
	Altered(new byte[] {0, 1, 3, 4, 6, 8, 10}),
	Phrygian(new byte[] {0, 1, 3, 5, 7, 9, 10}),
	Lydian(new byte[] {0, 2, 4, 6, 7, 9, 11}),
    Dorian(new byte[] {0, 2, 3, 5, 7, 9, 10});
	
	public final byte[] offsets;
	
	Scale(byte[] offsets) {
		this.offsets = offsets;
	}
	
	/**
	 * Return this scale for a specified root note (0-11)
	 */
	public byte[] notes(byte root) {
		byte[] newScale = new byte[offsets.length];
		for(int k = 0; k < offsets.length; k++) {
			newScale[k] = (byte)((offsets[k] + root) % 12);
		}
		Arrays.sort(newScale);  // can now binary search
		return newScale;
	}
}