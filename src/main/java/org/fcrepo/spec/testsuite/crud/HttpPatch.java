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
import static org.hamcrest.CoreMatchers.notNullValue;

import java.io.File;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.fcrepo.spec.testsuite.TestParameters;
import org.testng.annotations.Test;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class HttpPatch extends AbstractTest {

    private final String body = "PREFIX dcterms: <http://purl.org/dc/terms/>"
                         + " INSERT {"
                         + " <> dcterms:description \"Patch Updated Description\" ."
                         + "}"
                         + " WHERE { }";
    private final String ldpatch = "@prefix dcterms: <http://purl.org/dc/terms/>"
                            + "Add {"
                            + " <#> dcterms:description \"Patch LDP Updated Description\" ;"
                            + "} .";
    private final String resourceType = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                                 + " PREFIX ldp: <http://www.w3.org/ns/ldp#>"
                                 + " INSERT {"
                                 + " <> rdf:type ldp:NonRDFSource ."
                                 + "}"
                                 + " WHERE { }";
    private final String updateContainmentTriples = "PREFIX ldp: <http://www.w3.org/ns/ldp#>\n"
                                             + " INSERT {   \n"
                                             + "  <> ldp:contains \"some-url\" .\n"
                                             + "}\n"
                                             + " WHERE { }";

    private String constraintErrorGeneratingSparqlQuery = null;

    /**
     * Constructor
     */
    public HttpPatch() {
        super();
        this.constraintErrorGeneratingSparqlQuery = TestParameters.get().getConstraintErrorGenerator();
    }

    /**
     * 3.7-A
     */
    @Test(groups = {"MUST"})
    public void supportPatch() {
        final TestInfo info = setupTest("3.7-A",
                                        "Any LDP-RS must support PATCH ([LDP] 4.2.7 may becomes must). " +
                                        "[sparql11-update] must be an accepted "
                                        + "content-type for PATCH.",
                                        SPEC_BASE_URL + "#http-patch",
                                        ps);
        final Response resource = createBasicContainer(uri, info.getId(), BASIC_CONTAINER_BODY);
        final String locationHeader = getLocation(resource);
        final Headers headers = new Headers(new Header("Content-Type", APPLICATION_SPARQL_UPDATE));
        doPatch(locationHeader, headers, body);
    }

    /**
     * 3.7-B
     */
    @Test(groups = {"MAY"})
    public void ldpPatchContentTypeSupport() {
        final TestInfo info = setupTest("3.7-B",
                                        "Other content-types (e.g. [ldpatch]) may be available.",
                                        SPEC_BASE_URL + "#http-patch", ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resource);
        final Headers headers = new Headers(new Header("Content-Type", "text/ldpatch"));
        doPatch(locationHeader, headers, ldpatch);
    }

    /**
     * 3.7-C
     */
    @Test(groups = {"MUST"})
    public void serverManagedPropertiesModification() {
        final TestInfo info = setupTest("3.7-C",
                                        "If an otherwise valid HTTP PATCH request is received that attempts to modify "
                                        + "statements to a resource that a server disallows (not ignores per [LDP] "
                                        + "4.2.4.1), the server must fail the request by responding with a 4xx range"
                                        + " status code (e.g. 409 Conflict).",
                                        SPEC_BASE_URL + "#http-patch", ps);
        final Response resource = createBasicContainer(uri, info);
        final String resourceUri = getLocation(resource);
        patchWithDisallowedStatement(resourceUri).then().statusCode(clientErrorRange());
    }

    private Response patchWithDisallowedStatement(final String resource) {
        final Headers headers = new Headers(new Header("Content-Type", APPLICATION_SPARQL_UPDATE));
        String sparqlQuery = fileToString("/constraint-error-generator.sparql");
        if (constraintErrorGeneratingSparqlQuery != null) {
            final File constraintGeneratorFile = new File(constraintErrorGeneratingSparqlQuery);
            if (constraintGeneratorFile.exists()) {
                sparqlQuery = fileToString(constraintGeneratorFile);
            }
        }
        return doPatchUnverified(resource, headers, sparqlQuery);
    }

    /**
     * 3.7-D
     */
    @Test(groups = {"MUST"})
    public void statementNotPersistedResponseBody() {
        final TestInfo info = setupTest("3.7-D",
                                        "The server must provide a corresponding response body containing information"
                                        + " about which statements could not be persisted."
                                        + " ([LDP] 4.2.4.4 should becomes must).",
                                        SPEC_BASE_URL + "#http-patch", ps);
        final Response resource = createBasicContainer(uri, info);
        final String resourceUri = getLocation(resource);
        //the check for not null body is not ideal,
        //but given the dynamic nature of the test,
        //it's not clear to me how to test this requirement (dbernstein)
        patchWithDisallowedStatement(resourceUri).then().statusCode(clientErrorRange())
                                                 .body(notNullValue());

    }

    /**
     * 3.7-E
     */
    @Test(groups = {"MUST"})
    public void statementNotPersistedConstrainedBy() {
        final TestInfo info = setupTest("3.7-E",
                                        "In that response, the restrictions causing such a request to fail must be"
                                        + " described in a resource indicated by a Link: "
                                        + "rel=\"http://www.w3.org/ns/ldp#constrainedBy\" "
                                        + "response header per [LDP] 4.2.1.6.",
                                        SPEC_BASE_URL + "#http-patch", ps);

        final Response resource = createBasicContainer(uri, info);
        final String resourceUri = getLocation(resource);
        final Response patchResponse = patchWithDisallowedStatement(resourceUri);
        patchResponse.then().statusCode(clientErrorRange());
        confirmPresenceOfConstrainedByLink(patchResponse);
    }

    /**
     * 3.7-F
     */
    @Test(groups = {"MUST"})
    public void successfulPatchStatusCode() {
        final TestInfo info = setupTest("3.7-F",
                                        "A successful PATCH request must respond with a 2xx status code; the "
                                        + "specific code in the 2xx range may vary according to the response "
                                        + "body or request state.",
                                        SPEC_BASE_URL + "#http-patch", ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resource);
        ps.append("Request method:\tPATCH\n");
        ps.append("Request URI:\t").append(uri);
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("\t\t\t\tContent-Type=application/sparql-update; charset=ISO-8859-1\n");
        ps.append("Body:\n");
        ps.append(body + "\n");

        final Headers headers = new Headers(new Header("Content-Type", APPLICATION_SPARQL_UPDATE));
        final Response response = doPatch(locationHeader, headers, body);

        final int statusCode = response.getStatusCode();
        final Headers responseHeaders = response.getHeaders();

        ps.append("HTTP/1.1 ").append(String.valueOf(statusCode)).append("\n");
        for (Header h : responseHeaders) {
            ps.append(h.getName()).append(": ").append(h.getValue()).append("\n");
        }

        ps.append("\n" + response.asString());
    }

    /**
     * 3.7.1
     */
    @Test(groups = {"MUST"})
    public void disallowPatchContainmentTriples() {
        final TestInfo info = setupTest("3.7.1",
                                        "The server should not allow HTTP PATCH to update an LDPC’s containment " +
                                        "triples; if"
                                        + " the server receives such a request, it should respond with a"
                                        + " 409 (Conflict) status code.",
                                        SPEC_BASE_URL + "#http-patch-containment-triples",
                                        ps);
        final Response container = createBasicContainer(uri, info);
        final String locationHeader = getLocation(container);
        createBasicContainer(locationHeader, info.getId(), BASIC_CONTAINER_BODY);

        final Headers headers = new Headers(new Header("Content-Type", APPLICATION_SPARQL_UPDATE));
        doPatchUnverified(locationHeader, headers, updateContainmentTriples)
                .then()
                .statusCode(409);
    }

    /**
     * 3.7.2
     */
    @Test(groups = {"MUST"})
    public void disallowChangeResourceType() {
        final TestInfo info = setupTest("3.7.2",
                                        "The server must disallow a PATCH request that would change the LDP"
                                        + " interaction model of a resource to a type that is not a subtype"
                                        + " of the current resource type. That request must be rejected"
                                        + " with a 409 Conflict response.",
                                        SPEC_BASE_URL + "#http-patch-ixn-models", ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resource);

        final Headers headers = new Headers(new Header("Content-Type", APPLICATION_SPARQL_UPDATE));
        doPatchUnverified(locationHeader, headers, resourceType)
                .then()
                .statusCode(409);
    }
}
