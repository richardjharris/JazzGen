/**
 * Lists and creates fitness modules. Modules are referred to by a numerical
 * index from 0 to moduleCount - 1.
 */
public class FitnessModuleFactory {
    final static int moduleCount = 13;
    
    /**
     * Return a module by index, initialised with the supplied Config object.
     */
    public static FitnessModule getModule(int index, Config c) {
        switch(index) {
            case 0: return new FmKey(c);
            case 1: return new FmExpectancy(c);
			case 2: return new FmRhythm(c);
			case 3: return new FmRange(c);
			case 4: return new FmRepetition(c);
			case 5: return new FmPitch(c);
            case 6: return new FmRhythmRep(c);
            case 7: return new FmScales(c);
            case 8: return new FmExpectancy2(c);
            case 9: return new FmInfluenceTest(c);
            case 10: return new FmChord(c);
            case 11: return new FmNumNotes(c);
            case 12: return new FmRepetition2(c);
            default: return new FmKey(c); // invalid
        }
    }
    
    /**
     * Return an array of all modules.
     */
    public static FitnessModule[] getAllModules(Config c) {
        FitnessModule[] mods = new FitnessModule[moduleCount];
        for(int i = 0; i < moduleCount; i++) {
            mods[i] = getModule(i, c);
        }
        return mods;
    }
}