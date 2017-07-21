package info.ragozin.proflab;

import info.ragozin.proflab.CorruptedHashMapInterlock.ConfigBean;

import org.junit.Test;

public class CorruptedHashMapDeadlock {

    @Test
    public void deadlock() throws Exception {
        
        CorruptedHashMapInterlock pis = new CorruptedHashMapInterlock(new ConfigBean() {
            @Override
            public int threadCount() {
                return 4;
            }            
        });
        
        new DemoRunner(pis);        
    }
}
