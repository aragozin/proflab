package info.ragozin.proflab;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CorruptedHashMapInterlock implements Runnable {

    public interface Config {
        
        public String name();
        
        public int threadCount();
        
        public int putBatchSize();

        public float spinFactor();
        
        public int cycleCount();
        
    }
    
    public static abstract class ConfigBean implements Config {

        @Override
        public String name() {
            return CorruptedHashMapInterlock.class.getName();
        }

        @Override
        public int putBatchSize() {
            return 2000;
        }

        @Override
        public float spinFactor() {
            return 0.2f;
        }

        @Override
        public int cycleCount() {
            return 10;
        }
    }

    private final String name;
    private final Thread[] threads;
    private final CyclicBarrier barrier;
    private final CyclicBarrier jobBarrier = new CyclicBarrier(2);
    
    private final int putBatchSize;
    private final float spinFactor;
    private final int cycleCount;
    
    private int cycle;
    
    private boolean started;
    private volatile boolean terminated = false;
    
    private Map<Object, String> unsafeMap = new HashMap<Object, String>();
    
    public CorruptedHashMapInterlock(Config config) {
        
        name = config.name();
        
        threads = new Thread[config.threadCount()];
        for(int i = 0; i != threads.length; ++i) {
            threads[i] = newThread(i);
        }
        
        barrier = new CyclicBarrier(threads.length, new Runnable() {
            @Override
            public void run() {
                barrierPoint();                
            }
        });
        
        putBatchSize = config.putBatchSize();
        spinFactor = config.spinFactor();
        cycleCount = config.cycleCount();        
    }
    
    @Override
    public void run() {
        cycle = 0;
        if (!started) {
            started = true;
            for(Thread t: threads) {
                t.start();
            }        
        }
        try {
            jobBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void terminate() {
        terminated = true;
        for(Thread t: threads) {
            t.interrupt();
        }
    }
    
    protected Thread newThread(int n) {
        Thread t = new Thread(new Runnable() {
            
            @Override
            public void run() {
                workerLoop();
            }
        }, name + "-" + n);
        t.setDaemon(true);
        return t;
    }

    protected void barrierPoint() {
        try {
            unsafeMap = new HashMap<Object, String>();
            if (++cycle >= cycleCount) {
                jobBarrier.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    protected void workerLoop() {
        try {
            Random rnd = new Random(Thread.currentThread().hashCode());
            while(!terminated) {
                barrier.await();
                fillMap(rnd);
//                readMap(rnd);
            }
        } catch (InterruptedException e) {
            return;
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
    
    private void fillMap(Random rnd) {
        for(int i = 0; i != putBatchSize; ++i) {
            int key = rnd.nextInt(putBatchSize * threads.length);
            unsafeMap.put(key, Thread.currentThread().getName() + "-step-" + i);
            Spinner.spinMicros(spinFactor);
        }
    }
}
