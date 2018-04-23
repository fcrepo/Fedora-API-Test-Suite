/**
 * @author Jorge Abrego, Fernando Cardoza
 */
/*
 * Licensed to DuraSpace under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * DuraSpace licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fcrepo.spec.testsuite.test;

import static org.hamcrest.CoreMatchers.containsString;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.fcrepo.spec.testsuite.TestSuiteGlobals;
import org.fcrepo.spec.testsuite.TestsLabels;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.response.Response;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class HttpPut {
    public static String body = "@prefix ldp: <http://www.w3.org/ns/ldp#> ."
                                + "@prefix dcterms: <http://purl.org/dc/terms/> ."
                                + "<> a ldp:Container, ldp:BasicContainer;"
                                + "dcterms:title 'Put class Container' ;"
                                + "dcterms:description 'This is a  test container  for the Fedora API Test Suite.' . ";
    public String username;
    public String password;
    public TestsLabels tl = new TestsLabels();

    /**
     * Authentication
     *
     * @param username
     * @param password
     */
    @Parameters({"param2", "param3"})
    public HttpPut(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * 3.6-B
     *
     * @param uri
     */
    @Test(priority = 26, groups = {"MAY"})
    @Parameters({"param1"})
    public void httpPut(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();

        ps.append("\n26." + tl.httpPut()[0] + "-" + tl.httpPut()[1]).append("\n");
        ps.append("Request:\n");
        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Disposition", "attachment; filename=\"postCreate.txt\"")
                                             .header("slug", "Put-3.6-B")
                                             .body("TestString.")
                                             .when()
                                             .post(uri);
        final String locationHeader = resource.getHeader("Location");
        RestAssured.given().auth().basic(this.username, this.password)
                   .header("Content-Disposition", "attachment; filename=\"putUpdate.txt\"")
                   .header("Link", "<http://www.w3.org/ns/ldp#RDFSource>; rel=\"type\"")
                   .body("TestString2.")
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .put(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(409);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.6.1-A
     *
     * @param uri
     * @throws FileNotFoundException
     */
    @Test(priority = 27, groups = {"MUST"})
    @Parameters({"param1"})
    public void updateTriples(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n27." + tl.updateTriples()[0] + "-" + tl.updateTriples()[1]).append("\n");
        ps.append("Request:\n");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .contentType("text/turtle")
                                             .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                                             .header("slug", "Put-3.6.1-A")
                                             .body(body)
                                             .when()
                                             .post(uri);
        final String locationHeader = resource.getHeader("Location");

        final String body2 = RestAssured.given()
                                        .auth().basic(this.username, this.password)
                                        .when()
                                        .get(locationHeader).asString();

        final String newBody = body2.replace("Put class Container", "some-title");

        ps.append(newBody);
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("text/turtle")
                   .body(newBody)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .put(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(204);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.6.1-B
     *
     * @param uri
     * @throws FileNotFoundException
     */
    @Test(priority = 28, groups = {"MUST"})
    @Parameters({"param1"})
    public void updateDisallowedTriples(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n28." + tl.updateDisallowedTriples()[0] + "-" + tl.updateDisallowedTriples()[1]).append("\n");
        ps.append("Request:\n");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .contentType("text/turtle")
                                             .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                                             .header("slug", "Put-3.6.1-B")
                                             .body(body)
                                             .when()
                                             .post(uri);

        final String locationHeader = resource.getHeader("Location");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("text/turtle")
                   .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                   .header("slug", "containedFolderSlug")
                   .body(body)
                   .when()
                   .post(locationHeader);

        final String body2 = RestAssured.given()
                                        .auth().basic(this.username, this.password)
                                        .when()
                                        .get(locationHeader).asString();

        final String newBody = body2.replace("containedFolderSlug", "some-name");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("text/turtle")
                   .body(newBody)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .put(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(409);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.6.1-C
     *
     * @param uri
     * @throws FileNotFoundException
     */
    @Test(priority = 29, groups = {"MUST"})
    @Parameters({"param1"})
    public void updateDisallowedTriplesResponse(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n29." + tl.updateDisallowedTriplesResponse()[0] + "-"
                  + tl.updateDisallowedTriplesResponse()[1]).append("\n");
        ps.append("Request:\n");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .contentType("text/turtle")
                                             .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                                             .header("slug", "Put-3.6.1-C")
                                             .body(body)
                                             .when()
                                             .post(uri);
        final String locationHeader = resource.getHeader("Location");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("text/turtle")
                   .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                   .header("slug", "containedFolderSlug")
                   .body(body)
                   .when()
                   .post(locationHeader);

        final String body2 = RestAssured.given()
                                        .auth().basic(this.username, this.password)
                                        .when()
                                        .get(locationHeader).asString();

        final String newBody = body2.replace("containedFolderSlug", "some-name");

        ps.append("PUT Request: \n");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("text/turtle")
                   .body(newBody)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .put(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(409).body(containsString("ldp#contains"));

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.6.1-D
     *
     * @param uri
     * @throws FileNotFoundException
     */
    @Test(priority = 30, groups = {"MUST"})
    @Parameters({"param1"})
    public void updateDisallowedTriplesConstrainedByHeader(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n30." + tl.updateDisallowedTriplesConstrainedByHeader()[0] + "-"
                  + tl.updateDisallowedTriplesConstrainedByHeader()[1]).append("\n");
        ps.append("Request:\n");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .contentType("text/turtle")
                                             .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                                             .header("slug", "Put-3.6.1-D")
                                             .body(body)
                                             .when()
                                             .post(uri);

        final String locationHeader = resource.getHeader("Location");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("text/turtle")
                   .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                   .header("slug", "containedFolderSlug")
                   .body(body)
                   .when()
                   .post(locationHeader);

        final String body2 = RestAssured.given()
                                        .auth().basic(this.username, this.password)
                                        .when()
                                        .get(locationHeader).asString();

        final String newBody = body2.replace("containedFolderSlug", "some-name");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("text/turtle")
                   .body(newBody)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .put(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(409).header("Link", containsString("constrainedBy"));

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.6.2-A
     *
     * @param uri
     */
    @Test(priority = 31, groups = {"MUST"})
    @Parameters({"param1"})
    public void httpPutNR(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();

        ps.append("\n31." + tl.httpPutNR()[0] + "-" + tl.httpPutNR()[1]).append("\n");
        ps.append("Request:\n");
        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Disposition", "attachment; filename=\"postCreate.txt\"")
                                             .header("slug", "Put-3.6.2-A")
                                             .body("TestString.")
                                             .when()
                                             .post(uri);
        final String locationHeader = resource.getHeader("Location");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .header("Content-Disposition", "attachment; filename=\"putUpdate.txt\"")
                   .body("TestString2.")
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .put(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(204);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.6.2-B
     *
     * @param uri
     */
    @Test(priority = 32, groups = {"MUST"})
    @Parameters({"param1"})
    public void putDigestResponseHeaderAuthentication(final String uri) throws FileNotFoundException {
        final String checksum = "MD5=97c4627dc7734f65f5195f1d5f556d7a";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n32." + tl.putDigestResponseHeaderAuthentication()[0] + "-" +
                  tl.putDigestResponseHeaderAuthentication()[1]).append("\n");
        ps.append("Request:\n");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Disposition", "attachment; filename=\"digestAuth.txt\"")
                                             .header("slug", "Put-3.6.2-B")
                                             .body("TestString.")
                                             .when()
                                             .post(uri);
        final String locationHeader = resource.getHeader("Location");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .header("digest", checksum)
                   .header("Content-Disposition", "attachment; filename=\"digestAuth.txt\"")
                   .body("TestString.")
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .put(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(409);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.6.2-C
     *
     * @param uri
     */
    @Test(priority = 33, groups = {"SHOULD"})
    @Parameters({"param1"})
    public void putDigestResponseHeaderVerification(final String uri) throws FileNotFoundException {
        final String checksum = "abc=abc";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n33." + tl.putDigestResponseHeaderVerification()[0] + "-" +
                  tl.putDigestResponseHeaderVerification()[1]).append("\n");
        ps.append("Request:\n");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Disposition", "attachment; filename=\"postCreate.txt\"")
                                             .header("slug", "Put-3.6.2-C")
                                             .body("TestString.")
                                             .when()
                                             .post(uri);
        final String locationHeader = resource.getHeader("Location");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .header("digest", checksum)
                   .header("Content-Disposition", "attachment; filename=\"putUpdate.txt\"")
                   .body("TestString2.")
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .put(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(400);

        ps.append("\n -Case End- \n").close();
    }

}
