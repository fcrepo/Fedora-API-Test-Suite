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
package org.fcrepo.spec.testsuite.crud;

import static org.fcrepo.spec.testsuite.Constants.CONTENT_DISPOSITION;
import static org.fcrepo.spec.testsuite.Constants.DIGEST;
import static org.fcrepo.spec.testsuite.Constants.SLUG;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.fcrepo.spec.testsuite.TestSuiteGlobals;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class HttpHead extends AbstractTest {

    /**
     * 3.3-A
     */
    @Test(groups = {"MUST"})
    public void httpHeadResponseNoBody() {
        final TestInfo info = setupTest("3.3-A",
                                        "The HEAD method is identical to GET except that the server must not return a "
                                        + "message-body in the response, as "
                                        + "specified in [RFC7231] section 4.3.2.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-head",
                                        ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resource);
        doHead(locationHeader).then().assertThat().body(equalTo(""));
    }

    /**
     * 3.3-B
     */
    @Test(groups = {"MUST"})
    public void httpHeadResponseDigest() {
        final TestInfo info = setupTest("3.3-B",
                                        "The server must send the same Digest header in the response as it"
                                        +
                                        " would have sent if the request had been a GET (or omit it if it would have " +
                                        "been omitted for a GET).",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-head",
                                        ps);
        final Headers headers = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"headerwantdigest.txt\""),
                new Header(SLUG, info.getId()));
        final Response resource = doPost(uri, headers, "TestString.");

        final String locationHeader = getLocation(resource);
        final Response resget = doGet(locationHeader);

        ps.append(resget.getStatusLine()).append("\n");
        final Headers headersGet = resget.getHeaders();
        for (Header h : headersGet) {
            ps.append(h.getName()).append(": ");
            ps.append(h.getValue()).append("\n");
        }

        final Response reshead = doHead(locationHeader);

        ps.append(reshead.getStatusLine()).append("\n");
        final Headers responseHeaders = reshead.getHeaders();
        for (Header h : responseHeaders) {
            ps.append(h.getName()).append(": ");
            ps.append(h.getValue()).append("\n");
        }

        if (resget.getStatusCode() == 200 && reshead.getStatusCode() == 200) {

            if (resget.getHeader(DIGEST) == null) {
                if (reshead.getHeader(DIGEST) != null) {
                    Assert.fail();
                }
            } else if (reshead.getHeader(DIGEST) == null) {
                Assert.fail();
            }
        } else {
            Assert.fail();
        }
    }

    /**
     * 3.3-C
     */
    @Test(groups = {"SHOULD"})
    public void httpHeadResponseHeadersSameAsHttpGet() {
        final TestInfo info = setupTest("3.3-C",
                                        "In other cases, The server should send the same headers in response to a " +
                                        "HEAD request "
                                        + "as it would have sent if the request had "
                                        +
                                        "been a GET, except that the payload headers (defined in [RFC7231] section " +
                                        "3.3) may be omitted.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-head",
                                        ps);
        final Response resource = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resource);
        final Response resget = doGet(locationHeader);

        ps.append(resget.getStatusLine()).append("\n");
        final Headers headersGet = resget.getHeaders();
        for (Header h : headersGet) {
            ps.append(h.getName()).append(": ");
            ps.append(h.getValue()).append("\n");
        }

        final List<Header> h1 = new ArrayList<>();
        for (Header h : headersGet) {
            if (!TestSuiteGlobals.checkPayloadHeader(h.getName())) {
                h1.add(h);
            }
        }

        final Response reshead = doHead(locationHeader);

        ps.append(reshead.getStatusLine()).append("\n");
        final Headers headershead = reshead.getHeaders();
        for (Header h : headershead) {
            ps.append(h.getName()).append(": ");
            ps.append(h.getValue()).append("\n");
        }
        final List<Header> h2 = new ArrayList<>();
        for (Header h : headershead) {
            if (!TestSuiteGlobals.checkPayloadHeader(h.getName())) {
                h2.add(h);
            }
        }
        // Ensures that lists are the same size
        assertEquals(h1.size(), h2.size(), "Lists should be the same size: GET header count=" + h1.size() +
                                                      ", HEAD header count =" + h2.size());

        removeNonComparableHeaders(h1);
        removeNonComparableHeaders(h2);

        // Ensures that headers that should be compared are the same.
        assertEquals(h1, h2, "Comparable headers should be the same between GET and HEAD");
    }

    private void removeNonComparableHeaders(final List<Header> headers) {
        final List<String> nonComparableHeaders = Arrays.asList("Date", "Set-Cookie");
        for (int i = headers.size() - 1; i > -1; i--) {
            final Header header = headers.get(i);
            for (String headerName : nonComparableHeaders) {
                if (header.getName().equalsIgnoreCase(headerName)) {
                    headers.remove(i);
                    break;
                }
            }
        }
    }

}
