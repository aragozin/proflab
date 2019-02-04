Simple example doing crypto hash calculation to illustrate sampling bias.

Start process with default JVM settings

    java -cp target/classes CryptoBench

Start process with extra debug symblos (for Flight Recorder Sampling)

    java -XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints -cp target/classes CryptoBench

Start process for profiling with perf-map

    java -XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints -XX:+PreserveFramePointer -cp target/classes CryptoBench

Start with tracing in bench loop

    java -cp target/classes -DtrackTime=true CryptoBench
