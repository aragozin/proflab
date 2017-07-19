package info.ragozin.proflab;

import info.ragozin.proflab.PlainInterlockReenterantLock.ConfigBean;

import org.junit.Test;

public class ReenterantLockDeadlock {

    @Test
    public void deadlock() {
        
        PlainInterlockReenterantLock pis = new PlainInterlockReenterantLock(new ConfigBean() {
            @Override
            public int threadCount() {
                return 4;
            }            
        });
        
        new DemoRunner(pis);
//        
//        int c = 0;
//        while(true) {
//            long n = System.currentTimeMillis();
//            pis.run();
//            long time = System.currentTimeMillis() - n;
//            System.out.println("Batch " + (++c) + " completed in " + time + "ms");
//        }        
    }
}
