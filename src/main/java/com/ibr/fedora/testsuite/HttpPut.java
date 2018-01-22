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
package com.ibr.fedora.testsuite;

import com.ibr.fedora.TestSuiteGlobals;
import com.ibr.fedora.TestsLabels;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.containsString;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class HttpPut {
    public String username;
    public String password;
    public TestsLabels tl = new TestsLabels();

    /**
     * Authentication
     * @param username
     * @param password
     */
    @BeforeClass
    @Parameters({"param2", "param3"})
    public void auth(final String username, final String password) {
    this.username = username;
    this.password = password;
    }

    /**
     * 3.6-B
     * @param host
     */
    @Test(priority = 26)
    @Parameters({"param1"})
    public void httpPut(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();

        ps.append("\n26." + tl.httpPut()[0] + "-" + tl.httpPut()[1]).append("\n");
        ps.append("Request:\n");
        final String resource = RestAssured.given()
            .auth().basic(this.username, this.password)
            .header("Content-Disposition", "attachment; filename=\"postCreate.txt\"")
            .body("TestString.")
            .when()
            .post(host).asString();

        RestAssured.given().auth().basic(this.username, this.password)
            .header("Content-Disposition", "attachment; filename=\"putUpdate.txt\"")
            .header("Link", "<http://www.w3.org/ns/ldp#RDFSource>; rel=\"type\"")
            .body("TestString2.")
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .when()
            .put(resource)
            .then()
            .log().all()
            .statusCode(409);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.6.1-A
     * @param host
     * @throws FileNotFoundException
     */
    @Test(priority = 27)
    @Parameters({"param1"})
    public void updateTriples(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n27." + tl.updateTriples()[0] + "-" + tl.updateTriples()[1]).append("\n");
        ps.append("Request:\n");

        final String resource = RestAssured.given()
        .auth().basic(this.username, this.password)
        .contentType("text/turtle")
        .when()
        .post(host).asString();

        String body = RestAssured.given()
        .auth().basic(this.username, this.password)
        .when()
        .get(resource).asString();
        body += " <> dc:title \"some-title\" .";

        RestAssured.given()
            .auth().basic(this.username, this.password)
            .contentType("text/turtle")
            .body(body)
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .when()
            .put(resource)
            .then()
            .log().all()
            .statusCode(204);

    ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.6.1-B
     * @param host
     * @throws FileNotFoundException
     */
    @Test(priority = 28)
    @Parameters({"param1"})
    public void updateDisallowedTriples(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n28." + tl.updateDisallowedTriples()[0] + "-" + tl.updateDisallowedTriples()[1]).append("\n");
        ps.append("Request:\n");

        final String resource = RestAssured.given()
            .auth().basic(this.username, this.password)
            .contentType("text/turtle")
            .when()
            .post(host).asString();

        String body = RestAssured.given()
            .auth().basic(this.username, this.password)
            .when()
            .get(resource).asString();
        body += " <> fedora:createdBy \"user\" .";

        RestAssured.given()
            .auth().basic(this.username, this.password)
            .contentType("text/turtle")
            .body(body)
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .when()
            .put(resource)
            .then()
            .log().all()
            .statusCode(409);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.6.1-C
     * @param host
     * @throws FileNotFoundException
     */
    @Test(priority = 29)
    @Parameters({"param1"})
    public void updateDisallowedTriplesResponse(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n29." + tl.updateDisallowedTriplesResponse()[0] + "-"
        + tl.updateDisallowedTriplesResponse()[1]).append("\n");
        ps.append("Request:\n");

        final String resource = RestAssured.given()
             .auth().basic(this.username, this.password)
             .contentType("text/turtle")
             .when()
             .post(host).asString();
        String body = RestAssured.given()
             .auth().basic(this.username, this.password)
             .when()
             .get(resource).asString();
        body += " <> fedora:createdBy \"user\" .";

        RestAssured.given()
             .auth().basic(this.username, this.password)
             .contentType("text/turtle")
             .body(body)
             .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
             .log().all()
             .when()
             .put(resource)
             .then()
             .log().all()
             .statusCode(409).body(containsString("createdBy"));

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.6.1-D
     * @param host
     * @throws FileNotFoundException
     */
    @Test(priority = 30)
    @Parameters({"param1"})
    public void updateDisallowedTriplesConstrainedByHeader(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n30." + tl.updateDisallowedTriplesConstrainedByHeader()[0] + "-"
        + tl.updateDisallowedTriplesConstrainedByHeader()[1]).append("\n");
        ps.append("Request:\n");

        final String resource = RestAssured.given()
            .auth().basic(this.username, this.password)
            .contentType("text/turtle")
            .when()
            .post(host).asString();
        String body = RestAssured.given()
            .auth().basic(this.username, this.password)
            .when()
            .get(resource).asString();
        body += " <> fedora:createdBy \"user\" .";

        RestAssured.given()
            .auth().basic(this.username, this.password)
            .contentType("text/turtle")
            .body(body)
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .when()
            .put(resource)
            .then()
            .log().all()
            .statusCode(409).header("Link", containsString("constrainedBy"));

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.6.2-A
     * @param host
     */
    @Test(priority = 31)
    @Parameters({"param1"})
    public void httpPutNR(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();

        ps.append("\n31." + tl.httpPutNR()[0] + "-" + tl.httpPutNR()[1]).append("\n");
        ps.append("Request:\n");
        final String resource = RestAssured.given()
            .auth().basic(this.username, this.password)
            .header("Content-Disposition", "attachment; filename=\"postCreate.txt\"")
            .body("TestString.")
            .when()
            .post(host).asString();

        RestAssured.given()
            .auth().basic(this.username, this.password)
            .header("Content-Disposition", "attachment; filename=\"putUpdate.txt\"")
            .body("TestString2.")
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .when()
            .put(resource)
            .then()
            .log().all()
            .statusCode(204);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.6.2-B
     * @param host
     */
    @Test(priority = 32)
    @Parameters({"param1"})
    public void putDigestResponseHeaderAuthentication(final String host) throws FileNotFoundException {
        final String checksum = "MD5=97c4627dc7734f65f5195f1d5f556d7a";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n32." + tl.putDigestResponseHeaderAuthentication()[0] + "-" +
        tl.putDigestResponseHeaderAuthentication()[1]).append("\n");
        ps.append("Request:\n");

        final String resource = RestAssured.given()
            .auth().basic(this.username, this.password)
            .header("Content-Disposition", "attachment; filename=\"digestAuth.txt\"")
            .body("TestString.")
            .when()
            .post(host).asString();

        RestAssured.given()
            .auth().basic(this.username, this.password)
            .header("digest", checksum)
            .header("Content-Disposition", "attachment; filename=\"digestAuth.txt\"")
            .body("TestString.")
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .when()
            .put(resource)
            .then()
            .log().all()
            .statusCode(409);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.6.2-C
     * @param host
     */
    @Test(priority = 33)
    @Parameters({"param1"})
    public void putDigestResponseHeaderVerification(final String host) throws FileNotFoundException {
        final String checksum = "abc=abc";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n33." + tl.putDigestResponseHeaderVerification()[0] + "-" +
        tl.putDigestResponseHeaderVerification()[1]).append("\n");
        ps.append("Request:\n");

        final String resource = RestAssured.given()
            .auth().basic(this.username, this.password)
            .header("Content-Disposition", "attachment; filename=\"postCreate.txt\"")
            .body("TestString.")
            .when()
            .post(host).asString();

        RestAssured.given()
            .auth().basic(this.username, this.password)
            .header("digest", checksum)
            .header("Content-Disposition", "attachment; filename=\"putUpdate.txt\"")
            .body("TestString2.")
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .when()
            .put(resource)
            .then()
            .log().all()
            .statusCode(400);

        ps.append("\n -Case End- \n").close();
     }

    }