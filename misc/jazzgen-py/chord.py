import utils

# A list of chords, represented as intervals from the base note, in semitones.
chord = utils.Enumerate()
chords = {
    chord.maj:      (0, 4, 7),
    chord.m:        (0, 3, 7),
    chord.dom7:     (0, 4, 7, 10),
    chord.maj7:     (0, 4, 7, 11),
    chord.m6:       (0, 3, 7, 9),
    chord.m7:       (0, 3, 7, 10),
    chord.m9:       (0, 3, 7, 10, 14),
    chord.m11:      (0, 3, 7, 10, 14, 17),
    chord.mM7:      (0, 3, 7, 11),
    chord.m7b6:     (0, 4, 7, 8, 10),
    chord.m7b9:     (0, 4, 7, 10, 13),
    chord.sus4:     (0, 5, 7),
    chord.sus2:     (0, 2, 7),
    chord.M6:       (0, 4, 7, 9),
    chord.M9:       (0, 4, 7, 10, 14),
    chord.M11:      (0, 4, 7, 10, 14, 17),
    chord.M13:      (0, 4, 7, 9, 10, 14),
    chord.maj9:     (0, 2, 4, 7, 11),
    chord.aug7:     (0, 4, 8, 10),
    chord.dom7s11:  (0, 4, 6, 7, 10),
    chord.dom7sus:  (0, 5, 7, 10),
}
chord.lock()

def getChord(startNote=0, chordType=chord.maj):
    try:
        return [startNote + x for x in chords[chordType]]
    except KeyError:
        raise Exception("No such chord: %d" % chordType)
        