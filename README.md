# Fedora-API-Test-Suite
[![Build Status](https://travis-ci.org/fcrepo/Fedora-API-Test-Suite.svg?branch=master)](https://travis-ci.org/fcrepo/Fedora-API-Test-Suite)
[![LICENSE](https://img.shields.io/badge/license-Apache-blue.svg?style=flat-square)](./LICENSE)

A Standalone testing suite that exercises the requirements in the Fedora API Specification indicating the degree of the server’s compliance with the specification.


Building the test suite:
```
$ mvn install
```

Running the test suite:
```
$ java -jar target/testSuite-1.0-SNAPSHOT-shaded.jar --rooturl http://localhost:8080/
```

## Options
* `rooturl` The repository base URL, e.g., `http://localhost:8080/rest/`
* `admin-user` An user with admin access to the repository.
* `admin-password` The admin user's password.
* `user` The username to connect to the repository with
* `password` The password to connect to the repository with
* `testngxml` (optional) The custom testng.xml configuration ([documentation](http://testng.org/doc/documentation-main.html#testng-xml))
  * See example [testng.xml](https://github.com/fcrepo/Fedora-API-Test-Suite/tree/master/src/main/resources/testng.xml)
* `requirements` (optional) The requirement-levels of test to be run: ALL|MUST|SHOULD|MAY
  * Multiple levels can be provided, separated by ','
* `config-file` (optional) A yaml configuration file containing the configuration parameters. See distributed `config.yml.dist`
* `site-name` (optional) The above yaml file can contain multiple configurations, this chooses one. Defaults to "default"
* `constraint-error-generator` (optional)  A file containing a SPARQL query that will trigger a constraint error. If no file is specified, the test suite will use a default constraint error generating test.

### Configuration file syntax
The configuration file is Yaml and a simple structure. The first level groups a set of configuration parameters, these parameters are key value pairs with the keys being the above options.

```
default:
  rooturl: http://localhost:8088/rest
  admin-user: fedoraAdmin
  admin-password: fedoraAdmin
  user: testuser
  password: testpass
  broker-url: tcp://127.0.0.1:61616
  topic-name: fedora
  queue-name:
```

An example with multiple configurations looks like:

```
default:
  rooturl: http://localhost:8088/rest
  admin-user: fedoraAdmin
  admin-password: fedoraAdmin
  user: testuser
  password: testpass
  broker-url: tcp://127.0.0.1:61616
  topic-name: fedora
  queue-name:
othersite:
  rooturl: http://secondserver:8080/fcrepo/rest
  admin-user: totalBoss
  admin-password: totalBoss
  user: otherperson
  password: otherpassword
  broker-url: tcp://overtherainbow:61616
  topic-name:
  queue-name: fedora
```

To use the "othersite" settings run:

```
java -jar target/testSuite-1.0-SNAPSHOT-shaded.jar -c config.yml.dist -n othersite
```


### Notes
* Specific test methods may be invoked by using a custom testng.xml file (option: `testngxml`) with the addition of \<class>/\<methods> regular expression filters.
 See commented example in [testng.xml](https://github.com/fcrepo/Fedora-API-Test-Suite/tree/master/src/main/resources/testng.xml)

## Results
 Test results are available at:
 > report/testsuite-execution-report.html
