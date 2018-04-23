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
* `testngxml` (optional) The custom testng.xml configuration ([documentation](http://testng.org/doc/documentation-main.html#testng-xml))
  * See example [testng.xml](https://github.com/fcrepo4-labs/Fedora-API-Test-Suite/tree/master/src/main/resources/testng.xml)
* `requirements` (optional) The requirement-levels of test to be run: ALL|MUST|SHOULD|MAY|MUSTNOT|SHOULDNOT
  * Multiple levels can be provided, separated by ','

### Notes
* Specific test methods may be invoked by using a custom testng.xml file (option: `testngxml`) with the addition of \<class>/\<methods> regular expression filters.
 See commented example in [testng.xml](https://github.com/fcrepo4-labs/Fedora-API-Test-Suite/tree/master/src/main/resources/testng.xml)

## Results
 Test results are available at:
 > report/testsuite-execution-report.html
