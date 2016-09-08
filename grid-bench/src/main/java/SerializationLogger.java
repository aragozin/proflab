import net.java.btrace.annotations.BTrace;
import net.java.btrace.annotations.Duration;
import net.java.btrace.annotations.Kind;
import net.java.btrace.annotations.Location;
import net.java.btrace.annotations.OnEvent;
import net.java.btrace.annotations.OnMethod;
import net.java.btrace.annotations.Property;
import net.java.btrace.annotations.Self;
import net.java.btrace.ext.profiling.Profiler;
import net.java.btrace.ext.profiling.Profiling;

@BTrace
public class SerializationLogger {
    
    @Property(name="btrace:name=Serialization")
    static Profiler opBench = Profiling.newProfiler();
    
    @OnMethod(clazz = "java.io.ObjectOutputStream",
            method = "/writeObject|writeUnshared/")
    static void enterOpCommit( @Self Object op, Object obj) {
        if (obj != null) {
            String cname = obj.getClass().getName();
            Profiling.recordEntry(opBench, cname );
        }
    }

    @OnMethod(clazz = "java.io.ObjectOutputStream",
            method = "/writeObject|writeUnshared/",
            location = @Location(value = Kind.RETURN))
    static void exitOpCommit( @Self Object op, @Duration long time, Object obj) {
        if (obj != null) {
            String cname = obj.getClass().getName();
            Profiling.recordExit(opBench, cname, time);
        }
    }
    
    @OnEvent
    static void printSnap() {
        Profiling.printSnapshot("Serialization stats", opBench.snapshot());
    }
}
