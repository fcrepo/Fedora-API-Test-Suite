# Fedora-API-Test-Suite
[![Build Status](https://travis-ci.org/fcrepo4-labs/Fedora-API-Test-Suite.svg?branch=master)](https://travis-ci.org/fcrepo4-labs/Fedora-API-Test-Suite)
[![LICENSE](https://img.shields.io/badge/license-Apache-blue.svg?style=flat-square)](./LICENSE)

A Standalone testing suite that exercises the requirements in the Fedora API Specification indicating the degree of the server’s compliance with the specification.


Building the test suite:
```
$ mvn install
```

Running the test suite:
```
$ java -jar target/testSuite-1.0-SNAPSHOT-shaded.jar --host http://localhost:8080/
```

 Test results are available at:
 > report/testsuite-execution-report.html