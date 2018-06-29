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
 * Tests for DELETE requests on LDP Memento Resources
 *
 * @author Daniel Bernstein
 */
public class LdprmHttpDelete extends AbstractVersioningTest {

    /**
     * Authentication
     *
     * @param username The repository username
     * @param password The repository password
     */
    @Parameters({"param2", "param3"})
    public LdprmHttpDelete(final String username, final String password) {
        super(username, password);
    }

    /**
     * 4.2.6
     *
     * @param uri The repository root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldprmMustSupportDeleteIfAdvertised(final String uri) {
        final TestInfo info = setupTest("4.2.6", "ldprmMustSupportDeleteIfAdvertised",
                                        "LDPRm resources must support DELETE if DELETE is advertised in OPTIONS",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-delete",
                                        ps);

        //create an LDPRm
        //check if LDPRm supports DELETE and if so perform DELETE
        //verify that the DELETE is reflected in a 404 on a subsequent GET
        //and that the LDPCv no longer has a reference to it.

    }


}

