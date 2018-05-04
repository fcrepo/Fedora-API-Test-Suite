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

import java.io.FileNotFoundException;

import io.restassured.http.Header;
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
     * @param username
     * @param password
     */
    @Parameters({"param2", "param3"})
    public Ldpnr(final String username, final String password) {
        super(username, password);
    }

    /**
     * 3.1.2.-A
     *
     * @param uri
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void ldpnrCreationLinkType(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.1.2-A", "ldpnrCreationLinkType",
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

        final Response res = createRequest().header(CONTENT_DISPOSITION, "attachment; filename=\"sample.txt\"")
                                            .header("Link", Constants.NON_RDF_SOURCE_LINK_HEADER)
                                            .header(SLUG, info.getId())
                                            .body("TestString")
                                            .when()
                                            .post(uri);

        ps.append("Request method:\tPOST\n");
        ps.append("Request URI:\t" + uri + "\n");

        ps.append("Body:\n");
        ps.append("HTTP/1.1 " + res.getStatusCode() + "\n");
        for (Header h : res.getHeaders()) {
            ps.append(h.getName().toString() + ": " + h.getValue().toString() + "\n");
        }

        ps.append("\n" + res.asString() + "\n");
        final String locationHeader = getLocation(res);

        if (res.getStatusCode() == 201) {
            final Response nonr = createRequest()
                .when()
                .get(locationHeader);

            for (Header h : nonr.getHeaders()) {
                ps.append(h.getName().toString() + ": " + h.getValue().toString() + "\n");
            }
            ps.append("\n" + nonr.asString() + "\n");
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

        } else {
            ps.append("\nExpected response with a 2xx range status code.\n");

            throw new AssertionError("Expected response with a 2xx range status code.");
        }

    }

    /**
     * 3.1.2.-B
     *
     * @param uri
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void ldpnrCreationWrongLinkType(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.1.2-B", "ldpnrCreationWrongLinkType",
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
        final Response res = createRequest().header(CONTENT_DISPOSITION, "attachment; filename=\"sample.txt\"")
                                            .header("Link", "<http://www.w3.org/ns/ldp#RDFSource>; rel=\"type\"")
                                            .header(SLUG, info.getId())
                                            .body("TestString")
                                            .when()
                                            .post(uri);
        ps.append("Request method:\tPOST\n");
        ps.append("Request URI:\t" + uri + "\n");
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("Body:\n");

        ps.append("HTTP/1.1 " + res.getStatusCode() + "\n");
        for (Header h : res.getHeaders()) {
            ps.append(h.getName() + ": " + h.getValue() + "\n");
        }

        ps.append("\n" + res.asString() + "\n");

        if (res.getStatusCode() >= 200 && res.getStatusCode() < 300) {
            ps.append("\nExpected response with a 4xx range status code.\n");

            throw new AssertionError("Expected response with a 4xx range status code.");
        }

    }
}
