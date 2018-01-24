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
$ java -jar target/testSuite-1.0-SNAPSHOT-shaded.jar --baseurl http://localhost:8080/
```

## Options
* `baseurl` The repository base URL, e.g., `http://localhost:8080/rest/`
* `user` (optional) The username to connect to the repository with
* `password` (optional) The password to connect to the repository with

 Test results are available at:
 > report/testsuite-execution-report.html
