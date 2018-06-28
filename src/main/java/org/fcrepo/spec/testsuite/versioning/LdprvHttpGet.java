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
import static org.fcrepo.spec.testsuite.Constants.ORIGINAL_RESOURCE_LINK_HEADER;
import static org.fcrepo.spec.testsuite.Constants.SLUG;
import static org.fcrepo.spec.testsuite.Constants.TIME_GATE_LINK_HEADER;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Stream;
import javax.ws.rs.core.Link;

import io.restassured.http.Header;
import io.restassured.http.Headers;
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
     * @param username
     * @param password
     */
    @Parameters({"param2", "param3"})
    public LdprvHttpGet(final String username, final String password) {
        super(username, password);
    }

    /**
     * 4.1.1-A-1
     *
     * @param uri
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void shouldReturn406WhenNoLdprm(final String uri) {
        final TestInfo info = setupTest("4.1.1-A-1", "shouldReturn406WhenNoLdprm",
                                        "If no LDPRm is appropriate to the Accept-Datetime value, " +
                                        "implementations should return a 406 (Unacceptable).",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprv-get",
                                        ps);

        //create ldprv
        final Response response = createVersionedResource(uri, info);

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
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void shouldReturn302WhenLdprmFromTimeGate(final String uri) {
        final TestInfo info = setupTest("4.1.1-A-2", "shouldReturn302WhenLdprmFromTimeGate",
                                        "The Accept-Datetime header is used to request a past state, " +
                                        "exactly as per [RFC7089] section 2.1.1. A successful response must be a 302 " +
                                        "(Found) redirect to the appropriate LDPRm.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprv-get",
                                        ps);

        //create ldprv
        final Response createResponse = createVersionedResource(uri, info);

        final URI timeMapURI = getTimeMapUri(createResponse);

        //get timegate uri
        final URI timeGateUri = getTimeGateUri(createResponse);

        final Response newMementoResponse = doPost(timeMapURI.toString());
        final String newMementoUri = getLocation(newMementoResponse);

        //query timegate using Accept-Datetime
        final String isoDateString = "1970-01-01T00:00:00Z";

        final String rfc1123Date = convertToRfc1123DateTimeString(isoDateString);

        final Response timeGateResponse = doGet(timeGateUri.toString(), new Header("Accept-Datetime", rfc1123Date));

        timeGateResponse.then().statusCode(302);

        final String resolvedMementoUri = getLocation(timeGateResponse);

        Assert.assertEquals(newMementoUri, resolvedMementoUri);
    }

    /**
     * 4.1.1-B
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpvGetMustReturnHeaderOriginalTypeLink(final String uri) {
        final TestInfo info = setupTest("4.1.1-B", "ldpvGetMustReturnHeaderOriginalTypeLink",
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
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpvGetMustReturnHeaderTimeGateTypeLink(final String uri) {
        final TestInfo info = setupTest("4.1.1-C", "ldpvGetMustReturnHeaderTimeGateTypeLink",
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
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpvGetMustReturnOriginalResourceLink(final String uri) {
        final TestInfo info = setupTest("4.1.1-D", "ldpvGetMustReturnOriginalResourceLink",
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
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpvGetMustReturnTimeGateTypeLink(final String uri) {
        final TestInfo info = setupTest("4.1.1-E", "ldpvGetMustReturnTimeGateTypeLink",
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
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpvGetMustReturnTimeMapLink(final String uri) {
        final TestInfo info = setupTest("4.1.1-F", "ldpvGetMustReturnTimeMapLink",
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
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpvGetMustReturnVaryHeader(final String uri) {
        final TestInfo info = setupTest("4.1.1-G", "ldpvGetMustReturnVaryHeader",
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

    private Response createVersionedResource(final String uri, final TestInfo info) {
        final Headers headers = new Headers(
                new Header("Link", ORIGINAL_RESOURCE_LINK_HEADER),
                new Header(SLUG, info.getId()));
        return doPost(uri, headers);
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

    private void confirmPresenceOfLinkValue(final String linkValue, final Response response) {
        final Link link = Link.valueOf(linkValue);
        final String relType = link.getRel();
        Assert.assertEquals(getLinksOfRelType(response, link.getRel()).filter(l -> l.equals(link))
                                                                      .count(),
                            1,
                            "Link header with a value of " + linkValue + " must be present but is not!");
    }

    private URI getTimeGateUri(final Response response) {
        return getLinksOfRelTypeAsUris(response, "timegate").findFirst().get();
    }

    private URI getTimeMapUri(final Response response) {
        return getLinksOfRelTypeAsUris(response, "timemap").findFirst().get();
    }

    private URI getOriginalUri(final Response response) {
        return getLinksOfRelTypeAsUris(response, "original").findFirst().get();
    }

    private Stream<URI> getLinksOfRelTypeAsUris(final Response response, final String relType) {
        return getLinksOfRelType(response, relType)
            .map(link -> link.getUri());
    }

    private Stream<Header> getHeaders(final Response response, final String headerName) {
        return response.getHeaders()
                       .getList(headerName)
                       .stream();
    }

    private Stream<Link> getLinksOfRelType(final Response response, final String relType) {
        return getHeaders(response, "Link")
            .flatMap(header ->  Arrays.stream(header.getValue().split(",")).map(linkStr -> Link.valueOf(linkStr)))
            .filter(link -> link.getRel().equalsIgnoreCase(relType));

    }
}
