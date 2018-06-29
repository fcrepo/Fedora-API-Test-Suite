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

import java.util.ArrayList;
import java.util.List;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.fcrepo.spec.testsuite.TestSuiteGlobals;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class HttpHead extends AbstractTest {

    /**
     * Authentication
     *
     * @param username The repository username
     * @param password The repository password
     */
    @Parameters({"param2", "param3"})
    public HttpHead(final String username, final String password) {
        super(username, password);
    }

    /**
     * 3.3-A
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void httpHeadResponseNoBody(final String uri) {
        final TestInfo info = setupTest("3.3-A", "httpHeadResponseNoBody",
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
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void httpHeadResponseDigest(final String uri) {
        final TestInfo info = setupTest("3.3-B", "httpHeadResponseDigest",
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
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void httpHeadResponseHeadersSameAsHttpGet(final String uri) {
        final TestInfo info = setupTest("3.3-C", "httpHeadResponseHeadersSameAsHttpGet",
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
        // Compares if both lists have the same size
        if (!h2.equals(h1)) {
            Assert.fail();
        }

    }

}
