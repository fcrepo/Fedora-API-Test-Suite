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

import static org.fcrepo.spec.testsuite.test.Constants.BASIC_CONTAINER_BODY;
import static org.fcrepo.spec.testsuite.test.Constants.BASIC_CONTAINER_LINK_HEADER;
import static org.hamcrest.CoreMatchers.containsString;

import java.io.FileNotFoundException;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.response.Response;
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
    public void httpPut(final String uri) throws FileNotFoundException {
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
        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Disposition", "attachment; filename=\"postCreate.txt\"")
                                             .header("slug", info.getId())
                                             .body("TestString.")
                                             .when()
                                             .post(uri);
        final String locationHeader = resource.getHeader("Location");
        RestAssured.given().auth().basic(this.username, this.password)
                   .header("Content-Disposition", "attachment; filename=\"putUpdate.txt\"")
                   .header("Link", "<http://www.w3.org/ns/ldp#RDFSource>; rel=\"type\"")
                   .body("TestString2.")
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .put(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(409);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.6.1-A
     *
     * @param uri
     * @throws FileNotFoundException
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void httpPutpdateTriples(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.6.1-A", "httpPutpdateTriples",
                                        "Any LDP-RS must support PUT to update statements that are not server-managed" +
                                        " triples (as defined "
                                        + "in [LDP] 2). [LDP] 4.2.4.1 and 4.2.4.3 remain in effect.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-put-ldprs", ps);
        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .contentType("text/turtle")
                                             .header("Link", BASIC_CONTAINER_LINK_HEADER)
                                             .header("slug", info.getId())
                                             .body(BASIC_CONTAINER_BODY)
                                             .when()
                                             .post(uri);
        final String locationHeader = resource.getHeader("Location");

        final String body2 = RestAssured.given()
                                        .auth().basic(this.username, this.password)
                                        .when()
                                        .get(locationHeader).asString();

        final String newBody = body2.replace("Put class Container", "some-title");

        ps.append(newBody);
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("text/turtle")
                   .body(newBody)
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
     * 3.6.1-B
     *
     * @param uri
     * @throws FileNotFoundException
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void httpPutUpdateDisallowedTriples(final String uri) throws FileNotFoundException {
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
        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .contentType("text/turtle")
                                             .header("Link", BASIC_CONTAINER_LINK_HEADER)
                                             .header("slug", info.getId())
                                             .body(BASIC_CONTAINER_BODY)
                                             .when()
                                             .post(uri);

        final String locationHeader = resource.getHeader("Location");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("text/turtle")
                   .header("Link", BASIC_CONTAINER_LINK_HEADER)
                   .header("slug", "containedFolderSlug")
                   .body(BASIC_CONTAINER_BODY)
                   .when()
                   .post(locationHeader);

        final String body2 = RestAssured.given()
                                        .auth().basic(this.username, this.password)
                                        .when()
                                        .get(locationHeader).asString();

        final String newBody = body2.replace("containedFolderSlug", "some-name");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("text/turtle")
                   .body(newBody)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .put(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(409);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.6.1-C
     *
     * @param uri
     * @throws FileNotFoundException
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void httpPutUpdateDisallowedTriplesResponse(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.6.1-C", "httpPutUpdateDisallowedTriplesResponse",
                                        "The server must provide a corresponding response body containing information "
                                        + "about which statements could"
                                        + " not be persisted. ([LDP] 4.2.4.4 shouldbecomes must).",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-put-ldprs", ps);
        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .contentType("text/turtle")
                                             .header("Link", BASIC_CONTAINER_LINK_HEADER)
                                             .header("slug", info.getId())
                                             .body(BASIC_CONTAINER_BODY)
                                             .when()
                                             .post(uri);
        final String locationHeader = resource.getHeader("Location");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("text/turtle")
                   .header("Link", BASIC_CONTAINER_LINK_HEADER)
                   .header("slug", "containedFolderSlug")
                   .body(BASIC_CONTAINER_BODY)
                   .when()
                   .post(locationHeader);

        final String body2 = RestAssured.given()
                                        .auth().basic(this.username, this.password)
                                        .when()
                                        .get(locationHeader).asString();

        final String newBody = body2.replace("containedFolderSlug", "some-name");

        ps.append("PUT Request: \n");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("text/turtle")
                   .body(newBody)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .put(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(409).body(containsString("ldp#contains"));

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.6.1-D
     *
     * @param uri
     * @throws FileNotFoundException
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void httpPutUpdateDisallowedTriplesConstrainedByHeader(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.6.1-D", "httpPutUpdateDisallowedTriplesConstrainedByHeader",
                                        "In that response, the restrictions causing such a request to fail must be "
                                        + "described in a resource indicated"
                                        +
                                        " by a Link: rel=\"http://www.w3.org/ns/ldp#constrainedBy\" response header " +
                                        "per [LDP] 4.2.1.6.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-put-ldprs", ps);
        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .contentType("text/turtle")
                                             .header("Link", BASIC_CONTAINER_LINK_HEADER)
                                             .header("slug", info.getId())
                                             .body(BASIC_CONTAINER_BODY)
                                             .when()
                                             .post(uri);

        final String locationHeader = resource.getHeader("Location");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("text/turtle")
                   .header("Link", BASIC_CONTAINER_LINK_HEADER)
                   .header("slug", "containedFolderSlug")
                   .body(BASIC_CONTAINER_BODY)
                   .when()
                   .post(locationHeader);

        final String body2 = RestAssured.given()
                                        .auth().basic(this.username, this.password)
                                        .when()
                                        .get(locationHeader).asString();

        final String newBody = body2.replace("containedFolderSlug", "some-name");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("text/turtle")
                   .body(newBody)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .put(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(409).header("Link", containsString("constrainedBy"));

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.6.2-A
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void httpPutNR(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.6.2-A", "httpPutNR",
                                        "Any LDP-NR must support PUT to replace the binary content of that resource.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-put-ldpnr", ps);
        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Disposition", "attachment; filename=\"postCreate.txt\"")
                                             .header("slug", info.getId())
                                             .body("TestString.")
                                             .when()
                                             .post(uri);
        final String locationHeader = resource.getHeader("Location");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .header("Content-Disposition", "attachment; filename=\"putUpdate.txt\"")
                   .body("TestString2.")
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
     * 3.6.2-B
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void nonRDFSourcePutDigestResponseHeaderAuthentication(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.6.2-B", "nonRDFSourcePutDigestResponseHeaderAuthentication",
                                        "An HTTP PUT request that includes a Digest header (as described in " +
                                        "[RFC3230]) for which any "
                                        +
                                        "instance-digest in that header does not match the instance it describes, " +
                                        "must be rejected "
                                        + "with a 409 Conflict response.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-put-ldpnr", ps);
        final String checksum = "MD5=97c4627dc7734f65f5195f1d5f556d7a";
        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Disposition", "attachment; filename=\"digestAuth.txt\"")
                                             .header("slug", info.getId())
                                             .body("TestString.")
                                             .when()
                                             .post(uri);
        final String locationHeader = resource.getHeader("Location");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .header("digest", checksum)
                   .header("Content-Disposition", "attachment; filename=\"digestAuth.txt\"")
                   .body("TestString.")
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .put(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(409);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.6.2-C
     *
     * @param uri
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void nonRDFSourcePutDigestResponseHeaderVerification(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.6.2-C", "nonRDFSourcePutDigestResponseHeaderVerification",
                                        "An HTTP PUT request that includes an unsupported Digest type (as described " +
                                        "in [RFC3230]), should"
                                        + " be rejected with a 400 (Bad Request) response.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-put-ldpnr", ps);
        final String checksum = "abc=abc";
        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .header("Content-Disposition", "attachment; filename=\"postCreate.txt\"")
                                             .header("slug", info.getId())
                                             .body("TestString.")
                                             .when()
                                             .post(uri);
        final String locationHeader = resource.getHeader("Location");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .header("digest", checksum)
                   .header("Content-Disposition", "attachment; filename=\"putUpdate.txt\"")
                   .body("TestString2.")
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .put(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(400);

        ps.append("\n -Case End- \n").close();
    }

}
