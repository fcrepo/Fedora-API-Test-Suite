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

import static org.fcrepo.spec.testsuite.Constants.APPLICATION_SPARQL_UPDATE;
import static org.fcrepo.spec.testsuite.Constants.BASIC_CONTAINER_BODY;
import static org.hamcrest.CoreMatchers.containsString;

import java.io.FileNotFoundException;
import java.io.IOException;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.LogConfig;
import io.restassured.http.ContentType;
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
public class HttpPatch extends AbstractTest {

    public String body = "PREFIX dcterms: <http://purl.org/dc/terms/>"
                         + " INSERT {"
                         + " <> dcterms:description \"Patch Updated Description\" ."
                         + "}"
                         + " WHERE { }";
    public String ldpatch = "@prefix dcterms: <http://purl.org/dc/terms/>"
                            + "Add {"
                            + " <#> dcterms:description \"Patch LDP Updated Description\" ;"
                            + "} .";
    public String serverProps = "PREFIX fedora: <http://fedora.info/definitions/v4/repository#>"
                                + " INSERT {"
                                + " <> fedora:lastModifiedBy \"User\" ."
                                + "}"
                                + " WHERE { }";
    public String resourceType = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                                 + " PREFIX ldp: <http://www.w3.org/ns/ldp#>"
                                 + " INSERT {"
                                 + " <> rdf:type ldp:NonRDFSource ."
                                 + "}"
                                 + " WHERE { }";
    public String updateContainmentTriples = "PREFIX ldp: <http://www.w3.org/ns/ldp#>\n"
                                             + " INSERT {   \n"
                                             + "  <> ldp:contains \"some-url\" .\n"
                                             + "}\n"
                                             + " WHERE { }";

    /**
     * Authentication
     *
     * @param username
     * @param password
     */
    @Parameters({"param2", "param3"})
    public HttpPatch(final String username, final String password) {
        super(username, password);
    }

    /**
     * 3.7-A
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void supportPatch(final String uri) throws IOException {
        final TestInfo info = setupTest("3.7-A", "supportPatch",
                                        "Any LDP-RS must support PATCH ([LDP] 4.2.7 may becomes must). " +
                                        "[sparql11-update] must be an accepted "
                                        + "content-type for PATCH.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-patch",
                                        ps);
        final Response resource = createBasicContainer(uri, info.getId(), BASIC_CONTAINER_BODY);
        final String locationHeader = getLocation(resource);
        createRequestAuthOnly(APPLICATION_SPARQL_UPDATE)
            .config(RestAssured.config()
                               .encoderConfig(new EncoderConfig()
                                                  .encodeContentTypeAs(APPLICATION_SPARQL_UPDATE,
                                                                       ContentType.TEXT))
                               .logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .when()
            .request().body(body)
            .patch(locationHeader)
            .then()
            .log().all()
            .statusCode(204);

    }

    /**
     * 3.7-B
     *
     * @param uri
     */
    @Test(groups = {"MAY"})
    @Parameters({"param1"})
    public void ldpPatchContentTypeSupport(final String uri) throws IOException {
        final TestInfo info = setupTest("3.7-B", "ldpPatchContentTypeSupport",
                                        "Other content-types (e.g. [ldpatch]) may be available.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-patch", ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resource);
        createRequestAuthOnly("text/ldpatch")
            .config(RestAssured.config().encoderConfig(new EncoderConfig()
                                                           .encodeContentTypeAs("text/ldpatch",
                                                                                ContentType.TEXT))
                               .logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .body(ldpatch)
            .when()
            .patch(locationHeader)
            .then()
            .log().all()
            .statusCode(204);

    }

    /**
     * 3.7-C
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void serverManagedPropertiesModification(final String uri) throws IOException {
        final TestInfo info = setupTest("3.7-C", "serverManagedPropertiesModification",
                                        "If an otherwise valid HTTP PATCH request is received that attempts to modify "
                                        + "statements to a resource that a server disallows (not ignores per [LDP] "
                                        + "4.2.4.1), the server must fail the request by responding with a 4xx range"
                                        + " status code (e.g. 409 Conflict).",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-patch", ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resource);
        createRequestAuthOnly(APPLICATION_SPARQL_UPDATE)
            .config(RestAssured.config().encoderConfig(new EncoderConfig()
                                                           .encodeContentTypeAs(
                                                               APPLICATION_SPARQL_UPDATE,
                                                               ContentType.TEXT))
                               .logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .body(serverProps)
            .when()
            .patch(locationHeader)
            .then()
            .log().all()
            .statusCode(409);

    }

    /**
     * 3.7-D
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void statementNotPersistedResponseBody(final String uri) throws IOException {
        final TestInfo info = setupTest("3.7-D", "statementNotPersistedResponseBody",
                                        "The server must provide a corresponding response body containing information"
                                        + " about which statements could not be persisted."
                                        + " ([LDP] 4.2.4.4 should becomes must).",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-patch", ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resource);
        createRequestAuthOnly(APPLICATION_SPARQL_UPDATE)
            .config(RestAssured.config().encoderConfig(new EncoderConfig()
                                                           .encodeContentTypeAs(
                                                               APPLICATION_SPARQL_UPDATE,
                                                               ContentType.TEXT))
                               .logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .body(serverProps)
            .when()
            .patch(locationHeader)
            .then()
            .log().all()
            .statusCode(409).body(containsString("lastModified"));

    }

    /**
     * 3.7-E
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void statementNotPersistedConstrainedBy(final String uri) throws IOException {
        final TestInfo info = setupTest("3.7-E", "statementNotPersistedConstrainedBy",
                                        "In that response, the restrictions causing such a request to fail must be"
                                        + " described in a resource indicated by a Link: "
                                        + "rel=\"http://www.w3.org/ns/ldp#constrainedBy\" "
                                        + "response header per [LDP] 4.2.1.6.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-patch", ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resource);
        createRequestAuthOnly(APPLICATION_SPARQL_UPDATE)
            .config(RestAssured.config().encoderConfig(new EncoderConfig()
                                                           .encodeContentTypeAs(
                                                               APPLICATION_SPARQL_UPDATE,
                                                               ContentType.TEXT))
                               .logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .body(serverProps)
            .when()
            .patch(locationHeader)
            .then()
            .log().all()
            .statusCode(409).header("Link", containsString("constrainedBy"));

    }

    /**
     * 3.7-F
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void successfulPatchStatusCode(final String uri) throws IOException {
        final TestInfo info = setupTest("3.7-F", "successfulPatchStatusCode",
                                        "A successful PATCH request must respond with a 2xx status code; the "
                                        + "specific code in the 2xx range may vary according to the response "
                                        + "body or request state.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-patch", ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resource);
        ps.append("Request method:\tPATCH\n");
        ps.append("Request URI:\t" + uri);
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("\t\t\t\tContent-Type=application/sparql-update; charset=ISO-8859-1\n");
        ps.append("Body:\n");
        ps.append(body + "\n");

        final Response response =
            createRequestAuthOnly(APPLICATION_SPARQL_UPDATE)
                .config(RestAssured.config().encoderConfig(new EncoderConfig().encodeContentTypeAs(
                    APPLICATION_SPARQL_UPDATE,
                    ContentType.TEXT)))
                .body(body)
                .when()
                .patch(locationHeader);

        final int statusCode = response.getStatusCode();
        final Headers headers = response.getHeaders();

        ps.append("HTTP/1.1 " + statusCode + "\n");
        for (Header h : headers) {
            ps.append(h.getName() + ": " + h.getValue() + "\n");
        }

        String str = "";
        boolean err = false;
        if (statusCode >= 200 && statusCode < 300) {
            str = "\n" + response.asString();
        } else {
            err = true;
            str = "\nThe response status code is not a valid successful status code.\n\n";
            str += response.asString();
        }

        ps.append(str);
        if (err) {

            throw new AssertionError("\nThe response status code is not a valid successful status code for PATCH.");
        }

    }

    /**
     * 3.7.1
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void disallowPatchContainmentTriples(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.7.1", "disallowPatchContainmentTriples",
                                        "The server should not allow HTTP PATCH to update an LDPC’s containment " +
                                        "triples; if"
                                        + " the server receives such a request, it should respond with a"
                                        + " 409 (Conflict) status code.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-patch-containment-triples",
                                        ps);
        final Response container = createBasicContainer(uri, info);
        final String locationHeader = getLocation(container);
        createBasicContainer(locationHeader, info.getId(), BASIC_CONTAINER_BODY);

        createRequestAuthOnly(APPLICATION_SPARQL_UPDATE)
            .config(RestAssured.config().encoderConfig(new EncoderConfig()
                                                           .encodeContentTypeAs(
                                                               APPLICATION_SPARQL_UPDATE,
                                                               ContentType.TEXT))
                               .logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .body(updateContainmentTriples)
            .when()
            .patch(locationHeader)
            .then()
            .log().all()
            .statusCode(409);

    }

    /**
     * 3.7.2
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void disallowChangeResourceType(final String uri) throws IOException {
        final TestInfo info = setupTest("3.7.2", "disallowChangeResourceType",
                                        "The server must disallow a PATCH request that would change the LDP"
                                        + " interaction model of a resource to a type that is not a subtype"
                                        + " of the current resource type. That request must be rejected"
                                        + " with a 409 Conflict response.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-patch-ixn-models", ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resource);
        createRequestAuthOnly(APPLICATION_SPARQL_UPDATE)
            .config(RestAssured.config().encoderConfig(new EncoderConfig()
                                                           .encodeContentTypeAs(
                                                               APPLICATION_SPARQL_UPDATE,
                                                               ContentType.TEXT))
                               .logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .body(resourceType)
            .when()
            .patch(locationHeader)
            .then()
            .log().all()
            .statusCode(409);

    }
}
