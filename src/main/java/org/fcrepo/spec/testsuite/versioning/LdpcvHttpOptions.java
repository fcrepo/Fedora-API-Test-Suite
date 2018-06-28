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

import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Daniel Bernstein
 */
public class LdpcvHttpOptions extends AbstractVersioningTest {

    /**
     * Authentication
     *
     * @param username
     * @param password
     */
    @Parameters({"param2", "param3"})
    public LdpcvHttpOptions(final String username, final String password) {
        super(username, password);
    }

    /**
     * 4.3.2-A
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpcvMustSupportOptions(final String uri) {
        final TestInfo info = setupTest("4.3.2-A", "ldpcvMustSupportOptions",
                                        "LDPCv (version containers) MUST support OPTIONS.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-options",
                                        ps);

        //create ldprv
        final Response createResponse = createVersionedResource(uri, info);
        final URI timeMapURI = getTimeMapUri(createResponse);
        final Response response = doGet(timeMapURI.toString());
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "OPTIONS", response);
    }

    /**
     * 4.3.2-B
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpcvOptionsMustAllowHeadGetOptions(final String uri) {
        final TestInfo info = setupTest("4.3.2-B", "ldpcvOptionsMustAllowHeadGetOptions",
                                        "LDPCv's response to an OPTIONS request MUST include \"Allow: GET, " +
                                        "HEAD, OPTIONS\" per LDP",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-options",
                                        ps);

        //create ldprv
        final Response createResponse = createVersionedResource(uri, info);
        final URI timeMapURI = getTimeMapUri(createResponse);
        final Response response = doOptions(timeMapURI.toString());
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "GET", response);
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "HEAD", response);
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "OPTIONS", response);

    }

    /**
     * 4.3.2-C
     *
     * @param uri
     */
    @Test(groups = {"MAY"})
    @Parameters({"param1"})
    public void ldpcvMaySupportDelete(final String uri) {
        final TestInfo info = setupTest("4.3.2-C", "ldpcvMaySupportDelete",
                                        "LDPCv (version containers) MAY support DELETE.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-options",
                                        ps);

        //create ldprv
        final Response createResponse = createVersionedResource(uri, info);
        final URI timeMapURI = getTimeMapUri(createResponse);
        final Response response = doOptions(timeMapURI.toString());
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "DELETE", response);
    }

    /**
     * 4.3.2-D
     *
     * @param uri
     */
    @Test(groups = {"MAY"})
    @Parameters({"param1"})
    public void ldpcvMaySupportPatch(final String uri) {
        final TestInfo info = setupTest("4.3.2-D", "ldpcvMaySupportPatch",
                                        "LDPCv (version containers) MAY support PATCH.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-options",
                                        ps);

        //create ldprv
        final Response createResponse = createVersionedResource(uri, info);
        final URI timeMapURI = getTimeMapUri(createResponse);
        final Response response = doOptions(timeMapURI.toString());
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "PATCH", response);
    }

    /**
     * 4.3.2-D
     *
     * @param uri
     */
    @Test(groups = {"MAY"})
    @Parameters({"param1"})
    public void ldpcvMaySupportPost(final String uri) {
        final TestInfo info = setupTest("4.3.2-D", "ldpcvMaySupportPost",
                                        "LDPCv (version containers) MAY support POST.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-options",
                                        ps);

        //create ldprv
        final Response createResponse = createVersionedResource(uri, info);
        final URI timeMapURI = getTimeMapUri(createResponse);
        final Response response = doOptions(timeMapURI.toString());
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "POST", response);
    }

    /**
     * 4.3.2-E
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpcvMustReturnAcceptPostHeaderIfPostIsSupported(final String uri) {
        final TestInfo info = setupTest("4.3.2-E", "ldpcvMustReturnAcceptPostHeaderIfPostIsSupported",
                                        "If an LDPCv supports POST, the response to an OPTIONS request " +
                                        " MUST include the \"Accept-Post\" header",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-options",
                                        ps);

        //create ldprv
        final Response createResponse = createVersionedResource(uri, info);
        final URI timeMapURI = getTimeMapUri(createResponse);
        final Response response = doOptions(timeMapURI.toString());
        if (hasHeaderValueInMultiValueHeader("Allow", "POST", response)) {
            Assert.assertTrue(getHeaders(response, "Accept-Post").count() > 0,
                              "If an LDPCv supports POST, the response to an OPTIONS request " +
                              " MUST include the \"Accept-Post\" header");

        }
    }

    /**
     * 4.3.2-F
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpcvMustReturnAcceptPatchHeaderIfPatchIsSupported(final String uri) {
        final TestInfo info = setupTest("4.3.2-F", "ldpcvMustReturnAcceptPatchHeaderIfPatchIsSupported",
                                        "If an LDPCv supports PATCH, the response to an OPTIONS request " +
                                        " MUST include the \"Accept-Patch\" header",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-options",
                                        ps);

        //create ldprv
        final Response createResponse = createVersionedResource(uri, info);
        final URI timeMapURI = getTimeMapUri(createResponse);
        final Response response = doOptions(timeMapURI.toString());
        if (hasHeaderValueInMultiValueHeader("Allow", "PATCH", response)) {
            Assert.assertTrue(getHeaders(response, "Accept-Patch").count() > 0,
                              "If an LDPCv supports POST, the response to an OPTIONS request " +
                              " MUST include the \"Accept-Patch\" header");

        }
    }
}