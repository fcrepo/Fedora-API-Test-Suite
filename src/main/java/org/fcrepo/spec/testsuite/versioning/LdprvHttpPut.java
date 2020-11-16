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

import static org.fcrepo.spec.testsuite.Constants.LDP_CONSTRAINED_BY;
import static org.fcrepo.spec.testsuite.Constants.NON_RDF_SOURCE_LINK_HEADER;
import static org.fcrepo.spec.testsuite.Constants.ORIGINAL_RESOURCE_LINK_HEADER;
import static org.fcrepo.spec.testsuite.Constants.TIME_GATE_LINK_HEADER;
import static org.junit.Assert.fail;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.annotations.Test;

/**
 * @author Daniel Bernstein
 */
public class LdprvHttpPut extends AbstractVersioningTest {

    /**
     * 4-A
     */
    @Test(groups = {"MAY"})
    public void changeLDPRToAnLDPRv() {
        final TestInfo info = setupTest("4-A",
                                        "Implementations may allow a subsequent PUT request with a rel=\"type\"" +
                                        " link in the Link header specifying type http://mementoweb" +
                                        ".org/ns#OriginalResource to convert an existing LDPR into an LDPRv. If such " +
                                        "a conversion from an LDPR to an LDPRv is supported, it must be accompanied " +
                                        "by the creation of a version container (LDPCv), as noted above.",
                                        SPEC_BASE_URL + "#resource-versioning",
                                        ps);

        //create a resource
        final Response response = createBasicContainer(this.uri, info.getId());
        final String resourceUri = getLocation(response);
        //if it has an original resource header skip
        final Response getResponse = doGet(resourceUri);
        skipIfVersionedByDefault(getResponse);
        //otherwise PUT with the OriginalResource header.
        final String body = getResponse.getBody().asString();
        doPut(resourceUri, new Headers(new Header("Link", ORIGINAL_RESOURCE_LINK_HEADER)), body);
        final Response getResponse2 = doGet(resourceUri);
        //verify that OriginalResource header is returned.
        confirmPresenceOfLinkValue(ORIGINAL_RESOURCE_LINK_HEADER, getResponse2);
        //confirm there is a timemap link
        confirmPresenceOfTimeMapLink(getResponse2);
        //confirm that the timemap link is not broken.
        doGet(getTimeMapUri(getResponse2).toString());
    }

    /**
     * 4.1.2-A
     */
    @Test(groups = {"MUST"})
    public void ldprvMustSupportPUT() {
        final TestInfo info = setupTest("4.1.2-A",
                                        "Must support PUT for creating new LDPRv",
                                        SPEC_BASE_URL + "#ldprv-put",
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
     */
    @Test(groups = {"MUST"})
    public void ldprvMustSupportPUTForExistingResources() {
        final TestInfo info = setupTest("4.1.2-B",
                                        "Must support PUT for updating existing LDPRvs",
                                        SPEC_BASE_URL + "#ldprv-put",
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
        if (response2.getStatusCode() == 204) {
            final Response getResponse2 = doGet(resourceUri, acceptTurtleHeader);
            //verify that it was changed.
            assertTrue(getResponse2.getBody().asString().contains("title test"));
        } else if (clientErrorRange().matches(response2.statusCode())) {
            assertTrue(getLinksOfRelType(response2, LDP_CONSTRAINED_BY).count() > 0);
        } else {
            fail("PUT must succeed or fail with a 4xx and constrainedby link header");
        }
    }

    /**
     * 4.1.2-C
     */
    @Test(groups = {"MUST"})
    public void ldpnrvMustSupportPUT() {
        final TestInfo info = setupTest("4.1.2-C",
                                        "Must support PUT for creating new LDPNRv",
                                        SPEC_BASE_URL + "#ldprv-put",
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
     */
    @Test(groups = {"MUST"})
    public void ldpnrvMustSupportPUTForExistingResources() {
        final TestInfo info = setupTest("4.1.2-D",
                                        "Must support PUT for updating existing  LDPNRvs",
                                        SPEC_BASE_URL + "#ldprv-put",
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
