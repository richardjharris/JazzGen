import jm.util.*;
import jm.music.data.*;
import jm.music.tools.*;
import jm.midi.MidiSynth;
import java.util.*;
import java.util.regex.*;
import java.io.*;

/**
 * It's jazz, baby.
 * @author Richard Harris
 */
public final class JazzGen implements JGConstants {
    Score score;
    Part pPiano, pBass, pDrums;
    Phrase phSolo, phBass, phBD, phSD, phHH;
    CPhrase phChords;
    ChordProgression prog;
    Random rand = new Random();
    GeneticGenerator gen;
    GeneticObserver obs;
    double tempo = 105.0;
    
    /**
     * Sets up the JMusic arrangement.
     */
    public JazzGen() {
        gen = new GeneticGenerator(this);
    }
    
    public void setObserver(GeneticObserver obs) {
        this.obs = obs; this.gen.obs = obs;
    }
    public void removeObserver() {
        this.obs = null; this.gen.obs = null;
    }
    
    /**
     * Generates a new phrase.
     */
    public void generate() {
        score = new Score("JazzGen", tempo);
        pPiano = new Part("Piano", 0, 0);
        phSolo = new Phrase(0.0); // first beat
        try {
			gen.run(phSolo);        
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
        postPass(phSolo);
        pPiano.addPhrase(phSolo);
        phChords = new CPhrase(0.0);
        genChords();
        pPiano.addCPhrase(phChords);
        score.addPart(pPiano);
        
        pBass = new Part("Bass", ACOUSTIC_BASS, 1);
        phBass = new Phrase(0.0);
        genBass();
        pBass.addPhrase(phBass);
        score.addPart(pBass);
        
        pDrums = new Part("Drums", 0, 9); // force drum channel (10)
        phBD = new Phrase(0.0);
        phSD = new Phrase(0.0);
        phHH = new Phrase(0.0);
        genDrumLoop();
        score.addPart(pDrums);
    }
    
    /**
     * Play score via MIDI.
     */
    public void play() {
        Play.stopMidi();
        Play.midi(score, false);
    }
    
    /**
     * Write MIDI to a supplied output stream.
     */
    public void writeMidi(OutputStream out) {
        Write.midi(score, out);
    }
    
    /**
     * Write LilyPond engraving to the supplied file;
     */
    public void writeLilyPond(File out) throws IOException {
        LilyPondOutput.convert(score, prog, out);
    }
    
    /**
     * Simple wandering over the chord progression, always playing
     * the chord root note.
     */
    public void genBass() {
        // Blithely walk up and down the current chord
        for(int i = 0; i < prog.length(); i++) {
			int[] durs = prog.getDurations(i);
			int j = 0;
			for(Chord c: prog.get(i)) {
				int dur = durs[j]*8 / phraseLen;  // number of 8th notes to play
				phBass.addNote(new Note(c.rootNote + 36, QN, MF));  // root
				int[] chord = c.notes(3);
				int curPitch = 0;
				int dir = rand.nextBoolean() ? 1 : -1;
				for(int k = 0; k < dur - 1; k++) {
					curPitch += dir;
					if(curPitch >= chord.length) {
						curPitch = chord.length - 1;
						dir = -dir;
					}
					else if(curPitch < 0) {
						curPitch = 0;
						dir = -dir;
					}
					else if(rand.nextInt(2) == 0) {
						dir = -dir;
					}
					phBass.addNote(new Note(chord[curPitch], QN, MF));
				}
				j++;
			}
        }
        // Add unison at end
        Chord last = prog.getLast();
        phBass.addNote(new Note(last.rootNote + 3*12, QN, F));
    }
    
    /**
     * Plays seventh chords.
     */
    public void genChords() {
        for(int i = 0; i < prog.length(); i++) {
			int[] durs = prog.getDurations(i);
			int j = 0;
			for(Chord c: prog.get(i)) {
				int[] chord = c.notes(4);
				if(durs[j] == phraseLen) {
					// cover two bars
					phChords.addChord(chord, WN, MF);
					phChords.addChord(chord, WN, MF);
				}
				else {
					phChords.addChord(chord, durs[j]*2*WN/phraseLen, MF); // e.g. 16 maps to WN, 8 maps to HN
				}
				j++;
			}
		}
        // Add a unison chord at the end
        Chord last = prog.getLast();
        last.setInversion(0);
        phChords.addChord(last.unison(new int[]{4, 6}), WN, MF);
    }
    
    /**
     * Generates a simple 4/4 drum loop.
     */
    public void genDrumLoop() {      
		for (int i = 0; i < 4; i++) {
            phBD.addNote(new Note(BASS_DRUM_1, 0.666667, FF));
            phBD.addNote(REST, 1.333333);
        
	 		phSD.addNote(REST, 1.333333);
	 		phSD.addNote(new Note(ACOUSTIC_SNARE, 0.666667, (int)(rand.nextInt(60))));
	 	}
        
        // 'Cooking' ride pattern
        phHH.addNote(51, C);
        phHH.addNote(51, 0.67);
        phHH.addNote(51, 0.33);
        phHH.addNote(51, C);
        phHH.addNote(51, C);
        
        Mod.repeat(phBD, prog.length());
        Mod.repeat(phSD, prog.length());
        Mod.repeat(phHH, prog.length() * 2);
        
        pDrums.addPhrase(phBD);
        pDrums.addPhrase(phSD);
        pDrums.addPhrase(phHH);
    }
    
    /**
     * Apply post-pass stage to melody line.
     */
    public void postPass(Phrase p) {
        Mod.tieRests(p);
        Mod.fillRests(p);
        //Mod.tiePitches(p);
        
        // Apply dynamics + small random offset
        randomWalkVel(p);
        Mod.accents(p, 4.0); // 4 crotches per bar
    }
    
    // Apply velocity to phrase via random walk
    public void randomWalkVel(Phrase p) {
        int vel = -1;
        int lastPitch = -1;
        int dir = 1;
        for(Note n: p.getNoteArray()) {
            if(n.getPitch() != -1) {
                double dur = n.getDuration();
                if(dur >= 2.0 || vel == -1 || lastPitch == -1 || Math.abs(lastPitch - n.getPitch()) > 12) {
                    // Reseed velocity (new phrase fragment)
                    vel = Math.max(0, Math.min(127, (int)Math.round(rand.nextGaussian()*20.0 + 90.0)));
                    dir = rand.nextBoolean() ? 1 : -1;
                }
                else {
                    // Shift velocity (random walk) -- long dur -> larger change
                    vel += dir * (int)Math.round(Math.abs(rand.nextGaussian()*dur*15.0));
                    // If vel reaches edge, "bounce" it; randomly change walk direction
                    if(vel > 120) {
                        vel = 120 - (vel - 120);
                        dir = -1;
                    }
                    else if(vel < 80) {
                        vel = 80 + (80 - vel);
                        dir = 1;
                    }
                    else if(rand.nextDouble() < dur*0.3) { // longer dur -> more likely to change
                        dir = -dir;
                    }
                }
                n.setDynamic(vel);
                n.setOffset(n.getOffset() - 0.001 + (Math.random()*0.002));
                lastPitch = n.getPitch();
            }
        }
    }
}