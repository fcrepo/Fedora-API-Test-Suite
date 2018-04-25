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

import static org.hamcrest.Matchers.containsString;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fcrepo.spec.testsuite.TestSuiteGlobals;
import org.fcrepo.spec.testsuite.TestsLabels;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 *
 * @author Jorge Abrego, Fernando Cardoza
 */
public class ExternalBinaryContent {
    public static String body = "@prefix ldp: <http://www.w3.org/ns/ldp#> ."
                                + "@prefix dcterms: <http://purl.org/dc/terms/> ."
                                + "<> a ldp:Container, ldp:BasicContainer;"
                                + "dcterms:title 'External binary content class Container' ;"
                                + "dcterms:description 'This is a test container for the Fedora API Test Suite.' . ";
    public TestsLabels tl = new TestsLabels();
    public String username;
    public String password;

    /**
     * Authentication
     *
     * @param username
     * @param password
     */
    @Parameters({"param2", "param3"})
    public ExternalBinaryContent(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * 3.9-A PostCreate
     *
     * @param uri
     */
    @Test(priority = 45, groups = {"SHOULD"})
    @Parameters({"param1"})
    public void postCreateExternalBinaryContent(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n45." + tl.postCreateExternalBinaryContent()[1]).append('\n');
        ps.append("Request:\n");
        final String resource = RestAssured.given()
                                           .auth().basic(this.username, this.password)
                                           .header("Content-Disposition",
                                                   "attachment; filename=\"externalbinarycontentpostcreate.txt\"")
                                           .header("slug", "External-Binary-Content-3.9-A-PostCreate")
                                           .body("TestString.")
                                           .when()
                                           .post(uri).asString();

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .header("Content-Type", "message/external-body; access-type=URL; URL=\"" + resource + "\"")
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .post(uri)
                   .then()
                   .log().all()
                   .statusCode(201);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.9-A PutCreate
     *
     * @param uri
     */
    @Test(priority = 46, groups = {"SHOULD"})
    @Parameters({"param1"})
    public void putCreateExternalBinaryContent(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n46." + tl.putCreateExternalBinaryContent()[1]).append('\n');
        ps.append("Request:\n");
        final String resource = RestAssured.given()
                                           .auth().basic(this.username, this.password)
                                           .header("Content-Disposition",
                                                   "attachment; filename=\"externalbinarycontentputcreate.txt\"")
                                           .header("slug", "External-Binary-Content-3.9-A-PutCreate")
                                           .body("TestString.")
                                           .when()
                                           .post(uri).asString();

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .header("Content-Type", "message/external-body; access-type=URL; URL=\"" + resource + "\"")
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .put(uri)
                   .then()
                   .log().all()
                   .statusCode(201);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.9-A PutUpdate
     *
     * @param uri
     */
    @Test(priority = 47, groups = {"SHOULD"})
    @Parameters({"param1"})
    public void putUpdateExternalBinaryContent(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n47." + tl.putUpdateExternalBinaryContent()[1]).append('\n');
        ps.append("Request:\n");

        final String resource1 = RestAssured.given()
                                            .auth().basic(this.username, this.password)
                                            .header("Content-Disposition",
                                                    "attachment; filename=\"externalbinarycontentputupdate1.txt\"")
                                            .header("slug", "External-Binary-Content-3.9-A-PutUpdate1")
                                            .body("TestString1.")
                                            .when()
                                            .post(uri).asString();

        final String resource2 = RestAssured.given()
                                            .auth().basic(this.username, this.password)
                                            .header("Content-Disposition",
                                                    "attachment; filename=\"externalbinarycontentputupdate2.txt\"")
                                            .header("slug", "External-Binary-Content-3.9-A-PutUpdate2")
                                            .body("TestString2.")
                                            .when()
                                            .post(uri).asString();

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Type",
                                                     "message/external-body; access-type=URL; URL=\"" + resource1 +
                                                     "\"")
                                             .header("slug", "External-Binary-Content-3.9-A-PutUpdate3")
                                             .when()
                                             .post(uri);
        final String locationHeader = resource.getHeader("Location");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .header("Content-Type", "message/external-body; access-type=URL; URL=\"" + resource2 + "\"")
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
     * 3.9-B
     *
     * @param uri
     */
    @Test(priority = 48, groups = {"MUST"})
    @Parameters({"param1"})
    public void createExternalBinaryContentCheckAccesType(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n48." + tl.createExternalBinaryContentCheckAccesType()[1]).append('\n');
        ps.append("Request:\n");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .contentType("text/turtle")
                                             .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                                             .header("slug", "External-Binary-Content-3.9-B")
                                             .body(body)
                                             .when()
                                             .post(uri);
        final String locationHeader = resource.getHeader("Location");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .get(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(200).header("Accept-Post", containsString("access-type=URL"));

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.9-C
     *
     * @param uri
     */
    @Test(priority = 49, groups = {"MUST"})
    @Parameters({"param1"})
    public void postCheckUnsupportedMediaType(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n49." + tl.postCheckUnsupportedMediaType()[1]).append('\n');
        ps.append("Request:\n");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .header("Content-Type", "message/external-body; access-type=abc;"
                                           + " NAME=\"/some/file\"; site=\"example.com\"")
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .post(uri)
                   .then()
                   .log().all()
                   .statusCode(415);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.9-C
     *
     * @param uri
     */
    @Test(priority = 50, groups = {"MUST"})
    @Parameters({"param1"})
    public void putCheckUnsupportedMediaType(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n50." + tl.putCheckUnsupportedMediaType()[1]).append('\n');
        ps.append("Request:\n");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .header("Content-Type",
                           "message/external-body; access-type=abc; NAME=\"/some/file\"; site=\"example.com\"")
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .put(uri)
                   .then()
                   .log().all()
                   .statusCode(415);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.9-D
     *
     * @param uri
     */
    @Test(priority = 51, groups = {"MUST"})
    @Parameters({"param1"})
    public void checkUnsupportedMediaType(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n51." + tl.checkUnsupportedMediaType()[1]).append('\n');
        ps.append("Request:\n");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Disposition",
                                                     "attachment; filename=\"checkUnsupportedMediaType.txt\"")
                                             .header("slug", "External-Binary-Content-3.9-D")
                                             .body("TestString.")
                                             .when()
                                             .post(uri);
        final String locationHeader = resource.getHeader("Location");
        final Response res = RestAssured.given()
                                        .auth().basic(this.username, this.password)
                                        .header("Content-Type",
                                                "message/external-body; access-type=URL; URL=\"" + locationHeader +
                                                "\"")
                                        .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                                        .log().all()
                                        .when()
                                        .post(uri);

        ps.append(res.getStatusLine() + "\n");
        final Headers headers = res.getHeaders();
        for (Header h : headers) {
            ps.append(h.getName() + ": ");
            ps.append(h.getValue() + "\n");
        }
        ps.append("\n -Case End- \n").close();

        final String status = String.valueOf(res.getStatusCode());
        final char charStatus = status.charAt(0);
        if (charStatus != '2') {
            if (res.getStatusCode() == 415) {
                Assert.assertTrue(true, "OK");
            } else {
                Assert.assertTrue(false, "FAIL");
            }
        } else {
            Assert.assertTrue(true, "OK");
        }
    }

    /**
     * 3.9-E
     *
     * @param uri
     */
    @Test(priority = 52, groups = {"MUST NOT"})
    @Parameters({"param1"})
    public void postCheckHeaders(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n52." + tl.postCheckHeaders()[1]).append('\n');
        ps.append("Request:\n");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                                             .header("Content-Disposition",
                                                     "attachment; filename=\"testExamtxtpost.txt\"")
                                             .header("slug", "External-Binary-Content-3.9-E1")
                                             .body("TestString.")
                                             .when()
                                             .post(uri);

        ps.append(resource.getStatusLine() + "\n");
        final Headers headers = resource.getHeaders();
        for (Header h : headers) {
            ps.append(h.getName() + ": ");
            ps.append(h.getValue() + "\n");
        }
        final List<String> h1 = new ArrayList<>();
        for (Header h : headers) {
            h1.add(h.getName());
        }
        final Response exbcresource = RestAssured.given()
                                                 .auth().basic(this.username, this.password)
                                                 .header("Content-Disposition",
                                                         "attachment; filename=\"postCheckHeaders.txt\"")
                                                 .header("slug", "External-Binary-Content-3.9-E2")
                                                 .body("TestString.")
                                                 .when()
                                                 .post(uri);
        final String locationHeader = exbcresource.getHeader("Location");
        final Response res = RestAssured.given()
                                        .auth().basic(this.username, this.password)
                                        .header("Content-Type",
                                                "message/external-body; access-type=URL; URL=\"" + locationHeader +
                                                "\"")
                                        .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                                        .log().all()
                                        .when()
                                        .post(uri);

        ps.append(res.getStatusLine() + "\n");
        final Headers headersext = res.getHeaders();
        for (Header h : headersext) {
            ps.append(h.getName() + ": ");
            ps.append(h.getValue() + "\n");
        }


        final List<String> h2 = new ArrayList<>();
        for (Header h : headersext) {
            h2.add(h.getName());
        }

        final Set set1 = new HashSet(Arrays.asList(h1));
        final Set set2 = new HashSet(Arrays.asList(h2));

        if (set2.containsAll(set1)) {
            Assert.assertTrue(true, "OK");
        } else {
            Assert.assertTrue(false, "FAIL");
        }

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.9-E
     *
     * @param uri
     */
    @Test(priority = 53, groups = {"MUST NOT"})
    @Parameters({"param1"})
    public void putUpdateCheckHeaders(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n53." + tl.putUpdateCheckHeaders()[1]).append('\n');
        ps.append("Request:\n");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Disposition", "attachment; filename=\"testExamtxt.txt\"")
                                             .header("slug", "External-Binary-Content-3.9-E")
                                             .body("TestString.")
                                             .when()
                                             .post(uri);
        final String locationHeader = resource.getHeader("Location");
        final Response putup = RestAssured.given()
                                          .auth().basic(this.username, this.password)
                                          .header("Content-Disposition", "attachment; filename=\"putUpdatetext.txt\"")
                                          .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                                          .log().all()
                                          .when()
                                          .put(locationHeader);

        ps.append(putup.getStatusLine() + "\n");
        final Headers headers = putup.getHeaders();
        for (Header h : headers) {
            ps.append(h.getName() + ": ");
            ps.append(h.getValue() + "\n");
        }
        final List<String> h1 = new ArrayList<>();
        for (Header h : headers) {
            h1.add(h.getName());
        }

        final Response exbcresource1 = RestAssured.given()
                                                  .auth().basic(this.username, this.password)
                                                  .header("Content-Disposition",
                                                          "attachment; filename=\"putUpdateCheckHeaders1.txt\"")
                                                  .header("slug", "External-Binary-Content-3.9-E")
                                                  .body("TestString1.")
                                                  .when()
                                                  .post(uri);

        final Response exbcresource2 = RestAssured.given()
                                                  .auth().basic(this.username, this.password)
                                                  .header("Content-Disposition",
                                                          "attachment; filename=\"putUpdateCheckHeaders2.txt\"")
                                                  .header("slug", "External-Binary-Content-3.9-E")
                                                  .body("TestString2.")
                                                  .when()
                                                  .post(uri);

        final String locationHeader1 = exbcresource1.getHeader("Location");
        final String locationHeader2 = exbcresource2.getHeader("Location");

        final Response resourceext = RestAssured.given()
                                                .auth().basic(this.username, this.password)
                                                .header("Content-Type",
                                                        "message/external-body; access-type=URL; URL=\"" +
                                                        locationHeader1 + "\"")
                                                .when()
                                                .post(uri);
        final String locationHeader3 = resourceext.getHeader("Location");
        final Response resext = RestAssured.given()
                                           .auth().basic(this.username, this.password)
                                           .header("Content-Type",
                                                   "message/external-body; access-type=URL; URL=\"" + locationHeader2
                                                   + "\"")
                                           .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                                           .log().all()
                                           .when()
                                           .put(locationHeader3);

        ps.append(resext.getStatusLine() + "\n");
        final Headers headersext = resext.getHeaders();
        for (Header h : headersext) {
            ps.append(h.getName() + ": ");
            ps.append(h.getValue() + "\n");
        }

        final List<String> h2 = new ArrayList<>();
        for (Header h : headersext) {
            h2.add(h.getName());
        }

        final Set set1 = new HashSet(Arrays.asList(h1));
        final Set set2 = new HashSet(Arrays.asList(h2));

        if (set2.containsAll(set1)) {
            Assert.assertTrue(true, "OK");
        } else {
            Assert.assertTrue(false, "FAIL");
        }

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.9-F
     *
     * @param uri
     */
    @Test(priority = 54, groups = {"SHOULD"})
    @Parameters({"param1"})
    public void getCheckContentLocationHeader(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n54." + tl.getCheckContentLocationHeader()[1]).append('\n');
        ps.append("Request:\n");

        final Response exbcresource = RestAssured.given()
                                                 .auth().basic(this.username, this.password)
                                                 .header("Content-Disposition",
                                                         "attachment; filename=\"getCheckContentLocationHeader.txt\"")
                                                 .header("slug", "External-Binary-Content1-3.9-F")
                                                 .body("TestString1.")
                                                 .when()
                                                 .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Type",
                                                     "message/external-body; access-type=URL; URL=\"" +
                                                     locationHeader1 + "\"")
                                             .when()
                                             .post(uri);
        final String locationHeader2 = resource.getHeader("Location");

        ps.append("Request method:\tGET\n");
        ps.append("Request URI:\t" + uri);
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("\t\t\t\tContent-Type=message/external-body; access-type=URL; URL=\"" + locationHeader1 + "\"\n\n");

        final Headers headers = RestAssured.given()
                                           .auth().basic(this.username, this.password)
                                           .when()
                                           .get(locationHeader2).getHeaders();

        for (Header h : headers) {
            ps.append(h.getName() + ": ");
            ps.append(h.getValue() + "\n");
        }

        if (locationHeader2.indexOf("http") == 0) {
            boolean isValid = false;
            for (Header h : headers) {
                if (h.getName().equals("Content-location") && h.getValue() != " ") {
                    isValid = true;
                }
            }

            if (!isValid) {
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
     * 3.9-F
     *
     * @param uri
     */
    @Test(priority = 55, groups = {"SHOULD"})
    @Parameters({"param1"})
    public void headCheckContentLocationHeader(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n55." + tl.headCheckContentLocationHeader()[1]).append('\n');
        ps.append("Request:\n");

        final Response exbcresource = RestAssured.given()
                                                 .auth().basic(this.username, this.password)
                                                 .header("Content-Disposition",
                                                         "attachment; filename=\"headCheckContentLocationHeader.txt\"")
                                                 .header("slug", "External-Binary-Content2-3.9-F")
                                                 .body("TestString.")
                                                 .when()
                                                 .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Type",
                                                     "message/external-body; access-type=URL; URL=\"" +
                                                     locationHeader1 + "\"")
                                             .when()
                                             .post(uri);
        final String locationHeader2 = resource.getHeader("Location");

        ps.append("Request method:\tHEAD\n");
        ps.append("Request URI:\t" + uri);
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("\t\t\t\tContent-Type=message/external-body; access-type=URL; URL=\"" + locationHeader1 + "\"\n\n");

        final Headers headers = RestAssured.given()
                                           .auth().basic(this.username, this.password)
                                           .when()
                                           .head(locationHeader2).getHeaders();

        for (Header h : headers) {
            ps.append(h.getName() + ": ");
            ps.append(h.getValue() + "\n");
        }

        if (locationHeader2.indexOf("http") == 0) {
            boolean isValid = false;
            for (Header h : headers) {
                if (h.getName().equals("Content-Location") && h.getValue() != " ") {
                    isValid = true;
                }
            }

            if (!isValid) {
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
     * 3.9-G
     *
     * @param uri
     */
    @Test(priority = 56, groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestExternalBinaryContent(final String uri) throws FileNotFoundException {
        final String checksum = "md5";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n56." + tl.respondWantDigestExternalBinaryContent()[1]).append('\n');
        ps.append("Request:\n");

        final Response exbcresource = RestAssured.given()
                                                 .auth().basic(this.username, this.password)
                                                 .header("Content-Disposition",
                                                         "attachment; filename=\"respondWantDigest.txt\"")
                                                 .header("slug", "External-Binary-Content1-3.9-G")
                                                 .body("TestString.")
                                                 .when()
                                                 .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Type",
                                                     "message/external-body; access-type=URL; URL=\"" +
                                                     locationHeader1 + "\"")
                                             .when()
                                             .post(uri);
        final String locationHeader2 = resource.getHeader("Location");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .header("Want-Digest", checksum)
                   .when()
                   .get(locationHeader2)
                   .then()
                   .log().all()
                   .statusCode(200).header("Digest", containsString("md5"));

        ps.append("-Case End- \n").close();
    }

    /**
     * 3.9-G
     *
     * @param uri
     */
    @Test(priority = 57, groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestExternalBinaryContentHead(final String uri) throws FileNotFoundException {
        final String checksum = "md5";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n57." + tl.respondWantDigestExternalBinaryContentHead()[1]).append('\n');
        ps.append("Request:\n");

        final Response exbcresource = RestAssured.given()
                                                 .auth().basic(this.username, this.password)
                                                 .header("Content-Disposition",
                                                         "attachment; filename=\"respondWantDigestHead.txt\"")
                                                 .header("slug", "External-Binary-Content2-3.9-G")
                                                 .body("TestString.")
                                                 .when()
                                                 .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Type",
                                                     "message/external-body; access-type=URL; URL=\"" +
                                                     locationHeader1 + "\"")
                                             .when()
                                             .post(uri);
        final String locationHeader2 = resource.getHeader("Location");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .header("Want-Digest", checksum)
                   .when()
                   .head(locationHeader2)
                   .then()
                   .log().all()
                   .statusCode(200).header("Digest", containsString("md5"));

        ps.append("-Case End- \n").close();
    }

    /**
     * 3.9-H
     *
     * @param uri
     */
    @Test(priority = 58, groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestTwoSupportedExternalBinaryContent(final String uri) throws FileNotFoundException {
        final String checksum = "md5,sha";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n58." + tl.respondWantDigestTwoSupportedExternalBinaryContent()[1]).append('\n');
        ps.append("Request:\n");

        final Response exbcresource = RestAssured.given()
                                                 .auth().basic(this.username, this.password)
                                                 .header("Content-Disposition",
                                                         "attachment; filename=\"respondWantDigestTwoSupported.txt\"")
                                                 .header("slug", "External-Binary-Content1-3.9-H")
                                                 .body("TestString.")
                                                 .when()
                                                 .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Type",
                                                     "message/external-body; access-type=URL; URL=\"" +
                                                     locationHeader1 + "\"")
                                             .when()
                                             .post(uri);
        final String locationHeader2 = resource.getHeader("Location");

        final Response wantDigestResponse = RestAssured.given()
                                                       .auth().basic(this.username, this.password)
                                                       .config(RestAssured.config()
                                                                          .logConfig(new LogConfig().defaultStream(ps)))
                                                       .log().all()
                                                       .header("Want-Digest", checksum)
                                                       .when()
                                                       .get(locationHeader2);

        final Headers headers = wantDigestResponse.getHeaders();
        ps.append(wantDigestResponse.getStatusLine());

        for (Header h : headers) {
            ps.append(h.getName() + ": " + h.getValue() + "\n");
        }

        Assert.assertTrue(headers.getValue("Digest").contains("md5") ||
                          headers.getValue("Digest").contains("sha"), "OK");

        ps.append("-Case End- \n").close();
    }

    /**
     * 3.9-H
     *
     * @param uri
     */
    @Test(priority = 59, groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestTwoSupportedExternalBinaryContentHead(final String uri) throws FileNotFoundException {
        final String checksum = "md5,sha";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n59." + tl.respondWantDigestTwoSupportedExternalBinaryContentHead()[1]).append('\n');
        ps.append("Request:\n");

        final Response exbcresource = RestAssured.given()
                                                 .auth().basic(this.username, this.password)
                                                 .header("Content-Disposition",
                                                         "attachment; filename=\"respondWantDigestTwoSupportedHead" +
                                                         ".txt\"")
                                                 .header("slug", "External-Binary-Content2-3.9-H")
                                                 .body("TestString.")
                                                 .when()
                                                 .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Type",
                                                     "message/external-body; access-type=URL; URL=\"" +
                                                     locationHeader1 + "\"")
                                             .when()
                                             .post(uri);
        final String locationHeader2 = resource.getHeader("Location");

        final Response wantDigestResponse = RestAssured.given()
                                                       .auth().basic(this.username, this.password)
                                                       .config(RestAssured.config()
                                                                          .logConfig(new LogConfig().defaultStream(ps)))
                                                       .log().all()
                                                       .header("Want-Digest", checksum)
                                                       .when()
                                                       .head(locationHeader2);

        final Headers headers = wantDigestResponse.getHeaders();
        ps.append(wantDigestResponse.getStatusLine());

        for (Header h : headers) {
            ps.append(h.getName() + ": " + h.getValue() + "\n");
        }

        Assert.assertTrue(headers.getValue("Digest").contains("md5") ||
                          headers.getValue("Digest").contains("sha"), "OK");

        ps.append("-Case End- \n").close();
    }

    /**
     * 3.9-I
     *
     * @param uri
     */
    @Test(priority = 60, groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestTwoSupportedQvalueNonZeroExternalBinaryContent(final String uri)
        throws FileNotFoundException {
        final String checksum = "md5;q=0.3,sha;q=1";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n60." + tl.respondWantDigestTwoSupportedQvalueNonZeroExternalBinaryContent()[1]).append('\n');
        ps.append("Request:\n");

        final Response exbcresource = RestAssured.given()
                                                 .auth().basic(this.username, this.password)
                                                 .header("Content-Disposition",
                                                         "attachment; " +
                                                         "filename=\"respondWantDigestTwoSupportedQvalueNonZero.txt\"")
                                                 .header("slug", "External-Binary-Content1-3.9-I")
                                                 .body("TestString.")
                                                 .when()
                                                 .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Type",
                                                     "message/external-body; access-type=URL; URL=\"" +
                                                     locationHeader1 + "\"")
                                             .when()
                                             .post(uri);
        final String locationHeader2 = resource.getHeader("Location");

        final Response wantDigestResponse = RestAssured.given()
                                                       .auth().basic(this.username, this.password)
                                                       .config(RestAssured.config()
                                                                          .logConfig(new LogConfig().defaultStream(ps)))
                                                       .log().all()
                                                       .header("Want-Digest", checksum)
                                                       .when()
                                                       .get(locationHeader2);

        final Headers headers = wantDigestResponse.getHeaders();
        ps.append(wantDigestResponse.getStatusLine());

        for (Header h : headers) {
            ps.append(h.getName() + ": " + h.getValue() + "\n");
        }

        Assert.assertTrue(headers.getValue("Digest").contains("md5") ||
                          headers.getValue("Digest").contains("sha"), "OK");

        ps.append("-Case End- \n").close();
    }

    /**
     * 3.9-I
     *
     * @param uri
     */
    @Test(priority = 61, groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestTwoSupportedQvalueNonZeroExternalBinaryContentHead(final String uri)
        throws FileNotFoundException {
        final String checksum = "md5;q=0.3,sha;q=1";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n61." + tl.respondWantDigestTwoSupportedQvalueNonZeroExternalBinaryContentHead()[1]).append('\n');
        ps.append("Request:\n");

        final Response exbcresource = RestAssured.given()
                                                 .auth().basic(this.username, this.password)
                                                 .header("Content-Disposition",
                                                         "attachment; " +
                                                         "filename=\"respondWantDigestTwoSupportedQvalueNonZero.txt\"")
                                                 .header("slug", "External-Binary-Content2-3.9-I")
                                                 .body("TestString.")
                                                 .when()
                                                 .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Type",
                                                     "message/external-body; access-type=URL; URL=\"" +
                                                     locationHeader1 + "\"")
                                             .when()
                                             .post(uri);
        final String locationHeader2 = resource.getHeader("Location");

        final Response wantDigestResponse = RestAssured.given()
                                                       .auth().basic(this.username, this.password)
                                                       .config(RestAssured.config()
                                                                          .logConfig(new LogConfig().defaultStream(ps)))
                                                       .log().all()
                                                       .header("Want-Digest", checksum)
                                                       .when()
                                                       .head(locationHeader2);

        final Headers headers = wantDigestResponse.getHeaders();
        ps.append(wantDigestResponse.getStatusLine());

        for (Header h : headers) {
            ps.append(h.getName() + ": " + h.getValue() + "\n");
        }

        Assert.assertTrue(headers.getValue("Digest").contains("md5") ||
                          headers.getValue("Digest").contains("sha"), "OK");

        ps.append("-Case End- \n").close();
    }

    /**
     * 3.9-J
     *
     * @param uri
     */
    @Test(priority = 62, groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestNonSupportedExternalBinaryContent(final String uri)
        throws FileNotFoundException {
        final String checksum = "md5,abc";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n62." + tl.respondWantDigestNonSupportedExternalBinaryContent()[1]).append('\n');
        ps.append("Request:\n");

        final Response exbcresource = RestAssured.given()
                                                 .auth().basic(this.username, this.password)
                                                 .header("Content-Disposition",
                                                         "attachment; filename=\"respondWantDigestNonSupported.txt\"")
                                                 .header("slug", "External-Binary-Content1-3.9-J")
                                                 .body("TestString.")
                                                 .when()
                                                 .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Type",
                                                     "message/external-body; access-type=URL; URL=\"" +
                                                     locationHeader1 + "\"")
                                             .when()
                                             .post(uri);
        final String locationHeader2 = resource.getHeader("Location");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .header("Want-Digest", checksum)
                   .when()
                   .get(locationHeader2)
                   .then()
                   .log().all()
                   .statusCode(200).header("Digest", containsString("md5"));

        ps.append("-Case End- \n").close();
    }

    /**
     * 3.9-J
     *
     * @param uri
     */
    @Test(priority = 63, groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestNonSupportedExternalBinaryContentHead(final String uri)
        throws FileNotFoundException {
        final String checksum = "md5,abc";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n62." + tl.respondWantDigestNonSupportedExternalBinaryContentHead()[1]).append('\n');
        ps.append("Request:\n");

        final Response exbcresource = RestAssured.given()
                                                 .auth().basic(this.username, this.password)
                                                 .header("Content-Disposition",
                                                         "attachment; filename=\"respondWantDigestNonSupportedHead" +
                                                         ".txt\"")
                                                 .header("slug", "External-Binary-Content2-3.9-J")
                                                 .body("TestString.")
                                                 .when()
                                                 .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Type",
                                                     "message/external-body; access-type=URL; URL=\"" +
                                                     locationHeader1 + "\"")
                                             .when()
                                             .post(uri);
        final String locationHeader2 = resource.getHeader("Location");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .header("Want-Digest", checksum)
                   .when()
                   .head(locationHeader2)
                   .then()
                   .log().all()
                   .statusCode(200).header("Digest", containsString("md5"));

        ps.append("-Case End- \n").close();
    }
}
