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

import static org.fcrepo.spec.testsuite.Constants.MEMENTO_DATETIME_HEADER;
import static org.fcrepo.spec.testsuite.Constants.MEMENTO_LINK_HEADER;
import static org.testng.AssertJUnit.fail;

import java.net.URI;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;
import org.junit.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * Tests for POST requests on LDP Version Container Resources
 *
 * @author Daniel Bernstein
 */
public class LdpcvHttpPost extends AbstractVersioningTest {

    /**
     * 4.3 may disallow post
     */
    @Test(groups = { "MAY" })
    public void ldpcvMayDisallowPost() {
        final TestInfo info = setupTest("4.3",
                "Although an LDPCv is both a TimeMap and an LDPC, implementations MAY disallow POST requests.",
                SPEC_BASE_URL + "#ldpcv-post",
                ps);

        // create the versioned resource
        final Response createResponse = createVersionedResource(uri, info);

        final URI timeMapURI = getTimeMapUri(createResponse);
        final String allow = doOptions(timeMapURI.toString()).getHeader("Allow");

        if (!allow.contains("POST")) {
            // Verify POST is not supported
            final Response response = doPostUnverified(timeMapURI.toString());
            Assert.assertFalse(successRange().matches(response.statusCode()));

        } else {
            // POST is supported
            throw new SkipException("POST on LDPCv is supported");
        }
    }

    /**
     * 4.3.3.1-A
     */
    @Test(groups = {"SHOULD"})
    public void ldpcvOfLdprsShouldSupportPostWithoutMementoDatetimeHeader() {
        final TestInfo info = setupTest("4.3.3.1-A",
                                        "If an LDPCv of an LDP-RS supports POST, a POST request that does not contain" +
                                        " a Memento-Datetime header should be understood to create a new LDPRm " +
                                        "contained by the LDPCv, reflecting the state of the LDPRv at the time of " +
                                        "the POST. ",
                                        SPEC_BASE_URL + "#ldpcv-post",
                                        ps);

        final Header acceptNTriplesHeader = new Header("Accept", "application/n-triples");

        // create the versioned resource
        final Response createResponse = createVersionedResource(uri, info);

        // get the new resource, which should have all the proper memento headers
        final Response origResponse = doGet(getLocation(createResponse), acceptNTriplesHeader);
        final URI timeMapURI = getTimeMapUri(origResponse);

        // create a version on the resource
        final Response timeMapResponse = doPost(timeMapURI.toString());

        // should have location in header information
        confirmPresenceOfVersionLocationHeader(timeMapResponse);

        // get the new memento
        final Response versionResponse = doGet(getLocation(timeMapResponse), acceptNTriplesHeader);

        // is the memento exactly the same as the original?
        confirmResponseBodyNTriplesAreEqual(origResponse, versionResponse);

        // is the version marked as a memento?
        confirmPresenceOfLinkValue(MEMENTO_LINK_HEADER, versionResponse);
    }

    /**
     * 4.3.3.1-B
     */
    @Test(groups = {"SHOULD"})
    public void ldpcvOfLdpnrsShouldSupportPostWithoutMementoDatetimeHeader() {
        final TestInfo info = setupTest("4.3.3.1-B",
                                        "If an LDPCv of an LDP-NR supports POST, a POST request that does not contain" +
                                        " a Memento-Datetime header should be understood to create a new LDPRm " +
                                        "contained by the LDPCv, reflecting the state of the LDPRv at the time of " +
                                        "the POST. ",
                                        SPEC_BASE_URL + "#ldpcv-post",
                                        ps);

        // same as previous test but with LDP-NR
        // create the versioned resource
        final Response createResponse = createVersionedNonRDFResource(uri, info);

        // get the new resource, which should have all the proper memento headers
        final Response origResponse = doGet(getLocation(createResponse));
        final URI timeMapURI = getTimeMapUri(origResponse);

        // create a memento for the current time
        final Response timeMapResponse = doPost(timeMapURI.toString());

        // should have location in header information
        confirmPresenceOfVersionLocationHeader(timeMapResponse);

        // get the new memento
        final Response versionResponse = doGet(getLocation(timeMapResponse));

        // is the memento exactly the same as the original?
        confirmResponseBodyNonRDFSourcesAreEqual(origResponse, versionResponse);
        // is the version marked as a memento?
        confirmPresenceOfLinkValue(MEMENTO_LINK_HEADER, versionResponse);
    }

    /**
     * 4.3.3.1-C
     */
    @Test(groups = {"MUST"})
    public void postToldpcvOfLdprsWithoutMementoDatetimeMustIgnoreBody() {
        final TestInfo info = setupTest("4.3.3.1-C",
                                        "If an LDPCv of an LDP-RS supports POST, a POST request that does not contain" +
                                        " a " +
                                        "Memento-Datetime header MUST ignore any request body.",
                                        SPEC_BASE_URL + "#ldpcv-post",
                                        ps);

        final Header acceptNTriplesHeader = new Header("Accept", "application/n-triples");

        // create the versioned resource
        final Response createResponse = createVersionedResource(uri, info);

        // get the new resource, which should have all the proper memento headers
        final Response origResponse = doGet(getLocation(createResponse), acceptNTriplesHeader);
        final URI timeMapURI = getTimeMapUri(origResponse);

        // create a memento for the current time
        final Response timeMapResponse = doPost(timeMapURI.toString(), "<> dc:title \"HelloThere\".");

        // should have location header information of new memento
        confirmPresenceOfVersionLocationHeader(timeMapResponse);

        final Response versionResponse = doGet(getLocation(timeMapResponse), acceptNTriplesHeader);

        // does the version look like the original still?
        confirmResponseBodyNTriplesAreEqual(origResponse, versionResponse);

        // is version marked as memento?
        confirmPresenceOfLinkValue(MEMENTO_LINK_HEADER, versionResponse);
    }

    /**
     * 4.3.3.1-D
     */
    @Test(groups = {"MUST"})
    public void postToldpcvOfLdpnrWithoutMementoDatetimeMustIgnoreBody() {
        final TestInfo info = setupTest("4.3.3.1-D",
                                        "If an LDPCv of an LDP-NR supports POST, a POST request that does not contain" +
                                        " a " +
                                        "Memento-Datetime header MUST ignore any request body.",
                                        SPEC_BASE_URL + "#ldpcv-post",
                                        ps);
        //same as previous but with LDP-NR
        // create the versioned resource
        final Response createResponse = createVersionedNonRDFResource(uri, info);

        // get the new resource, which should have all the proper memento headers
        final Response origResponse = doGet(getLocation(createResponse));
        final URI timeMapURI = getTimeMapUri(origResponse);

        // create a memento for the current time
        final Response timeMapResponse = doPost(timeMapURI.toString(), "Send some random data here");

        //should have location header information of new memento
        confirmPresenceOfVersionLocationHeader(timeMapResponse);

        // get the new memento
        final Response versionResponse = doGet(getLocation(timeMapResponse));

        // is the memento exactly the same as the original?
        confirmResponseBodyNonRDFSourcesAreEqual(origResponse, versionResponse);
        // is version marked as memento?
        confirmPresenceOfLinkValue(MEMENTO_LINK_HEADER, versionResponse);
    }

    /**
     * 4.3.3.1-E
     */
    @Test(groups = {"SHOULD"})
    public void postToldpcvOfLdprWithMementoDatetimeShouldCreateNewResource() {
        final TestInfo info = setupTest("4.3.3.1-E",
                                        "If an LDPCv supports POST, a POST with a Memento-Datetime header " +
                                        "should be understood to create a new LDPRm contained by the LDPCv, with the " +
                                        "state given in the request body.",
                                        SPEC_BASE_URL + "#ldpcv-post",
                                        ps);
        //create a versioned resource
        final Response createResponse = createVersionedResource(uri, info);
        //if post is supported on the ldpcv
        // get the new resource, which should have all the proper memento headers
        final String originalResource = getLocation(createResponse);
        final Header acceptNTriples = new Header("Accept", "application/n-triples");
        final Response originalResponse = doGet(originalResource, acceptNTriples);
        //get the original resource and add a triple
        final String body =
            originalResponse.getBody().asString() +
                    "<" + originalResource + "> <http://purl.org/dc/elements/1.1/description> \"test\"";
        final URI timeMapURI = getTimeMapUri(originalResponse);
        if (hasHeaderValueInMultiValueHeader("Allow", "POST", doGet(timeMapURI.toString()))) {
            //create a memento using the Memento-Datetime and a body
            final String mementoUri =
                createMemento(originalResource, "Sat, 1 Jan 2000 00:00:00 GMT", "text/turtle", body);
            // is the memento exactly the same as provided?
            confirmResponseBodyNTriplesAreEqual(body, doGet(mementoUri, acceptNTriples).getBody().asString());

        }
    }

    /**
     * 4.3.3.1-F
     */
    @Test(groups = {"SHOULD"})
    public void mementoDatetimeHeaderShouldMatchThatUsedWhenMementoCreated() {
        final TestInfo info = setupTest("4.3.3.1-F",
                                        " If an LDPCv supports POST, a POST with a Memento-Datetime header " +
                                        "should be understood to create a new LDPRm contained by the LDPCv, with the " +
                                        "datetime given in the Memento-Datetime request header.",
                                        SPEC_BASE_URL + "#ldpcv-post",
                                        ps);

        final String mementoDateTime = "Sat, 1 Jan 2000 00:00:00 GMT";
        //create a versioned resource
        final Response createResponse = createVersionedResource(uri, info);
        //if post is supported on the ldpcv
        // get the new resource, which should have all the proper memento headers
        final String originalResource = getLocation(createResponse);
        final Response originalResponse = doGet(originalResource);
        final String body = originalResponse.getBody().asString();
        final URI timeMapURI = getTimeMapUri(originalResponse);
        if (hasHeaderValueInMultiValueHeader("Allow", "POST", doGet(timeMapURI.toString()))) {
            //create a memento using the Memento-Datetime and a body
            final String mementoUri = createMemento(originalResource, mementoDateTime, "text/turtle", body);
            //verify Memento-Datetime- is in the request header
            confirmPresenceOfMementoDatetimeHeader(mementoDateTime, doGet(mementoUri));
        }

    }

    /**
     * 4.3.3.2
     */
    @Test(groups = {"MUST"})
    public void ldpcvDoesNotSupportPost() {
        final TestInfo info = setupTest("4.3.3.2",
                                        "If an implementation does not support one or both of POST cases " +
                                        "above, it must respond to such requests with a 4xx range status code and a " +
                                        "link to an appropriate constraints document",
                                        SPEC_BASE_URL + "#ldpcv-post",
                                        ps);

        final Response createResponse = createVersionedResource(uri, info);
        final String originalResource = getLocation(createResponse);
        final Response originalResponse = doGet(originalResource);
        final String timeMapURI = getTimeMapUri(originalResponse).toString();
        final Response getResponse = doGet(timeMapURI);
        if (!hasHeaderValueInMultiValueHeader("Allow", "POST", getResponse)) {
            //if post is supported on the ldpcv, try posting
            final Response postResponse = doPostUnverified(timeMapURI);
            //verify correct error range
            postResponse.then().statusCode(clientErrorRange());
            //verify presence of constraints link
            confirmPresenceOfConstrainedByLink(postResponse);
        } else {
            //if it supports post but not with the Memento-Datetime header
            if (!hasHeaderValueInMultiValueHeader("Vary-Post", MEMENTO_DATETIME_HEADER, getResponse)) {
                final Response postResponse = doPostUnverified(timeMapURI, new Headers(
                    new Header(MEMENTO_DATETIME_HEADER, "Sat, 1 Jan 2000 00:00:00 GMT")));
                //verify correct error range
                postResponse.then().statusCode(clientErrorRange());
                //verify presence of constraints link
                confirmPresenceOfConstrainedByLink(postResponse);
            }
        }
    }

    /**
     * 4.3.4
     */
    @Test(groups = {"MAY"})
    public void ldpcvMayDisallowPut() {
        final TestInfo info = setupTest("4.3.4",
                                        "Implementations MAY disallow PUT.",
                                        SPEC_BASE_URL + "#ldpcv-put",
                                        ps);

        final Response createResponse = createVersionedResource(uri, info);
        final String originalResource = getLocation(createResponse);
        final Response originalResponse = doGet(originalResource);
        final String timeMapURI = getTimeMapUri(originalResponse).toString();
        final Response getResponse = doGet(timeMapURI);
        if (hasHeaderValueInMultiValueHeader("Allow", "PUT", getResponse)
            || doPutUnverified(timeMapURI).statusCode() != 405) {
            fail("This Fedora implementation allows PUTs on LDPCv.");
        }
    }

    /**
     * 4.3.5
     */
    @Test(groups = {"MAY"})
    public void ldpcvMayDisallowPatch() {
        final TestInfo info = setupTest("4.3.5",
                                        "Implementations MAY disallow PATCH",
                                        SPEC_BASE_URL + "#ldpcv-patch",
                                        ps);
        final Response createResponse = createVersionedResource(uri, info);
        final String originalResource = getLocation(createResponse);
        final Response originalResponse = doGet(originalResource);
        final String timeMapURI = getTimeMapUri(originalResponse).toString();
        final Response getResponse = doGet(timeMapURI);
        if (hasHeaderValueInMultiValueHeader("Allow", "PATCH", getResponse)
            || doPatchUnverified(timeMapURI).statusCode() != 405) {
            fail("This Fedora implementation allows PATCHs on LDPCv.");
        }

    }

}

