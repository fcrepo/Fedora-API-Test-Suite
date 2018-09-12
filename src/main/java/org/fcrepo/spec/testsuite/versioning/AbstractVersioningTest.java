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

import static java.util.Arrays.sort;
import static org.fcrepo.spec.testsuite.Constants.CONTENT_DISPOSITION;
import static org.fcrepo.spec.testsuite.Constants.ORIGINAL_RESOURCE_LINK_HEADER;

import java.net.URI;
import java.util.Arrays;
import javax.ws.rs.core.Link;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.Assert;
import org.testng.annotations.Parameters;

/**
 * @author Daniel Bernstein
 */
public class AbstractVersioningTest extends AbstractTest {

    /**
     * Authentication
     *
     * @param username The repository username
     * @param password The repository password
     */
    @Parameters({"param2", "param3"})
    public AbstractVersioningTest(final String username, final String password) {
        super(username, password);
    }

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

    private Response putVersionedResourceUnverified(final String uri) {
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
        return getHeaders(response, headerName).flatMap(header -> {
            return Arrays.stream(header.getValue().split(","));
        })
                                               .map(val -> val.trim())
                                               .filter(val -> {
                                                   return val.equalsIgnoreCase(headerValue);
                                               }).count() > 0;

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

    protected void confirmResponseBodyNTriplesAreEqual(final Response resourceA, final Response resourceB) {

        final String[] aTriples = resourceA.body().asString().split(".(\\r\\n|\\r|\\n)");
        final String[] bTriples = resourceB.body().asString().split(".(\\r\\n|\\r|\\n)");

        sort(aTriples);
        sort(bTriples);

        Assert.assertTrue(Arrays.equals(aTriples, bTriples));
    }

    /**
     * Given the URI of the original resource, create a memento based on the
     * current state of the reosurce.
     * @param originalResourceUri The resource to be memento-ized.
     * @return
     */
    protected String createMemento(final String originalResourceUri) {
        final Response response = doGet(originalResourceUri);
        final URI timeMapURI = getTimeMapUri(response);
        final Response timeMapResponse = doPost(timeMapURI.toString());
        return getLocation(timeMapResponse);
    }

}
