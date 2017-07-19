package info.ragozin.proflab;

public class PhantomHandle implements ResourceFacade {

    private final Resource resource;
    
    public PhantomHandle(Resource resource) {
        this.resource = resource;
    }

    public void dispose() {
        resource.dispose();
    }    
    
    Resource getResource() {
        return resource;
    }
}