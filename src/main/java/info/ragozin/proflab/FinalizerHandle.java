package info.ragozin.proflab;

public class FinalizerHandle extends Resource {

    protected void finalize() {
        dispose();
    }
}