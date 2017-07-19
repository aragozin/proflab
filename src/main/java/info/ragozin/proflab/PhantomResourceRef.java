package info.ragozin.proflab;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

public class PhantomResourceRef extends PhantomReference<PhantomHandle> {

    private Resource resource;

    public PhantomResourceRef(PhantomHandle referent, ReferenceQueue<? super PhantomHandle> q) {
        super(referent, q);
        this.resource = referent.getResource();
    }

    public void dispose() {
        Resource r = resource;
        if (r != null) {
            r.dispose();
        }        
    }    
}