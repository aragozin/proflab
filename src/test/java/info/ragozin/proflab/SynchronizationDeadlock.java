package info.ragozin.proflab;

import info.ragozin.proflab.PlainInterlockSynchronize.ConfigBean;

import org.junit.Test;

public class SynchronizationDeadlock {

    @Test
    public void deadlock() {
        
        PlainInterlockSynchronize pis = new PlainInterlockSynchronize(new ConfigBean() {
            @Override
            public int threadCount() {
                return 4;
            }            
        });
        
        new DemoRunner(pis);
    }
}
