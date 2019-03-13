for(int i = 0; i < numNotes; i ++) {
    // Notes should be in key
    byte scaleIndex = inCurScale(pitch[i]);
    if(scaleIndex < 0) fit -= 30; // not in scale
    
    if(pitch[i] == Rest) {
        fit -= 10; // Punish rests as most penalties don't apply to them
    }
    else if(i != 0) {
        // Adjacent pitches should be reasonably close (<= 4 semitones)
        // The octave interval is punished slightly less.
        int sqrDiff = (int)Math.pow((double)(pitch[i] - pitch[i-1]), 2);
        if(sqrDiff == 144) fit -= 5;
        else if(sqrDiff > 16) fit -= Math.floor(sqrDiff/16.0);
        
        // Similar note length: encourage similar note lengths and gradients
        // Also strongly encourage 8ths (to avoid 16th runs overwhelming)
        sqrDiff = (int)Math.pow((double)(length[i] - length[i-1]), 2);
        if(sqrDiff == 4) fit += 30;
        else fit -= 10 + Math.floor(sqrDiff/4.0);
        
        // Encourage note lengths that are powers of two
        if(isPowerOfTwo(length[i])) fit += 15;
        
        // Encourage notes on certain beat positions
        if(i % 16 == 0) fit += 10;
        if(i % 8 == 0) fit += 5;
        if(i % 4 == 0) fit += 2;
        
        // Punish notes outside a reasonable range
        if(pitch[i] < C4) fit -= (int)Math.floor((C4 - pitch[i])/5);
        if(pitch[i] >= C7) fit += (int)Math.floor((pitch[i] - C7)/3);
    }
    else {
        if(scaleIndex == 0) fit += 40; // root on first note
    }
}

// Slightly discourage syncopation
if(p[p.length - 2] != Rest && p[p.length - 1] == Rest) {
    fit -= 40;
}

// Encourage repetition of two note pairs
for(int i = 0; i < pitch.length - 2; i++) {
    for(int j = i+2; j < pitch.length - 1; j++) {
        if(pitch[i] == pitch[j] &&
            pitch[i + 1] == pitch[j + 1]
            && pitch[i] != pitch[i + 1]) {
            fit += 3;
        }
    }
}

// Encourage repetition of three note pairs
for(int i = 0; i < pitch.length - 3; i++) {
    for(int j = i+3; j < pitch.length - 2; j++) {
        if(pitch[i] == pitch[j] &&
            pitch[i + 1] == pitch[j + 1] &&
            pitch[i + 2] == pitch[j + 2] &&
            (pitch[i] != pitch[i + 1] || pitch[i + 1] != pitch[i + 2])) {
            fit += 10;
        }
    }
}

// Add a linear bonus per note, to compensate for the average
// negative value of a note (which encourages long rests)
fit += numNotes * 5;        