package info.ragozin.proflab;

import java.util.concurrent.atomic.AtomicLong;

public class Resource implements ResourceFacade {

    public static AtomicLong GLOBAL_ALLOCATED = new AtomicLong(); 
    public static AtomicLong GLOBAL_RELEASED = new AtomicLong(); 
    
    int[] data = new int[1 << 10];
    protected boolean disposed;
    
    public Resource() {
        GLOBAL_ALLOCATED.incrementAndGet();
    }
    
    public synchronized void dispose() {
        if (!disposed) {
            disposed = true;
            releaseResources();
        }
    }

    protected void releaseResources() {
        GLOBAL_RELEASED.incrementAndGet();
    }    
}
