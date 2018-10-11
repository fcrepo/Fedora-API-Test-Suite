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

import io.restassured.http.Header;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static org.fcrepo.spec.testsuite.App.ROOT_CONTROLLER_USER_WEBID_PARAM;
import static org.fcrepo.spec.testsuite.App.TEST_CONTAINER_URL_PARAM;
import static org.fcrepo.spec.testsuite.Constants.CONTAINER_LINK_HEADER;
import static org.fcrepo.spec.testsuite.Constants.TIME_MAP_LINK_HEADER;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * Tests for GET requests on LDP Version Container Resources
 *
 * @author Daniel Bernstein
 */
public class LdpcvHttpGet extends AbstractVersioningTest {

    /**
     * Authentication
     *
     * @param username The repository username
     */
    @Parameters({ROOT_CONTROLLER_USER_WEBID_PARAM})
    public LdpcvHttpGet(final String username) {
        super(username);
    }

    /**
     * 4.3.1-A
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void ldpcvMustSupportGet(final String uri) {
        final TestInfo info = setupTest("4.3.1-A",
                                        "LDPCv must support GET, as is the case for any LDPR",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-get",
                                        ps);

        final String timeMap = createVersionedResourceAndGetTimeMapURL(uri, info);
        //perform a GET and verify that it is successful.
        doGet(timeMap);
    }

    /**
     * 4.3.1-B
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void ldpcvMustHaveTimeMapLinkHeader(final String uri) {
        final TestInfo info = setupTest("4.3.1-B",
                                        "LDPCv contain TimeMap type link header.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-get",
                                        ps);

        final String timeMap = createVersionedResourceAndGetTimeMapURL(uri, info);
        //perform a GET and verify it has the proper header.
        confirmPresenceOfLinkValue(TIME_MAP_LINK_HEADER, doGet(timeMap));
    }

    private String createVersionedResourceAndGetTimeMapURL(final String uri, final TestInfo info) {
        final Response resource = createVersionedResource(uri, info);
        //get the timemap
        return getTimeMapUri(doGet(getLocation(resource))).toString();
    }

    /**
     * 4.3.1-C
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void ldpcvMustRespondToGetWithApplicationLinkAcceptHeader(final String uri) {
        final TestInfo info = setupTest("4.3.1-C",
                                        "An LDPCv must respond to GET Accept: application/link-format as " +
                                        "indicated in [ RFC7089 ] section 5 and specified in [ RFC6690 ] section 7.3.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-get",
                                        ps);

        final String timeMap = createVersionedResourceAndGetTimeMapURL(uri, info);
        //ensure timemap can be retrieved with the Accept: application/link-format
        doGet(timeMap, new Header("Accept", "application/link-format"));
    }

    /**
     * 4.3.1-D
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void lpcvMustIncludeAllowHeader(final String uri) {
        final TestInfo info = setupTest("4.3.1-D",
                                        "LDPCv resources must include the Allow header",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-get",
                                        ps);

        final String timeMap = createVersionedResourceAndGetTimeMapURL(uri, info);
        //perform a GET and verify it has the proper header.
        final Response timeMapResponse = doGet(timeMap);
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "GET", timeMapResponse);
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "HEAD", timeMapResponse);
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "OPTIONS", timeMapResponse);
    }

    /**
     * 4.3.1-E
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void ldpcvMustIncludeAcceptPostIfPostAllowed(final String uri) {
        final TestInfo info = setupTest("4.3.1-E",
                                        "If an LDPCv supports POST, then it must include the Accept-Post header",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-get",
                                        ps);
        final String timeMap = createVersionedResourceAndGetTimeMapURL(uri, info);
        final Response timeMapResponse = doGet(timeMap);
        //check if LDPCv allows "POST"
        if (hasHeaderValueInMultiValueHeader("Allow", "POST", timeMapResponse)) {
            //If so,  check for presence of Accept-Post header.
            assertNotNull("Accept-Post must be present if POST is allowed", timeMapResponse.getHeader("Accept-Post"));
        }
    }

    /**
     * 4.3.1-F
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void ldpcvMustIncludeAcceptPatchIfPatchAllowed(final String uri) {
        final TestInfo info = setupTest("4.3.1-F",
                                        "If an LDPCv supports PATCH, then it must include the Accept-Patch header",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-get",
                                        ps);
        final String timeMap = createVersionedResourceAndGetTimeMapURL(uri, info);
        final Response timeMapResponse = doGet(timeMap);
        //check if LDPCv allows "PATCH"
        if (hasHeaderValueInMultiValueHeader("Allow", "PATCH", timeMapResponse)) {
            //If so,  check for presence of Accept-Patch header.
            assertNotNull("Accept-Patch must be present if PATCH is allowed",
                          timeMapResponse.getHeader("Accept-Patch"));
        }

    }

    /**
     * 4.3.1-G
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void ldpcvMustHaveContainerLinkHeader(final String uri) {
        final TestInfo info = setupTest("4.3.1-G",
                                        "An LDPCv, being a container must have a \"Link: <http://www" +
                                        ".w3.org/ns/ldp#Container>;rel=\"type\"\"",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-get",
                                        ps);

        final String timeMap = createVersionedResourceAndGetTimeMapURL(uri, info);
        //perform a GET and verify it has the proper header.
        final Response timeMapResponse = doGet(timeMap);
        confirmPresenceOfLinkValue(CONTAINER_LINK_HEADER, timeMapResponse);
    }

}

