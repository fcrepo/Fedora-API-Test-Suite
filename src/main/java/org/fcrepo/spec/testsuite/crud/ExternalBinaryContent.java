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
import static org.fcrepo.spec.testsuite.Constants.EXTERNAL_CONTENT_LINK_REL;
import static org.fcrepo.spec.testsuite.Constants.LINK;
import static org.fcrepo.spec.testsuite.Constants.SLUG;
import static org.fcrepo.spec.testsuite.Constants.NON_RDF_SOURCE_INTERACTION_MODEL;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertTrue;
import static java.util.Collections.emptyList;

/**
 * @author awoods
 * @since 2018-06-28
 */
public class ExternalBinaryContent extends AbstractTest {

    private final List<String> HANDLINGS = Arrays.asList("copy", "redirect", "proxy");

    private WireMockServer wireMockServer;
    private String externalUri;

    private String externalFileUri;

    private String externalUriWithNoType;

    private Boolean extContentSupported;

    /**
     * Authentication
     *
     * @param username The repository username
     * @param password The repository password
     * @throws IOException
     */
    @Parameters({"param2", "param3"})
    public ExternalBinaryContent(final String username, final String password) throws IOException {
        super(username, password);

        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        externalUri = mockHttpResource("file.txt", "text/plain", "binary content");
        externalUriWithNoType = mockHttpResourceNoType("notype", "other content");

        final File tempFile = File.createTempFile("ext", null);
        tempFile.deleteOnExit();
        externalFileUri = tempFile.toURI().toString();
    }

    /**
     * Stop the mock server
     */
    @AfterClass
    public void stopServer() {
        wireMockServer.stop();
    }

    /**
     * 3.9-A-1 PostCreate Copy
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void postCreateExternalBinaryContentCopy(final String uri) {
        final TestInfo info = setupTest("3.9-A-1", "postCreateExternalBinaryContentCopy",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "copy")),
                new Header(SLUG, info.getId()));

        doPost(uri, headers);
    }

    /**
     * 3.9-A-1b PostCreate Copy File URI
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = { "SHOULD" })
    @Parameters({ "param1" })
    public void postCreateExternalFileUriBinaryCopy(final String uri) {
        final TestInfo info = setupTest("3.9-A-1b", "postCreateExternalFileUriBinaryCopy",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalFileUri, "copy")),
                new Header(SLUG, info.getId()));

        doPost(uri, headers);
    }

    /**
     * 3.9-A-2 PostCreate Redirect
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void postCreateExternalBinaryContentRedirect(final String uri) {
        final TestInfo info = setupTest("3.9-A-2", "postCreateExternalBinaryContentRedirect",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "redirect")),
                new Header(SLUG, info.getId()));
        doPost(uri, headers);
    }

    /**
     * 3.9-A-2b PostCreate Redirect File URI
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = { "SHOULD" })
    @Parameters({ "param1" })
    public void postCreateExternalFileUriBinaryRedirect(final String uri) {
        final TestInfo info = setupTest("3.9-A-2b", "postCreateExternalFileUriBinaryRedirect",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalFileUri, "redirect")),
                new Header(SLUG, info.getId()));
        doPost(uri, headers);
    }

    /**
     * 3.9-A-3 PostCreate Proxy
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void postCreateExternalBinaryContentProxy(final String uri) {
        final TestInfo info = setupTest("3.9-A-3", "postCreateExternalBinaryContentProxy",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "proxy")),
                new Header(SLUG, info.getId()));
        doPost(uri, headers);
    }

    /**
     * 3.9-A-3b PostCreate Proxy File URI
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = { "SHOULD" })
    @Parameters({ "param1" })
    public void postCreateExternalFileUriBinaryProxy(final String uri) {
        final TestInfo info = setupTest("3.9-A-3b", "postCreateExternalFileUriBinaryProxy",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalFileUri, "proxy")),
                new Header(SLUG, info.getId()));
        doPost(uri, headers);
    }

    /**
     * 3.9-B-1 PutCreate Copy
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void putCreateExternalBinaryContentCopy(final String uri) {
        final TestInfo info = setupTest("3.9-B-1", "putCreateExternalBinaryContentCopy",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String location = uri + info.getId();
        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "copy")));
        doPutUnverified(location, headers).then()
                .statusCode(201);
    }

    /**
     * 3.9-B-1b PutCreate Copy File URI
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = { "SHOULD" })
    @Parameters({ "param1" })
    public void putCreateExternalFileUriBinaryCopy(final String uri) {
        final TestInfo info = setupTest("3.9-B-1b", "putCreateExternalFileUriBinaryCopy",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String location = uri + info.getId();
        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalFileUri, "copy")));
        doPutUnverified(location, headers).then()
                .statusCode(201);
    }

    /**
     * 3.9-B-2 PutCreate Redirect
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void putCreateExternalBinaryContentRedirect(final String uri) {
        final TestInfo info = setupTest("3.9-B-2", "putCreateExternalBinaryContentRedirect",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String location = uri + info.getId();
        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "redirect")));
        doPutUnverified(location, headers).then()
                .statusCode(201);
    }

    /**
     * 3.9-B-2b PutCreate Redirect File URI
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = { "SHOULD" })
    @Parameters({ "param1" })
    public void putCreateExternalFileUriBinaryRedirect(final String uri) {
        final TestInfo info = setupTest("3.9-B-2b", "putCreateExternalFileUriBinaryRedirect",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String location = uri + info.getId();
        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalFileUri, "redirect")));
        doPutUnverified(location, headers).then()
                .statusCode(201);
    }

    /**
     * 3.9-B-3 PutCreate Proxy
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void putCreateExternalBinaryContentProxy(final String uri) {
        final TestInfo info = setupTest("3.9-B-3", "putCreateExternalBinaryContentProxy",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String location = uri + info.getId();
        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "proxy")));
        doPutUnverified(location, headers).then()
                .statusCode(201);
    }

    /**
     * 3.9-B-3b PutCreate Proxy File URI
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = { "SHOULD" })
    @Parameters({ "param1" })
    public void putCreateExternalFileUriBinaryProxy(final String uri) {
        final TestInfo info = setupTest("3.9-B-3b", "putCreateExternalFileUriBinaryProxy",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String location = uri + info.getId();
        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalFileUri, "proxy")));
        doPutUnverified(location, headers).then()
                .statusCode(201);
    }

    /**
     * 3.9-C-1 PutUpdate Copy
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void putUpdateExternalBinaryContentCopy(final String uri) {
        final TestInfo info = setupTest("3.9-C-1", "putUpdateExternalBinaryContentCopy",
                "Fedora servers should support the update of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String locationHeader = createNonRdfSource(uri, info.getId());

        final Headers headers2 = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "copy")));
        doPut(locationHeader, headers2);
    }

    /**
     * 3.9-C-1b PutUpdate Copy File URI
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = { "SHOULD" })
    @Parameters({ "param1" })
    public void putUpdateExternalFileUriBinaryCopy(final String uri) {
        final TestInfo info = setupTest("3.9-C-1b", "putUpdateExternalFileUriBinaryCopy",
                "Fedora servers should support the update of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String locationHeader = createNonRdfSource(uri, info.getId());

        final Headers headers2 = new Headers(
                new Header(LINK, externalContentHeader(externalFileUri, "copy")));
        doPut(locationHeader, headers2);
    }

    /**
     * 3.9-C-2 PutUpdate Redirect
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void putUpdateExternalBinaryContentRedirect(final String uri) {
        final TestInfo info = setupTest("3.9-C-2", "putUpdateExternalBinaryContentRedirect",
                "Fedora servers should support the update of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String locationHeader = createNonRdfSource(uri, info.getId());

        final Headers headers2 = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "redirect")));
        doPut(locationHeader, headers2);
    }

    /**
     * 3.9-C-2b PutUpdate Redirect File URI
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = { "SHOULD" })
    @Parameters({ "param1" })
    public void putUpdateExternalFileUriBinaryRedirect(final String uri) {
        final TestInfo info = setupTest("3.9-C-2b", "putUpdateExternalFileUriBinaryRedirect",
                "Fedora servers should support the update of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String locationHeader = createNonRdfSource(uri, info.getId());

        final Headers headers2 = new Headers(
                new Header(LINK, externalContentHeader(externalFileUri, "redirect")));
        doPut(locationHeader, headers2);
    }

    /**
     * 3.9-C-3 PutUpdate Proxy
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void putUpdateExternalBinaryContentProxy(final String uri) {
        final TestInfo info = setupTest("3.9-C-3", "putUpdateExternalBinaryContentProxy",
                "Fedora servers should support the update of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String locationHeader = createNonRdfSource(uri, info.getId());

        final Headers headers2 = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "proxy")));
        doPut(locationHeader, headers2);
    }

    /**
     * 3.9-C-3b PutUpdate Proxy File URI
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = { "SHOULD" })
    @Parameters({ "param1" })
    public void putUpdateExternalFileUriBinaryProxy(final String uri) {
        final TestInfo info = setupTest("3.9-C-3b", "putUpdateExternalFileUriBinaryProxy",
                "Fedora servers should support the update of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String locationHeader = createNonRdfSource(uri, info.getId());

        final Headers headers2 = new Headers(
                new Header(LINK, externalContentHeader(externalFileUri, "proxy")));
        doPut(locationHeader, headers2);
    }

    /**
     * 3.9-D-1 Unsupported status code
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void unsupportedExternalBinaryContentStatus(final String uri) {
        final TestInfo info = setupTest("3.9-D-1", "unsupportedExternalBinaryContentStatus",
                "Fedora servers that do not support the creation of LDP-NRs with content external must reject " +
                        "with a 4xx range status code",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (externalContentSupported(uri)) {
            return;
        }

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "proxy")),
                new Header(SLUG, info.getId()));
        doPostUnverified(uri, headers)
                .then()
                .statusCode(clientErrorRange());
    }

    /**
     * 3.9-D-2 Unsupported constraint
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void unsupportedExternalBinaryContentConstraint(final String uri) {
        final TestInfo info = setupTest("3.9-D-2", "unsupportedExternalBinaryContentConstraint",
                "Fedora servers that do not support the creation of LDP-NRs with content external must describe " +
                        "this restriction in a resource indicated by a " +
                        "rel=\"http://www.w3.org/ns/ldp#constrainedBy\" link in the Link response header.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (externalContentSupported(uri)) {
            return;
        }

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "proxy")),
                new Header(SLUG, info.getId()));

        confirmPresenceOfConstrainedByLink(doPostUnverified(uri, headers));
    }

    /**
     * 3.9-E-1 handling attribute
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void externalBinaryContentHandlingAttribute(final String uri) {
        final TestInfo info = setupTest("3.9-E-1", "externalBinaryContentHandlingAttribute",
                "Fedora servers must use the handling attribute in the external content link to determine how to " +
                        "process the request. At least one of the following handling attributes must be supported: " +
                        "copy, redirect, and/or proxy.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final List<String> acceptedHandlingTypes = getAcceptedHandlingTypes(uri);

        final List<String> acceptedAndExpected = acceptedHandlingTypes.stream()
                .filter(HANDLINGS::contains)
                .collect(Collectors.toList());

        assertThat("Accept-External-Content-Handling header contains at least one expected handling",
                acceptedAndExpected.size(), greaterThanOrEqualTo(1));

        // Test that at least one of the handlings specified by the serve is supported
        for (final String handling : acceptedAndExpected) {
            final Headers headers = new Headers(
                    new Header(LINK, externalContentHeader(externalUri, handling)),
                    new Header(SLUG, info.getId()));

            final Response resp = doPostUnverified(uri, headers);
            if (resp.statusCode() == 201) {
                return;
            }
        }
    }

    /**
     * 3.9-E-2 handling status
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void externalBinaryContentHandlingStatus(final String uri) {
        final TestInfo info = setupTest("3.9-E-2", "externalBinaryContentHandlingStatus",
                "Fedora servers must reject with a 4xx range status code requests for which the handling attribute " +
                        "is not present or cannot be respected.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, null)),
                new Header(SLUG, info.getId()));

        doPostUnverified(uri, headers)
                .then()
                .statusCode(clientErrorRange());
    }

    /**
     * 3.9-E-3 unsupported handling
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void unsupportedBinaryContentHandlingAttribute(final String uri) {
        final TestInfo info = setupTest("3.9-E-3", "unsupportedBinaryContentHandlingAttribute",
                "In the case that the specified handling cannot be respected, the restrictions causing the request " +
                        "to fail must be described in a resource indicated by a " +
                        "rel=\"http://www.w3.org/ns/ldp#constrainedBy\" link in the Link response header.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "unsupported")),
                new Header(SLUG, info.getId()));

        final Response resp = doPostUnverified(uri, headers);
        resp.then().statusCode(clientErrorRange());
        confirmPresenceOfConstrainedByLink(resp);
    }

    /**
     * 3.9-F-1 media type
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentMediaType(final String uri) {
        final TestInfo info = setupTest("3.9-F-1", "binaryContentMediaType",
                "Fedora servers must use the value of the type attribute in the external content link as the media " +
                        "type of the external content, if provided.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUriWithNoType, "proxy", "text/special")),
                new Header(SLUG, info.getId()));
        final String location = getLocation(doPost(uri, headers));

        doHead(location)
                .then()
                .header("Content-Type", "text/special");
    }

    /**
     * 3.9-F-2 no type attribute - external type
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MAY"})
    @Parameters({"param1"})
    public void binaryContentNoTypeExternalType(final String uri) {
        final TestInfo info = setupTest("3.9-F-2", "binaryContentNoTypeExternalType",
                "Fedora servers must use the value of the type attribute in the external content link as the media " +
                        "type of the external content, if provided. If there is no type attribute: " +
                        "Servers may use the media type obtained when accessing the external content via the " +
                        "specified scheme (e.g. the Content-Type header for external content accessed via http).",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "proxy")),
                new Header(SLUG, info.getId()));
        final String location = getLocation(doPost(uri, headers));

        doHead(location).then()
                .header("Content-Type", "text/plain");
    }

    /**
     * 3.9-F-3 no type attribute - default
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MAY"})
    @Parameters({"param1"})
    public void binaryContentNoTypeDefault(final String uri) {
        final TestInfo info = setupTest("3.9-F-3", "binaryContentNoTypeDefault",
                "Fedora servers must use the value of the type attribute in the external content link as the media " +
                        "type of the external content, if provided. If there is no type attribute: " +
                        "Servers may use a default media type.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUriWithNoType, "proxy")),
                new Header(SLUG, info.getId()));
        final String location = getLocation(doPost(uri, headers));

        // Verify that any content type is provided, unspecified by the client or the content
        doHead(location).then()
                .assertThat().header("Content-Type", not(isEmptyString()));
    }

    /**
     * 3.9-F-4 no type attribute - unsupported
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MAY"})
    @Parameters({"param1"})
    public void binaryContentNoTypeUnsupported(final String uri) {
        final TestInfo info = setupTest("3.9-F-4", "binaryContentNoTypeUnsupported",
                "Fedora servers must use the value of the type attribute in the external content link as the media " +
                        "type of the external content, if provided. If there is no type attribute: " +
                        "Servers may reject the request with a 4xx range status code.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUriWithNoType, "proxy")),
                new Header(SLUG, info.getId()));
        doPostUnverified(uri, headers).then()
                .statusCode(clientErrorRange());
    }

    /**
     * 3.9-F-5 Content-Type and type
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void binaryContentContentTypeAndType(final String uri) {
        final TestInfo info = setupTest("3.9-F-5", "binaryContentContentTypeAndType",
                "Fedora servers must use the value of the type attribute in the external content link as the media " +
                        "type of the external content, if provided. Any Content-Type header in the request should be " +
                        "ignored.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "proxy", "text/special")),
                new Header(SLUG, info.getId()));
        final String location = getLocation(doPost(uri, headers));


        // Type attribute supersedes Content-Type from external server
        doHead(location).then()
                .header("Content-Type", "text/special");
    }

    /**
     * 3.9-F-6 Content-Type and no type
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = { "SHOULD" })
    @Parameters({ "param1" })
    public void binaryContentContentTypeAndNoType(final String uri) {
        final TestInfo info = setupTest("3.9-F-6", "binaryContentContentTypeAndNoType",
                "Fedora servers must use the value of the type attribute in the external content link as the media " +
                        "type of the external content, if provided. Any Content-Type header in the request should be " +
                        "ignored.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        // Providing external content which does not return Content-Type
        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUriWithNoType, "proxy", "text/special")),
                new Header(SLUG, info.getId()));
        final String location = getLocation(doPost(uri, headers));

        doHead(location).then()
                .header("Content-Type", "text/special");
    }

    /**
     * 3.9-G-1 Guaranteed headers - describedby
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentGuaranteeHeadersDescribedBy(final String uri) {
        final TestInfo info = setupTest("3.9-G-1", "binaryContentGuaranteeHeadersDescribedBy",
                "A Fedora server receiving requests that would create or update an LDP-NR with content external to " +
                        "the request entity must reject request if it cannot guarantee all of the response headers " +
                        "required by the LDP-NR interaction model in this specification.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String location = createExternalBinary(uri, info.getId(), "proxy", null);

        final Response resp = doGet(location);
        assertTrue("Response does not contain link of rel type = describedby",
                getLinksOfRelType(resp, "describedby").count() > 0);
    }

    /**
     * 3.9-G-2 Guaranteed headers - Content-Type
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentGuaranteeHeadersContentType(final String uri) {
        final TestInfo info = setupTest("3.9-G-2", "binaryContentGuaranteeHeadersContentType",
                "A Fedora server receiving requests that would create or update an LDP-NR with content external to " +
                        "the request entity must reject request if it cannot guarantee all of the response headers " +
                        "required by the LDP-NR interaction model in this specification.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String location = createExternalBinary(uri, info.getId(), "proxy", "text/plain");

        doGet(location).then().header("Content-Type", "text/plain");
    }

    /**
     * 3.9-G-3 Guaranteed headers - Content-Length
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentGuaranteeHeadersContentLength(final String uri) {
        final TestInfo info = setupTest("3.9-G-3", "binaryContentGuaranteeHeadersContentLength",
                "A Fedora server receiving requests that would create or update an LDP-NR with content external to " +
                        "the request entity must reject request if it cannot guarantee all of the response headers " +
                        "required by the LDP-NR interaction model in this specification.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String location = createExternalBinary(uri, info.getId(), "proxy", "text/plain");

        doGet(location).then().header("Content-Length", is(notNullValue()));
    }

    /**
     * 3.9-G-4 Guaranteed headers - interaction model
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentGuaranteeHeadersInteractionModel(final String uri) {
        final TestInfo info = setupTest("3.9-G-4", "binaryContentGuaranteeHeadersInteractionModel",
                "A Fedora server receiving requests that would create or update an LDP-NR with content external to " +
                        "the request entity must reject request if it cannot guarantee all of the response headers " +
                        "required by the LDP-NR interaction model in this specification.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        final String location = createExternalBinary(uri, info.getId(), "proxy", "text/plain");

        final Response resp = doGet(location);
        assertTrue(getLinksOfRelTypeAsUris(resp, "type")
                .anyMatch(p -> p.toString().equals(NON_RDF_SOURCE_INTERACTION_MODEL)),
                "Interaction model link header of rel=type is required");
    }

    /**
     * 3.9.1 OPTIONS values
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentOptions(final String uri) {
        final TestInfo info = setupTest("3.9.1", "binaryContentOptions",
                "Fedora servers supporting external content MUST include \"Accept-External-Content-Handling\" " +
                        "header in response to \"OPTIONS\" request.",
                "https://fedora.info/2018/06/25/spec/#external-content-options", ps);

        doOptions(uri).then()
                .assertThat().header("Accept-External-Content-Handling", is(notNullValue()));
    }

    /**
     * 3.9.3-A-1 redirect want-digest header
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentRedirectWantDigest(final String uri) {
        final TestInfo info = setupTest("3.9.3-A-1", "binaryContentRedirectWantDigest",
                "Fedora servers supporting \"redirect\" external content types MUST correctly respond to the " +
                        "\"Want-Digest\" header.",
                "https://fedora.info/2018/06/25/spec/#redirect-and-proxy", ps);

        final String location = createExternalBinary(uri, info.getId(), "redirect", null);

        final String checksum = "md5;q=0.3,sha;q=1";
        final Response wantDigestResponse = doGetUnverified(location, new Header("Want-Digest", checksum));

        final Headers responseHeaders = wantDigestResponse.getHeaders();
        assertTrue(responseHeaders.getValue(DIGEST).contains("md5") ||
                responseHeaders.getValue(DIGEST).contains("sha"), "OK");
    }

    /**
     * 3.9.3-A-2 proxy want-digest header
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentProxyWantDigest(final String uri) {
        final TestInfo info = setupTest("3.9.3-A-2", "binaryContentProxyWantDigest",
                "Fedora servers supporting \"redirect\" external content types MUST correctly respond to the " +
                        "\"Want-Digest\" header.",
                "https://fedora.info/2018/06/25/spec/#redirect-and-proxy", ps);

        final String location = createExternalBinary(uri, info.getId(), "proxy", null);

        final String checksum = "md5;q=0.3,sha;q=1";
        final Response wantDigestResponse = doGet(location, new Header("Want-Digest", checksum));

        final Headers responseHeaders = wantDigestResponse.getHeaders();
        assertTrue(responseHeaders.getValue(DIGEST).contains("md5") ||
                responseHeaders.getValue(DIGEST).contains("sha"), "OK");
    }

    /**
     * 3.9.3-B-1 redirect content status code on GET
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentRedirectStatusGet(final String uri) {
        final TestInfo info = setupTest("3.9.3-B-1", "binaryContentRedirectStatusGet",
                "A successful response to a GET request for external content with handling of redirect " +
                        "must have status code of either 302 (Found) or 307 (Temporary Redirect)",
                "https://fedora.info/2018/06/25/spec/#redirect-and-proxy", ps);

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "redirect")),
                new Header(SLUG, info.getId()));
        final String location = getLocation(doPostUnverified(uri, headers));

        doGetUnverified(location).then()
                .statusCode(anyOf(is(302), is(307)));
    }

    /**
     * 3.9.3-B-2 redirect content status code on HEAD
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentRedirectStatusHead(final String uri) {
        final TestInfo info = setupTest("3.9.3-B-2", "binaryContentRedirectStatusHead",
                "A successful response to a HEAD request for external content with handling of redirect " +
                        "must have status code of either 302 (Found) or 307 (Temporary Redirect)",
                "https://fedora.info/2018/06/25/spec/#redirect-and-proxy", ps);

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "redirect")),
                new Header(SLUG, info.getId()));
        final String location = getLocation(doPostUnverified(uri, headers));

        doHeadUnverified(location, true).then()
                .statusCode(anyOf(is(302), is(307)));
    }

    private boolean externalContentSupported(final String uri) {
        if (extContentSupported == null) {
            final String header = doOptions(uri).header("Accept-External-Content-Handling");
            extContentSupported = header != null;
        }

        return extContentSupported;
    }

    private List<String> getAcceptedHandlingTypes(final String uri) {
        final String header = doOptions(uri).header("Accept-External-Content-Handling");
        if (header == null) {
            return emptyList();
        }
        return Arrays.asList(header.split("\\s+,\\s+"));
    }

    private String mockHttpResource(final String filename, final String type, final String content) {
        wireMockServer.stubFor(head(urlEqualTo("/" + filename))
                .willReturn(aResponse()
                        .withHeader("Content-Length", Long.toString(content.length()))
                        .withHeader("Content-Type", type)));
        wireMockServer.stubFor(get(urlEqualTo("/" + filename))
                .willReturn(aResponse()
                        .withHeader("Content-Length", Long.toString(content.length()))
                        .withHeader("Content-Type", type)
                        .withBody(content)));

        return "http://localhost:" + wireMockServer.port() + "/" + filename;
    }

    private String mockHttpResourceNoType(final String filename, final String content) {
        wireMockServer.stubFor(head(urlEqualTo("/" + filename))
                .willReturn(aResponse()
                        .withHeader("Content-Length", Long.toString(content.length()))));
        wireMockServer.stubFor(get(urlEqualTo("/" + filename))
                .willReturn(aResponse()
                        .withHeader("Content-Length", Long.toString(content.length()))
                        .withBody(content)));

        return "http://localhost:" + wireMockServer.port() + "/" + filename;
    }

    private String externalContentHeader(final String uri, final String handling) {
        return externalContentHeader(uri, handling, null);
    }

    private String externalContentHeader(final String uri, final String handling, final String type) {
        String link = "<" + uri + ">; rel=\"" + EXTERNAL_CONTENT_LINK_REL + "\"";
        if (handling != null) {
            link += "; handling=\"" + handling + "\"";
        }
        if (type != null) {
            link += "; type=\"" + type + "\"";
        }
        return link;
    }

    private String createExternalBinary(final String uri, final String id, final String handling,
            final String contentType) {
        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, handling, contentType)),
                new Header(SLUG, id));
        return getLocation(doPost(uri, headers));
    }

    private String createNonRdfSource(final String uri, final String id) {
        final Headers headers = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"postCreate.txt\""),
                new Header(SLUG, id));
        final Response resource = doPost(uri, headers, "TestString.");
        return getLocation(resource);
    }
}
