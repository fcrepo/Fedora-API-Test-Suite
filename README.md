# Fedora-API-Test-Suite
[![Build Status](https://travis-ci.org/fcrepo4-labs/Fedora-API-Test-Suite.svg?branch=master)](https://travis-ci.org/fcrepo4-labs/Fedora-API-Test-Suite)
[![LICENSE](https://img.shields.io/badge/license-Apache-blue.svg?style=flat-square)](./LICENSE)

A Standalone testing suite that exercises the requirements in the Fedora API Specification indicating the degree of the server’s compliance with the specification.

Requires Maven to generate .jar file
https://maven.apache.org/install.html

Standalone
```
$ mvn install
$ mvn package

$ java -jar target/testSuite-1.0-SNAPSHOT-shaded.jar http://localhost:8080/
```
