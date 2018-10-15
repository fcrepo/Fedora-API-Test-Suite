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
package org.fcrepo.spec.testsuite.versioning;

import static org.fcrepo.spec.testsuite.Constants.NON_RDF_SOURCE_LINK_HEADER;
import static org.fcrepo.spec.testsuite.Constants.ORIGINAL_RESOURCE_LINK_HEADER;
import static org.fcrepo.spec.testsuite.Constants.TIME_GATE_LINK_HEADER;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Daniel Bernstein
 */
public class LdprvHttpPut extends AbstractVersioningTest {

    /**
     * Authentication
     *
     * @param username The repository username
     * @param password The repository password
     */
    @Parameters({"param2", "param3"})
    public LdprvHttpPut(final String username, final String password) {
        super(username, password);
    }

    /**
     * 4.1.2-A
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldprvMustSupportPUT(final String uri) {
        final TestInfo info = setupTest("4.1.2-A",
                                        "Must support PUT for creating new LDPRv",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprv-put",
                                        ps);

        //create an LDPRv using a put
        final Response response = putVersionedResourceUnverified(uri, info);
        assertEquals(response.getStatusCode(), 201);
        //verify that it is a TimeGate and has a TimeMap
        final Response getResponse = doGet(getLocation(response));
        confirmPresenceOfLinkValue(ORIGINAL_RESOURCE_LINK_HEADER, getResponse);
        confirmPresenceOfLinkValue(TIME_GATE_LINK_HEADER, getResponse);
    }

    /**
     * 4.1.2-B
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldprvMustSupportPUTForExistingResources(final String uri) {
        final TestInfo info = setupTest("4.1.2-B",
                                        "Must support PUT for updating existing LDPRvs",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprv-put",
                                        ps);
        //create an LDPRv
        final Response response = putVersionedResourceUnverified(uri, info);
        assertEquals(response.getStatusCode(), 201);

        final String resourceUri = getLocation(response);

        final Header acceptTurtleHeader = new Header("Accept", "text/turtle");
        final Response getResponse = doGet(resourceUri, acceptTurtleHeader);
        //verify that "Allow: PUT" header is present
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "PUT", getResponse);

        final String body = getResponse.getBody().asString();

        //update an existing LDPRv using a put
        final Headers headers = new Headers(
            new Header("Content-Type", "text/turtle"),
            new Header("Link", ORIGINAL_RESOURCE_LINK_HEADER));

        //add a triple to the body
        final String newBody = body +  "@prefix dc: <http://purl.org/dc/elements/1.1/>\n\n" +
                "<" + resourceUri + "> dc:title \"title test\" .";
        final Response response2 = doPutUnverified(resourceUri, headers, newBody);
        assertEquals(response2.getStatusCode(), 204);
        final Response getResponse2 = doGet(resourceUri, acceptTurtleHeader);
        //verify that it was changed.
        assertTrue(getResponse2.getBody().asString().contains("title test"));
    }

    /**
     * 4.1.2-C
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpnrvMustSupportPUT(final String uri) {
        final TestInfo info = setupTest("4.1.2-C",
                                        "Must support PUT for creating new LDPNRv",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprv-put",
                                        ps);

        //create an LDPNRv using a put
        final Response response = putVersionedResourceWithBodyUnverified(uri, info, "test");
        assertEquals(response.getStatusCode(), 201);

        //verify that it is a TimeGate and has a TimeMap
        final Response getResponse = doGet(getLocation(response));
        confirmPresenceOfLinkValue(NON_RDF_SOURCE_LINK_HEADER, getResponse);
        confirmPresenceOfLinkValue(ORIGINAL_RESOURCE_LINK_HEADER, getResponse);
        confirmPresenceOfLinkValue(TIME_GATE_LINK_HEADER, getResponse);

    }

    /**
     * 4.1.2-D
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpnrvMustSupportPUTForExistingResources(final String uri) {
        final TestInfo info = setupTest("4.1.2-D",
                                        "Must support PUT for updating existing  LDPNRvs",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprv-put",
                                        ps);
        //create an LDPNRv
        final Response response = putVersionedResourceWithBodyUnverified(uri, info, "test");
        assertEquals(response.getStatusCode(), 201);
        final String resourceUri = getLocation(response);
        final Response getResponse = doGet(resourceUri);
        //verify that "Allow: PUT" header is present
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "PUT", getResponse);
        //update an existing LDPNRv using a put
        final Response response2 = putVersionedResourceWithBodyUnverified(resourceUri,"test2");
        assertEquals(response2.getStatusCode(), 204);

        final Response getResponse2 = doGet(resourceUri);
        //verify that it was changed.
        assertTrue(getResponse2.getBody().asString().contains("test2"));
        confirmPresenceOfLinkValue(NON_RDF_SOURCE_LINK_HEADER, getResponse2);
        confirmPresenceOfLinkValue(ORIGINAL_RESOURCE_LINK_HEADER, getResponse2);
        confirmPresenceOfLinkValue(TIME_GATE_LINK_HEADER, getResponse2);
    }
}
