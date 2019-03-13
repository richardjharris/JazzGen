import utils
from chord import chord

# A list of scales and their intervals from the base note, in semitones.       
scale = utils.Enumerate()
scales = {
    scale.Major:             (0, 2, 4, 5, 7, 9, 11), # == Ionian
    scale.BebopMajor:        (0, 2, 4, 5, 7, 9, 10, 11),
    scale.Dorian:            (0, 2, 3, 5, 7, 9, 10),
    scale.BebopDorian:       (0, 2, 3, 5, 6, 7, 9, 10),
    scale.Phrygian:          (0, 1, 3, 5, 7, 8, 10),
    scale.PhrygianSharp6:    (0, 1, 3, 5, 7, 9, 10),
    scale.Lydian:            (0, 2, 4, 6, 7, 9, 11),
    scale.LydianDominant:    (0, 2, 4, 6, 7, 9, 10),
    scale.Mixolydian:        (0, 2, 4, 5, 7, 9, 10),
    scale.BebopDominant:     (0, 2, 4, 5, 7, 9, 10, 11), # desc.: start on (down)beat/chord tone
    scale.Minor:             (0, 2, 3, 5, 7, 8, 10), # == Aeolian
    scale.HarmonicMinor:     (0, 2, 3, 5, 7, 8, 11),
    scale.MelodicMinor:      (0, 2, 3, 5, 7, 9, 11),
    scale.BebopMinor:        (0, 2, 3, 5, 7, 9, 10, 11),
    scale.Locrian:           (0, 1, 3, 5, 6, 8, 10), # diminished, not really used
    scale.Blues:             (0, 3, 5, 6, 7, 10),
    scale.PentatonicMajor:   (0, 2, 4, 7, 9),
    scale.PentatonicMinor:   (0, 3, 5, 7, 10),
    scale.InSen:             (0, 1, 5, 7, 10), # root is third note!!
    scale.InSenAlt:          (5, 7, 10, 12, 13),
    scale.WholeTone:         (0, 2, 4, 6, 8, 10),
    scale.Hirajoshi:         (0, 1, 5, 7, 8),
    scale.Chinese:           (0, 2, 4, 7, 9),
    scale.Oriental:          (0, 1, 4, 5, 6, 9, 10),
    scale.Spanish:           (0, 1, 4, 5, 7, 8, 10),
    scale.Diminished:        (0, 2, 3, 5, 6, 8, 9, 11),
    scale.Octatonic:         (0, 1, 3, 4, 6, 7, 9, 10),
    scale.Augmented:         (0, 3, 4, 7, 8, 11),
    scale.Chromatic:         (0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
}
scale.lock()

# A mapping of one or more scales to one or more chords. Chords represented as (chordName, num) have a base
# note offset _num_ semitones from that of the chord.
scaleFromChord = {
    (chord.maj, chord.maj9, chord.M6, chord.maj7):
        (scale.Major, scale.Lydian, scale.BebopMajor, scale.PentatonicMajor, (scale.PentatonicMajor, 7), (scale.HarmonicMinor, 1)), # last is Ab altered over G: suspect!
    (chord.m7, chord.m9, chord.m11, chord.m, chord.m6):
        (scale.Dorian, scale.BebopMinor, scale.PentatonicMinor, (scale.PentatonicMajor, 5), (scale.PentatonicMajor, 10), (scale.BebopMajor, 3), scale.Blues, scale.Minor),
    (chord.m6, chord.m):
        (scale.MelodicMinor, (scale.InSen, 2)),
    (chord.mM7,):
        (scale.MelodicMinor, scale.HarmonicMinor, (scale.BebopMajor, 3)),
    (chord.m7b6,):
        (scale.Minor, (scale.PentatonicMajor, 8)),
    (chord.m7b9,):
        (scale.Phrygian, scale.PhrygianSharp6),
    (chord.dom7, chord.M9, chord.M13, chord.maj):
        (scale.Mixolydian, scale.LydianDominant, scale.BebopDominant, scale.Blues, scale.PentatonicMajor),
    (chord.dom7sus, chord.sus2, chord.sus4, chord.M11):
        (scale.Mixolydian,),
    # Bb/C, Gm7/C           C suspended pentatonic, F major pentatonic
    (chord.dom7, chord.dom7s11):
        (scale.LydianDominant,),
    # C7alt, C7#9#5, C7#9   C altered, F harmonic minor, F melodic minor
    # C7b9b5, C7b9          C HW diminished, F harmonic minor, F melodic minor
    (chord.aug7,):
        (scale.WholeTone,),
    # Cm7b5                 C locrian #2, C locrian
    # Cdim7                 C WH diminished
    # Cphryg                C phrygian, C phrygian #6, C Spanish phrygian, C in sen
    # Cmaj7#5               C lydian augmented, C major bebop
    # C7susb9               C phrygian #6, C phrygian
}

# Transform list into more efficient one-to-many mapping
mapping = {}
for chds, scls in scaleFromChord.iteritems():
    for chd in chds:
        try:             mapping[chd].extend(scls)
        except KeyError: mapping[chd] = list(scls)
#scaleFromChord = dict([(c, set(s)) for c, s in mapping.iteritems()])  # remove duplicates
scaleFromChord = mapping

def getScale(startNote=0, scaleType=scale.Major):
    "Return a list of note numbers for the given scale and start note."
    try:
        return [startNote + x for x in scales[scaleType]]
    except:
        raise Exception("No such scale: %i" % scaleType)
        
def getScaleFromChord(startNote=0, chordType=chord.maj):
    "Return a list of possible scales for a given chord."
    opts = {}
    for opt in scaleFromChord[chordType]:
        try:
            scl = [startNote + x for x in scales[opt]]
        except KeyError:
            scl = [startNote + x + opt[1] for x in scales[opt[0]]] # (scale, offset) form
        opts[opt] = scl
    return opts
    