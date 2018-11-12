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
* `root-controller-user-webid` A URI representing the WebID of a user with read, write, and control permissions on root container (corresponding with either root-controller-user-name and root-controller-user-password, or root-controller-user-auth-header-value).
* `root-controller-user-name` Username of user associated with root-controller-user-webid
* `root-controller-user-password` Password of user associated with root-controller-user-webid 
* `root-controller-user-auth-header-value` "Authorization" header value for a user with read, write, and control.  When present, this value will be added to the request effectively overriding Authenticator implementations, custom or default, found in the classpath.
* `permissionless-user-webid` A URI representing the WebID of a user with no permissions (corresponding with either permissionless-user-name and permissionless-user-password, or permissionless-user-auth-header-value). 
* `permissionless-user-name` Username of user associated with the permissionless-user-webid
* `permissionless-user-password` Password of user associated with permissionless-user-webid
* `permissionless-user-auth-header-value` "Authorization" header value for a user with no preset permissions.  When present, this value will be added to the request, effectively overriding Authenticator implementations, custom or default, found in the classpath.
* `testngxml` (optional) The custom testng.xml configuration ([documentation](http://testng.org/doc/documentation-main.html#testng-xml))
  * See example [testng.xml](https://github.com/fcrepo/Fedora-API-Test-Suite/tree/master/src/main/resources/testng.xml)
* `requirements` (optional) The requirement-levels of test to be run: ALL|MUST|SHOULD|MAY
  * Multiple levels can be provided, separated by ','
* `config-file` (optional) A yaml configuration file containing the configuration parameters. See distributed `config.yml.dist`
* `site-name` (optional) The above yaml file can contain multiple configurations, this chooses one. Defaults to "default"
* `constraint-error-generator` (optional)  A file containing a SPARQL query that will trigger a constraint error. If no file is specified, the test suite will use a default constraint error generating test.
* `auth-class` (optional) The class name of an implementation of the org.fcrepo.spec.testsuite.authn.Authenticator interface.

### Authenticators
The Fedora Specification does not have anything to say about how requests are authenticated by implementations.  Therefore it is necessary for each implementation to perform
container authentications where required and add any required auth information to the request originating from the testsuite.  See org.fcrepo.spec.testsuite.authn.Authenticator and the
org.fcrepo.spec.testsuite.authn.DefaultAuthenticator for a sample implementation.  Once you've implemented your Authenticator and packaged it in a jar,  you must drop the jar in a directory named
`authenticators` located in the same directory as your testsuite jar.

### Configuration file syntax
The configuration file is Yaml and a simple structure. The first level groups a set of configuration parameters, these parameters are key value pairs with the keys being the above options.

```
default:
  rooturl: http://localhost:8088/rest
  root-controller-user-webid: http://example.com/fedoraAdmin
  root-controller-user-password: fedoraAdmin
  permissionless-user-webid: http://example.com/testuser
  permissionless-user-password: testpass
  broker-url: tcp://127.0.0.1:61616
  topic-name: fedora
  queue-name:
```

An example with multiple configurations looks like:

```
default:
  rooturl: http://localhost:8088/rest
  root-controller-user-webid: http://example.com/fedoraAdmin
  root-controller-user-password: fedoraAdmin
  permissionless-user-webid: http://example.com/testuser
  permissionless-user-password: testpass
  broker-url: tcp://127.0.0.1:61616
  topic-name: fedora
  queue-name:
othersite:
  rooturl: http://secondserver:8080/fcrepo/rest
  root-controller-user-webid: http://example.com/totalBoss
  root-controller-user-password: totalBoss 
  permissionless-user-webid: http://example.com/otherperson
  permissionless-user-password: otherpassword 
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
