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
 * Tests for DELETE requests on LDP Version Container Resources
 *
 * @author Daniel Bernstein
 */
public class LdpcvHttpDelete extends AbstractVersioningTest {

    /**
     * Authentication
     *
     * @param username The repository username
     * @param password The repository password
     */
    @Parameters({"param2", "param3"})
    public LdpcvHttpDelete(final String username, final String password) {
        super(username, password);
    }

    /**
     * 4.3.6
     *
     * @param uri The repostory root URI
     */
    //@Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void ldpcvThatAdvertisesDeleteShouldRemoveContainerAndMementos(final String uri) {
        final TestInfo info = setupTest("4.3.6", "ldpcvThatAdvertisesDeleteShouldRemoveContainerAndMementos",
                                        "An implementation that does support DELETE should do so by both " +
                                        "removing the LDPCv and removing the versioning interaction model from the " +
                                        "original LDPRv.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-delete",
                                        ps);

    }

}