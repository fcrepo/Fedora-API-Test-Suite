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
package org.fcrepo.spec.testsuite.crud;

import static org.fcrepo.spec.testsuite.Constants.CONTENT_DISPOSITION;
import static org.fcrepo.spec.testsuite.Constants.DIGEST;
import static org.fcrepo.spec.testsuite.Constants.RDF_BODY;
import static org.fcrepo.spec.testsuite.Constants.SLUG;
import static org.hamcrest.Matchers.containsString;

import java.net.URI;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;

import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import javax.ws.rs.core.Link;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class HttpGet extends AbstractTest {

    /**
     * 3.2.1-A
     */
    @Test(groups = {"SHOULD"})
    public void additionalValuesForPreferHeader() {
        final TestInfo info = setupTest("3.2.1-A",
                                        "In addition to the requirements of [LDP], an implementation ... " +
                                        "should support the value " +
                                        "http://fedora.info/definitions/fcrepo#PreferInboundReferences for the " +
                                        "Prefer header when making GET requests on LDPC resources.",
                                        SPEC_BASE_URL + "#additional-prefer-values",
                                        ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resource);

        // Create second resource that references first resource.
        final Response referrer = createBasicContainer(
                uri, info, "<> <http://purl.org/dc/terms/isPartOf> <" + locationHeader + "> .");

        // Triple expected in result body
        final Statement triple = ResourceFactory.createStatement(
                ResourceFactory.createResource(getLocation(referrer)),
                ResourceFactory.createProperty("http://purl.org/dc/terms/isPartOf"),
                ResourceFactory.createResource(locationHeader));

        doGet(locationHeader, new Header("Prefer", "return=representation; "
                + "include=\"http://fedora.info/definitions/fcrepo#PreferInboundReferences\""))
                .then()
                .header("preference-applied", containsString("return=representation"))
                .body(new TripleMatcher(triple));
    }

    /**
     * 3.2.1-B
     */
    @Test(groups = {"MAY"})
    public void additionalValuesForPreferHeaderContainedDescriptions() {
        final TestInfo info = setupTest("3.2.1-B",
                "In addition to the requirements of [LDP], an implementation ... " +
                        "may support the value " +
                        "http://www.w3.org/ns/oa#PreferContainedDescriptions for the " +
                        "Prefer header when making GET requests on LDPC resources.",
                SPEC_BASE_URL + "#additional-prefer-values",
                ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resource);

        final Response child = createBasicContainer(locationHeader, "child", RDF_BODY);

        // Triple expected in result body
        final Statement triple = ResourceFactory.createStatement(
                ResourceFactory.createResource(getLocation(child)),
                ResourceFactory.createProperty("http://xmlns.com/foaf/0.1/name"),
                ResourceFactory.createStringLiteral("Pythagoras"));

        final Response response = doGet(locationHeader, new Header("Prefer", "return=representation; "
                + "include=\"http://www.w3.org/ns/oa#PreferContainedDescriptions\""));

        final String header = response.getHeader("preference-applied");
        if (header.contains("return=representation")) {
            // Verify child triple is returned in parent response
            response.then().body(new TripleMatcher(triple));

        } else {
            // Preference not supported
            throw new SkipException("PreferContainedDescriptions not supported");
        }
    }

    /**
     * 3.2.2-A
     */
    @Test(groups = {"MUST"})
    public void responsePreferenceAppliedHeader() {
        final TestInfo info = setupTest("3.2.2-A",
                                        "Responses to GET requests that apply a Prefer request header to any LDP-RS " +
                                        "must "
                                        + "include the Preference-Applied"
                                        + " response header as defined in [RFC7240] section 3.",
                                        SPEC_BASE_URL + "#http-get-ldprs",
                                        ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resource);
        doGet(locationHeader, new Header("Prefer", "return=representation; "
                        + "include=\"http://www.w3.org/ns/ldp#PreferMinimalContainer\""))
                .then()
                .header("preference-applied", containsString("return=representation"));
    }

    /**
     * 3.2.2-B
     */
    @Test(groups = {"MUST"})
    public void responseDescribesHeader() {
        final TestInfo info = setupTest("3.2.2-B",
                                        "When a GET request is made to an LDP-RS that describes an associated LDP-NR "
                                        + "(3.5 HTTP POST and [LDP]5.2.3.12),"
                                        +
                                        "the response must include a Link: rel=\"describes\" header referencing the " +
                                        "LDP-NR "
                                        + "in question, as defined in [RFC6892].",
                                        SPEC_BASE_URL + "#http-get-ldprs",
                                        ps);
        final Headers headers = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"responseDescribesHeader.txt\""),
                new Header(SLUG, info.getId()));
        final Response resource = doPost(uri, headers, "TestString");
        final String locationHeader = getLocation(resource);

        // Get Binary description (from community impl: /fcr:metadata)
        final Response getResponse = doGet(locationHeader);

        final URI description = getLinksOfRelType(getResponse, "describedby").map(Link::getUri)
                .findFirst()
                .orElse(null);

        Assert.assertNotNull(description, "Description is null!");

        final Link expected = Link.fromUri(locationHeader).rel("describes").build();
        confirmPresenceOfLinkValue(expected, doGet(description.toString()));

    }

    /**
     * 3.2.3-A-1
     */
    @Test(groups = {"MUST"})
    public void respondWantDigestMd5() {
        final TestInfo info = setupTest("3.2.3-A-1",
                                        "Testing for supported digest: md5 . "
                                        + "GET requests to any LDP-NR must correctly respond to the Want-Digest "
                                        + "header defined in [RFC3230]",
                                        SPEC_BASE_URL + "#http-get-ldpnr",
                                        ps);
        final String checksum = "md5";

        final Headers headers = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"respondwantdigest.txt\""),
                new Header(SLUG, info.getId()));
        final Response resource = doPost(uri, headers, "TestString");
        final String locationHeader = getLocation(resource);
        doGet(locationHeader, new Header("Want-Digest", checksum))
                .then()
                .statusCode(200)
                .header(DIGEST, containsString("md5"));
    }

    /**
     * 3.2.3-A-2
     */
    @Test(groups = {"MUST"})
    public void respondWantDigestSha1() {
        final TestInfo info = setupTest("3.2.3-A-2",
                                        "Testing for supported digest: sha . "
                                        + "GET requests to any LDP-NR must correctly respond to the Want-Digest "
                                        + "header defined in [RFC3230]",
                                        SPEC_BASE_URL + "#http-get-ldpnr",
                                        ps);
        final String checksum = "sha";

        final Headers headers = new Headers(
            new Header(CONTENT_DISPOSITION, "attachment; filename=\"respondwantdigest.txt\""),
            new Header(SLUG, info.getId()));
        final Response resource = doPost(uri, headers, "TestString");
        final String locationHeader = getLocation(resource);
        doGet(locationHeader, new Header("Want-Digest", checksum))
            .then()
            .statusCode(200)
            .header(DIGEST, containsString(checksum));
    }

    /**
     * 3.2.3-A-3
     */
    @Test(groups = {"MUST"})
    public void respondWantDigestSha256() {
        final TestInfo info = setupTest("3.2.3-A-3",
                                        "Testing for supported digest: sha-256 . "
                                        + "GET requests to any LDP-NR must correctly respond to the Want-Digest "
                                        + "header defined in [RFC3230]",
                                        SPEC_BASE_URL + "#http-get-ldpnr",
                                        ps);
        final String checksum = "sha-256";

        final Headers headers = new Headers(
            new Header(CONTENT_DISPOSITION, "attachment; filename=\"respondwantdigest.txt\""),
            new Header(SLUG, info.getId()));
        final Response resource = doPost(uri, headers, "TestString");
        final String locationHeader = getLocation(resource);
        doGet(locationHeader, new Header("Want-Digest", checksum))
            .then()
            .statusCode(200)
            .header(DIGEST, containsString(checksum));
    }

    /**
     * 3.2.3-B
     */
    @Test(groups = {"MUST"})
    public void respondWantDigestTwoSupported() {
        final String checksum = "md5,sha";
        final TestInfo info = setupTest("3.2.3-B",
                                        "Testing for two supported digests with no weights"
                                        + " GET requests to any LDP-NR must correctly respond to the Want-Digest "
                                        + "header defined in [RFC3230]",
                                        SPEC_BASE_URL + "#http-get-ldpnr",
                                        ps);
        final Headers headers = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"wantdigestTwoSupported.txt\""),
                new Header(SLUG, info.getId()));
        final Response resource = doPost(uri, headers, "TestString");
        final String locationHeader = getLocation(resource);

        final Response wantDigestResponse = doGet(locationHeader, new Header("Want-Digest", checksum));

        final Headers responseHeaders = wantDigestResponse.getHeaders();
        ps.append(wantDigestResponse.getStatusLine());

        for (final Header h : responseHeaders) {
            ps.append(h.getName()).append(": ").append(h.getValue()).append("\n");
        }

        Assert
            .assertTrue(responseHeaders.getValue(DIGEST).contains("md5") ||
                        responseHeaders.getValue(DIGEST).contains("sha"), "OK");

    }

    /**
     * 3.2.3-C
     */
    @Test(groups = {"MUST"})
    public void respondWantDigestTwoSupportedQvalueNonZero() {
        final String checksum = "md5;q=0.3,sha;q=1";
        final TestInfo info = setupTest("3.2.3-C",
                                        "Testing for two supported digests with different weights"
                                        + "GET requests to any LDP-NR must correctly respond to the Want-Digest "
                                        + "header defined in [RFC3230]",
                                        SPEC_BASE_URL + "#http-get-ldpnr",
                                        ps);

        final Headers headers = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"wantdigestTwoSupportedQvalueNonZero.txt\""),
                new Header(SLUG, info.getId()));
        final Response resource = doPost(uri, headers, "TestString");
        final String locationHeader = getLocation(resource);
        final Response wantDigestResponse = doGet(locationHeader, new Header("Want-Digest", checksum));

        final Headers responseHeaders = wantDigestResponse.getHeaders();
        ps.append(wantDigestResponse.getStatusLine());

        for (final Header h : responseHeaders) {
            ps.append(h.getName()).append(": ").append(h.getValue()).append("\n");
        }

        Assert
            .assertTrue(responseHeaders.getValue(DIGEST).contains("md5") ||
                        responseHeaders.getValue(DIGEST).contains("sha"), "OK");

    }

    /**
     * 3.2.3-D
     */
    @Test(groups = {"MUST"})
    public void respondWantDigestTwoSupportedQvalueZero() {
        final String checksum = "md5;q=0.3,sha;q=0";
        final TestInfo info = setupTest("3.2.3-D",
                                        "Testing for two supported digests with different weights q=0.3,q=0"
                                        + " GET requests to any LDP-NR must correctly respond to the Want-Digest"
                                        + " header defined in [RFC3230]",
                                        SPEC_BASE_URL + "#http-get-ldpnr",
                                        ps);
        final Headers headers = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"wantDigestTwoSupportedQvalueZero.txt\""),
                new Header(SLUG, info.getId()));
        final Response resource = doPost(uri, headers, "TestString");
        final String locationHeader = getLocation(resource);
        doGet(locationHeader, new Header("Want-Digest", checksum))
                .then()
                .header(DIGEST, containsString("md5"));
    }

    /**
     * 3.2.3-E
     */
    @Test(groups = {"MUST"})
    public void respondWantDigestNonSupportedWithSupported() {
        final String checksum = "md5,abc";

        final TestInfo info = setupTest("3.2.3-E",
                                        "Testing for one supported digest and one unsupported digest."
                                        + "GET requests to any LDP-NR must correctly respond to the Want-Digest "
                                        + "header defined in [RFC3230]",
                                        SPEC_BASE_URL + "#http-get-ldpnr",
                                        ps);
        final Headers headers = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"wantDigestNonSupported.txt\""),
                new Header(SLUG, info.getId()));
        final Response resource = doPost(uri, headers, "TestString");
        final String locationHeader = getLocation(resource);
        doGet(locationHeader, new Header("Want-Digest", checksum))
                .then()
                .header(DIGEST, containsString("md5"));
    }

    /**
     * 3.2.3-F
     */
    @Test(groups = {"MUST"})
    public void respondWantDigestNonSupported() {
        final String checksum = "abc";

        final TestInfo info = setupTest("3.2.3-F",
                                        "Testing that unsupported digest is rejected with a 400."
                                        + "GET requests to any LDP-NR must correctly respond to the Want-Digest "
                                        + "header defined in [RFC3230].",
                                        SPEC_BASE_URL + "#http-get-ldpnr",
                                        ps);
        final Headers headers = new Headers(
            new Header(CONTENT_DISPOSITION, "attachment; filename=\"wantDigestNonSupported.txt\""),
            new Header(SLUG, info.getId()));
        final Response resource = doPost(uri, headers, "TestString");
        final String locationHeader = getLocation(resource);
        doGetUnverified(locationHeader, new Header("Want-Digest", checksum))
            .then()
            .statusCode(400);
    }

}
