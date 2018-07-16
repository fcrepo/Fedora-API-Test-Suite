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
package org.fcrepo.spec.testsuite.authz;

import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author awoods
 * @since 2018-07-16
 */
public class WebACLinkHeaders extends AbstractTest {


    /**
     * Constructor
     *
     * @param username username
     * @param password password
     */
    @Parameters({"param2", "param3"})
    public WebACLinkHeaders(final String username, final String password) {
        super(username, password);
    }

    /**
     * 5.3-A - Link header points to existing ACL
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void linkToAclExisting(final String uri) {
        final TestInfo info = setupTest("5.3-A", "linkToAclExisting",
                "A conforming server must advertise the individual resource ACL for every controlled resource in " +
                        "HTTP responses with a rel=\"acl\" link in the Link header, whether or not the ACL exists.",
                "https://fedora.info/2018/06/25/spec/#link-rel-acl", ps);

        // check for Link header with existing ACL.
    }

    /**
     * 5.3-B - Link header points to yet uncreated ACL
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void linkToAclNonexisting(final String uri) {
        final TestInfo info = setupTest("5.3-B", "linkToAclNonexisting",
                "A conforming server must advertise the individual resource ACL for every controlled resource in " +
                        "HTTP responses with a rel=\"acl\" link in the Link header, whether or not the ACL exists.",
                "https://fedora.info/2018/06/25/spec/#link-rel-acl", ps);

        // check for Link header even if ACL does not exist.
    }

    /**
     * 5.3-C - ACL on the same server
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void aclOnSameServer(final String uri) {
        final TestInfo info = setupTest("5.3-C", "aclOnSameServer",
                "The ACL resource should be located in the same server as the controlled resource.",
                "https://fedora.info/2018/06/25/spec/#link-rel-acl", ps);
    }

}
