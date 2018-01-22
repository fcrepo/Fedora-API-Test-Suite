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

import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibr.fedora.TestSuiteGlobals;
import com.ibr.fedora.TestsLabels;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.http.Header;
import io.restassured.http.Headers;

public class ExternalBinaryContent {
    public TestsLabels tl = new TestsLabels();
    public String username;
    public String password;
    public String binary = "https://www.w3.org/StyleSheets/TR/2016/logos/UD-watermark";
    public String binary2 = "https://wiki.duraspace.org/download/attachments/4980737/"
    + "atl.site.logo?version=3&modificationDate=1383695533307&api=v2";

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
     * 3.8-A
     * @param host
     */
    @Test(priority = 45)
    @Parameters({"param1"})
    public void postCreateExternalBinaryContent(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n45." + tl.postCreateExternalBinaryContent()[1]).append('\n');
        ps.append("Request:\n");

        RestAssured.given()
    .auth().basic(this.username, this.password)
                .header("Content-Type", "message/external-body; access-type=URL; URL=\"" + binary + "\"")
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
     * 3.8-A
     * @param host
     */
    @Test(priority = 46)
    @Parameters({"param1"})
    public void putCreateExternalBinaryContent(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n46." + tl.putCreateExternalBinaryContent()[1]).append('\n');
        ps.append("Request:\n");

        RestAssured.given()
    .auth().basic(this.username, this.password)
                .header("Content-Type", "message/external-body; access-type=URL; URL=\"" + binary + "\"")
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .when()
                .put(host)
                .then()
                .log().all()
                .statusCode(201);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.8-A
     * @param host
     */
    @Test(priority = 47)
    @Parameters({"param1"})
    public void putUpdateExternalBinaryContent(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n47." + tl.putUpdateExternalBinaryContent()[1]).append('\n');
        ps.append("Request:\n");

        final String resource =
                RestAssured.given()
    .auth().basic(this.username, this.password)
                        .header("Content-Type", "message/external-body; access-type=URL; URL=\"" + binary + "\"")
                        .when()
                        .post(host).asString();
        RestAssured.given()
    .auth().basic(this.username, this.password)
                .header("Content-Type", "message/external-body; access-type=URL; URL=\"" + binary2 + "\"")
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
     * 3.8-C
     * @param host
     */
    @Test(priority = 48)
    @Parameters({"param1"})
    public void postCheckUnsupportedMediaType(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n48." + tl.postCheckUnsupportedMediaType()[1]).append('\n');
        ps.append("Request:\n");

        RestAssured.given()
    .auth().basic(this.username, this.password)
                .header("Content-Type", "message/external-body; access-type=ftp;"
    + " NAME=\"/some/file\"; site=\"example.com\"")
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .when()
                .post(host)
                .then()
                .log().all()
                .statusCode(415);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.8-C
     * @param host
     */
    @Test(priority = 49)
    @Parameters({"param1"})
    public void putCheckUnsupportedMediaType(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n49." + tl.putCheckUnsupportedMediaType()[1]).append('\n');
        ps.append("Request:\n");

        RestAssured.given()
    .auth().basic(this.username, this.password)
        .header("Content-Type", "message/external-body; access-type=ftp; NAME=\"/some/file\"; site=\"example.com\"")
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .when()
                .put(host)
                .then()
                .log().all()
                .statusCode(415);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.8-E
     * @param host
     */
    @Test(priority = 50)
    @Parameters({"param1"})
    public void getCheckContentLocationHeader(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n50." + tl.getCheckContentLocationHeader()[1]).append('\n');
        ps.append("Request:\n");

        final String resource =
                RestAssured.given()
    .auth().basic(this.username, this.password)
                        .header("Content-Type", "message/external-body; access-type=URL; URL=\"" + binary2 + "\"")
                        .when()
                        .post(host).asString();

        ps.append("Request method:\tGET\n");
        ps.append("Request URI:\t" + host);
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("\t\t\t\tContent-Type=message/external-body; access-type=URL; URL=\"" + binary2 + "\"\n\n");

        final Headers headers =
                RestAssured.given()
    .auth().basic(this.username, this.password)
                        .when()
                        .get(resource).getHeaders();

        if (resource.indexOf("http") == 0) {
            boolean isPresent = false;
            for (Header h : headers) {

                if ( h.getName().equals("Content-Location")) {
                    isPresent = true;
                }
            }

            if (!isPresent) {
                ps.append("Content-Location header was not sent in the response.");
                ps.append("\n -Case End- \n").close();
                throw new AssertionError("Content-Location header was not set in the response.");
            }
        } else {
            throw new SkipException("Skipping this exception");
        }

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.8-E
     * @param host
     */
    @Test(priority = 51)
    @Parameters({"param1"})
    public void headCheckContentLocationHeader(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n51." + tl.headCheckContentLocationHeader()[1]).append('\n');
        ps.append("Request:\n");

        final String resource =
                RestAssured.given()
    .auth().basic(this.username, this.password)
                        .header("Content-Type", "message/external-body; access-type=URL; URL=\"" + binary2 + "\"")
                        .when()
                        .post(host).asString();

        ps.append("Request method:\tHEAD\n");
        ps.append("Request URI:\t" + host);
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("\t\t\t\tContent-Type=message/external-body; access-type=URL; URL=\"" + binary2 + "\"\n\n");

        final Headers headers =
                RestAssured.given()
    .auth().basic(this.username, this.password)
                        .when()
                        .head(resource).getHeaders();

        if (resource.indexOf("http") == 0) {
            boolean isPresent = false;
            for (Header h : headers) {

                if (h.getName().equals("Content-Location")) {
                    isPresent = true;
                }
            }

            if (!isPresent) {
                ps.append("Content-Location header was not sent in the response.");
                ps.append("\n -Case End- \n").close();
                throw new AssertionError("Content-Location header was not set in the response.");
            }
        } else {
            throw new SkipException("Skipping this exception");
        }

        ps.append("\n -Case End- \n").close();
    }
}