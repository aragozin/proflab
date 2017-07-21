package info.ragozin.proflab;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class PlainInterlockSynchronize implements Runnable {

    public interface Config {
        
        public String name();
        
        public int threadCount();
        
        public int resourceCount();

        public int lockPerTask();
        
        public int spinFactor();
        
        public int cycleCount();
        
    }
    
    public static abstract class ConfigBean implements Config {

        @Override
        public String name() {
            return PlainInterlockSynchronize.class.getName();
        }

        @Override
        public int resourceCount() {
            return 50;
        }

        @Override
        public int lockPerTask() {
            return 3;
        }

        @Override
        public int spinFactor() {
            return 1; // micros
        }

        @Override
        public int cycleCount() {
            return 20;
        }
    }

    private final String name;
    private final Thread[] threads;
    private final ContentionResource<Void>[] resources; 
    private final CyclicBarrier barrier;
    private final CyclicBarrier jobBarrier = new CyclicBarrier(2);
    
    private final int lockPerTask;
    private final int spinFactor;
    private final int cycleCount;
    
    private int cycle;
    
    private boolean started;
    private volatile boolean terminated = false;
    
    @SuppressWarnings("unchecked")
    public PlainInterlockSynchronize(Config config) {
        
        name = config.name();
        
        threads = new Thread[config.threadCount()];
        for(int i = 0; i != threads.length; ++i) {
            threads[i] = newThread(i);
        }
        
        resources = new ContentionResource[config.resourceCount()];
        for(int i = 0; i != resources.length; ++i) {
            resources[i] = new ContentionResource<Void>(name + "@Res-" + i, null);
        }
        
        barrier = new CyclicBarrier(threads.length, new Runnable() {
            @Override
            public void run() {
                barrierPoint();                
            }
        });
        
        lockPerTask = config.lockPerTask();
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
//            System.out.println("Pass barrier: " + cycle);
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
                lockIn(rnd, lockPerTask);
            }
        } catch (InterruptedException e) {
            return;
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
    
    protected void lockIn(Random rnd, int n) {
        if (n > 0) {
            ContentionResource<Void> lock = resources[rnd.nextInt(resources.length)];
//            System.out.println("[" + Thread.currentThread().getName() + "] lock " + lock);
            synchronized (lock) {
                Spinner.spinMicros(spinFactor);
                lockIn(rnd, n - 1);
            }
        }
    }
}
