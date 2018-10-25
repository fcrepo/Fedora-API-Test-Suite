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

import static java.time.Instant.now;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static java.util.Arrays.sort;
import static org.fcrepo.spec.testsuite.Constants.CONTENT_DISPOSITION;
import static org.fcrepo.spec.testsuite.Constants.MEMENTO_DATETIME_HEADER;
import static org.fcrepo.spec.testsuite.Constants.ORIGINAL_RESOURCE_LINK_HEADER;
import static org.fcrepo.spec.testsuite.Constants.RDF_SOURCE_LINK_HEADER;
import static org.testng.AssertJUnit.assertEquals;

import java.net.URI;
import java.time.ZoneId;
import java.util.Arrays;
import javax.ws.rs.core.Link;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.apache.http.message.BasicHeaderValueParser;
import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.Assert;

/**
 * @author Daniel Bernstein
 */
public class AbstractVersioningTest extends AbstractTest {

    protected URI getTimeMapUri(final Response response) {
        return getLinksOfRelTypeAsUris(response, "timemap").findFirst().get();
    }

    protected Response createVersionedResource(final String uri, final TestInfo info) {
        final Headers headers = new Headers(
            new Header("Link", ORIGINAL_RESOURCE_LINK_HEADER),
            new Header("Content-Type", "text/turtle"));  // this line keeps it from becoming a LDP-NR
        return doPost(uri, headers);
    }

    protected Response createVersionedNonRDFResource(final String uri, final TestInfo info) {
        final Headers headers = new Headers(
            new Header("Link", ORIGINAL_RESOURCE_LINK_HEADER),
            new Header(CONTENT_DISPOSITION, "attachment; filename=\"postNonRDFSource.txt\""),
            new Header("Content-Type", "text/plain"));
        return doPost(uri, headers, "Test String.");
    }

    protected Response putVersionedResourceUnverified(final String uri, final TestInfo info) {
        return putVersionedResourceUnverified(joinLocation(uri, info.getId()));
    }

    protected Response putVersionedResourceWithBodyUnverified(final String uri, final TestInfo info,
                                                              final String body) {
        return putVersionedResourceWithBodyUnverified(joinLocation(uri, info.getId()), body);
    }

    protected Response putVersionedResourceUnverified(final String uri) {
        final Headers headers = new Headers(
            new Header("Link", ORIGINAL_RESOURCE_LINK_HEADER));
        return doPutUnverified(uri, headers);
    }

    protected Response putVersionedResourceWithBodyUnverified(final String uri, final String body) {
        final Headers headers = new Headers(
            new Header("Link", ORIGINAL_RESOURCE_LINK_HEADER));
        return doPutUnverified(uri, headers, body);
    }

    protected void confirmPresenceOfHeaderValueInMultiValueHeader(final String headerName, final String headerValue,
                                                                  final Response response) {
        Assert
            .assertTrue(hasHeaderValueInMultiValueHeader(headerName, headerValue, response),
                        headerName + " with a value of " + headerValue + " must be present but is not!");
    }

    protected void confirmAbsenceOfHeaderValueInMultiValueHeader(final String headerName, final String headerValue,
                                                                 final Response response) {
        Assert
            .assertFalse(hasHeaderValueInMultiValueHeader(headerName, headerValue, response),
                        headerName + " with a value of " + headerValue + " must be not present but it is!");
    }

    protected boolean hasHeaderValueInMultiValueHeader(final String headerName, final String headerValue,
                                                       final Response response) {
        return getHeaders(response, headerName).flatMap(header ->
            Arrays.stream(BasicHeaderValueParser.parseElements(header.getValue(), null)))
                .map(headerElement -> headerElement.toString().trim())
                .anyMatch(val -> val.equalsIgnoreCase(headerValue));
    }

    protected void confirmPresenceOfMementoDatetimeHeader(final String mementoDateTime, final Response response) {
        final String dateTime = response.getHeader(MEMENTO_DATETIME_HEADER);
        assertEquals("Memento-Datetime header does not match expected", mementoDateTime, dateTime);
    }


    protected void confirmPresenceOfVersionLocationHeader(final Response response) {
        final String location = response.getHeader("Location");
        Assert.assertTrue(location.contains("fcr:versions"));
    }


    protected void confirmAbsenceOfLinkValue(final String linkValue, final Response response) {
        final Link link = Link.valueOf(linkValue);
        final String relType = link.getRel();
        Assert.assertEquals(getLinksOfRelType(response, link.getRel()).filter(l -> l.equals(link))
                                                                      .count(),
                            0,
                            "Link header with a value of " + linkValue + " must not be present (but it is)!");
    }

    protected void confirmResponseBodyNonRDFSourcesAreEqual(final Response resourceA, final Response resourceB) {
        Assert.assertTrue(Arrays.equals(resourceA.body().asByteArray(), resourceB.body().asByteArray()));
    }

    protected void confirmResponseBodyNTriplesAreEqual(final String responseBodyA, final String responseBodyB) {
        final String[] aTriples = responseBodyA.split(".(\\r\\n|\\r|\\n)");
        final String[] bTriples = responseBodyB.split(".(\\r\\n|\\r|\\n)");
        Arrays.stream(aTriples).map(String::trim).toArray(unused -> aTriples);
        Arrays.stream(bTriples).map(String::trim).toArray(unused -> bTriples);
        sort(aTriples);
        sort(bTriples);
        Assert.assertTrue(Arrays.equals(aTriples, bTriples));
    }

    protected void confirmResponseBodyNTriplesAreEqual(final Response resourceA, final Response resourceB) {
        confirmResponseBodyNTriplesAreEqual(resourceA.getBody().asString(), resourceB.getBody().asString());
    }

    /**
     * Given the URI of the original resource, create a memento based on the
     * specified body.
     * @param originalResourceUri The resource to be memento-ized.
     * @param mementoDateTime the memento datetime
     * @param contentType the content Type of the memento
     * @param body the body of the memento
     * @return The uri of the newly created memento
     */
    protected String createMemento(final String originalResourceUri, final String mementoDateTime,
                                   final String contentType, final String body) {
        final Response response = doGet(originalResourceUri);
        final URI timeMapURI = getTimeMapUri(response);
        final Headers headers = new Headers(new Header("Content-Type", contentType),
                                            new Header(MEMENTO_DATETIME_HEADER, mementoDateTime));

        final Response timeMapResponse = doPost(timeMapURI.toString(),headers, body);
        return getLocation(timeMapResponse);
    }

    protected URI getTimeGateUri(final Response response) {
        return getLinksOfRelTypeAsUris(response, "timegate").findFirst().get();
    }

    protected String createMemento(final String originalResourceUri) {
        final Response response = doGet(originalResourceUri);
        final URI timeMapURI = getTimeMapUri(response);
        if (hasHeaderValueInMultiValueHeader("Allow", "Post", doGet(timeMapURI.toString()))) {
            //if POST allowed (client-managed versioning)
            return getLocation(doPost(timeMapURI.toString()));
        } else {
            // otherwise create a memento by altering the original resource and retrieving the most recent memento
            //if ldp-rs
            if (getLinksOfRelType(response, "type")
                .anyMatch(link -> link.equals(Link.valueOf(RDF_SOURCE_LINK_HEADER)))) {
                final String body = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                                    "PREFIX pcdm: <http://pcdm.org/models#>\n" +
                                    "INSERT  {\n" +
                                    " <>  rdf:type pcdm:Collection .\n" +
                                    "} WHERE {}";
                doPatch(originalResourceUri, new Headers(new Header("Content-Type", "application/sparql-update")),
                        body);
            } else {
                //otherwise ldp-nr
                doPut(originalResourceUri, new Headers(new Header("Content-Type", "text/plain")),
                      "body-" + System.currentTimeMillis());
            }

            final String now = RFC_1123_DATE_TIME.withZone(ZoneId.of("UTC")).format(now());
            final String timeGate = getTimeGateUri(response).toString();
            //get the most recent memento
            final Response timeGateResponse = doGetUnverified(timeGate, new Header("Accept-Datetime", now));
            timeGateResponse.then().statusCode(302);
            return getLocation(timeGateResponse);
        }
    }
}
