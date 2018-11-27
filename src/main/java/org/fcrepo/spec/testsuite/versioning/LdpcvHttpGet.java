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
import org.testng.annotations.Test;

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
     * 4.3.1-A
     */
    @Test(groups = {"MUST"})
    public void ldpcvMustSupportGet() {
        final TestInfo info = setupTest("4.3.1-A",
                                        "LDPCv must support GET, as is the case for any LDPR",
                                        SPEC_BASE_URL + "#ldpcv-get",
                                        ps);

        final String timeMap = createVersionedResourceAndGetTimeMapURL(uri, info);
        //perform a GET and verify that it is successful.
        doGet(timeMap);
    }

    /**
     * 4.3.1-B
     */
    @Test(groups = {"MUST"})
    public void ldpcvMustHaveTimeMapLinkHeader() {
        final TestInfo info = setupTest("4.3.1-B",
                                        "LDPCv contain TimeMap type link header.",
                                        SPEC_BASE_URL + "#ldpcv-get",
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
     */
    @Test(groups = {"MUST"})
    public void ldpcvMustRespondToGetWithApplicationLinkAcceptHeader() {
        final TestInfo info = setupTest("4.3.1-C",
                                        "An LDPCv must respond to GET Accept: application/link-format as " +
                                        "indicated in [ RFC7089 ] section 5 and specified in [ RFC6690 ] section 7.3.",
                                        SPEC_BASE_URL + "#ldpcv-get",
                                        ps);

        final String timeMap = createVersionedResourceAndGetTimeMapURL(uri, info);
        //ensure timemap can be retrieved with the Accept: application/link-format
        doGet(timeMap, new Header("Accept", "application/link-format"));
    }

    /**
     * 4.3.1-D
     */
    @Test(groups = {"MUST"})
    public void lpcvMustIncludeAllowHeader() {
        final TestInfo info = setupTest("4.3.1-D",
                                        "LDPCv resources must include the Allow header",
                                        SPEC_BASE_URL + "#ldpcv-get",
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
     */
    @Test(groups = {"MUST"})
    public void ldpcvMustIncludeAcceptPostIfPostAllowed() {
        final TestInfo info = setupTest("4.3.1-E",
                                        "If an LDPCv supports POST, then it must include the Accept-Post header",
                                        SPEC_BASE_URL + "#ldpcv-get",
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
     */
    @Test(groups = {"MUST"})
    public void ldpcvMustIncludeAcceptPatchIfPatchAllowed() {
        final TestInfo info = setupTest("4.3.1-F",
                                        "If an LDPCv supports PATCH, then it must include the Accept-Patch header",
                                        SPEC_BASE_URL + "#ldpcv-get",
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
     */
    @Test(groups = {"MUST"})
    public void ldpcvMustHaveContainerLinkHeader() {
        final TestInfo info = setupTest("4.3.1-G",
                                        "An LDPCv, being a container must have a \"Link: <http://www" +
                                        ".w3.org/ns/ldp#Container>;rel=\"type\"\"",
                                        SPEC_BASE_URL + "#ldpcv-get",
                                        ps);

        final String timeMap = createVersionedResourceAndGetTimeMapURL(uri, info);
        //perform a GET and verify it has the proper header.
        final Response timeMapResponse = doGet(timeMap);
        confirmPresenceOfLinkValue(CONTAINER_LINK_HEADER, timeMapResponse);
    }

}

