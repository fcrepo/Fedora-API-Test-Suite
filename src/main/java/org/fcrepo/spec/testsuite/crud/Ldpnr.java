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
import static org.fcrepo.spec.testsuite.Constants.SLUG;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.Constants;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class Ldpnr extends AbstractTest {

    /**
     * Authentication
     *
     * @param username The repository username
     * @param password The repository password
     */
    @Parameters({"param2", "param3"})
    public Ldpnr(final String username, final String password) {
        super(username, password);
    }

    /**
     * 3.1.2.-A
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void ldpnrCreationLinkType(final String uri) {
        final TestInfo info = setupTest("3.1.2-A",
                                        "If, in a successful resource creation request, a Link: rel=\"type\" request " +
                                        "header specifies"
                                        +
                                        " the LDP-NR interaction model (http://www.w3.org/ns/ldp#NonRDFSource, " +
                                        "regardless of "
                                        +
                                        "Content-Type: value), then the server should handle subsequent requests to " +
                                        "the newly "
                                        + "created resource as if it is an LDP-NR. ([LDP] 5.2.3.4 extension)",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpnr-ixn-model", ps);

        final Headers headers = new Headers(
                new Header(SLUG, info.getId()),
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"sample.txt\""),
                new Header("Link", Constants.NON_RDF_SOURCE_LINK_HEADER));
        final Response res = doPost(uri, headers, "TestString");

        ps.append("Request method:\tPOST\n");
        ps.append("Request URI:\t").append(uri).append("\n");

        ps.append("Body:\n");
        ps.append("HTTP/1.1 ").append(String.valueOf(res.getStatusCode())).append("\n");
        for (Header h : res.getHeaders()) {
            ps.append(h.getName()).append(": ").append(h.getValue()).append("\n");
        }

        ps.append("\n").append(res.asString()).append("\n");
        final String locationHeader = getLocation(res);

        final Response nonr = doGet(locationHeader);

        for (Header h : nonr.getHeaders()) {
            ps.append(h.getName()).append(": ").append(h.getValue()).append("\n");
        }
        ps.append("\n").append(nonr.asString()).append("\n");
        boolean header = false;

        for (Header h : nonr.getHeaders()) {
            if (h.getName().equals("Link") && h.getValue().contains("NonRDFSource")) {
                header = true;
            }
        }

        if (header) {
            Assert.assertTrue(true, "OK");
        } else {
            ps.append("\nExpected a Link: rel=\"type\" http://www.w3.org/ns/ldp#NonRDFSource.\n");

            throw new AssertionError("Expected a Link: rel=\"type\" http://www.w3.org/ns/ldp#NonRDFSource.");
        }
    }

    /**
     * 3.1.2.-B
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void ldpnrCreationWrongLinkType(final String uri) {
        final TestInfo info = setupTest("3.1.2-B",
                                        "If, in a successful resource creation request, a Link: rel=\"type\" request " +
                                        "header specifies"
                                        +
                                        " the LDP-NR interaction model (http://www.w3.org/ns/ldp#NonRDFSource, " +
                                        "regardless of "
                                        +
                                        "Content-Type: value), then the server should handle subsequent requests to " +
                                        "the newly "
                                        + "created resource as if it is an LDP-NR. ([LDP] 5.2.3.4 extension)",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpnr-ixn-model", ps);
        final Headers headers = new Headers(
                new Header(SLUG, info.getId()),
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"sample.txt\""),
                new Header("Link", "<http://www.w3.org/ns/ldp#RDFSource>; rel=\"type\""));
        final Response res = doPostUnverified(uri, headers, "TestString");

        ps.append("Request method:\tPOST\n");
        ps.append("Request URI:\t").append(uri).append("\n");
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("Body:\n");

        ps.append("HTTP/1.1 ").append(String.valueOf(res.getStatusCode())).append("\n");
        for (Header h : res.getHeaders()) {
            ps.append(h.getName()).append(": ").append(h.getValue()).append("\n");
        }

        ps.append("\n").append(res.asString()).append("\n");

        if (res.getStatusCode() >= 200 && res.getStatusCode() < 300) {
            ps.append("\nExpected response with a 4xx range status code.\n");

            throw new AssertionError("Expected response with a 4xx range status code.");
        }

    }
}
