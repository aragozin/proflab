package info.ragozin.proflab;

public class FinalizedResourceFactory {

    public static ResourceFacade newResource() {
        return new FinalizerHandle();
    }    
}