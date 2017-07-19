Special Java references gargabe collection impact demo
====

This is a material for Java profiling workshop by Aleskey Ragozin

Benches
----

 - `mvn -Pfinalizer test` - finalizer based reference management
 - `mvn -Pphantom test` - phantom based reference managment
 - `mvn -Pclean-finalizer test` - finalizer based reference management with explicit clean up
 - `mvn -Pclean-phantom test` - phantom based reference managment with explicit clean up
