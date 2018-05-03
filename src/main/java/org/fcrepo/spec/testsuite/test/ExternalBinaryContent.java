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

import static org.fcrepo.spec.testsuite.test.Constants.CONTENT_DISPOSITION;
import static org.fcrepo.spec.testsuite.test.Constants.DIGEST;
import static org.fcrepo.spec.testsuite.test.Constants.SLUG;
import static org.hamcrest.Matchers.containsString;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class ExternalBinaryContent extends AbstractTest {

    /**
     * Authentication
     *
     * @param username
     * @param password
     */
    @Parameters({"param2", "param3"})
    public ExternalBinaryContent(final String username, final String password) {
        super(username, password);
    }

    /**
     * 3.9-A PostCreate
     *
     * @param uri
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void postCreateExternalBinaryContent(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.9-A", "postCreateExternalBinaryContent",
                                        "Fedora servers should support the creation of LDP-NRs with Content-Type "
                                        + "of message/external-body and"
                                        + " access-type parameter of url.",
                                        "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
        final String resource = createRequest().header(CONTENT_DISPOSITION,
                                                       "attachment; filename=\"externalbinarycontentpostcreate.txt\"")
                                               .header(SLUG, info.getId())
                                               .body("TestString.")
                                               .when()
                                               .post(uri).asString();

        createRequest().contentType( "message/external-body; access-type=URL; URL=\"" + resource + "\"")
                       .when()
                       .post(uri)
                       .then()
                       .log().all()
                       .statusCode(201);

    }

    /**
     * 3.9-A PutCreate
     *
     * @param uri
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void putCreateExternalBinaryContent(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.9-A", "putCreateExternalBinaryContent",
                                        "Fedora servers should support the creation of LDP-NRs with Content-Type "
                                        + "of message/external-body and"
                                        + " access-type parameter of url.",
                                        "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
        final String resource = createRequest().header(CONTENT_DISPOSITION,
                                                       "attachment; filename=\"externalbinarycontentputcreate.txt\"")
                                               .header(SLUG, info.getId())
                                               .body("TestString.")
                                               .when()
                                               .post(uri).asString();

        createRequest().contentType( "message/external-body; access-type=URL; URL=\"" + resource + "\"")
                       .when()
                       .put(uri)
                       .then()
                       .log().all()
                       .statusCode(201);

    }

    /**
     * 3.9-A PutUpdate
     *
     * @param uri
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void putUpdateExternalBinaryContent(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.9-A", "putUpdateExternalBinaryContent",
                                        "Fedora servers should support the creation of LDP-NRs with Content-Type "
                                        + "of message/external-body and"
                                        + " access-type parameter of url.",
                                        "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String resource1 = createRequest().header(CONTENT_DISPOSITION,
                                                        "attachment; filename=\"externalbinarycontentputupdate1.txt\"")
                                                .header(SLUG, info.getId())
                                                .body("TestString1.")
                                                .when()
                                                .post(uri).asString();

        final String resource2 = createRequest().header(CONTENT_DISPOSITION,
                                                        "attachment; filename=\"externalbinarycontentputupdate2.txt\"")
                                                .header(SLUG, info.getId() + "-PutUpdate2")
                                                .body("TestString2.")
                                                .when()
                                                .post(uri).asString();

        final Response resource = createRequest().contentType(
                                                         "message/external-body; access-type=URL; URL=\"" + resource1 +
                                                         "\"")
                                                 .header(SLUG, info.getId() + "PutUpdate3")
                                                 .when()
                                                 .post(uri);
        final String locationHeader = resource.getHeader("Location");
        createRequest().contentType( "message/external-body; access-type=URL; URL=\"" + resource2 + "\"")
                       .when()
                       .put(locationHeader)
                       .then()
                       .log().all()
                       .statusCode(204);

    }

    /**
     * 3.9-B
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void createExternalBinaryContentCheckAccesType(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.9-B", "createExternalBinaryContentCheckAccesType",
                                        "Fedora servers must advertise support in the Accept-Post response header for" +
                                        " each supported access-type "
                                        + " parameter value of Content-Type: message/external-body.",
                                        "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = resource.getHeader("Location");
        createRequest().when()
                       .get(locationHeader)
                       .then()
                       .log().all()
                       .statusCode(200).header("Accept-Post", containsString("access-type=URL"));

    }

    /**
     * 3.9-C
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void postCheckUnsupportedMediaType(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.9-C", "postCheckUnsupportedMediaType",
                                        "Fedora servers receiving requests that would create or update a LDP-NR with "
                                        + "a message/external-body with an "
                                        +
                                        "unsupported type parameter must respond with HTTP 415 UNSUPPORTED MEDIA TYPE. "
                                        + "In the case that a Fedora"
                                        +
                                        " server does not support external LDP-NR content, all message/external-body " +
                                        "messages must be rejected"
                                        + " with HTTP 415.",
                                        "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        createRequest().contentType( "message/external-body; access-type=abc;"
                                               + " NAME=\"/some/file\"; site=\"example.com\"")
                       .when()
                       .post(uri)
                       .then()
                       .log().all()
                       .statusCode(415);

    }

    /**
     * 3.9-C
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void putCheckUnsupportedMediaType(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.9-C", "putCheckUnsupportedMediaType",
                                        "Fedora servers receiving requests that would create or update a LDP-NR with a "
                                        + "message/external-body with an "
                                        +
                                        "unsupported type parameter must respond with HTTP 415 UNSUPPORTED MEDIA TYPE" +
                                        ". In the case that a Fedora"
                                        +
                                        " server does not support external LDP-NR content, all message/external-body " +
                                        "messages must be rejected"
                                        + " with HTTP 415.",
                                        "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        createRequest().contentType(
                               "message/external-body; access-type=abc; NAME=\"/some/file\"; site=\"example.com\"")
                       .when()
                       .put(uri)
                       .then()
                       .log().all()
                       .statusCode(415);

    }

    /**
     * 3.9-D
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void checkUnsupportedMediaType(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.9-D", "checkUnsupportedMediaType",
                                        "In the case that a Fedora server does not support external LDP-NR content, "
                                        +
                                        "all message/external-body messages must be rejected with 415 (Unsupported " +
                                        "Media Type).",
                                        "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final Response resource = createRequest().header(CONTENT_DISPOSITION,
                                                         "attachment; filename=\"checkUnsupportedMediaType.txt\"")
                                                 .header(SLUG, info.getId())
                                                 .body("TestString.")
                                                 .when()
                                                 .post(uri);
        final String locationHeader = resource.getHeader("Location");
        final Response res = createRequest().contentType(
                                                    "message/external-body; access-type=URL; URL=\"" + locationHeader +
                                                    "\"")
                                            .when()
                                            .post(uri);

        ps.append(res.getStatusLine() + "\n");
        final Headers headers = res.getHeaders();
        for (Header h : headers) {
            ps.append(h.getName() + ": ");
            ps.append(h.getValue() + "\n");
        }

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
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void postCheckHeaders(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.9-E", "postCheckHeaders",
                                        "Fedora servers receiving requests that would create or update an LDP-NR with" +
                                        " Content-Type: "
                                        +
                                        "message/external-body must not accept the request if it cannot guarantee all. "
                                        +
                                        "of the response headers required by the LDP-NR interaction model in this " +
                                        "specification.",
                                        "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
        final Response resource = createRequest().header(CONTENT_DISPOSITION,
                                                         "attachment; filename=\"testExamtxtpost.txt\"")
                                                 .header(SLUG, "External-Binary-Content-3.9-E1")
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
        final Response exbcresource = createRequest().header(CONTENT_DISPOSITION,
                                                             "attachment; filename=\"postCheckHeaders.txt\"")
                                                     .header(SLUG, "External-Binary-Content-3.9-E2")
                                                     .body("TestString.")
                                                     .when()
                                                     .post(uri);
        final String locationHeader = exbcresource.getHeader("Location");
        final Response res = createRequest().contentType(
                                                    "message/external-body; access-type=URL; URL=\"" + locationHeader +
                                                    "\"")
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

    }

    /**
     * 3.9-E
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void putUpdateCheckHeaders(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.9-E", "putUpdateCheckHeaders",
                                        "Fedora servers receiving requests that would create or update an LDP-NR with" +
                                        " Content-Type: "
                                        +
                                        "message/external-body must not accept the request if it cannot guarantee all. "
                                        +
                                        "of the response headers required by the LDP-NR interaction model in this " +
                                        "specification.",
                                        "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final Response resource =
            createRequest().header(CONTENT_DISPOSITION, "attachment; filename=\"testExamtxt.txt\"")
                           .header(SLUG, info.getId())
                           .body("TestString.")
                           .when()
                           .post(uri);
        final String locationHeader = resource.getHeader("Location");
        final Response putup =
            createRequest().header(CONTENT_DISPOSITION, "attachment; filename=\"putUpdatetext.txt\"")
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

        final Response exbcresource1 = createRequest().header(CONTENT_DISPOSITION,
                                                              "attachment; filename=\"putUpdateCheckHeaders1.txt\"")
                                                      .header(SLUG, info.getId())
                                                      .body("TestString1.")
                                                      .when()
                                                      .post(uri);

        final Response exbcresource2 = createRequest().header(CONTENT_DISPOSITION,
                                                              "attachment; filename=\"putUpdateCheckHeaders2.txt\"")
                                                      .header(SLUG, info.getId())
                                                      .body("TestString2.")
                                                      .when()
                                                      .post(uri);

        final String locationHeader1 = exbcresource1.getHeader("Location");
        final String locationHeader2 = exbcresource2.getHeader("Location");

        final Response resourceext = createRequest().contentType(
                                                            "message/external-body; access-type=URL; URL=\"" +
                                                            locationHeader1 + "\"")
                                                    .when()
                                                    .post(uri);
        final String locationHeader3 = resourceext.getHeader("Location");
        final Response resext = createRequest().contentType(
                                                       "message/external-body; access-type=URL; URL=\"" +
                                                       locationHeader2
                                                       + "\"")
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

    }

    /**
     * 3.9-F
     *
     * @param uri
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void getCheckContentLocationHeader(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.9-F", "getCheckContentLocationHeader",
                                        "GET and HEAD responses for any external LDP-NR should include a " +
                                        "Content-Location header with a URI  "
                                        +
                                        "representation of the location of the external content if the Fedora server " +
                                        "is proxying the content.",
                                        "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
        final Response exbcresource = createRequest().header(CONTENT_DISPOSITION,
                                                             "attachment; filename=\"getCheckContentLocationHeader" +
                                                             ".txt\"")
                                                     .header(SLUG, info.getId())
                                                     .body("TestString1.")
                                                     .when()
                                                     .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = createRequest().contentType(
                                                         "message/external-body; access-type=URL; URL=\"" +
                                                         locationHeader1 + "\"")
                                                 .when()
                                                 .post(uri);
        final String locationHeader2 = resource.getHeader("Location");

        ps.append("Request method:\tGET\n");
        ps.append("Request URI:\t" + uri);
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("\t\t\t\tContent-Type=message/external-body; access-type=URL; URL=\"" + locationHeader1 + "\"\n\n");

        final Headers headers = doGet(locationHeader2).getHeaders();

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

                throw new AssertionError("Content-Location header was not set in the response.");
            }
        } else {
            throw new SkipException("Skipping this exception");
        }

    }

    /**
     * 3.9-F
     *
     * @param uri
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void headCheckContentLocationHeader(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.9-F", "headCheckContentLocationHeader",
                                        "GET and HEAD responses for any external LDP-NR should include a " +
                                        "Content-Location header with a URI  "
                                        +
                                        "representation of the location of the external content if the Fedora server " +
                                        "is proxying the content.",
                                        "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final Response exbcresource = createRequest().header(CONTENT_DISPOSITION,
                                                             "attachment; filename=\"headCheckContentLocationHeader" +
                                                             ".txt\"")
                                                     .header(SLUG, info.getId())
                                                     .body("TestString.")
                                                     .when()
                                                     .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = createRequest().contentType(
                                                         "message/external-body; access-type=URL; URL=\"" +
                                                         locationHeader1 + "\"")
                                                 .when()
                                                 .post(uri);
        final String locationHeader2 = resource.getHeader("Location");

        ps.append("Request method:\tHEAD\n");
        ps.append("Request URI:\t" + uri);
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("\t\t\t\tContent-Type=message/external-body; access-type=URL; URL=\"" + locationHeader1 + "\"\n\n");

        final Headers headers = createRequest().when()
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

                throw new AssertionError("Content-Location header was not set in the response.");
            }
        } else {
            throw new SkipException("Skipping this exception");
        }

    }

    /**
     * 3.9-G-1
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestExternalBinaryContent(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.9-G-1", "respondWantDigestExternalBinaryContent",
                                        "GET and HEAD requests to any external LDP-NR must correctly respond to the "
                                        + "Want-Digest header defined in [RFC3230].",
                                        "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String checksum = "md5";
        final Response exbcresource = createRequest().header(CONTENT_DISPOSITION,
                                                             "attachment; filename=\"respondWantDigest.txt\"")
                                                     .header(SLUG, info.getId())
                                                     .body("TestString.")
                                                     .when()
                                                     .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = createRequest().contentType(
                                                         "message/external-body; access-type=URL; URL=\"" +
                                                         locationHeader1 + "\"")
                                                 .when()
                                                 .post(uri);
        final String locationHeader2 = resource.getHeader("Location");

        createRequest().header("Want-Digest", checksum)
                       .when()
                       .get(locationHeader2)
                       .then()
                       .log().all()
                       .statusCode(200).header(DIGEST, containsString("md5"));

    }

    /**
     * 3.9-G-2
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestExternalBinaryContentHead(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.9-G-2", "respondWantDigestExternalBinaryContentHead",
                                        "GET and HEAD requests to any external LDP-NR must correctly respond to the "
                                        + "Want-Digest header defined in [RFC3230].",
                                        "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String checksum = "md5";
        final Response exbcresource = createRequest().header(CONTENT_DISPOSITION,
                                                             "attachment; filename=\"respondWantDigestHead.txt\"")
                                                     .header(SLUG, info.getId())
                                                     .body("TestString.")
                                                     .when()
                                                     .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = createRequest().contentType(
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
                   .statusCode(200).header(DIGEST, containsString("md5"));

    }

    /**
     * 3.9-H-1
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestTwoSupportedExternalBinaryContent(final String uri) throws FileNotFoundException {
        final TestInfo info =
            setupTest("3.9-H-1", "respondWantDigestTwoSupportedExternalBinaryContent",
                      "GET and HEAD requests to any external LDP-NR must correctly respond to the "
                      + "Want-Digest header defined in [RFC3230]. With two supported digests.",
                      "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String checksum = "md5,sha";
        final Response exbcresource = createRequest().header(CONTENT_DISPOSITION,
                                                             "attachment; filename=\"respondWantDigestTwoSupported" +
                                                             ".txt\"")
                                                     .header(SLUG, info.getId())
                                                     .body("TestString.")
                                                     .when()
                                                     .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = createRequest().contentType(
                                                         "message/external-body; access-type=URL; URL=\"" +
                                                         locationHeader1 + "\"")
                                                 .when()
                                                 .post(uri);
        final String locationHeader2 = resource.getHeader("Location");

        final Response wantDigestResponse = createRequest().header("Want-Digest", checksum)
                                                           .when()
                                                           .get(locationHeader2);

        final Headers headers = wantDigestResponse.getHeaders();
        ps.append(wantDigestResponse.getStatusLine());

        for (Header h : headers) {
            ps.append(h.getName() + ": " + h.getValue() + "\n");
        }

        Assert.assertTrue(headers.getValue(DIGEST).contains("md5") ||
                          headers.getValue(DIGEST).contains("sha"), "OK");

    }

    /**
     * 3.9-H-2
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestTwoSupportedExternalBinaryContentHead(final String uri) throws FileNotFoundException {
        final TestInfo info =
            setupTest("3.9-H-2", "respondWantDigestTwoSupportedExternalBinaryContentHead",
                      "GET and HEAD requests to any external LDP-NR must correctly respond to the "
                      + "Want-Digest header defined in [RFC3230]. With two supported digests.",
                      "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String checksum = "md5,sha";
        final Response exbcresource = createRequest().header(CONTENT_DISPOSITION,
                                                             "attachment; " +
                                                             "filename=\"respondWantDigestTwoSupportedHead" +
                                                             ".txt\"")
                                                     .header(SLUG, info.getId())
                                                     .body("TestString.")
                                                     .when()
                                                     .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = createRequest().contentType(
                                                         "message/external-body; access-type=URL; URL=\"" +
                                                         locationHeader1 + "\"")
                                                 .when()
                                                 .post(uri);
        final String locationHeader2 = resource.getHeader("Location");

        final Response wantDigestResponse = createRequest().header("Want-Digest", checksum)
                                                           .when()
                                                           .head(locationHeader2);

        final Headers headers = wantDigestResponse.getHeaders();
        ps.append(wantDigestResponse.getStatusLine());

        for (Header h : headers) {
            ps.append(h.getName() + ": " + h.getValue() + "\n");
        }

        Assert.assertTrue(headers.getValue(DIGEST).contains("md5") ||
                          headers.getValue(DIGEST).contains("sha"), "OK");

    }

    /**
     * 3.9-I-1
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestTwoSupportedQvalueNonZeroExternalBinaryContent(final String uri)
        throws FileNotFoundException {
        final TestInfo info = setupTest("3.9-I-1",
                                        "" +
                                        "respondWantDigestTwoSupportedQvalueNonZeroExternalBinaryContent",
                                        "GET and HEAD requests to any external LDP-NR must correctly respond to the "
                                        +
                                        "Want-Digest header defined in [RFC3230]. Two digests with different weights," +
                                        " q values.",
                                        "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String checksum = "md5;q=0.3,sha;q=1";
        final Response exbcresource = createRequest().header(CONTENT_DISPOSITION,
                                                             "attachment; " +
                                                             "filename=\"respondWantDigestTwoSupportedQvalueNonZero" +
                                                             ".txt\"")
                                                     .header(SLUG, info.getId())
                                                     .body("TestString.")
                                                     .when()
                                                     .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = createRequest().contentType(
                                                         "message/external-body; access-type=URL; URL=\"" +
                                                         locationHeader1 + "\"")
                                                 .when()
                                                 .post(uri);
        final String locationHeader2 = resource.getHeader("Location");

        final Response wantDigestResponse = createRequest().header("Want-Digest", checksum)
                                                           .when()
                                                           .get(locationHeader2);

        final Headers headers = wantDigestResponse.getHeaders();
        ps.append(wantDigestResponse.getStatusLine());

        for (Header h : headers) {
            ps.append(h.getName() + ": " + h.getValue() + "\n");
        }

        Assert.assertTrue(headers.getValue(DIGEST).contains("md5") ||
                          headers.getValue(DIGEST).contains("sha"), "OK");

    }

    /**
     * 3.9-I-2
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestTwoSupportedQvalueNonZeroExternalBinaryContentHead(final String uri)
        throws FileNotFoundException {
        final TestInfo info = setupTest("3.9-I-2",
                                        "respondWantDigestTwoSupportedQvalueNonZeroExternalBinaryContentHead",
                                        "GET and HEAD requests to any external LDP-NR must correctly respond to the "
                                        +
                                        "Want-Digest header defined in [RFC3230]. Two digests with different weights," +
                                        " q values.",
                                        "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String checksum = "md5;q=0.3,sha;q=1";
        final Response exbcresource = createRequest().header(CONTENT_DISPOSITION,
                                                             "attachment; " +
                                                             "filename=\"respondWantDigestTwoSupportedQvalueNonZero" +
                                                             ".txt\"")
                                                     .header(SLUG, info.getId())
                                                     .body("TestString.")
                                                     .when()
                                                     .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = createRequest().contentType(
                                                         "message/external-body; access-type=URL; URL=\"" +
                                                         locationHeader1 + "\"")
                                                 .when()
                                                 .post(uri);
        final String locationHeader2 = resource.getHeader("Location");

        final Response wantDigestResponse = createRequest().header("Want-Digest", checksum)
                                                           .when()
                                                           .head(locationHeader2);

        final Headers headers = wantDigestResponse.getHeaders();
        ps.append(wantDigestResponse.getStatusLine());

        for (Header h : headers) {
            ps.append(h.getName() + ": " + h.getValue() + "\n");
        }

        Assert.assertTrue(headers.getValue(DIGEST).contains("md5") ||
                          headers.getValue(DIGEST).contains("sha"), "OK");

    }

    /**
     * 3.9-J-2
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestNonSupportedExternalBinaryContent(final String uri)
        throws FileNotFoundException {
        final TestInfo info =
            setupTest("3.9-J-2", "respondWantDigestNonSupportedExternalBinaryContent",
                      "GET and HEAD requests to any external LDP-NR must correctly respond to the "
                      + "Want-Digest header defined in [RFC3230]. One supported and an unsupported Digest.",
                      "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String checksum = "md5,abc";
        final Response exbcresource = createRequest().header(CONTENT_DISPOSITION,
                                                             "attachment; filename=\"respondWantDigestNonSupported" +
                                                             ".txt\"")
                                                     .header(SLUG, info.getId())
                                                     .body("TestString.")
                                                     .when()
                                                     .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = createRequest().contentType(
                                                         "message/external-body; access-type=URL; URL=\"" +
                                                         locationHeader1 + "\"")
                                                 .when()
                                                 .post(uri);
        final String locationHeader2 = resource.getHeader("Location");

        createRequest().header("Want-Digest", checksum)
                       .when()
                       .get(locationHeader2)
                       .then()
                       .log().all()
                       .statusCode(200).header(DIGEST, containsString("md5"));

    }

    /**
     * 3.9-J-2
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestNonSupportedExternalBinaryContentHead(final String uri)
        throws FileNotFoundException {
        final TestInfo info =
            setupTest("3.9-J-2", "respondWantDigestNonSupportedExternalBinaryContentHead",
                      "GET and HEAD requests to any external LDP-NR must correctly respond to the "
                      + "Want-Digest header defined in [RFC3230]. One supported and an unsupported Digest.",
                      "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String checksum = "md5,abc";
        final Response exbcresource = createRequest().header(CONTENT_DISPOSITION,
                                                             "attachment; " +
                                                             "filename=\"respondWantDigestNonSupportedHead" +
                                                             ".txt\"")
                                                     .header(SLUG, info.getId())
                                                     .body("TestString.")
                                                     .when()
                                                     .post(uri);
        final String locationHeader1 = exbcresource.getHeader("Location");

        final Response resource = createRequest().contentType(
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
                   .statusCode(200).header(DIGEST, containsString("md5"));

    }
}
