package info.ragozin.perflab.hazelagg;

import java.io.FileNotFoundException;

import org.junit.Test;

public class HazelcastDemoStarter {

    @Test
    public void demo() throws FileNotFoundException, InterruptedException {
        new HazelcastDemo().testStresstPositionAggregation();
    }
}
