public float evaluate(PhraseInfo p) {
    byte[] pitch = p.pitches;
    float score = 0;
    
    if(pitch.length < 3) return 0.5f; // not enough notes to work with
            
    // Operate over groups of three items
    for(int i = 2; i < pitch.length; i++) {
        int implInt = pitch[i - 2] - pitch[i - 1]; // implicative interval
        int realInt = pitch[i] - pitch[i - 1]; // realised interval
        int implSgn = (int)Math.signum(implInt);
        int realSgn = (int)Math.signum(realInt);
        implInt = Math.abs(implInt);
        realInt = Math.abs(realInt);
        
        // Registral direction
        // If the impl. int is large, assign 1 if different dir, -1 if same dir
        if(implInt > 6) {
            score += regDirCE * ((realSgn != implSgn) ? 2 : 0);
        }
        else score += regDirCE;   
        
        // Registral return
        // 1. Realised interval must be in different direction
        // 2. Real. interval must be within 2 semitones of implicative interval
        if(realSgn != implSgn && realInt != 0 && implInt != 0) {
            if(Math.abs(implInt - realInt) <= 1) score += regRetCE;
        }
        
        // Proximity
        if(realInt > 0 && realInt < 12)
            score += proxCE * (12 - realInt);
        else if(realInt == 0) {
            score += proxCE * 5;
        }
    }

    // Max possible score
    float maxScore = (regDirCE * 2 + proxCE * 12 + regRetCE) * (p.noteCount - 2);
        
    // Normalisation
    // Return a fitness value based on proximity of exp. to user-defined target value
    float tExp = targetExp / 100.0f;
    return (float)(1.0 - Math.pow(Math.abs((score/maxScore) - tExp)/tExp, punishFactor));
}