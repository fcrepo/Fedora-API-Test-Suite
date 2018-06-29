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
 * @author Daniel Bernstein
 */
public class LdprvHttpPut extends AbstractVersioningTest {

    /**
     * Authentication
     *
     * @param username The repository username
     * @param password The repository password
     */
    @Parameters({"param2", "param3"})
    public LdprvHttpPut(final String username, final String password) {
        super(username, password);
    }

    /**
     * 4.1.2-A
     *
     * @param uri The repository root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldprvMustSupportPUT(final String uri) {
        final TestInfo info = setupTest("4.1.2-A", "ldprvMustSupportPUT",
                                        "Must support PUT for creating new LDPRv",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprv-put",
                                        ps);

        //create an LDPRv using a put
        //verify that it is a TimeGate and has a TimeMap

    }

    /**
     * 4.1.2-B
     *
     * @param uri The repository root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldprvMustSupportPUTForExistingResources(final String uri) {
        final TestInfo info = setupTest("4.1.2-B", "ldprvMustSupportPUTForExistingResources",
                                        "Must support PUT for updating existing LDPRvs",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprv-put",
                                        ps);
        //create an LDPRv
        //verify that "Allow: PUT" header is present
        //update an existing LDPRv using a put
        //verify that it is a TimeGate and has a TimeMap

    }

    /**
     * 4.1.2-C
     *
     * @param uri The repository root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpnrvMustSupportPUT(final String uri) {
        final TestInfo info = setupTest("4.1.2-C", "ldpnrvMustSupportPUT",
                                        "Must support PUT for creating new LDPNRv",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprv-put",
                                        ps);

        //create an LDPNRv using a put
        //verify that it is a TimeGate and has a TimeMap

    }

    /**
     * 4.1.2-D
     *
     * @param uri The repository root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpnrvMustSupportPUTForExistingResources(final String uri) {
        final TestInfo info = setupTest("4.1.2-D", "ldpnrvMustSupportPUTForExistingResources",
                                        "Must support PUT for updating existing  LDPNRvs",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprv-put",
                                        ps);
        //create an LDPNRv
        //verify that "Allow: PUT" header is present
        //update an existing LDPNRv using a put
        //verify that it is a TimeGate and has a TimeMap

    }
  }
