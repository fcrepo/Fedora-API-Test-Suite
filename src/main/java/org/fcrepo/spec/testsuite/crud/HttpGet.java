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
import java.util.List;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;

import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Link;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class HttpGet extends AbstractTest {

    /**
     * Authentication
     *
     * @param username The repository username
     * @param password The repository password
     */
    @Parameters({"param2", "param3"})
    public HttpGet(final String username, final String password) {
        super(username, password);
    }

    /**
     * 3.2.1-A
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void additionalValuesForPreferHeader(final String uri) {
        final TestInfo info = setupTest("3.2.1-A", "additionalValuesForPreferHeader",
                                        "In addition to the requirements of [LDP], an implementation ... " +
                                        "should support the value " +
                                        "http://fedora.info/definitions/fcrepo#PreferInboundReferences for the " +
                                        "Prefer header when making GET requests on LDPC resources.",
                                        "https://fcrepo.github.io/fcrepo-specification/#additional-prefer-values",
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
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MAY"})
    @Parameters({"param1"})
    public void additionalValuesForPreferHeaderContainedDescriptions(final String uri) {
        final TestInfo info = setupTest("3.2.1-B", "additionalValuesForPreferHeaderContainedDescriptions",
                "In addition to the requirements of [LDP], an implementation ... " +
                        "may support the value " +
                        "http://www.w3.org/ns/oa#PreferContainedDescriptions for the " +
                        "Prefer header when making GET requests on LDPC resources.",
                "https://fcrepo.github.io/fcrepo-specification/#additional-prefer-values",
                ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resource);

        final Response child = createBasicContainer(locationHeader, "child", RDF_BODY);

        // Triple expected in result body
        final Statement triple = ResourceFactory.createStatement(
                ResourceFactory.createResource(getLocation(child)),
                ResourceFactory.createProperty("http://xmlns.com/foaf/0.1/name"),
                ResourceFactory.createStringLiteral("Pythagoras"));

        doGet(locationHeader, new Header("Prefer", "return=representation; "
                + "include=\"http://www.w3.org/ns/oa#PreferContainedDescriptions\""))
                .then()
                .header("preference-applied", containsString("return=representation"))
                .body(new TripleMatcher(triple));
    }

    /**
     * 3.2.2-A
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void responsePreferenceAppliedHeader(final String uri) {
        final TestInfo info = setupTest("3.2.2-A", "responsePreferenceAppliedHeader",
                                        "Responses to GET requests that apply a Prefer request header to any LDP-RS " +
                                        "must "
                                        + "include the Preference-Applied"
                                        + " response header as defined in [RFC7240] section 3.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-get-ldprs",
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
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void responseDescribesHeader(final String uri) {
        final TestInfo info = setupTest("3.2.2-B", "responseDescribesHeader",
                                        "When a GET request is made to an LDP-RS that describes an associated LDP-NR "
                                        + "(3.5 HTTP POST and [LDP]5.2.3.12),"
                                        +
                                        "the response must include a Link: rel=\"describes\" header referencing the " +
                                        "LDP-NR "
                                        + "in question, as defined in [RFC6892].",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-get-ldprs",
                                        ps);
        final Headers headers = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"responseDescribesHeader.txt\""),
                new Header(SLUG, info.getId()));
        final Response resource = doPost(uri, headers, "TestString");
        final String locationHeader = getLocation(resource);

        // Get Binary description (from community impl: /fcr:metadata)
        final Response getResponse = doGet(locationHeader);
        final List<Header> linkHeaders = getResponse.getHeaders().getList("Link");

        final URI description = linkHeaders.stream().map(Header::getValue).map(h -> new String(h)).map(Link::valueOf)
                .filter(l -> l.getRel().equals("describedby")).map(Link::getUri).findFirst().orElse(null);

        Assert.assertNotNull(description, "Description is null!");

        final Link expected = Link.fromUri(locationHeader).rel("describes").build();
        confirmPresenceOfLinkValue(expected, doGet(description.toString()));

    }

    /**
     * 3.2.3-A
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigest(final String uri) {
        final TestInfo info = setupTest("3.2.3-A", "respondWantDigest",
                                        "Testing for supported digest "
                                        + "GET requests to any LDP-NR must correctly respond to the Want-Digest "
                                        + "header defined in [RFC3230]",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-get-ldpnr",
                                        ps);
        final String checksum = "md5";

        final Headers headers = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"respondwantdigest.txt\""),
                new Header(SLUG, info.getId()));
        final Response resource = doPost(uri, headers, "TestString");
        final String locationHeader = getLocation(resource);
        doGet(locationHeader, new Header("Want-Digest", checksum))
                .then()
                .header(DIGEST, containsString("md5"));
    }

    /**
     * 3.2.3-B
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestTwoSupported(final String uri) {
        final String checksum = "md5,sha";
        final TestInfo info = setupTest("3.2.3-B", "respondWantDigestTwoSupported",
                                        "Testing for two supported digests with no weights"
                                        + " GET requests to any LDP-NR must correctly respond to the Want-Digest "
                                        + "header defined in [RFC3230]",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-get-ldpnr",
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
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestTwoSupportedQvalueNonZero(final String uri) {
        final String checksum = "md5;q=0.3,sha;q=1";
        final TestInfo info = setupTest("3.2.3-C", "respondWantDigestTwoSupportedQvalueNonZero",
                                        "Testing for two supported digests with different weights"
                                        + "GET requests to any LDP-NR must correctly respond to the Want-Digest "
                                        + "header defined in [RFC3230]",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-get-ldpnr",
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
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestTwoSupportedQvalueZero(final String uri) {
        final String checksum = "md5;q=0.3,sha;q=0";
        final TestInfo info = setupTest("3.2.3-D", "respondWantDigestTwoSupportedQvalueZero",
                                        "Testing for two supported digests with different weights q=0.3,q=0"
                                        + " GET requests to any LDP-NR must correctly respond to the Want-Digest"
                                        + " header defined in [RFC3230]",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-get-ldpnr",
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
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void respondWantDigestNonSupported(final String uri) {
        final String checksum = "md5,abc";

        final TestInfo info = setupTest("3.2.3-E", "respondWantDigestNonSupported",
                                        "Testing for one supported digest and one unsupported digest."
                                        + "GET requests to any LDP-NR must correctly respond to the Want-Digest "
                                        + "header defined in [RFC3230]",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-get-ldpnr",
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

}
