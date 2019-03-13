import javax.swing.SwingWorker;
import java.util.List;
import javax.swing.JProgressBar;

public class JazzGenTask extends SwingWorker<Void, Integer> implements GeneticObserver {
    JazzGen jg;
    JProgressBar bar;
    public JazzGenTask(JazzGen jg, JProgressBar bar) {
        super();
        this.jg = jg;
        this.bar = bar;
    }
    public Void doInBackground() {
        bar.setMaximum(jg.prog.length());
        bar.setValue(0);
        jg.setObserver(this);
        jg.generate();
        jg.removeObserver();
        return null;
    }
    
    public void updateProgress(int progress) {
        publish(new Integer(progress));
    }
    
    public void process(List<Integer> progress) {
        for(int i: progress) {
            bar.setValue(i);
        }
    }
    
    public void done() {
        
    }
}