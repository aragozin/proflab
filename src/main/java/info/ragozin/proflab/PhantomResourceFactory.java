package info.ragozin.proflab;

import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PhantomResourceFactory {

    private static Set<Resource> GLOBAL_RESOURCES = Collections.synchronizedSet(new HashSet<Resource>());
    private static ResourceDisposalQueue REF_QUEUE = new ResourceDisposalQueue();
    @SuppressWarnings("unused")
    private static ResourceDisposalThread REF_THREAD = new ResourceDisposalThread(REF_QUEUE);
    
    public static ResourceFacade newResource() {
        ReferedResource resource = new ReferedResource();
        GLOBAL_RESOURCES.add(resource);
        PhantomHandle handle = new PhantomHandle(resource);
        PhantomResourceRef ref = new PhantomResourceRef(handle, REF_QUEUE);
        resource.setPhantomReference(ref);
        return handle;
    }
    
    private static class ReferedResource extends Resource {
        
        @SuppressWarnings("unused")
        private PhantomResourceRef handle;
        
        void setPhantomReference(PhantomResourceRef ref) {
            this.handle = ref;
        }

        @Override
        public synchronized void dispose() {
            handle.clear();
        	handle = null;
            super.dispose();
            GLOBAL_RESOURCES.remove(this);
        }
    }
    
    private static class ResourceDisposalQueue extends ReferenceQueue<PhantomHandle> {
        
    }
    
    private static class ResourceDisposalThread extends Thread {

        private ResourceDisposalQueue queue;
        
        public ResourceDisposalThread(ResourceDisposalQueue queue) {
            this.queue = queue;
            setDaemon(true);
            setName("ReferenceDisposalThread");
            start();
        }

        @Override
        public void run() {
            while(true) {
                try {
                    PhantomResourceRef ref = (PhantomResourceRef) queue.remove();
                    ref.dispose();
                    ref.clear();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }
}