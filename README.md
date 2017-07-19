Multiple Java deadlock demo
====

This is a material for Java profiling workshop by Aleskey Ragozin

Benches
----

 - `mvn -Psync_lock test` - Java synchronize deadlock
 - `mvn -Pjuc_lock test` - java.util.concurrent.ReenterantLock deadlock
 - `mvn -Prw_lock test` - Read/Write lock deadlock
 - `mvn -Pmap_lock test` - thread unsafe Map deadlock