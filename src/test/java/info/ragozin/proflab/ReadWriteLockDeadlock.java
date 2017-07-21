package info.ragozin.proflab;

import java.io.IOException;

import info.ragozin.proflab.ReadWriteInterlock.ConfigBean;

import org.junit.Test;

public class ReadWriteLockDeadlock {

    @Test
    public void deadlock() throws IOException {
        
        ReadWriteInterlock pis = new ReadWriteInterlock(new ConfigBean() {
            @Override
            public int threadCount() {
                return 3;
            }            
        });
        
        new DemoRunner(pis);        
    }
}
