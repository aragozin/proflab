package info.ragozin.proflab;

import org.junit.BeforeClass;
import org.junit.Test;

public class FinalizerBench {

	@BeforeClass
	public static void deathWatch() {
		DeathWatch.start();
	}

	@Test
	public void bench() {
		new ReferenceUsageCheck().leaking_references_finalizer();
	}	
}
