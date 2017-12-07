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

import java.io.FileNotFoundException;
import java.io.PrintStream;

import static org.hamcrest.Matchers.containsString;


public class HttpPost {
    public String username;
    public String password;
    public TestsLabels tl = new TestsLabels();
    public String resource = "";
    public String binary = "https://www.w3.org/StyleSheets/TR/2016/logos/UD-watermark";

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
     * 3.5-A
     * @param host
     */
    @Test(priority = 19)
    @Parameters({"param1"})
    public void httpPost(final String host) throws FileNotFoundException {
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n19." + tl.httpPost()[1]).append("\n");
    ps.append("Request:\n");
    RestAssured.given()
    .auth().basic(this.username, this.password)
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .contentType("text/turtle")
            .log().all()
            .when()
            .post(host)
            .then()
            .log().all()
            .statusCode(201);
    ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.5-B
     * @param host
     */
    @Test(priority = 20)
    @Parameters({"param1"})
    public void constrainedByResponseHeader(final String host) throws FileNotFoundException {
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n20." + tl.constrainedByResponseHeader()[1]).append("\n");
    ps.append("Request:\n");

    final String resource =
            RestAssured.given()
    .auth().basic(this.username, this.password)
                    .header("Content-Disposition", "attachment; filename=\"constrainedByResponseHeader.txt\"")
                    .body("TestString.")
                    .when()
                    .post(host).asString();

    RestAssured.given()
    .auth().basic(this.username, this.password)
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .contentType("text/turtle")
            .log().all()
            .when()
            .post(resource)
            .then()
            .log().all()
            .statusCode(409).header("Link", containsString("constrainedBy"));

    ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.5-C
     * @param host
     */
    @Test(priority = 21)
    @Parameters({"param1"})
    public void postNonRDFSource(final String host) throws FileNotFoundException {
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n21." + tl.postNonRDFSource()[1]).append('\n');
    ps.append("Request:\n");
    RestAssured.given()
    .auth().basic(this.username, this.password)
            .header("Content-Disposition", "attachment; filename=\"postNonRDFSource.txt\"")
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .when()
            .post(host)
            .then()
            .log().all()
            .statusCode(201);
    ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.5-D
     * @param host
     */
    @Test(priority = 22)
    @Parameters({"param1"})
    public void postResourceAndCheckAssociatedResource(final String host) throws FileNotFoundException {
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n22." + tl.postResourceAndCheckAssociatedResource()[1]).append('\n');
    ps.append("Request:\n");
    RestAssured.given()
    .auth().basic(this.username, this.password)
            .header("Content-Disposition", "attachment; filename=\"postResourceAndCheckAssociatedResource.txt\"")
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .when()
            .post(host)
            .then()
            .log().all()
            .statusCode(201).header("Link", containsString("describedby"));

    ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.5.1-A
     * @param host
     */
    @Test(priority = 23)
    @Parameters({"param1"})
    public void postDigestResponseHeaderAuthentication(final String host) throws FileNotFoundException {
        final String checksum = "sha1=372ea08cab33e71c02c651dbc83a474d32c676b";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n23." + tl.postDigestResponseHeaderAuthentication()[1]).append('\n');
        ps.append("Request:\n");
        RestAssured.given()
    .auth().basic(this.username, this.password)
                .header("Content-Disposition",
                        "attachment; filename=\"test1digesttext.txt\"","Digest", checksum)
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .contentType("text/plane")
                .log().all()
                .when()
                .post(host)
                .then()
                .log().all()
                .statusCode(409);

        ps.append("-Case End- \n").close();
    }

    /**
     * 3.5.1-B
     * @param host
     */
    @Test(priority = 24)
    @Parameters({"param1"})
    public void postDigestResponseHeaderVerification(final String host) throws FileNotFoundException {
        final String checksum = "abc=abc";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n24." + tl.postDigestResponseHeaderVerification()[1]).append('\n');
        ps.append("Request:\n");

        RestAssured.given()
    .auth().basic(this.username, this.password)
                .header("Content-Disposition",
                        "attachment; filename=\"test1digesttext.txt\"","Digest", checksum)
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .contentType("text/plane")
                .log().all()
                .when()
                .post(host)
                .then()
                .log().all()
                .statusCode(400);

        ps.append("\n -Case End- \n").close();
      }
    }