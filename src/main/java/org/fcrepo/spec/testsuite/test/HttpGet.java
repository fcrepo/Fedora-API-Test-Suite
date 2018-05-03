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

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class HttpGet extends AbstractTest {

    /**
     * Authentication
     *
     * @param username
     * @param password
     */
    @Parameters({"param2", "param3"})
    public HttpGet(final String username, final String password) {
        super(username, password);
    }

    /**
     * 3.2.1-A
     *
     * @param uri
     */
    @Test(groups = {"MAY"})
    @Parameters({"param1"})
    public void additionalValuesForPreferHeader(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.2.1-A", "additionalValuesForPreferHeader",
                                        "In addition to the requirements of [LDP], an implementation may support the " +
                                        "value "
                                        +
                                        "http://www.w3.org/ns/oa#PreferContainedDescriptions and should support the " +
                                        "value "
                                        +
                                        "http://fedora.info/definitions/fcrepo#PreferInboundReferences for the Prefer" +
                                        " header when making GET "
                                        + "requests on LDPC resources.",
                                        "https://fcrepo.github.io/fcrepo-specification/#additional-prefer-values",
                                        ps);
        final Response resource = createBasicContainer(uri, info);

        final String locationHeader = resource.getHeader("Location");
        createRequest().header("Prefer", "return=representation; "
                                         + "include=\"http://fedora.info/definitions/fcrepo#PreferInboundReferences\"")
                       .when()
                       .get(locationHeader)
                       .then()
                       .log().all()
                       .statusCode(200).header("preference-applied",
                                               containsString(
                                                   "http://fedora.info/definitions/fcrepo#PreferInboundReferences"));

    }

    /**
     * 3.2.2-A
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void responsePreferenceAppliedHeader(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.2.2-A", "responsePreferenceAppliedHeader",
                                        "Responses to GET requests that apply a Prefer request header to any LDP-RS " +
                                        "must "
                                        + "include the Preference-Applied"
                                        + " response header as defined in [RFC7240] section 3.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-get-ldprs",
                                        ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = resource.getHeader("Location");
        createRequest().header("Prefer", "return=minimal")
                       .when()
                       .get(locationHeader)
                       .then()
                       .log().all()
                       .statusCode(200).header("preference-applied", containsString("return=minimal"));

    }

    /**
     * 3.2.2-B
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void responseDescribesHeader(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.2.2-B", "responseDescribesHeader",
                                        "When a GET request is made to an LDP-RS that describes an associated LDP-NR "
                                        + "(3.5 HTTP POST and [LDP]5.2.3.12),"
                                        +
                                        "the response must include a Link: rel=\"describes\" header referencing the " +
                                        "LDP-NR "
                                        + "in question, as defined in [RFC6892].",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-get-ldprs",
                                        ps);
        final Response resource =
            createRequest().header("Content-Disposition", "attachment; filename=\"responseDescribesHeader.txt\"")
                           .header("slug", info.getId())
                           .body("TestString")
                           .when()
                           .post(uri);
        final String locationHeader = resource.getHeader("Location");
        createRequest().when()
                       .get(locationHeader + "/fcr:metadata")
                       .then()
                       .log().all()
                       .statusCode(200).header("Link", containsString("describes"));

    }

    /**
     * 3.2.3-A
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigest(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.2.3-A", "respondWantDigest",
                                        "Testing for supported digest "
                                        + "GET requests to any LDP-NR must correctly respond to the Want-Digest "
                                        + "header defined in [RFC3230]",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-get-ldpnr",
                                        ps);
        final String checksum = "md5";

        final Response resource =
            createRequest().header("Content-Disposition", "attachment; filename=\"respondwantdigest.txt\"")
                           .header("slug", info.getId())
                           .body("TestString")
                           .when()
                           .post(uri);
        final String locationHeader = resource.getHeader("Location");
        createRequest().header("Want-Digest", checksum)
                       .when()
                       .get(locationHeader)
                       .then()
                       .log().all()
                       .statusCode(200).header("Digest", containsString("md5"));

    }

    /**
     * 3.2.3-B
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestTwoSupported(final String uri) throws FileNotFoundException {
        final String checksum = "md5,sha";
        final TestInfo info = setupTest("3.2.3-B", "respondWantDigestTwoSupported",
                                        "Testing for two supported digests with no weights"
                                        + " GET requests to any LDP-NR must correctly respond to the Want-Digest "
                                        + "header defined in [RFC3230]",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-get-ldpnr",
                                        ps);
        final Response resource =
            createRequest().header("Content-Disposition", "attachment; filename=\"wantdigestTwoSupported.txt\"")
                           .header("slug", info.getId())
                           .body("TestString")
                           .when()
                           .post(uri);
        final String locationHeader = resource.getHeader("Location");

        final Response wantDigestResponse = createRequest().header("Want-Digest", checksum)
                                                           .when()
                                                           .get(locationHeader);

        final Headers headers = wantDigestResponse.getHeaders();
        ps.append(wantDigestResponse.getStatusLine());

        for (Header h : headers) {
            ps.append(h.getName() + ": " + h.getValue() + "\n");
        }

        Assert
            .assertTrue(headers.getValue("Digest").contains("md5") ||
                        headers.getValue("Digest").contains("sha"), "OK");

    }

    /**
     * 3.2.3-C
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestTwoSupportedQvalueNonZero(final String uri) throws FileNotFoundException {
        final String checksum = "md5;q=0.3,sha;q=1";
        final TestInfo info = setupTest("3.2.3-C", "respondWantDigestTwoSupportedQvalueNonZero",
                                        "Testing for two supported digests with different weights"
                                        + "GET requests to any LDP-NR must correctly respond to the Want-Digest "
                                        + "header defined in [RFC3230]",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-get-ldpnr",
                                        ps);

        final Response resource = createRequest().header("Content-Disposition",
                                                         "attachment; filename=\"wantdigestTwoSupportedQvalueNonZero" +
                                                         ".txt\"")
                                                 .header("slug", info.getId())
                                                 .body("TestString")
                                                 .when()
                                                 .post(uri);
        final String locationHeader = resource.getHeader("Location");
        final Response wantDigestResponse = createRequest().header("Want-Digest", checksum)
                                                           .when()
                                                           .get(locationHeader);

        final Headers headers = wantDigestResponse.getHeaders();
        ps.append(wantDigestResponse.getStatusLine());

        for (Header h : headers) {
            ps.append(h.getName() + ": " + h.getValue() + "\n");
        }

        Assert
            .assertTrue(headers.getValue("Digest").contains("md5") ||
                        headers.getValue("Digest").contains("sha"), "OK");

    }

    /**
     * 3.2.3-D
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestTwoSupportedQvalueZero(final String uri) throws FileNotFoundException {
        final String checksum = "md5;q=0.3,sha;q=0";
        final TestInfo info = setupTest("3.2.3-D", "respondWantDigestTwoSupportedQvalueZero",
                                        "Testing for two supported digests with different weights q=0.3,q=0"
                                        + " GET requests to any LDP-NR must correctly respond to the Want-Digest"
                                        + " header defined in [RFC3230]",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-get-ldpnr",
                                        ps);
        final Response resource = createRequest()
            .header("Content-Disposition", "attachment; filename=\"wantDigestTwoSupportedQvalueZero.txt\"")
            .header("slug", info.getId())
            .body("TestString")
            .when()
            .post(uri);
        final String locationHeader = resource.getHeader("Location");
        createRequest().header("Want-Digest", checksum)
                       .when()
                       .get(locationHeader)
                       .then()
                       .log().all()
                       .statusCode(200).header("Digest", containsString("md5"));

    }

    /**
     * 3.2.3-E
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestNonSupported(final String uri) throws FileNotFoundException {
        final String checksum = "md5,abc";

        final TestInfo info = setupTest("3.2.3-E", "respondWantDigestNonSupported",
                                        "Testing for one supported digest and one unsupported digest."
                                        + "GET requests to any LDP-NR must correctly respond to the Want-Digest "
                                        + "header defined in [RFC3230]",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-get-ldpnr",
                                        ps);
        final Response resource =
            createRequest().header("Content-Disposition", "attachment; filename=\"wantDigestNonSupported.txt\"")
                           .header("slug", info.getId())
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

    }

}
