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
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.http.message.BasicHeaderValueParser;
import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
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
     * Constructor
     * @throws IOException thrown if unable to create temp file
     */
    public ExternalBinaryContent() throws IOException {
        super();
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
    @AfterClass(alwaysRun = true)
    public void stopServer() {
        wireMockServer.stop();
    }

    /**
     * 3.9-A-1 PostCreate Copy
     */
    @Test(groups = {"SHOULD"})
    public void postCreateExternalBinaryContentCopy() {
        final TestInfo info = setupTest("3.9-A-1",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "copy")),
                new Header(SLUG, info.getId()));

        doPost(uri, headers);
    }

    /**
     * 3.9-A-1b PostCreate Copy File URI
     */
    @Test(groups = { "SHOULD" })
    public void postCreateExternalFileUriBinaryCopy() {
        final TestInfo info = setupTest("3.9-A-1b",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalFileUri, "copy")),
                new Header(SLUG, info.getId()));

        doPost(uri, headers);
    }

    /**
     * 3.9-A-2 PostCreate Redirect
     */
    @Test(groups = {"SHOULD"})
    public void postCreateExternalBinaryContentRedirect() {
        final TestInfo info = setupTest("3.9-A-2",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "redirect")),
                new Header(SLUG, info.getId()));
        doPost(uri, headers);
    }

    /**
     * 3.9-A-2b PostCreate Redirect File URI
     */
    @Test(groups = { "SHOULD" })
    public void postCreateExternalFileUriBinaryRedirect() {
        final TestInfo info = setupTest("3.9-A-2b",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalFileUri, "redirect")),
                new Header(SLUG, info.getId()));
        doPost(uri, headers);
    }

    /**
     * 3.9-A-3 PostCreate Proxy
     */
    @Test(groups = {"SHOULD"})
    public void postCreateExternalBinaryContentProxy() {
        final TestInfo info = setupTest("3.9-A-3",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "proxy")),
                new Header(SLUG, info.getId()));
        doPost(uri, headers);
    }

    /**
     * 3.9-A-3b PostCreate Proxy File URI
     */
    @Test(groups = { "SHOULD" })
    public void postCreateExternalFileUriBinaryProxy() {
        final TestInfo info = setupTest("3.9-A-3b",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalFileUri, "proxy")),
                new Header(SLUG, info.getId()));
        doPost(uri, headers);
    }

    /**
     * 3.9-B-1 PutCreate Copy
     */
    @Test(groups = {"SHOULD"})
    public void putCreateExternalBinaryContentCopy() {
        final TestInfo info = setupTest("3.9-B-1",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final String location = joinLocation(uri, info.getId());
        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "copy")));
        doPutUnverified(location, headers).then()
                .statusCode(201);
    }

    /**
     * 3.9-B-1b PutCreate Copy File URI
     */
    @Test(groups = { "SHOULD" })
    public void putCreateExternalFileUriBinaryCopy() {
        final TestInfo info = setupTest("3.9-B-1b",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final String location = joinLocation(uri, info.getId());
        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalFileUri, "copy")));
        doPutUnverified(location, headers).then()
                .statusCode(201);
    }

    /**
     * 3.9-B-2 PutCreate Redirect
     */
    @Test(groups = {"SHOULD"})
    public void putCreateExternalBinaryContentRedirect() {
        final TestInfo info = setupTest("3.9-B-2",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final String location = joinLocation(uri, info.getId());
        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "redirect")));
        doPutUnverified(location, headers).then()
                .statusCode(201);
    }

    /**
     * 3.9-B-2b PutCreate Redirect File URI
     */
    @Test(groups = { "SHOULD" })
    public void putCreateExternalFileUriBinaryRedirect() {
        final TestInfo info = setupTest("3.9-B-2b",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final String location = joinLocation(uri, info.getId());
        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalFileUri, "redirect")));
        doPutUnverified(location, headers).then()
                .statusCode(201);
    }

    /**
     * 3.9-B-3 PutCreate Proxy
     */
    @Test(groups = {"SHOULD"})
    public void putCreateExternalBinaryContentProxy() {
        final TestInfo info = setupTest("3.9-B-3",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final String location = joinLocation(uri, info.getId());
        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "proxy")));
        doPutUnverified(location, headers).then()
                .statusCode(201);
    }

    /**
     * 3.9-B-3b PutCreate Proxy File URI
     */
    @Test(groups = { "SHOULD" })
    public void putCreateExternalFileUriBinaryProxy() {
        final TestInfo info = setupTest("3.9-B-3b",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final String location = joinLocation(uri, info.getId());
        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalFileUri, "proxy")));
        doPutUnverified(location, headers).then()
                .statusCode(201);
    }

    /**
     * 3.9-C-1 PutUpdate Copy
     */
    @Test(groups = {"SHOULD"})
    public void putUpdateExternalBinaryContentCopy() {
        final TestInfo info = setupTest("3.9-C-1",
                "Fedora servers should support the update of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final String locationHeader = createNonRdfSource(uri, info.getId());

        final Headers headers2 = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "copy")));
        doPut(locationHeader, headers2);
    }

    /**
     * 3.9-C-1b PutUpdate Copy File URI
     */
    @Test(groups = { "SHOULD" })
    public void putUpdateExternalFileUriBinaryCopy() {
        final TestInfo info = setupTest("3.9-C-1b",
                "Fedora servers should support the update of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final String locationHeader = createNonRdfSource(uri, info.getId());

        final Headers headers2 = new Headers(
                new Header(LINK, externalContentHeader(externalFileUri, "copy")));
        doPut(locationHeader, headers2);
    }

    /**
     * 3.9-C-2 PutUpdate Redirect
     */
    @Test(groups = {"SHOULD"})
    public void putUpdateExternalBinaryContentRedirect() {
        final TestInfo info = setupTest("3.9-C-2",
                "Fedora servers should support the update of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final String locationHeader = createNonRdfSource(uri, info.getId());

        final Headers headers2 = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "redirect")));
        doPut(locationHeader, headers2);
    }

    /**
     * 3.9-C-2b PutUpdate Redirect File URI
     */
    @Test(groups = { "SHOULD" })
    public void putUpdateExternalFileUriBinaryRedirect() {
        final TestInfo info = setupTest("3.9-C-2b",
                "Fedora servers should support the update of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final String locationHeader = createNonRdfSource(uri, info.getId());

        final Headers headers2 = new Headers(
                new Header(LINK, externalContentHeader(externalFileUri, "redirect")));
        doPut(locationHeader, headers2);
    }

    /**
     * 3.9-C-3 PutUpdate Proxy
     */
    @Test(groups = {"SHOULD"})
    public void putUpdateExternalBinaryContentProxy() {
        final TestInfo info = setupTest("3.9-C-3",
                "Fedora servers should support the update of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final String locationHeader = createNonRdfSource(uri, info.getId());

        final Headers headers2 = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "proxy")));
        doPut(locationHeader, headers2);
    }

    /**
     * 3.9-C-3b PutUpdate Proxy File URI
     */
    @Test(groups = { "SHOULD" })
    public void putUpdateExternalFileUriBinaryProxy() {
        final TestInfo info = setupTest("3.9-C-3b",
                "Fedora servers should support the update of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final String locationHeader = createNonRdfSource(uri, info.getId());

        final Headers headers2 = new Headers(
                new Header(LINK, externalContentHeader(externalFileUri, "proxy")));
        doPut(locationHeader, headers2);
    }

    /**
     * 3.9-D-1 Unsupported status code
     */
    @Test(groups = {"MUST"})
    public void unsupportedExternalBinaryContentStatus() {
        final TestInfo info = setupTest("3.9-D-1",
                "Fedora servers that do not support the creation of LDP-NRs with content external must reject " +
                        "with a 4xx range status code",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (externalContentSupported(uri)) {
            throw new SkipException("External Binaries are supported");
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
     */
    @Test(groups = {"MUST"})
    public void unsupportedExternalBinaryContentConstraint() {
        final TestInfo info = setupTest("3.9-D-2",
                "Fedora servers that do not support the creation of LDP-NRs with content external must describe " +
                        "this restriction in a resource indicated by a " +
                        "rel=\"http://www.w3.org/ns/ldp#constrainedBy\" link in the Link response header.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (externalContentSupported(uri)) {
            throw new SkipException("External Binaries are supported");
        }

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "proxy")),
                new Header(SLUG, info.getId()));

        confirmPresenceOfConstrainedByLink(doPostUnverified(uri, headers));
    }

    /**
     * 3.9-E-1 handling attribute
     */
    @Test(groups = {"MUST"})
    public void externalBinaryContentHandlingAttribute() {
        final TestInfo info = setupTest("3.9-E-1",
                "Fedora servers must use the handling attribute in the external content link to determine how to " +
                        "process the request. At least one of the following handling attributes must be supported: " +
                        "copy, redirect, and/or proxy.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final List<String> acceptedHandlingTypes = getAcceptedHandlingTypes(uri);

        final List<String> acceptedAndExpected = acceptedHandlingTypes.stream()
                .filter(HANDLINGS::contains)
                .collect(Collectors.toList());

        assertThat("Accept-External-Content-Handling header contains at least one expected handling",
                acceptedAndExpected.size(), greaterThanOrEqualTo(1));

        // Test that all of the handlings specified by the server are supported
        for (final String handling : acceptedAndExpected) {
            final Headers headers = new Headers(
                    new Header(LINK, externalContentHeader(externalUri, handling)),
                    new Header(SLUG, info.getId()));

            doPost(uri, headers);
        }
    }

    /**
     * 3.9-E-2 handling status
     */
    @Test(groups = {"MUST"})
    public void externalBinaryContentHandlingStatus() {
        final TestInfo info = setupTest("3.9-E-2",
                "Fedora servers must reject with a 4xx range status code requests for which the handling attribute " +
                        "is not present or cannot be respected.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, null)),
                new Header(SLUG, info.getId()));

        doPostUnverified(uri, headers)
                .then()
                .statusCode(clientErrorRange());
    }

    /**
     * 3.9-E-3 unsupported handling
     */
    @Test(groups = {"MUST"})
    public void unsupportedBinaryContentHandlingAttribute() {
        final TestInfo info = setupTest("3.9-E-3",
                "In the case that the specified handling cannot be respected, the restrictions causing the request " +
                        "to fail must be described in a resource indicated by a " +
                        "rel=\"http://www.w3.org/ns/ldp#constrainedBy\" link in the Link response header.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "unsupported")),
                new Header(SLUG, info.getId()));

        final Response resp = doPostUnverified(uri, headers);
        resp.then().statusCode(clientErrorRange());
        confirmPresenceOfConstrainedByLink(resp);
    }

    /**
     * 3.9-F-1 media type
     */
    @Test(groups = {"MUST"})
    public void binaryContentMediaType() {
        final TestInfo info = setupTest("3.9-F-1",
                "Fedora servers must use the value of the type attribute in the external content link as the media " +
                        "type of the external content, if provided.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

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
     */
    @Test(groups = {"MAY"})
    public void binaryContentNoTypeExternalType() {
        final TestInfo info = setupTest("3.9-F-2",
                "Fedora servers must use the value of the type attribute in the external content link as the media " +
                        "type of the external content, if provided. If there is no type attribute: " +
                        "Servers may use the media type obtained when accessing the external content via the " +
                        "specified scheme (e.g. the Content-Type header for external content accessed via http).",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "proxy")),
                new Header(SLUG, info.getId()));
        final String location = getLocation(doPost(uri, headers));

        doHead(location).then()
                .header("Content-Type", "text/plain");
    }

    /**
     * 3.9-F-3 no type attribute - default
     */
    @Test(groups = {"MAY"})
    public void binaryContentNoTypeDefault() {
        final TestInfo info = setupTest("3.9-F-3",
                "Fedora servers must use the value of the type attribute in the external content link as the media " +
                        "type of the external content, if provided. If there is no type attribute: " +
                        "Servers may use a default media type.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

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
     */
    @Test(groups = {"MAY"})
    public void binaryContentNoTypeUnsupported() {
        final TestInfo info = setupTest("3.9-F-4",
                "Fedora servers must use the value of the type attribute in the external content link as the media " +
                        "type of the external content, if provided. If there is no type attribute: " +
                        "Servers may reject the request with a 4xx range status code.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUriWithNoType, "proxy")),
                new Header(SLUG, info.getId()));
        doPostUnverified(uri, headers).then()
                .statusCode(anyOf(clientErrorRange(), successRange()));
    }

    /**
     * 3.9-F-5 Content-Type and type
     */
    @Test(groups = {"SHOULD"})
    public void binaryContentContentTypeAndType() {
        final TestInfo info = setupTest("3.9-F-5",
                "Fedora servers must use the value of the type attribute in the external content link as the media " +
                        "type of the external content, if provided. Any Content-Type header in the request should be " +
                        "ignored.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "proxy", "text/special")),
                new Header("Content-Type", "text/plain"),
                new Header(SLUG, info.getId()));
        final String location = getLocation(doPost(uri, headers));


        // Type attribute supersedes Content-Type from external server
        doHead(location).then()
                .header("Content-Type", "text/special");
    }

    /**
     * 3.9-F-6 Content-Type and no type
     */
    @Test(groups = { "SHOULD" })
    public void binaryContentContentTypeAndNoType() {
        final TestInfo info = setupTest("3.9-F-6",
                "Fedora servers must use the value of the type attribute in the external content link as the media " +
                        "type of the external content, if provided. Any Content-Type header in the request should be " +
                        "ignored.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        // Providing external content which does not return Content-Type
        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUriWithNoType, "proxy", "text/special")),
                new Header("Content-Type", "text/plain"),
                new Header(SLUG, info.getId()));
        final String location = getLocation(doPost(uri, headers));

        doHead(location).then()
                .header("Content-Type", "text/special");
    }

    /**
     * 3.9-G-1 Guaranteed headers
     */
    @Test(groups = {"MUST"})
    public void binaryContentGuaranteeHeaders() {
        final TestInfo info = setupTest("3.9-G-1",
                "A Fedora server receiving requests that would create or update an LDP-NR with content external to " +
                        "the request entity must reject request if it cannot guarantee all of the response headers " +
                        "required by the LDP-NR interaction model in this specification.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final String location = createExternalBinary(uri, info.getId(), "proxy", null);

        final Response resp = doGet(location);

        resp.then()
                .header("Content-Type", "text/plain")
                .header("Content-Length", is(notNullValue()));
        assertTrue(getLinksOfRelTypeAsUris(resp, "type")
                .anyMatch(p -> p.toString().equals(NON_RDF_SOURCE_INTERACTION_MODEL)),
                "Interaction model link header of rel=type is required");
        assertTrue("Response does not contain link of rel type = describedby",
                getLinksOfRelType(resp, "describedby").count() > 0);
    }

    /**
     * 3.9.1 OPTIONS values
     */
    @Test(groups = {"MUST"})
    public void binaryContentOptions() {
        final TestInfo info = setupTest("3.9.1",
                "Fedora servers supporting external content MUST include \"Accept-External-Content-Handling\" " +
                        "header in response to \"OPTIONS\" request.",
                "https://fedora.info/2018/06/25/spec/#external-content-options", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        doOptions(uri).then()
                .assertThat().header("Accept-External-Content-Handling", is(notNullValue()));
    }

    /**
     * 3.9.3-A-1 redirect want-digest header
     */
    @Test(groups = {"MUST"})
    public void binaryContentRedirectWantDigest() {
        final TestInfo info = setupTest("3.9.3-A-1",
                "Fedora servers supporting \"redirect\" external content types MUST correctly respond to the " +
                        "\"Want-Digest\" header.",
                "https://fedora.info/2018/06/25/spec/#redirect-and-proxy", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final String location = createExternalBinary(uri, info.getId(), "redirect", null);

        final String checksum = "md5;q=0.3,sha;q=1";
        final Response wantDigestResponse = doGetUnverified(location, new Header("Want-Digest", checksum));

        final Headers responseHeaders = wantDigestResponse.getHeaders();
        assertTrue(responseHeaders.getValue(DIGEST).contains("md5") ||
                responseHeaders.getValue(DIGEST).contains("sha"),
                "Expected Want-Digest value not found");
    }

    /**
     * 3.9.3-A-2 proxy want-digest header
     */
    @Test(groups = {"MUST"})
    public void binaryContentProxyWantDigest() {
        final TestInfo info = setupTest("3.9.3-A-2",
                "Fedora servers supporting \"redirect\" external content types MUST correctly respond to the " +
                        "\"Want-Digest\" header.",
                "https://fedora.info/2018/06/25/spec/#redirect-and-proxy", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final String location = createExternalBinary(uri, info.getId(), "proxy", null);

        final String checksum = "md5;q=0.3,sha;q=1";
        final Response wantDigestResponse = doGet(location, new Header("Want-Digest", checksum));

        final Headers responseHeaders = wantDigestResponse.getHeaders();
        assertTrue(responseHeaders.getValue(DIGEST).contains("md5") ||
                responseHeaders.getValue(DIGEST).contains("sha"),
                "Expected Want-Digest value not found");
    }

    /**
     * 3.9.3-B-1 redirect content status code on GET
     */
    @Test(groups = {"MUST"})
    public void binaryContentRedirectStatusGet() {
        final TestInfo info = setupTest("3.9.3-B-1",
                "A successful response to a GET request for external content with handling of redirect " +
                        "must have status code of either 302 (Found) or 307 (Temporary Redirect)",
                "https://fedora.info/2018/06/25/spec/#redirect-and-proxy", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

        final Headers headers = new Headers(
                new Header(LINK, externalContentHeader(externalUri, "redirect")),
                new Header(SLUG, info.getId()));
        final String location = getLocation(doPostUnverified(uri, headers));

        doGetUnverified(location).then()
                .statusCode(anyOf(is(302), is(307)));
    }

    /**
     * 3.9.3-B-2 redirect content status code on HEAD
     */
    @Test(groups = {"MUST"})
    public void binaryContentRedirectStatusHead() {
        final TestInfo info = setupTest("3.9.3-B-2",
                "A successful response to a HEAD request for external content with handling of redirect " +
                        "must have status code of either 302 (Found) or 307 (Temporary Redirect)",
                "https://fedora.info/2018/06/25/spec/#redirect-and-proxy", ps);

        if (!externalContentSupported(uri)) {
            throw new SkipException("External Binaries are NOT supported");
        }

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
        return Arrays.stream(BasicHeaderValueParser.parseElements(header, null))
                .map(Objects::toString).collect(Collectors.toList());
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
