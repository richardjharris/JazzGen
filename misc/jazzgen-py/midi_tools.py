import midi, player, time, note

tick = 0

class Player:
    "Plays supplied note data in real-time via MIDI synth."
    def __init__(self):
        self.mr = player.Player()
        self.mr.setInstrument(0, 1) # Acoustic piano
        self.mr.setInstrument(9, 2) # Acoustic bass
    def play(self, phrase):
        pos = 0
        unit = 15.0/phrase.tempo
        open = {} # notes that need to be turned off in the future
        print phrase
        for note in phrase.notes:
            print "NOTE: %s" % note
            print "Note position: %d, Currently: %d" % (note.position, pos)
            while pos < note.position: # sleep until next note
                print "Pos = %d" % pos
                # Turn off any open notes
                if pos in open:
                    for onote in open.pop(pos):
                        print "CLOSING note: %s" % onote
                        self.mr.outShortMsg(0x80+onote.chan, onote.pitch, onote.vel)
                time.sleep(unit); pos += 1
            
            # Play note
            print "Playing note: %s" % note
            self.mr.outShortMsg(0x90+note.chan, note.pitch, note.vel)
            print "Adding OFF entry at %d" % (pos + note.duration)
            try:    open[pos + note.duration].append(note)
            except: open[pos + note.duration] = [note]
            print ''
        # Close remaining notes
        last_pos = max(open.keys())
        while pos <= last_pos:
            if pos in open:
                for note in open.pop(pos):
                    print "CLOSING note: %s" % note
                    self.mr.outShortMsg(0x80+note.chan, note.pitch, note.vel)
            time.sleep(unit); pos += 1    
        time.sleep(unit*4)
        
def writeMidiToFile(phrase, filename):
    "Writes the supplied note data to file."
    stream = midi.new_stream(resolution=120, tempo=phrase.tempo)
    
    def writeNote(note):
        global tick
        on = midi.NoteOnEvent()
        on.channel, on.pitch = note.chan, note.pitch
        on.velocity, on.tick = note.vel, int(tick)
        stream.add_event(on)
        tick += note.duration*500
        off = midi.NoteOffEvent()
        off.channel, on.pitch = note.chan, note.pitch
        off.velocity = 0
        off.tick = int(tick)
        stream.add_event(off)

    map(writeNote, phrase.notes)
    
    # Add soundless note so we can hear the sustain of the last note played
    writeNote(note.Note(1, 1, 2, 0, 0))

    return midi.write_midifile(stream, filename)
    