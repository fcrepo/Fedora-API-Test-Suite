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

import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static org.fcrepo.spec.testsuite.App.ROOT_CONTROLLER_USER_WEBID_PARAM;
import static org.fcrepo.spec.testsuite.App.TEST_CONTAINER_URL_PARAM;
import static org.fcrepo.spec.testsuite.Constants.ORIGINAL_RESOURCE_LINK_HEADER;
import static org.fcrepo.spec.testsuite.Constants.TIME_GATE_LINK_HEADER;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import io.restassured.http.Header;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Daniel Bernstein
 */
public class LdprvHttpGet extends AbstractVersioningTest {

    /**
     * Authentication
     *
     * @param username The repository username
     */
    @Parameters({ROOT_CONTROLLER_USER_WEBID_PARAM})
    public LdprvHttpGet(final String username) {
        super(username);
    }

    /**
     * 4.1.1-A-1
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"SHOULD"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void shouldReturn406WhenNoLdprm(final String uri) {
        final TestInfo info = setupTest("4.1.1-A-1",
                                        "If no LDPRm is appropriate to the Accept-Datetime value, " +
                                        "implementations should return a 406 (Unacceptable).",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprv-get",
                                        ps);

        //create ldprv
        final Response creationResponse = createVersionedResource(uri, info);

        //get location of new resource
        final String resourceUri = getLocation(creationResponse);

        final Response response = doGet(resourceUri);

        //confirm presence of timegate link
        confirmPresenceOfTimeGateLink(response);

        //get timegate uri
        final URI timeGateUri = getTimeGateUri(response);

        //query timegate using Accept-Datetime
        final String isoDateString = "1970-01-01T00:00:00Z";

        final String rfc1123Date = convertToRfc1123DateTimeString(isoDateString);

        doGetUnverified(timeGateUri.toString(), new Header("Accept-Datetime", rfc1123Date)).then().statusCode(406);
    }

    /**
     * 4.1.1-A-2
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void shouldReturn302WhenLdprmFromTimeGate(final String uri) {
        final TestInfo info = setupTest("4.1.1-A-2",
                                        "The Accept-Datetime header is used to request a past state, " +
                                        "exactly as per [RFC7089] section 2.1.1. A successful response must be a 302 " +
                                        "(Found) redirect to the appropriate LDPRm.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprv-get",
                                        ps);

        //create ldprv
        final Response createResponse = createVersionedResource(uri, info);

        //get location of new resource
        final String resourceUri = getLocation(createResponse);

        final Response response = doGet(resourceUri);

        final URI timeMapURI = getTimeMapUri(response);

        //get timegate uri
        final URI timeGateUri = getTimeGateUri(response);

        final String newMementoUri = createMemento(resourceUri);

        //query timegate using Accept-Datetime
        final String isoDateString = "1970-01-01T00:00:00Z";

        final String rfc1123Date = convertToRfc1123DateTimeString(isoDateString);

        final Response timeGateResponse = doGetUnverified(timeGateUri.toString(),
                                                          new Header("Accept-Datetime", rfc1123Date));

        timeGateResponse.then().statusCode(302);

        final String resolvedMementoUri = getLocation(timeGateResponse);

        Assert.assertEquals(newMementoUri, resolvedMementoUri);
    }

    /**
     * 4.1.1-B
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void ldpvGetMustReturnHeaderOriginalTypeLink(final String uri) {
        final TestInfo info = setupTest("4.1.1-B",
                                        "The response to a GET request on an LDPRv must return " +
                                        " a rel=\"timegate\" Link header referencing itself ",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprv-get",
                                        ps);

        final Response createdResponse = createVersionedResource(uri, info);
        final String resourceUri = getLocation(createdResponse);
        final Response response = doGet(resourceUri);
        final URI original = getOriginalUri(response);
        Assert.assertEquals(resourceUri, original.toString());
    }

    /**
     * 4.1.1-C
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void ldpvGetMustReturnHeaderTimeGateTypeLink(final String uri) {
        final TestInfo info = setupTest("4.1.1-C",
                                        "The response to a GET request on an LDPRv must return " +
                                        " a rel=\"timegate\" Link header referencing itself",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprv-get",
                                        ps);

        final Response createdResponse = createVersionedResource(uri, info);
        final String resourceUri = getLocation(createdResponse);
        final Response response = doGet(resourceUri);
        final URI original = getTimeGateUri(response);
        Assert.assertEquals(resourceUri, original.toString());
    }

    /**
     * 4.1.1-D
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void ldpvGetMustReturnOriginalResourceLink(final String uri) {
        final TestInfo info = setupTest("4.1.1-D",
                                        "The response to a GET request on an LDPRv must return a " +
                                        "<http://mementoweb.org/ns#OriginalResource>; rel=\"type\" link in the " +
                                        "Link header.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprv-get",
                                        ps);

        final Response createdResponse = createVersionedResource(uri, info);
        final String resourceUri = getLocation(createdResponse);
        final Response response = doGet(resourceUri);
        confirmPresenceOfLinkValue(ORIGINAL_RESOURCE_LINK_HEADER, response);
    }

    /**
     * 4.1.1-E
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void ldpvGetMustReturnTimeGateTypeLink(final String uri) {
        final TestInfo info = setupTest("4.1.1-E",
                                        "The response to a GET request on an LDPRv must return a " +
                                        "<http://mementoweb.org/ns#OriginalResource>; rel=\"type\" link in the " +
                                        "Link header.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprv-get",
                                        ps);

        final Response createdResponse = createVersionedResource(uri, info);
        final String resourceUri = getLocation(createdResponse);
        final Response response = doGet(resourceUri);
        confirmPresenceOfLinkValue(TIME_GATE_LINK_HEADER, response);
    }

    /**
     * 4.1.1-F
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void ldpvGetMustReturnTimeMapLink(final String uri) {
        final TestInfo info = setupTest("4.1.1-F",
                                        "The response to a GET request on an LDPRv must return At least one " +
                                        "rel=\"timemap\" link in the Link header referencing an associated LDPCv",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprv-get",
                                        ps);

        final Response createdResponse = createVersionedResource(uri, info);
        final String resourceUri = getLocation(createdResponse);
        final Response response = doGet(resourceUri);
        confirmPresenceOfTimeMapLink(response);

    }

    /**
     * 4.1.1-G
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void ldpvGetMustReturnVaryHeader(final String uri) {
        final TestInfo info = setupTest("4.1.1-G",
                                        "The response to a GET request on an LDPRv must return " +
                                        "a Vary: Accept-Datetime header, exactly as per [RFC7089] section 2.1.2.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprv-get",
                                        ps);

        final Response createdResponse = createVersionedResource(uri, info);
        final String resourceUri = getLocation(createdResponse);
        final Response response = doGet(resourceUri);

        Assert.assertEquals(getHeaders(response, "Vary").filter(x -> {
            return Arrays.stream(x.getValue().split(","))
                         .filter(val -> {
                             return val.trim().equalsIgnoreCase("accept-datetime");
                         }).count() > 0;
        }).count(), 1, "Link header with " +
                       "'Vary: accept-datetime' value " +
                       "must be present");

    }

    private String convertToRfc1123DateTimeString(final String isoDateString) {
        final DateTimeFormatter FMT = RFC_1123_DATE_TIME.withZone(ZoneId.of("UTC"));
        return FMT.format(ISO_INSTANT.parse(isoDateString, Instant::from));
    }

    private void confirmPresenceOfTimeGateLink(final Response response) {
        confirmPresenceOfRelType(response, "timegate");
    }

    private void confirmPresenceOfTimeMapLink(final Response response) {
        confirmPresenceOfRelType(response, "timemap");
    }

    private void confirmPresenceOfRelType(final Response response, final String relType) {
        Assert.assertEquals(getLinksOfRelType(response, relType).count(),
                            1,
                            "Link with rel type '" + relType + "' must be present but is not!");
    }

    private URI getOriginalUri(final Response response) {
        return getLinksOfRelTypeAsUris(response, "original").findFirst().get();
    }
}
