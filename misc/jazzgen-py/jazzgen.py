# JazzGen
# An algorithmic jazz solo generator
# Richard Harris 2008

# Two parameters: BPM, and notes per beat
# E.g. 24, 16, 32
# Can be 'swung' dynamically later on by adjusting timing

import generator, midi_tools, note, chord, re, crossover

def parseChords(chords):
    pchords = []
    cm = re.compile('^([a-g](?:#|b)?)(.*)$', re.IGNORECASE)
    for c in chords:
        # Parse chord
        pitch, typ = cm.match(c).groups()
        if typ == '7': typ = 'dom7'
        typ = chord.chord.getConst(typ)
        pitch = pitch.upper()
        if pitch[-1:] == 'B':
            pitch = (note.parsePitchClass(pitch[:-1]) - 1) % 12
        else:
            pitch = note.parsePitchClass(pitch)
        if typ == None or pitch == None:
            raise Exception("Invalid chord: %s" % c)
        pchords.append((pitch, typ))
    return pchords

gen = generator.Generator()
gen.chords = parseChords([
    'C7', 'C7', 'F7', 'C7',
    'G7', 'F7', 'C7', 'C7'
])
gen.tempo = 130
gen.setMeter(4, 4)
phrase = gen.run()
#midi_tools.writeMidiToFile(phrase, 'first.mid')
player = midi_tools.Player()
player.play(phrase)