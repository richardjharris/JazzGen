public float evaluate(PhraseInfo p) {
    int score = 0;
    // Only consider last phrase
    for(int i = 0; i < phraseLen; i++) {
        if(p.phrase[i] != Rest && p.getChordAt(i).scaleIndex(p.phrase[i]) >= 0) {
            score++;
        }
    }
    if(p.noteCount == 0) return 0.0f; // no basis for evaluation
    return ((float)score/p.noteCount);
}