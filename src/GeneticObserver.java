/**
 * Observes the JazzGen process and handles state updates. Used
 * primarily for UI updates while the music is generated.
 */
public interface GeneticObserver {
    public void updateProgress(int progress);
}