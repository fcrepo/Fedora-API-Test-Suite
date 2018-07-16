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
public class WebACCrossDomain extends AbstractTest {


    /**
     * Constructor
     *
     * @param username username
     * @param password password
     */
    @Parameters({"param2", "param3"})
    public WebACCrossDomain(final String username, final String password) {
        super(username, password);
    }

    /**
     * 5.5-A - Restrict ACLs to local resources
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MAY"})
    @Parameters({"param1"})
    public void restrictAclToLocal(final String uri) {
        final TestInfo info = setupTest("5.5-A", "restrictAclToLocal",
                "Implementations may restrict support for ACLs to local resources.",
                "https://fedora.info/2018/06/25/spec/#cross-domain-acls", ps);
    }

    /**
     * 5.5-B - Rejected requests to have 4xx range status code
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void rejectRemoteAclStatus(final String uri) {
        final TestInfo info = setupTest("5.5-B", "rejectRemoteAclStatus",
                "If an implementation chooses to reject requests concerning remote ACLs, it must respond with a " +
                        "4xx range status code.",
                "https://fedora.info/2018/06/25/spec/#cross-domain-acls", ps);
    }

    /**
     * 5.5-C - Rejected requests to have constrainedBy Link header
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void rejectRemoteAclConstraint(final String uri) {
        final TestInfo info = setupTest("5.5-C", "rejectRemoteAclConstraint",
                "If an implementation chooses to reject requests concerning remote ACLs, it must advertise the " +
                        "restriction with a rel=\"http://www.w3.org/ns/ldp#constrainedBy\" link in the Link " +
                        "response header.",
                "https://fedora.info/2018/06/25/spec/#cross-domain-acls", ps);
    }

    /**
     * 5.6-A - Restrict ACL group to local resources
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MAY"})
    @Parameters({"param1"})
    public void restrictGroupToLocal(final String uri) {
        final TestInfo info = setupTest("5.6-A", "restrictGroupToLocal",
                "Implementations may restrict support for groups of agents to local Group Listing documents.",
                "https://fedora.info/2018/06/25/spec/#cross-domain-groups", ps);
    }

    /**
     * 5.6-B - Rejected requests to have 4xx range status code
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void rejectRemoteGroupStatus(final String uri) {
        final TestInfo info = setupTest("5.6-B", "rejectRemoteGroupStatus",
                "If an implementation chooses to reject requests concerning remote Group Listings, it must respond " +
                        "with a 4xx range status code.",
                "https://fedora.info/2018/06/25/spec/#cross-domain-groups", ps);
    }

    /**
     * 5.6-C - Rejected requests to have constrainedBy Link header
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void rejectRemoteGroupConstraint(final String uri) {
        final TestInfo info = setupTest("5.6-C", "rejectRemoteGroupConstraint",
                "If an implementation chooses to reject requests concerning remote Group Listings, it must advertise " +
                        "the restriction with a rel=\"http://www.w3.org/ns/ldp#constrainedBy\" link in the Link " +
                        "response header.",
                "https://fedora.info/2018/06/25/spec/#cross-domain-groups", ps);
    }

}
