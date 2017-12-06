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

public class HttpGet {
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
     * 3.2
     * @param host
     */
    @Test(priority = 6)
    @Parameters({"param1"})
    public void responseDescribesHeader(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n6." + tl.responseDescribesHeader()[1] + "-" + tl.responseDescribesHeader()[1]).append("\n");
        ps.append("Request:\n");
        final String resource =
            RestAssured.given()
    .auth().basic(this.username, this.password)
                .header("Content-Disposition", "attachment; filename=\"responseDescribesHeader.txt\"")
                .when()
                .post(host).asString();
        RestAssured.given()
    .auth().basic(this.username, this.password)
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .when()
            .get(resource + "/fcr:metadata")
            .then()
            .log().all()
            .statusCode(200).header("Link", containsString("describes"));

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.2.1-A
     * @param host
     */
    @Test(priority = 7)
    @Parameters({"param1"})
    public void additionalValuesForPreferHeader(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n7." + tl.additionalValuesForPreferHeader()[1]).append("\n");
        ps.append("Request:\n");
        final String resource =
            RestAssured.given()
    .auth().basic(this.username, this.password)
                .contentType("text/turtle")
                .when()
                .post(host).asString();
        RestAssured.given()
    .auth().basic(this.username, this.password)
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .header("Prefer", "return=representation; "
            + "include=\"http://fedora.info/definitions/v4/repository#InboundReferences\"")
            .when()
            .get(resource)
            .then()
            .log().all()
            .statusCode(200).header("preference-applied",
            containsString("http://fedora.info/definitions/v4/repository#InboundReferences"));

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.2.2
     * @param host
     */
    @Test(priority = 8)
    @Parameters({"param1"})
    public void responsePreferenceAppliedHeader(final String host) throws FileNotFoundException {
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n8." + tl.responsePreferenceAppliedHeader()[1]).append("\n");
    ps.append("Request:\n");
    final String resource =
        RestAssured.given()
    .auth().basic(this.username, this.password)
            .contentType("text/turtle")
            .when()
            .post(host).asString();
    RestAssured.given()
    .auth().basic(this.username, this.password)
        .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
        .log().all()
        .header("Prefer", "return=minimal")
        .when()
        .get(resource)
        .then()
        .log().all()
        .statusCode(200).header("preference-applied", containsString("return=minimal"));

    ps.append("\n -Case End- \n").close();
      }

    /**
     * 3.2.3-A
     * @param host
     */
    @Test(priority = 9)
    @Parameters({"param1"})
    public void respondWantDigest(final String host) throws FileNotFoundException {
        final String checksum = "md5";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n9." + tl.respondWantDigest()[1]).append('\n');
        ps.append("Request:\n");

    final String resource =
            RestAssured.given()
            .auth().basic(this.username, this.password)
                    .header("Content-Disposition", "attachment; filename=\"respondwantdigest.txt\"")
                    .body("TestString.")
                    .when()
                    .post(host).asString();

        RestAssured.given()
        .auth().basic(this.username, this.password)
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .header("Want-Digest",checksum)
            .when()
            .get(resource)
            .then()
            .log().all()
            .statusCode(200).header("Digest", containsString("md5"));


           ps.append("-Case End- \n").close();
       }

    /**
     * 3.2.3-B
     * @param host
     */
    @Test(priority = 10)
    @Parameters({"param1"})
    public void respondWantDigestTwoSupported(final String host) throws FileNotFoundException {
        final String checksum = "md5,sha1";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n10." + tl.respondWantDigestTwoSupported()[1]).append('\n');
        ps.append("Request:\n");

    final String resource =
            RestAssured.given()
            .auth().basic(this.username, this.password)
                    .header("Content-Disposition", "attachment; filename=\"wantdigestTwoSupported.txt\"")
                    .body("TestString.")
                    .when()
                    .post(host).asString();

        RestAssured.given()
        .auth().basic(this.username, this.password)
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .header("Want-Digest",checksum)
            .when()
            .get(resource)
            .then()
            .log().all()
            .statusCode(200).header("Digest", containsString("md5")).and().header("Digest", containsString("sha1"));


           ps.append("-Case End- \n").close();
       }

    /**
     * 3.2.3-C
     * @param host
     */
    @Test(priority = 11)
    @Parameters({"param1"})
    public void respondWantDigestTwoSupportedQvalueNonZero(final String host) throws FileNotFoundException {
        final String checksum = "md5;q=0.3,sha1;q=1";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n11." + tl.respondWantDigestTwoSupportedQvalueNonZero()[1]).append('\n');
        ps.append("Request:\n");

    final String resource =
            RestAssured.given()
            .auth().basic(this.username, this.password)
                    .header("Content-Disposition", "attachment; filename=\"wantdigestTwoSupportedQvalueNonZero.txt\"")
                    .body("TestString.")
                    .when()
                    .post(host).asString();

        RestAssured.given()
        .auth().basic(this.username, this.password)
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .header("Want-Digest",checksum)
            .when()
            .get(resource)
            .then()
            .log().all()
            .statusCode(200).header("Digest", containsString("md5")).and().header("Digest", containsString("sha1"));


           ps.append("-Case End- \n").close();
       }

    /**
     * 3.2.3-D
     * @param host
     */
    @Test(priority = 12)
    @Parameters({"param1"})
    public void respondWantDigestTwoSupportedQvalueZero(final String host) throws FileNotFoundException {
        final String checksum = "md5;q=0.3,sha1;q=0";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n12." + tl.respondWantDigestTwoSupportedQvalueZero()[1]).append('\n');
        ps.append("Request:\n");

    final String resource =
            RestAssured.given()
            .auth().basic(this.username, this.password)
                    .header("Content-Disposition", "attachment; filename=\"wantDigestTwoSupportedQvalueZero.txt\"")
                    .body("TestString.")
                    .when()
                    .post(host).asString();

        RestAssured.given()
        .auth().basic(this.username, this.password)
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .header("Want-Digest",checksum)
            .when()
            .get(resource)
            .then()
            .log().all()
            .statusCode(200).header("Digest", containsString("md5"));


           ps.append("-Case End- \n").close();
       }

    /**
     * 3.2.3-E
     * @param host
     */
    @Test(priority = 13)
    @Parameters({"param1"})
    public void respondWantDigestNonSupported(final String host) throws FileNotFoundException {
        final String checksum = "md5,abc";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n13." + tl.respondWantDigestTwoSupportedQvalueZero()[1]).append('\n');
        ps.append("Request:\n");

    final String resource =
            RestAssured.given()
            .auth().basic(this.username, this.password)
                    .header("Content-Disposition", "attachment; filename=\"wantDigestNonSupported.txt\"")
                    .body("TestString.")
                    .when()
                    .post(host).asString();

        RestAssured.given()
        .auth().basic(this.username, this.password)
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .header("Want-Digest",checksum)
            .when()
            .get(resource)
            .then()
            .log().all()
            .statusCode(200).header("Digest", containsString("md5"));


           ps.append("-Case End- \n").close();
       }


    }