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

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNotSame;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * @author dbernstein
 */
public class StateToken extends AbstractTest {
    private static final String STATE_TOKEN = "X-State-Token";
    private static final String IF_STATE_TOKEN = "X-If-State-Token";

    /**
     * 3.10.1-A
     */
    @Test(groups = {"MAY"})
    public void stateTokenGet() {
        final TestInfo info = setupTest("3.10.1-A",
                                        "Implementations may include the X-State-Token header field in GET responses " +
                                        "to provide a token representing the current state of resource. If provided, " +
                                        "this value must change whenever the underlying state of the resource has " +
                                        "changed.",
                                        SPEC_BASE_URL + "#x-state-token", ps);

        final Response createResponse = createBasicContainer(uri, info.getId());
        final String resourceUri = getLocation(createResponse);
        final Response getResponse = doGet(resourceUri);

        //throw skip exception if state token not supported
        skipIfStateTokenNotSupported(getResponse);

        final String value = getResponse.getHeader(STATE_TOKEN);
        assertNotNull(STATE_TOKEN + " must not be null.", value);
        final String sparqlUpdate =
            "INSERT { <> <http://purl.org/dc/elements/1.1/title> \"test\" . } WHERE {}";
        doPatch(resourceUri, new Headers(new Header("Content-Type", "application/sparql-update")), sparqlUpdate);
        assertNotSame("New " + STATE_TOKEN + " should not match previous token value", value,
                      doGet(resourceUri).header(STATE_TOKEN));
    }

    private void skipIfStateTokenNotSupported(final Response response) throws SkipException {
        if (!isStateTokenSupported(response)) {
            throw new SkipException("This implementation does not support " + STATE_TOKEN);
        }
    }

    private boolean isStateTokenSupported(final Response response) {
        return response.getHeaders().hasHeaderWithName(STATE_TOKEN);
    }

    /**
     * 3.10.1-B
     */
    @Test(groups = {"MAY"})
    public void stateTokenHead() {
        final TestInfo info = setupTest("3.10.1-B",
                                        "Implementations may include the X-State-Token header field in  HEAD " +
                                        "responses to provide a token representing the current state of resource. If " +
                                        "provided, this value must change whenever the underlying state of the " +
                                        "resource has changed.",
                                        SPEC_BASE_URL + "#x-state-token", ps);

        final Response createResponse = createBasicContainer(uri, info.getId());
        final String resourceUri = getLocation(createResponse);
        final Response headResponse = doHead(resourceUri);

        //throw skip exception if state token not supported
        skipIfStateTokenNotSupported(headResponse);

        final String value = headResponse.getHeader(STATE_TOKEN);
        assertNotNull(STATE_TOKEN + " must not be null.", value);
        final String sparqlUpdate =
            "INSERT { <> <http://purl.org/dc/elements/1.1/title> \"test\" . } WHERE {}";
        doPatch(resourceUri, new Headers(new Header("Content-Type", "application/sparql-update")), sparqlUpdate);
        assertNotSame("New " + STATE_TOKEN + " should not match previous token value", value,
                      doHead(resourceUri).header(STATE_TOKEN));
    }

    /**
     * 3.10.2-A
     */
    @Test(groups = {"MUST"})
    public void goodStateTokenOnPatchWhenStateTokensSupported() {
        final TestInfo info = setupTest("3.10.2-A",
                                        "A client may include the X-If-State-Token header field in a PATCH request to" +
                                        " make the request conditional on the resource's current state token matching" +
                                        " the client's value.",
                                        SPEC_BASE_URL + "#x-if-state-token", ps);
        final Response createResponse = createBasicContainer(uri, info.getId());
        final String resourceUri = getLocation(createResponse);
        final Response getResponse = doGet(resourceUri);

        //throw skip exception if state token not supported
        skipIfStateTokenNotSupported(getResponse);

        final String value = getResponse.getHeader(STATE_TOKEN);
        assertNotNull(STATE_TOKEN + " must not be null.", value);
        final String sparqlUpdate =
            "INSERT { <> <http://purl.org/dc/elements/1.1/title> \"test\" . } WHERE {} ";
        doPatch(resourceUri, new Headers(new Header(IF_STATE_TOKEN, value),
                                         new Header("Content-Type", "application/sparql-update")),
                sparqlUpdate);
    }

    /**
     * 3.10.2-B
     */
    @Test(groups = {"MAY"})
    public void badStateTokenOnPatchWhenStateTokensIgnored() {
        final TestInfo info = setupTest("3.10.2-B",
                                        "A client may include the X-If-State-Token header field in a PATCH request to" +
                                        " make the request conditional on the resource's current state token matching" +
                                        " the client's value. If an implementation does not support state tokens, it " +
                                        "may ignore any X-If-State-Token header in HTTP PATCH requests.",
                                        SPEC_BASE_URL + "#x-if-state-token", ps);
        final Response createResponse = createBasicContainer(uri, info.getId());
        final String resourceUri = getLocation(createResponse);
        final Response getResponse = doGet(resourceUri);
        if (!isStateTokenSupported(getResponse)) {
            final String sparqlUpdate =
                "INSERT { <> <http://purl.org/dc/elements/1.1/title> \"test\" . } WHERE {}";
            doPatchUnverified(resourceUri, new Headers(new Header(IF_STATE_TOKEN, "random-token"),
                                                       new Header("Content-Type", "application/sparql-update")),
                              sparqlUpdate).then().statusCode(successRange());
        }
    }

    /**
     * 3.10.2-C
     */
    @Test(groups = {"MUST"})
    public void badStateTokenOnPatchWhenStateTokensSupported() {
        final TestInfo info = setupTest("3.10.2-C",
                                        "A client may include the X-If-State-Token header field in a PATCH request to" +
                                        " make the request conditional on the resource's current state token matching" +
                                        " the client's value.  An HTTP " +
                                        "PATCH request that includes an X-If-State-Token header must be " +
                                        "rejected with a 412 (Precondition Failed) response if the implementation " +
                                        "supports state tokens, but the client-supplied value does not match the " +
                                        "resource's current state token.",
                                        SPEC_BASE_URL + "#x-if-state-token", ps);
        final Response createResponse = createBasicContainer(uri, info.getId());
        final String resourceUri = getLocation(createResponse);
        final Response getResponse = doGet(resourceUri);

        //throw skip exception if state token not supported
        skipIfStateTokenNotSupported(getResponse);

        final String value = getResponse.getHeader(STATE_TOKEN);
        assertNotNull(STATE_TOKEN + " must not be null.", value);
        final String sparqlUpdate =
            "INSERT { <> <http://purl.org/dc/elements/1.1/title> \"test\" . } WHERE {} ";
        doPatchUnverified(resourceUri, new Headers(new Header(IF_STATE_TOKEN, value + "-bad-token"),
                                                   new Header("Content-Type", "application/sparql-update")),
                          sparqlUpdate).then().statusCode(412);
    }


    /**
     * 3.10.2-D
     */
    @Test(groups = {"MUST"})
    public void goodStateTokenOnPutSucceedsWhenStateTokensSupported() {
        final TestInfo info = setupTest("3.10.2-D",
                                        "A client may include the X-If-State-Token header field in a PUT request to" +
                                        " make the request conditional on the resource's current state token matching" +
                                        " the client's value.",
                                        SPEC_BASE_URL + "#x-if-state-token", ps);
        final Response createResponse = createBasicContainer(uri, info.getId());
        final String resourceUri = getLocation(createResponse);
        final Header preferHeader = new Header("Prefer", "return=representation; " +
                "include=\"http://www.w3.org/ns/ldp#PreferMinimalContainer\"; " +
                "omit=\"http://fedora.info/definitions/v4/repository#ServerManaged\"");
        final Response getResponse = doGet(resourceUri, preferHeader);

        //throw skip exception if state token not supported
        skipIfStateTokenNotSupported(getResponse);

        final String value = getResponse.getHeader(STATE_TOKEN);
        assertNotNull(STATE_TOKEN + " must not be null.", value);
        final String responseTxt = getResponse.getBody().asString();

        final String newBody = responseTxt + "\n<> <http://example.org/test> \"any value\".";

        doPut(resourceUri, new Headers(new Header(IF_STATE_TOKEN, value),
                                       new Header("Content-Type", "text/turtle")), newBody);
    }

    /**
     * 3.10.2-E
     */
    @Test(groups = {"MAY"})
    public void badStateTokenOnPutWhenStateTokensIgnored() {
        final TestInfo info = setupTest("3.10.2-E",
                                        "A client may include the X-If-State-Token header field in a PUT request to" +
                                        " make the request conditional on the resource's current state token matching" +
                                        " the client's value. If an implementation does not support state tokens, it " +
                                        "may ignore any X-If-State-Token header in HTTP PUT requests.",
                                        SPEC_BASE_URL + "#x-if-state-token", ps);
        final Response createResponse = createBasicContainer(uri, info.getId());
        final String resourceUri = getLocation(createResponse);
        final Response getResponse = doGet(resourceUri);
        if (!isStateTokenSupported(getResponse)) {
            final String responseTxt = getResponse.getBody().asString();
            doPut(resourceUri, new Headers(new Header(IF_STATE_TOKEN, "random-token"),
                                           new Header("Content-Type", "text/turtle")),
                  responseTxt);
        }
    }

    /**
     * 3.10.2-F
     */
    @Test(groups = {"MUST"})
    public void badStateTokenOnPutWhenStateTokensSupported() {
        final TestInfo info = setupTest("3.10.2-F",
                                        "A client may include the X-If-State-Token header field in a PUT request to" +
                                        " make the request conditional on the resource's current state token matching" +
                                        " the client's value.  An HTTP " +
                                        "PUT request that includes an X-If-State-Token header must be " +
                                        "rejected with a 412 (Precondition Failed) response if the implementation " +
                                        "supports state tokens, but the client-supplied value does not match the " +
                                        "resource's current state token.",
                                        SPEC_BASE_URL + "#x-if-state-token", ps);
        final Response createResponse = createBasicContainer(uri, info.getId());
        final String resourceUri = getLocation(createResponse);
        final Response getResponse = doGet(resourceUri);

        //throw skip exception if state token not supported
        skipIfStateTokenNotSupported(getResponse);

        final String value = getResponse.getHeader(STATE_TOKEN);
        assertNotNull(STATE_TOKEN + " must not be null.", value);
        final String responseTxt = getResponse.getBody().asString();
        final String newBody = responseTxt + "\n<> <http://example.org/test> \"any value\".";

        doPutUnverified(resourceUri, new Headers(new Header(IF_STATE_TOKEN, "random-token"),
                                                 new Header("Content-Type", "text/turtle")),
                        newBody).then().statusCode(412);
    }
}
