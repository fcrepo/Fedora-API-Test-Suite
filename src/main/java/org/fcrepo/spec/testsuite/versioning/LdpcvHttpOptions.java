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


import java.net.URI;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import static org.fcrepo.spec.testsuite.Constants.APPLICATION_SPARQL_UPDATE;

/**
 * @author Daniel Bernstein
 */
public class LdpcvHttpOptions extends AbstractVersioningTest {

    /**
     * 4.3.2-A
     */
    @Test(groups = {"MUST"})
    public void ldpcvMustSupportOptions() {
        final TestInfo info = setupTest("4.3.2-A",
                                        "LDPCv (version containers) MUST support OPTIONS.",
                                        SPEC_BASE_URL + "#ldpcv-options",
                                        ps);

        //create ldprv
        final Response createResponse = createVersionedResource(uri, info);

        //get location of new resource
        final String resourceUri = getLocation(createResponse);
        final Response response = doGet(resourceUri);

        final URI timeMapURI = getTimeMapUri(response);
        final Response timeMapResponse = doGet(timeMapURI.toString());
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "OPTIONS", timeMapResponse);
    }

    /**
     * 4.3.2-B
     */
    @Test(groups = {"MUST"})
    public void ldpcvOptionsMustAllowHeadGetOptions() {
        final TestInfo info = setupTest("4.3.2-B",
                                        "LDPCv's response to an OPTIONS request MUST include \"Allow: GET, " +
                                        "HEAD, OPTIONS\" per LDP",
                                        SPEC_BASE_URL + "#ldpcv-options",
                                        ps);

        //create ldprv
        final Response createResponse = createVersionedResource(uri, info);

        //get location of new resource
        final String resourceUri = getLocation(createResponse);
        final Response response = doGet(resourceUri);

        final URI timeMapURI = getTimeMapUri(response);
        final Response optionsResponse = doOptions(timeMapURI.toString());
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "GET", optionsResponse);
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "HEAD", optionsResponse);
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "OPTIONS", optionsResponse);

    }

    /**
     * 4.3.2-C
     */
    @Test(groups = {"MAY"})
    public void ldpcvMaySupportDeleteOption() {
        final TestInfo info = setupTest("4.3.2-C",
                                        "LDPCv (version containers) MAY support DELETE.",
                                        SPEC_BASE_URL + "#ldpcv-options",
                                        ps);

        //create ldprv
        final Response createResponse = createVersionedResource(uri, info);

        //get location of new resource
        final String resourceUri = getLocation(createResponse);
        final Response response = doGet(resourceUri);

        final URI timeMapURI = getTimeMapUri(response);
        final Response optionsResponse = doOptions(timeMapURI.toString());

        if (hasHeaderValueInMultiValueHeader("Allow", "DELETE", optionsResponse)) {
            // Verify DELETE is supported on LDPCv
            doDelete(timeMapURI.toString());

        } else {
            // DELETE not supported on LDPCv
            throw new SkipException("DELETE on LDPCv not supported");
        }
    }

    /**
     * 4.3.2-D
     */
    @Test(groups = {"MAY"})
    public void ldpcvMaySupportPatch() {
        final TestInfo info = setupTest("4.3.2-D",
                                        "LDPCv (version containers) MAY support PATCH.",
                                        SPEC_BASE_URL + "#ldpcv-options",
                                        ps);

        //create ldprv
        final Response createResponse = createVersionedResource(uri, info);

        //get location of new resource
        final String resourceUri = getLocation(createResponse);
        final Response response = doGet(resourceUri);

        final URI timeMapURI = getTimeMapUri(response);
        final Response optionsResponse = doOptions(timeMapURI.toString());

        if (hasHeaderValueInMultiValueHeader("Allow", "PATCH", optionsResponse)) {
            final String body = "PREFIX dcterms: <http://purl.org/dc/terms/>"
                    + " INSERT {"
                    + " <> dcterms:description \"Patch Updated Description\" ."
                    + "}"
                    + " WHERE { }";
            final Headers headers = new Headers(new Header("Content-Type", APPLICATION_SPARQL_UPDATE));

            // Verify PATCH is supported on LDPCv
            doPatch(timeMapURI.toString(), headers, body);

        } else {
            // PATCH not supported on LDPCv
            throw new SkipException("PATCH on LDPCv not supported");
        }
    }

    /**
     * 4.3.2-E
     */
    @Test(groups = {"MAY"})
    public void ldpcvMaySupportPost() {
        final TestInfo info = setupTest("4.3.2-E",
                                        "LDPCv (version containers) MAY support POST.",
                                        SPEC_BASE_URL + "#ldpcv-options",
                                        ps);

        //create ldprv
        final Response createResponse = createVersionedResource(uri, info);

        //get location of new resource
        final String resourceUri = getLocation(createResponse);
        final Response response = doGet(resourceUri);

        final URI timeMapURI = getTimeMapUri(response);
        final Response optionsResponse = doOptions(timeMapURI.toString());
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "POST", optionsResponse);
    }

    /**
     * 4.3.2-F
     */
    @Test(groups = {"MUST"})
    public void ldpcvMustReturnAcceptPostHeaderIfPostIsSupported() {
        final TestInfo info = setupTest("4.3.2-F",
                                        "If an LDPCv supports POST, the response to an OPTIONS request " +
                                        " MUST include the \"Accept-Post\" header",
                                        SPEC_BASE_URL + "#ldpcv-options",
                                        ps);

        //create ldprv
        final Response createResponse = createVersionedResource(uri, info);

        //get location of new resource
        final String resourceUri = getLocation(createResponse);
        final Response response = doGet(resourceUri);

        final URI timeMapURI = getTimeMapUri(response);
        final Response optionsResponse = doOptions(timeMapURI.toString());
        if (hasHeaderValueInMultiValueHeader("Allow", "POST", optionsResponse)) {
            Assert.assertTrue(getHeaders(optionsResponse, "Accept-Post").count() > 0,
                              "If an LDPCv supports POST, the response to an OPTIONS request " +
                              " MUST include the \"Accept-Post\" header");

        }
    }

    /**
     * 4.3.2-G
     */
    @Test(groups = {"MUST"})
    public void ldpcvMustReturnAcceptPatchHeaderIfPatchIsSupported() {
        final TestInfo info = setupTest("4.3.2-G",
                                        "If an LDPCv supports PATCH, the response to an OPTIONS request " +
                                        " MUST include the \"Accept-Patch\" header",
                                        SPEC_BASE_URL + "#ldpcv-options",
                                        ps);

        //create ldprv
        final Response createResponse = createVersionedResource(uri, info);

        //get location of new resource
        final String resourceUri = getLocation(createResponse);
        final Response response = doGet(resourceUri);

        final URI timeMapURI = getTimeMapUri(response);
        final Response optionsResponse = doOptions(timeMapURI.toString());
        if (hasHeaderValueInMultiValueHeader("Allow", "PATCH", optionsResponse)) {
            Assert.assertTrue(getHeaders(optionsResponse, "Accept-Patch").count() > 0,
                              "If an LDPCv supports PATCH, the response to an OPTIONS request " +
                              " MUST include the \"Accept-Patch\" header");

        }
    }
}