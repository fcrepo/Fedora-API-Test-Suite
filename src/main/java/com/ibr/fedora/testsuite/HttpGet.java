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

import static org.hamcrest.Matchers.containsString;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import com.ibr.fedora.TestSuiteGlobals;
import com.ibr.fedora.TestsLabels;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class HttpGet {
    public static String body = "@prefix ldp: <http://www.w3.org/ns/ldp#> ."
                                + "@prefix dcterms: <http://purl.org/dc/terms/> ."
                                + "<> a ldp:Container, ldp:BasicContainer;"
                                + "dcterms:title 'Get class Container' ;"
                                + "dcterms:description 'This is a test container for the Fedora API Test Suite.' . ";
    public String username;
    public String password;
    public TestsLabels tl = new TestsLabels();

    /**
     * Authentication
     *
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
     * 3.2.1-A
     *
     * @param uri
     */
    @Test(priority = 7)
    @Parameters({"param1"})
    public void additionalValuesForPreferHeader(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n7." + tl.additionalValuesForPreferHeader()[1]).append("\n");
        ps.append("Request:\n");
        final Response resource =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                       .contentType("text/turtle")
                       .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                       .header("slug", "Get-3.2.1-A")
                       .body(body)
                       .when()
                       .post(uri);
        final String locationHeader = resource.getHeader("Location");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .header("Prefer", "return=representation; "
                                     + "include=\"http://fedora.info/definitions/fcrepo#PreferInboundReferences\"")
                   .when()
                   .get(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(200).header("preference-applied",
                                           containsString(
                                               "http://fedora.info/definitions/fcrepo#PreferInboundReferences"));

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.2.2-A
     *
     * @param uri
     */
    @Test(priority = 8)
    @Parameters({"param1"})
    public void responsePreferenceAppliedHeader(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n8." + tl.responsePreferenceAppliedHeader()[1]).append("\n");
        ps.append("Request:\n");
        final Response resource =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                       .contentType("text/turtle")
                       .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                       .header("slug", "Get-3.2.2-A")
                       .body(body)
                       .when()
                       .post(uri);
        final String locationHeader = resource.getHeader("Location");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .header("Prefer", "return=minimal")
                   .when()
                   .get(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(200).header("preference-applied", containsString("return=minimal"));

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.2.2-B
     *
     * @param uri
     */
    @Test(priority = 9)
    @Parameters({"param1"})
    public void responseDescribesHeader(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n9." + tl.responseDescribesHeader()[1] + "-" + tl.responseDescribesHeader()[1]).append("\n");
        ps.append("Request:\n");
        final Response resource =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                       .header("Content-Disposition", "attachment; filename=\"responseDescribesHeader.txt\"")
                       .header("slug", "Get-3.2.2-B")
                       .body("TestString")
                       .when()
                       .post(uri);
        final String locationHeader = resource.getHeader("Location");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .get(locationHeader + "/fcr:metadata")
                   .then()
                   .log().all()
                   .statusCode(200).header("Link", containsString("describes"));

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.2.3-A
     *
     * @param uri
     */
    @Test(priority = 10)
    @Parameters({"param1"})
    public void respondWantDigest(final String uri) throws FileNotFoundException {
        final String checksum = "md5";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n10." + tl.respondWantDigest()[1]).append('\n');
        ps.append("Request:\n");

        final Response resource =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                       .header("Content-Disposition", "attachment; filename=\"respondwantdigest.txt\"")
                       .header("slug", "Get-3.2.3-A")
                       .body("TestString")
                       .when()
                       .post(uri);
        final String locationHeader = resource.getHeader("Location");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .header("Want-Digest", checksum)
                   .when()
                   .get(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(200).header("Digest", containsString("md5"));


        ps.append("-Case End- \n").close();
    }

    /**
     * 3.2.3-B
     *
     * @param uri
     */
    @Test(priority = 11)
    @Parameters({"param1"})
    public void respondWantDigestTwoSupported(final String uri) throws FileNotFoundException {
        final String checksum = "md5,sha";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n11." + tl.respondWantDigestTwoSupported()[1]).append('\n');
        ps.append("Request:\n");

        final Response resource =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                       .header("Content-Disposition", "attachment; filename=\"wantdigestTwoSupported.txt\"")
                       .header("slug", "Get-3.2.3-B")
                       .body("TestString")
                       .when()
                       .post(uri);
        final String locationHeader = resource.getHeader("Location");

        final Response wantDigestResponse = RestAssured.given()
                                                       .auth().basic(this.username, this.password)
                                                       .config(RestAssured.config()
                                                                          .logConfig(new LogConfig().defaultStream(ps)))
                                                       .log().all()
                                                       .header("Want-Digest", checksum)
                                                       .when()
                                                       .get(locationHeader);

        final Headers headers = wantDigestResponse.getHeaders();
        ps.append(wantDigestResponse.getStatusLine());

        for (Header h : headers) {
            ps.append(h.getName() + ": " + h.getValue() + "\n");
        }

        Assert
            .assertTrue(headers.getValue("Digest").contains("md5") || headers.getValue("Digest").contains("sha"), "OK");

        ps.append("-Case End- \n").close();
    }

    /**
     * 3.2.3-C
     *
     * @param uri
     */
    @Test(priority = 12)
    @Parameters({"param1"})
    public void respondWantDigestTwoSupportedQvalueNonZero(final String uri) throws FileNotFoundException {
        final String checksum = "md5;q=0.3,sha;q=1";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n12." + tl.respondWantDigestTwoSupportedQvalueNonZero()[1]).append('\n');
        ps.append("Request:\n");

        final Response resource =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                       .header("Content-Disposition",
                               "attachment; filename=\"wantdigestTwoSupportedQvalueNonZero.txt\"")
                       .header("slug", "Get-3.2.3-C")
                       .body("TestString")
                       .when()
                       .post(uri);
        final String locationHeader = resource.getHeader("Location");
        final Response wantDigestResponse = RestAssured.given()
                                                       .auth().basic(this.username, this.password)
                                                       .config(RestAssured.config()
                                                                          .logConfig(new LogConfig().defaultStream(ps)))
                                                       .log().all()
                                                       .header("Want-Digest", checksum)
                                                       .when()
                                                       .get(locationHeader);

        final Headers headers = wantDigestResponse.getHeaders();
        ps.append(wantDigestResponse.getStatusLine());

        for (Header h : headers) {
            ps.append(h.getName() + ": " + h.getValue() + "\n");
        }

        Assert
            .assertTrue(headers.getValue("Digest").contains("md5") || headers.getValue("Digest").contains("sha"), "OK");

        ps.append("-Case End- \n").close();
    }

    /**
     * 3.2.3-D
     *
     * @param uri
     */
    @Test(priority = 13)
    @Parameters({"param1"})
    public void respondWantDigestTwoSupportedQvalueZero(final String uri) throws FileNotFoundException {
        final String checksum = "md5;q=0.3,sha;q=0";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n13." + tl.respondWantDigestTwoSupportedQvalueZero()[1]).append('\n');
        ps.append("Request:\n");

        final Response resource =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                       .header("Content-Disposition", "attachment; filename=\"wantDigestTwoSupportedQvalueZero.txt\"")
                       .header("slug", "Get-3.2.3-D")
                       .body("TestString")
                       .when()
                       .post(uri);
        final String locationHeader = resource.getHeader("Location");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .header("Want-Digest", checksum)
                   .when()
                   .get(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(200).header("Digest", containsString("md5"));


        ps.append("-Case End- \n").close();
    }

    /**
     * 3.2.3-E
     *
     * @param uri
     */
    @Test(priority = 14)
    @Parameters({"param1"})
    public void respondWantDigestNonSupported(final String uri) throws FileNotFoundException {
        final String checksum = "md5,abc";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n14." + tl.respondWantDigestTwoSupportedQvalueZero()[1]).append('\n');
        ps.append("Request:\n");

        final Response resource =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                       .header("Content-Disposition", "attachment; filename=\"wantDigestNonSupported.txt\"")
                       .header("slug", "Get-3.2.3-E")
                       .body("TestString")
                       .when()
                       .post(uri);
        final String locationHeader = resource.getHeader("Location");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .header("Want-Digest", checksum)
                   .when()
                   .get(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(200).header("Digest", containsString("md5"));


        ps.append("-Case End- \n").close();
    }


}
