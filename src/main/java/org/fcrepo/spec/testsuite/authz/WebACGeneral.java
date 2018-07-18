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
 * @since 2018-07-14
 */
public class WebACGeneral extends AbstractTest {


    /**
     * Constructor
     *
     * @param username username
     * @param password password
     */
    @Parameters({"param2", "param3"})
    public WebACGeneral(final String username, final String password) {
        super(username, password);
    }


    /**
     * 5.0-A - Access to a single agent
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void agentSingle(final String uri) {
        final TestInfo info = setupTest("5.0-A", "agentSingle",
                "An authorization may list any number of individual agents (that are being given access) by using " +
                        "the acl:agent predicate",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

    /**
     * 5.0-B - Different access to different agents
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void agentDouble(final String uri) {
        final TestInfo info = setupTest("5.0-B", "agentDouble",
                "An authorization may list any number of individual agents (that are being given access) by using " +
                        "the acl:agent predicate.",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

    /**
     * 5.0-C - Access to an agent group
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void agentGroup(final String uri) {
        final TestInfo info = setupTest("5.0-C", "agentGroup",
                "To give access to a group of agents, use the acl:agentGroup predicate. The object of an " +
                        "agentGroup statement is a link to a Group Listing document. The group members are " +
                        "listed in it, using the vcard:hasMember predicate.",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

    /**
     * 5.0-D - Public access
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void agentAll(final String uri) {
        final TestInfo info = setupTest("5.0-D", "agentAll",
                "To specify that you're giving a particular mode of access to everyone, you can use acl:agentClass " +
                        "foaf:Agent to denote that you're giving access to the class of all agents " +
                        "(the general public).",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

    /**
     * 5.0-E - Access to a specific resource
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void resourceSingle(final String uri) {
        final TestInfo info = setupTest("5.0-E", "resourceSingle",
                "The acl:accessTo predicate specifies which resources you're giving access to, using their URLs as " +
                        "the subjects.",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

}
