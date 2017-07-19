package info.ragozin.proflab;

import org.junit.Test;

public class SjkParserBench {

    @Test
    public void bench() {

    	SjkDumpParser parser = new SjkDumpParser();
        
        new DemoRunner(parser);        
    }
}
