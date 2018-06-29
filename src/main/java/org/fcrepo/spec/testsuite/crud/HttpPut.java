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
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class HttpPut extends AbstractTest {

    /**
     * Authentication
     *
     * @param username
     * @param password
     */
    @Parameters({"param2", "param3"})
    public HttpPut(final String username, final String password) {
        super(username, password);
    }

    /**
     * 3.6-B
     *
     * @param uri
     */
    @Test(groups = {"MAY"})
    @Parameters({"param1"})
    public void httpPut(final String uri) {
        final TestInfo info = setupTest("3.6-B", "httpPut",
                                        "When accepting a PUT request against an existant resource, an HTTP Link: " +
                                        "rel=\"type\" header "
                                        +
                                        "may be included. If that type is a value in the LDP namespace and is not " +
                                        "either a current "
                                        +
                                        "type of the resource or a subtype of a current type of the resource, the " +
                                        "request must be "
                                        + "rejected with a 409 Conflict response.",
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
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void httpPutpdateTriples(final String uri) {
        final TestInfo info = setupTest("3.6.1-A", "httpPutpdateTriples",
                                        "Any LDP-RS must support PUT to update statements that are not server-managed" +
                                        " triples (as defined "
                                        + "in [LDP] 2). [LDP] 4.2.4.1 and 4.2.4.3 remain in effect.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-put-ldprs", ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resource);

        final String body2 = doGet(locationHeader).asString();

        final String newBody = body2.replace("Put class Container", "some-title");

        ps.append(newBody);
        doPut(locationHeader, new Headers(new Header("Content-Type", "text/turtle")), newBody);
    }

    /**
     * 3.6.1-B
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void httpPutUpdateDisallowedTriples(final String uri) {
        final TestInfo info = setupTest("3.6.1-B", "httpPutUpdateDisallowedTriples",
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

        final String body2 = doGet(locationHeader).asString();

        final String newBody = body2.replace("containedFolderSlug", "some-name");

        doPutUnverified(locationHeader, new Headers(new Header("Content-Type", "text/turtle")), newBody)
                .then()
                .statusCode(409);
    }

    /**
     * 3.6.1-C
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void httpPutUpdateDisallowedTriplesResponse(final String uri) {
        final TestInfo info = setupTest("3.6.1-C", "httpPutUpdateDisallowedTriplesResponse",
                                        "The server must provide a corresponding response body containing information "
                                        + "about which statements could"
                                        + " not be persisted. ([LDP] 4.2.4.4 should becomes must).",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-put-ldprs", ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resource);

        createBasicContainer(locationHeader, "containedFolderSlug", BASIC_CONTAINER_BODY);

        final String body2 = doGet(locationHeader).asString();

        final String newBody = body2.replace("containedFolderSlug", "conflicting-contained-resource");

        ps.append("PUT Request: \n");
        doPutUnverified(locationHeader, new Headers(new Header("Content-Type", "text/turtle")), newBody)
                .then()
                .statusCode(409)
                .body(containsString("contains"))
                .body(containsString("conflicting-contained-resource"));
    }

    /**
     * 3.6.1-D
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void httpPutUpdateDisallowedTriplesConstrainedByHeader(final String uri) {
        final TestInfo info = setupTest("3.6.1-D", "httpPutUpdateDisallowedTriplesConstrainedByHeader",
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

        final String body2 = doGet(locationHeader).asString();

        final String newBody = body2.replace(containedFolderSlug, "some-name");

        doPutUnverified(locationHeader, new Headers(new Header("Content-Type", "text/turtle")), newBody)
                .then()
                .statusCode(409)
                .header("Link", containsString("constrainedBy"));
    }

    /**
     * 3.6.2-A
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void httpPutNR(final String uri) {
        final TestInfo info = setupTest("3.6.2-A", "httpPutNR",
                                        "Any LDP-NR must support PUT to replace the binary content of that resource.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-put-ldpnr", ps);
        final Headers headers = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"postCreate.txt\""),
                new Header(SLUG, info.getId()));
        final Response resource = doPost(uri, headers, "TestString.");
        final String locationHeader = getLocation(resource);
        doPut(locationHeader,
                new Headers(new Header(CONTENT_DISPOSITION, "attachment; filename=\"putUpdate.txt\"")),
                "TestString2.");
    }

    /**
     * 3.6.2-B
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void nonRDFSourcePutDigestResponseHeaderAuthentication(final String uri) {
        final TestInfo info = setupTest("3.6.2-B", "nonRDFSourcePutDigestResponseHeaderAuthentication",
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
     * @param uri
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void nonRDFSourcePutDigestResponseHeaderVerification(final String uri) {
        final TestInfo info = setupTest("3.6.2-C", "nonRDFSourcePutDigestResponseHeaderVerification",
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

}
