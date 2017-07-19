package info.ragozin.proflab;

import java.io.File;
import java.io.FileInputStream;

import org.gridkit.jvmtool.stacktrace.StackTraceCodec;
import org.gridkit.jvmtool.stacktrace.StackTraceReader;

public class SjkDumpParser implements Runnable {

	@Override
	public void run() {
		try {
			StackTraceReader str = StackTraceCodec.newReader(new FileInputStream(new File("src/test/resources/jboss-10k.std")));
			while(str.loadNext()) {};
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}		
	}
}
