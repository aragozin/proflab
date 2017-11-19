Java profiling lab workbench root
====

This is a root directory structure for profiling workshop.

Multuple demo applications linked here.

Sumbmodule checkout
----

After clone of this repository use following command to checkout submodules.

    git submodule update --init

Compile and prepare example for execution.

    mvn install -DskipTests

Step above would fetch all dependencies from Maven Central. This may take some time.

Tools
----

*tools/sjk/sjk.jar* - Swiss Java Knife diagnostic tool. See [https://github.com/aragozin/jvm-tools](https://github.com/aragozin/jvm-tools) for details.