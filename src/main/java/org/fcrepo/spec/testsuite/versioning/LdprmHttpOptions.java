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

import static org.testng.Assert.assertEquals;

import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.annotations.Test;

/**
 * Tests for OPTIONS requests on LDP Memento Resources
 *
 * @author Daniel Bernstein
 */
public class LdprmHttpOptions extends AbstractVersioningTest {

    /**
     * 4.2.2-A
     */
    @Test(groups = {"MUST"})
    public void ldprmMustSupportOptions() {
        final TestInfo info = setupTest("4.2.2-A",
                                        "LDPRm resources must support OPTIONS",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-options",
                                        ps);

        //create an LDPRm
        final String mementoURI = createMemento(uri, info);
        final Response mementoResponse = doGet(mementoURI);
        //verify that a subsequent GET returns OPTIONS in "Allow" header.
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "OPTIONS", mementoResponse);
    }

    private String createMemento(final String uri, final TestInfo info) {
        final Response createResponse = createVersionedResource(uri, info);
        final String resourceUri = getLocation(createResponse);
        return createMemento(resourceUri);
    }

    /**
     * 4.2.2-B
     */
    @Test(groups = {"MUST"})
    public void ldprmOptionsMustSupportGetHeadAndOptions() {
        final TestInfo info = setupTest("4.2.2-B",
                                        "A response to an OPTIONS request must include Allow: GET, HEAD, OPTIONS",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-options",
                                        ps);

        //create an LDPRm
        final String mementoURI = createMemento(uri, info);
        final Response mementoResponse = doGet(mementoURI);
        //verify that a subsequent OPTIONS return "GET", "HEAD", and "OPTIONS" in "Allow" header.
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "OPTIONS", mementoResponse);
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "GET", mementoResponse);
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "HEAD", mementoResponse);
    }

    /**
     * 4.2.2-C
     */
    @Test(groups = {"MAY"})
    public void ldprmOptionsMaySupportDelete() {
        final TestInfo info = setupTest("4.2.2-C",
                                        "A response to an OPTIONS request may include Allow: DELETE",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-options",
                                        ps);

        //create an LDPRm
        final String mementoURI = createMemento(uri, info);
        final Response mementoResponse = doGet(mementoURI);
        //verify that a subsequent OPTIONS return "DELETE"
        confirmPresenceOfHeaderValueInMultiValueHeader("Allow", "DELETE", mementoResponse);

    }

    /**
     * 4.2.3
     */
    @Test(groups = {"MUST"})
    public void ldprmMustNotSupportPost() {
        final TestInfo info = setupTest("4.2.3",
                                        "An LDPRm must not support POST",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-post",
                                        ps);
        //create an LDPRm
        final String mementoURI = createMemento(uri, info);
        final Response mementoResponse = doGet(mementoURI);
        //verify that a subsequent OPTIONS does not return POST
        confirmAbsenceOfHeaderValueInMultiValueHeader("Allow", "POST", mementoResponse);
        //verify that issuing a POST returns a 405 (method not allowed).
        final Response response = doPostUnverified(mementoURI);
        assertEquals(response.getStatusCode(), 405, "Response to a POST should be 405");
    }

    /**
     * 4.2.4
     */
    @Test(groups = {"MUST"})
    public void ldprmMustNotSupportPut() {
        final TestInfo info = setupTest("4.2.4",
                                        "An LDPRm must not support PUT",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-put",
                                        ps);

        //create an LDPRm
        final String mementoURI = createMemento(uri, info);
        final Response mementoResponse = doGet(mementoURI);
        //verify that a subsequent OPTIONS does not return PUT
        confirmAbsenceOfHeaderValueInMultiValueHeader("Allow", "PUT", mementoResponse);
        //verify that issuing a PUT returns a 405 (method not allowed).
        final Response response = doPutUnverified(mementoURI);
        assertEquals(response.getStatusCode(), 405, "Response to a PUT should be 405");

    }

    /**
     * 4.2.5
     */
    @Test(groups = {"MUST"})
    public void ldprmMustNotSupportPatch() {
        final TestInfo info = setupTest("4.2.5",
                                        "An LDPRm must not support PATCH",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-patch",
                                        ps);

        //create an LDPRm
        final String mementoURI = createMemento(uri, info);
        final Response mementoResponse = doGet(mementoURI);
        //verify that a subsequent OPTIONS does not return PATCH
        confirmAbsenceOfHeaderValueInMultiValueHeader("Allow", "PATCH", mementoResponse);
        //verify that issuing a PATCH returns a 405 (method not allowed).
        final Response response = doPatchUnverified(mementoURI);
        assertEquals(response.getStatusCode(), 405, "Response to a PATCH should be 405");

    }

}

