package info.ragozin.proflab;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.junit.Test;

public class ReferenceUsageCheck {

    public int queueLimit = 1000;
    public Deque<ResourceFacade> queue = new ArrayDeque<ResourceFacade>();
    
    @Test
    public void leaking_references_finalizer() {
        while(true) {
            gcFiller();
            reportStats();
            ResourceFacade rf = FinalizedResourceFactory.newResource();
            queue.addLast(rf);
            if (queue.size() > queueLimit) {
                queue.removeFirst();
            }
        }        
    }

    @Test
    public void leaking_references_phantom() {
        while(true) {
            gcFiller();
            reportStats();
            ResourceFacade rf = PhantomResourceFactory.newResource();
            queue.addLast(rf);
            if (queue.size() > queueLimit) {
                queue.removeFirst();
            }
        }        
    }

    @Test
    public void low_leaking_references_finalizer() {
        int n = 0;
        while(true) {
            gcFiller();
            reportStats();
            ResourceFacade rf = FinalizedResourceFactory.newResource();
            queue.addLast(rf);
            if (queue.size() > queueLimit) {
                rf = queue.removeFirst();
                if ((++n) % 100 != 0) {
                    rf.dispose();
                }
            }
        }        
    }
    
    @Test
    public void low_leaking_references_phantom() {
        int n = 0;
        while(true) {
            gcFiller();
            reportStats();
            ResourceFacade rf = PhantomResourceFactory.newResource();
            queue.addLast(rf);
            if (queue.size() > queueLimit) {
                rf = queue.removeFirst();
                if ((++n) % 100 != 0) {
                    rf.dispose();
                }
            }
        }        
    }
    
    GarbageCollectorMXBean youngGC = ManagementFactory.getGarbageCollectorMXBeans().get(0); 
    GarbageCollectorMXBean oldGC = ManagementFactory.getGarbageCollectorMXBeans().get(1); 
    long lastYoungGC;
    long lastOldGC;
    long lastReleased;
    
    public void reportStats() {
        if (lastYoungGC != youngGC.getCollectionCount() || lastOldGC != oldGC.getCollectionCount()) {
            long released = Resource.GLOBAL_RELEASED.get() - lastReleased;
            long used = Resource.GLOBAL_ALLOCATED.get() - Resource.GLOBAL_RELEASED.get();
            lastReleased = Resource.GLOBAL_RELEASED.get();
            System.out.println("Released: " + released + " In use: " + used);
            lastYoungGC = youngGC.getCollectionCount();
            lastOldGC = oldGC.getCollectionCount();
        }
    }
    
    public void gcFiller() {
        List<int[]> data = new ArrayList<int[]>();
        for(int i = 0; i != 16; ++i) {
            data.add(new int[256]);
        }
        data.toString();
    }
    
}