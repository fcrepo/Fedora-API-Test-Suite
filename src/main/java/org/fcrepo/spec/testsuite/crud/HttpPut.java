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

import static org.fcrepo.spec.testsuite.App.ROOT_CONTROLLER_USER_WEBID_PARAM;
import static org.fcrepo.spec.testsuite.App.TEST_CONTAINER_URL_PARAM;
import static org.fcrepo.spec.testsuite.Constants.BASIC_CONTAINER_BODY;
import static org.fcrepo.spec.testsuite.Constants.CONTENT_DISPOSITION;
import static org.fcrepo.spec.testsuite.Constants.DIGEST;
import static org.fcrepo.spec.testsuite.Constants.SLUG;
import static org.hamcrest.CoreMatchers.containsString;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Link;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class HttpPut extends AbstractTest {

    /**
     * Authentication
     *
     * @param rootControllerUserWebId The repository rootControllerUserWebId
     */
    @Parameters({ROOT_CONTROLLER_USER_WEBID_PARAM})
    public HttpPut(final String rootControllerUserWebId) {
        super(rootControllerUserWebId);
    }

    /**
     * 3.6-A
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MAY"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void httpPutChangeTypeAllowed(final String uri) {
        final TestInfo info = setupTest("3.6-A", "httpPutChangeTypeAllowed",
                "Implementations MAY allow the interaction model of an existing " +
                        "resource to be changed by specification of a new LDP type in a " +
                        "rel=\"type\" link in the HTTP Link header",
                "https://fcrepo.github.io/fcrepo-specification/#http-put", ps);

        final Headers headers = new Headers(
                new Header("Link", "<http://www.w3.org/ns/ldp#RDFSource>; rel=\"type\""),
                new Header(SLUG, info.getId()));
        final Response resource = doPost(uri, headers);

        final String locationHeader = getLocation(resource);
        final Headers headers1 = new Headers(
                new Header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\""));
        doPutUnverified(locationHeader, headers1)
                .then()
                .statusCode(successRange());
    }

    /**
     * 3.6-B
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MAY"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void httpPutChangeTypeNotAllowed(final String uri) {
        final TestInfo info = setupTest("3.6-B",
                                        "When accepting a PUT request against an existant resource, an HTTP Link: " +
                                        "rel=\"type\" header may be included. If that type is a value in the LDP " +
                                        "namespace and is not either a current type of the resource or a subtype " +
                                        "of a current type of the resource, the request must be " +
                                        "rejected with a 409 Conflict response.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-put", ps);
        final Headers headers = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"postCreate.txt\""),
                new Header(SLUG, info.getId()));
        final Response resource = doPost(uri, headers, "TestString.");

        final String locationHeader = getLocation(resource);
        final Headers headers1 = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"putUpdate.txt\""),
                new Header("Link", "<http://www.w3.org/ns/ldp#RDFSource>; rel=\"type\""));
        doPutUnverified(locationHeader, headers1, "TestString2.")
                .then()
                .statusCode(409);
    }

    /**
     * 3.6.1-A
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void httpPutUpdateTriples(final String uri) {
        final TestInfo info = setupTest("3.6.1-A",
                                        "Any LDP-RS must support PUT to update statements that are not server-managed" +
                                        " triples (as defined "
                                        + "in [LDP] 2). [LDP] 4.2.4.1 and 4.2.4.3 remain in effect.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-put-ldprs", ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resource);

        final Response getResponse = doGet(locationHeader);
        final String body2 = getResponse.asString();
        final String etag = getETag(getResponse);

        final String newBody = body2.replace("Put class Container", "some-title");

        ps.append("ETag: ");
        ps.append(etag);
        ps.append("\n");
        ps.append("new body: ");
        ps.append(newBody);
        ps.append("\n");

        // Add 'If-Match' header if ETag is available
        final EntityTag entityTag = etag == null ? null : EntityTag.valueOf(etag);
        final Headers headers;
        if (entityTag != null && !entityTag.isWeak()) {
            ps.append("Using entityTag");
            headers = new Headers(
                    new Header("Content-Type", "text/turtle"),
                    new Header("If-Match", "\"" + entityTag.getValue() + "\"")
            );
        } else {
            headers = new Headers(new Header("Content-Type", "text/turtle"));
        }

        doPut(locationHeader, headers, newBody);
    }

    /**
     * 3.6.1-B
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void httpPutUpdateDisallowedTriples(final String uri) {
        final TestInfo info = setupTest("3.6.1-B",
                                        "If an otherwise valid HTTP PUT request is received that attempts to modify " +
                                        "resource "
                                        + "statements that a server"
                                        +
                                        " disallows (not ignores per [LDP] 4.2.4.1), the server must fail the request" +
                                        " by "
                                        + "responding with a 4xx "
                                        + "range status code (e.g. 409 Conflict).",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-put-ldprs", ps);
        final Response resource = createBasicContainer(uri, info);

        final String locationHeader = getLocation(resource);

        createBasicContainer(locationHeader, "containedFolderSlug", BASIC_CONTAINER_BODY);

        final Response getResponse = doGet(locationHeader);
        final String body2 = getResponse.asString();
        final String etag = getETag(getResponse);

        final String newBody = body2.replace("containedFolderSlug", "some-name");

        // Add 'If-Match' header if ETag is available
        final EntityTag entityTag = etag == null ? null : EntityTag.valueOf(etag);
        final Headers headers;
        if (entityTag != null && !entityTag.isWeak()) {
            ps.append("Using entityTag: ");
            ps.append(entityTag.getValue());
            headers = new Headers(
                    new Header("Content-Type", "text/turtle"),
                    new Header("If-Match", "\"" + entityTag.getValue() + "\"")
            );
        } else {
            headers = new Headers(new Header("Content-Type", "text/turtle"));
        }

        doPutUnverified(locationHeader, headers, newBody)
                .then()
                .statusCode(clientErrorRange());
    }

    /**
     * 3.6.1-C
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void httpPutUpdateDisallowedTriplesResponse(final String uri) {
        final TestInfo info = setupTest("3.6.1-C",
                                        "The server must provide a corresponding response body containing information "
                                        + "about which statements could"
                                        + " not be persisted. ([LDP] 4.2.4.4 should becomes must).",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-put-ldprs", ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resource);

        createBasicContainer(locationHeader, "containedFolderSlug", BASIC_CONTAINER_BODY);

        final Response getResponse = doGet(locationHeader);
        final String body2 = getResponse.getBody().asString();
        final String etag = getETag(getResponse);

        final String newBody = body2.replace("containedFolderSlug", "conflicting-contained-resource");

        ps.append("PUT Request: \n");

        // Add 'If-Match' header if ETag is available
        final EntityTag entityTag = etag == null ? null : EntityTag.valueOf(etag);
        final Headers headers;
        if (entityTag != null && !entityTag.isWeak()) {
            ps.append("Using entityTag: ");
            ps.append(entityTag.getValue());
            headers = new Headers(
                    new Header("Content-Type", "text/turtle"),
                    new Header("If-Match", "\"" + entityTag.getValue() + "\"")
            );
        } else {
            headers = new Headers(new Header("Content-Type", "text/turtle"));
        }
        doPutUnverified(locationHeader, headers, newBody)
                .then()
                .statusCode(409)
                .body(containsString("contains"));
    }

    /**
     * 3.6.1-D
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void httpPutUpdateDisallowedTriplesConstrainedByHeader(final String uri) {
        final TestInfo info = setupTest("3.6.1-D",
                                        "In that response, the restrictions causing such a request to fail must be "
                                        + "described in a resource indicated"
                                        +
                                        " by a Link: rel=\"http://www.w3.org/ns/ldp#constrainedBy\" response header " +
                                        "per [LDP] 4.2.1.6.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-put-ldprs", ps);
        final Response resource = createBasicContainer(uri, info);

        final String locationHeader = getLocation(resource);

        final String containedFolderSlug = "containedFolderSlug";

        createBasicContainer(locationHeader, containedFolderSlug);

        final Response getResponse = doGet(locationHeader);
        final String body2 = getResponse.asString();
        final String etag = getETag(getResponse);

        final String newBody = body2.replace(containedFolderSlug, "some-name");

        // Add 'If-Match' header if ETag is available
        final EntityTag entityTag = etag == null ? null : EntityTag.valueOf(etag);
        final Headers headers;
        if (entityTag != null && !entityTag.isWeak()) {
            ps.append("Using entityTag: ");
            ps.append(entityTag.getValue());
            headers = new Headers(
                    new Header("Content-Type", "text/turtle"),
                    new Header("If-Match", "\"" + entityTag.getValue() + "\"")
            );
        } else {
            headers = new Headers(new Header("Content-Type", "text/turtle"));
        }
        doPutUnverified(locationHeader, headers, newBody)
                .then()
                .statusCode(409)
                .header("Link", containsString("constrainedBy"));
    }

    /**
     * 3.6.2-A
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void httpPutNR(final String uri) {
        final TestInfo info = setupTest("3.6.2-A",
                                        "Any LDP-NR must support PUT to replace the binary content of that resource.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-put-ldpnr", ps);
        final Headers headers = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"postCreate.txt\""),
                new Header(SLUG, info.getId()));
        final Response resource = doPost(uri, headers, "TestString.");
        final String locationHeader = getLocation(resource);

        final Response getResponse = doGet(locationHeader);
        final String etag = getETag(getResponse);

        // Add 'If-Match' header if ETag is available
        final EntityTag entityTag = etag == null ? null : EntityTag.valueOf(etag);
        final Headers headers2;
        if (entityTag != null && !entityTag.isWeak()) {
            ps.append("Using entityTag: ");
            ps.append(entityTag.getValue());
            headers2 = new Headers(
                    new Header(CONTENT_DISPOSITION, "attachment; filename=\"putUpdate.txt\""),
                    new Header("If-Match", "\"" + entityTag.getValue() + "\"")
            );
        } else {
            headers2 = new Headers(new Header(CONTENT_DISPOSITION, "attachment; filename=\"putUpdate.txt\""));
        }

        doPut(locationHeader, headers2, "TestString2.");
    }

    /**
     * 3.6.2-B
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void nonRDFSourcePutDigestResponseHeaderAuthentication(final String uri) {
        final TestInfo info = setupTest("3.6.2-B",
                                        "An HTTP PUT request that includes a Digest header (as described in " +
                                        "[RFC3230]) for which any "
                                        +
                                        "instance-digest in that header does not match the instance it describes, " +
                                        "must be rejected "
                                        + "with a 409 Conflict response.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-put-ldpnr", ps);
        final String checksum = "MD5=97c4627dc7734f65f5195f1d5f556d7a";
        final Headers headers = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"digestAuth.txt\""),
                new Header(SLUG, info.getId()));
        final Response resource = doPost(uri, headers, "TestString.");
        final String locationHeader = getLocation(resource);

        final Headers headers1 = new Headers(
                new Header(DIGEST, checksum),
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"digestAuth.txt\""));
        doPutUnverified(locationHeader, headers1, "TestString.")
                .then()
                .statusCode(409);
    }

    /**
     * 3.6.2-C
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"SHOULD"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void nonRDFSourcePutDigestResponseHeaderVerification(final String uri) {
        final TestInfo info = setupTest("3.6.2-C",
                                        "An HTTP PUT request that includes an unsupported Digest type (as described " +
                                        "in [RFC3230]), should"
                                        + " be rejected with a 400 (Bad Request) response.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-put-ldpnr", ps);
        final String checksum = "abc=abc";
        final Headers headers = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"postCreate.txt\""),
                new Header(SLUG, info.getId()));
        final Response resource = doPost(uri, headers, "TestString.");
        final String locationHeader = getLocation(resource);

        final Headers headers1 = new Headers(
                new Header(DIGEST, checksum),
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"putUpdate.txt\""));
        doPutUnverified(locationHeader, headers1, "TestString2.")
                .then()
                .statusCode(400);
    }

    /**
     * 3.6.3-A
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MAY"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void putCreateRDFSource(final String uri) {
        final TestInfo info = setupTest("3.6.3-A",
                "Implementations may accept HTTP PUT to create resources",
                "https://fcrepo.github.io/fcrepo-specification/#http-put-create", ps);

        final String location = joinLocation(uri, info.getId());

        doPutUnverified(location)
                .then()
                .statusCode(201);
    }

    /**
     * 3.6.3-B create non-rdf source
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MAY"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void putCreateNonRDFSource(final String uri) {
        final TestInfo info = setupTest("3.6.3-B",
                "Implementations may accept HTTP PUT to create non-RDF resources",
                "https://fcrepo.github.io/fcrepo-specification/#http-put-create", ps);

        final String location = joinLocation(uri, info.getId());

        final Headers headers = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"putCreate.txt\""));

        doPutUnverified(location, headers)
                .then()
                .statusCode(201);
    }

    /**
     * 3.6.3-C put create ldprs for non-rdf source
     *
     * @param uri The repository root URI
     */
    @Test(groups = { "MUST" })
    @Parameters({ TEST_CONTAINER_URL_PARAM })
    public void putNonRDFSourceCreateLDPRS(final String uri) {
        final TestInfo info = setupTest("3.6.3-C",
                "On creation of an LDP-NR with HTTP PUT, implementations MUST create " +
                        "an associated LDP-RS describing that LDP-NR in the same way that it " +
                        "would when 3.5.1 Creating LDP-NRs with HTTP POST",
                "https://fcrepo.github.io/fcrepo-specification/#http-put-create", ps);

        final String location = joinLocation(uri, info.getId());

        final Headers headers = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"putCreate.txt\""));

        final Response resp = doPutUnverified(location, headers, "content");
        if (resp.statusCode() != 201) {
            throw new SkipException("PUT not supported");
        }
        final Link describedby = getLinksOfRelType(resp, "describedby").findFirst().get();
        doGet(describedby.getUri().toString());
    }
}
