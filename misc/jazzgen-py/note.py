Piano, Bass, Percussion = 1, 2, 9
Notes = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B', 'B#']

def parsePitchClass(alph):
    global Notes
    return Notes.index(alph)

class Note:
    "An extensive representation of a musical note, suitable for use by all possible output drivers."
    def __init__(self, chan, pitch, position, duration, vel=90):
        self.chan, self.pitch = chan, pitch
        self.position, self.duration, self.vel = position, duration, vel
    def pitchClass(self):
        global Notes
        return Notes[self.pitch % 12]
    def octave(self):
        return self.pitch // 12
    def __repr__(self):
        return "<Note %s%d vel=%d pos=%s dur=%s chan=%d>" % (self.pitchClass(), self.octave(), self.vel, self.position, self.duration, self.chan)

class Phrase:
    "Represents a sequence of notes, with timing information."
    def __init__(self, notes, tempo, meter):
        self.notes, self.tempo = notes, tempo
        if meter[1] != 4:
            self.tempo *= (meter[1] / 4.0)
    def __repr__(self):
        return "<Phrase tempo=%d notes=%s>" % (self.tempo, self.notes)