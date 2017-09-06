# Fedora-API-Test-Suite
A Standalone testing suite that exercises the requirements in the Fedora API Specification indicating the degree of the server’s compliance with the specification.

Requires Maven to generate .jar file
https://maven.apache.org/install.html

Standalone

$ mvn package

$ java -jar target/testSuite-1.0-SNAPSHOT-shaded.jar http://localhost:8080/
