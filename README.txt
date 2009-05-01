Once you have checked the source out of the project repository,
follow the instructions below to build Probatron.

1. Ensure you have the a suitable version of the JDK installed: either
JDK 5.0 or 6 should work.  The JDK can be downloaded from
<http://java.sun.com/javase/downloads/>.

All other dependencies are included in the repository.

2. Set the JAVA_HOME environment variable to point to the directory
where the JDK is installed. For example, on Windows, do something like

  set JAVA_HOME=c:\Program Files\Java\jdk1.6.0_10

3. Change your working directory to the root source directory (i.e. the
directory containing this file).

4. Run the ant script included in the repository. On Windows, use

  .\ant

This runs the version of ant included in the repository.  When the ant
script completes, you should find probatron.jar in the dist directory.