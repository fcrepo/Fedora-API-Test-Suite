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
 * Tests for POST requests on LDP Version Container Resources
 *
 * @author Daniel Bernstein
 */
public class LdpcvHttpPost extends AbstractVersioningTest {

    /**
     * Authentication
     *
     * @param username The repository username
     * @param password The repository password
     */
    @Parameters({"param2", "param3"})
    public LdpcvHttpPost(final String username, final String password) {
        super(username, password);
    }

    /**
     * 4.3.3.1-A
     *
     * @param uri The repostory root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpcvOfLdprsMustSupportPostWithoutMementoDatetimeHeader(final String uri) {
        final TestInfo info = setupTest("4.3.3.1-A", "ldpcvOfLdprsMustSupportPostWithoutMementoDatetimeHeader",
                                        "If an LDPCv of an LDP-RS supports POST, a POST request that does not contain" +
                                        " a " +
                                        "Memento-Datetime header should be understood to create a new LDPRm " +
                                        "contained by the LDPCv, reflecting the state of the LDPRv at the time of " +
                                        "the POST. ",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-post",
                                        ps);

    }

    /**
     * 4.3.3.1-B
     *
     * @param uri The repostory root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpcvOfLdpnrsMustSupportPostWithoutMementoDatetimeHeader(final String uri) {
        final TestInfo info = setupTest("4.3.3.1-B", "ldpcvOfLdpnrsMustSupportPostWithoutMementoDatetimeHeader",
                                        "If an LDPCv of an LDP-NR supports POST, a POST request that does not contain" +
                                        " a " +
                                        "Memento-Datetime header should be understood to create a new LDPRm " +
                                        "contained by the LDPCv, reflecting the state of the LDPRv at the time of " +
                                        "the POST. ",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-post",
                                        ps);

        //same as previous test but with LDP-NR
    }

    /**
     * 4.3.3.1-C
     *
     * @param uri The repostory root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void postToldpcvOfLdprsWithoutMementoDatetimeMustIgnoreBody(final String uri) {
        final TestInfo info = setupTest("4.3.3.1-C", "postToldpcvOfLdprsWithoutMementoDatetimeMustIgnoreBody",
                                        "If an LDPCv of an LDP-RS supports POST, a POST request that does not contain" +
                                        " a " +
                                        "Memento-Datetime header MUST ignore any request body.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-post",
                                        ps);

    }

    /**
     * 4.3.3.1-D
     *
     * @param uri The repostory root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void postToldpcvOfLdpnrWithoutMementoDatetimeMustIgnoreBody(final String uri) {
        final TestInfo info = setupTest("4.3.3.1-D", "postToldpcvOfLdpnrWithoutMementoDatetimeMustIgnoreBody",
                                        "If an LDPCv of an LDP-NR supports POST, a POST request that does not contain" +
                                        " a " +
                                        "Memento-Datetime header MUST ignore any request body.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-post",
                                        ps);
        //same as previous but with LDP-NR
    }

    /**
     * 4.3.3.1-E
     *
     * @param uri The repostory root URI
     */
    // @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void postToldpcvOfLdprWithMementoDatetimeShouldCreateNewResource(final String uri) {
        final TestInfo info = setupTest("4.3.3.1-E", "postToldpcvOfLdprWithMementoDatetimeShouldCreateNewResource",
                                        "If an LDPCv supports POST, a POST with a Memento-Datetime header " +
                                        "should be understood to create a new LDPRm contained by the LDPCv, with the " +
                                        "state given in the request body.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-post",
                                        ps);

    }

    /**
     * 4.3.3.1-F
     *
     * @param uri The repostory root URI
     */
    //@Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void mementoDatetimeHeaderShouldMatchThatUsedWhenMementoCreated(final String uri) {
        final TestInfo info = setupTest("4.3.3.1-F", "mementoDatetimeHeaderShouldMatchThatUsedWhenMementoCreated",
                                        " If an LDPCv supports POST, a POST with a Memento-Datetime header " +
                                        "should be understood to create a new LDPRm contained by the LDPCv, with the " +
                                        "datetime given in the Memento-Datetime request header.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-post",
                                        ps);

    }

    /**
     * 4.3.3.2
     *
     * @param uri The repostory root URI
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpcvDoesNotSupportPost(final String uri) {
        final TestInfo info = setupTest("4.3.3.2", "ldpcvDoesNotSupportPost",
                                        "If an implementation does not support one or both of POST cases " +
                                        "above, it must respond to such requests with a 4xx range status code and a " +
                                        "link to an appropriate constraints document",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-post",
                                        ps);

    }

    /**
     * 4.3.4
     *
     * @param uri The repostory root URI
     */
    //@Test(groups = {"MAY"})
    @Parameters({"param1"})
    public void ldpcvMayDisallowPut(final String uri) {
        final TestInfo info = setupTest("4.3.4", "ldpcvMayDisallowPut",
                                        "Implementations MAY disallow PUT.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-put",
                                        ps);

    }

    /**
     * 4.3.5
     *
     * @param uri The repostory root URI
     */
    //@Test(groups = {"MAY"})
    @Parameters({"param1"})
    public void ldpcvMayDisallowPatch(final String uri) {
        final TestInfo info = setupTest("4.3.5", "ldpcvMayDisallowPatch",
                                        "Implementations MAY disallow PATCH",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-patch",
                                        ps);

    }

}

