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

import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.annotations.Parameters;

/**
 * Tests for OPTIONS requests on LDP Memento Resources
 *
 * @author Daniel Bernstein
 */
public class LdprmHttpOptions extends AbstractVersioningTest {

    /**
     * Authentication
     *
     * @param username The repository username
     * @param password The repository password
     */
    @Parameters({"param2", "param3"})
    public LdprmHttpOptions(final String username, final String password) {
        super(username, password);
    }

    /**
     * 4.2.2-A
     *
     * @param uri The repository root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldprmMustSupportOptions(final String uri) {
        final TestInfo info = setupTest("4.2.2-A", "ldprmMustSupportOptions",
                                        "LDPRm resources must support OPTIONS",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-options",
                                        ps);

        //create an LDPRm
        //verify that a subsequent GET returns OPTIONS in "Allow" header.

    }

    /**
     * 4.2.2-B
     *
     * @param uri The repository root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldprmOptionsMustSupportGetHeadAndOptions(final String uri) {
        final TestInfo info = setupTest("4.2.2-B", "ldprmOptionsMustSupportGetHeadAndOptions",
                                        "A response to an OPTIONS request must include Allow: GET, HEAD, OPTIONS",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-options",
                                        ps);

        //create an LDPRm
        //verify that a subsequent OPTIONS return "GET", "HEAD", and "OPTIONS" in "Allow" header.

    }

    /**
     * 4.2.2-C
     *
     * @param uri The repository root URI
     */
    //@Test(groups = {"MAY"})
    @Parameters({"param1"})
    public void ldprmOptionsMaySupportDelete(final String uri) {
        final TestInfo info = setupTest("4.2.2-C", "ldprmOptionsMaySupportDelete",
                                        "A response to an OPTIONS request may include Allow: DELETE",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-options",
                                        ps);

        //create an LDPRm
        //verify that a subsequent OPTIONS return "DELETE"
    }

    /**
     * 4.2.3
     *
     * @param uri The repository root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldprmMustNotSupportPost(final String uri) {
        final TestInfo info = setupTest("4.2.3", "ldprmMustNotSupportPost",
                                        "An LDPRm must not support POST",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-post",
                                        ps);

        //create an LDPRm
        //verify that a subsequent OPTIONS does not return POST
        //verify that issuing a POST returns a 405 (method not allowed).
    }

    /**
     * 4.2.4
     *
     * @param uri The repository root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldprmMustNotSupportPut(final String uri) {
        final TestInfo info = setupTest("4.2.4", "ldprmMustNotSupportPut",
                                        "An LDPRm must not support PUT",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-put",
                                        ps);

        //create an LDPRm
        //verify that a subsequent OPTIONS does not return PUT
        //verify that issuing a PUT returns a 405 (method not allowed).
    }

    /**
     * 4.2.5
     *
     * @param uri The repository root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldprmMustNotSupportPatch(final String uri) {
        final TestInfo info = setupTest("4.2.5", "ldprmMustNotSupportPatch",
                                        "An LDPRm must not support PATCH",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-patch",
                                        ps);

        //create an LDPRm
        //verify that a subsequent OPTIONS does not return PATCH
        //verify that issuing a PATCH returns a 405 (method not allowed).
    }

}

