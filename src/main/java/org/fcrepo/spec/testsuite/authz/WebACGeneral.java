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

import java.util.HashMap;
import java.util.Map;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author awoods
 * @since 2018-07-14
 */
public class WebACGeneral extends AbstractAuthzTest {

    /**
     * Constructor
     *
     * @param adminUsername admin username
     * @param adminPassword admin password
     * @param username      username
     * @param password      password
     */
    @Parameters({"param2", "param3", "param4", "param5"})
    public WebACGeneral(final String adminUsername, final String adminPassword, final String username,
                        final String password) {
        super(adminUsername, adminPassword, username, password);
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
                                        "An authorization may list any number of individual agents (that are being " +
                                        "given access) by using " +
                                        "the acl:agent predicate",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-only.ttl", this.username);
        doGet(resourceUri, false);
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
                                        "An authorization may list any number of individual agents (that are being " +
                                        "given access) by using " +
                                        "the acl:agent predicate.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-only-multiple-agents.ttl", this.username);
        doGet(resourceUri, false);
    }

    /**
     * 5.0-C-A - Access to an agent group
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void agentGroup(final String uri) {
        final TestInfo info = setupTest("5.0-C-A", "agentGroup",
                                        "To give access to a group of agents, use the acl:agentGroup predicate. The " +
                                        "object of an " +
                                        "agentGroup statement is a link to a Group Listing document. The group " +
                                        "members are " +
                                        "listed in it, using the vcard:hasMember predicate.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        //create test container
        final String testContainerUri = createResource(uri, info.getId());

        //create agent-group list
        final String groupListUri = testContainerUri + "/agent-group";
        final Map<String, String> params = new HashMap<>();
        params.put("user", "testuser");
        final Response response = doPutUnverified(groupListUri,
                                                  new Headers(new Header("Content-Type", "text/turtle")),
                                                  filterFileAndConvertToString("agent-group.ttl", params));
        response.then().statusCode(201);

        final String resourceUri = createResource(testContainerUri, "group-test");
        //create a resource
        final Map<String, String> aclParams = new HashMap<>();
        aclParams.put("resource", resourceUri);
        aclParams.put("groupListResource", groupListUri);
        createAclForResource(resourceUri, "group-authorization.ttl", aclParams);
        doGet(resourceUri, false);
    }

    /**
     * 5.0-C-B - Access to an agent group with hash uris
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void agentGroupWithHashUris(final String uri) {
        final TestInfo info = setupTest("5.0-C-B", "agentGroupWithHashUris",
                                        "To give access to a group of agents, use the acl:agentGroup predicate. The " +
                                        "object of an " +
                                        "agentGroup statement is a link to a Group Listing document. The group " +
                                        "members are " +
                                        "listed in it, using the vcard:hasMember predicate.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        //create test container
        final String testContainerUri = createResource(uri, info.getId());

        //create agent-group list
        final String groupListUri = testContainerUri + "/agent-group";
        final Map<String, String> params = new HashMap<>();
        params.put("user", "testuser");
        final Response response = doPutUnverified(groupListUri,
                                                  new Headers(new Header("Content-Type", "text/turtle")),
                                                  filterFileAndConvertToString("agent-group-with-hash-uris.ttl",
                                                                               params));
        response.then().statusCode(201);

        final String resourceUri = createResource(testContainerUri, "group-test");
        //create a resource
        final Map<String, String> aclParams = new HashMap<>();
        aclParams.put("resource", resourceUri);
        aclParams.put("groupListResource", groupListUri + "#group1");
        createAclForResource(resourceUri, "group-authorization.ttl", aclParams);
        doGet(resourceUri, false);
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
                                        "To specify that you're giving a particular mode of access to everyone, you " +
                                        "can use acl:agentClass " +
                                        "foaf:Agent to denote that you're giving access to the class of all agents " +
                                        "(the general public).",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "everyone-read-only.ttl", "");
        doGet(resourceUri, false);
    }

    /**
     * 5.0-E - Authenticated access
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void allAuthenticatedAgents(final String uri) {
        final TestInfo info = setupTest("5.0-E", "allAuthenticatedAgents",
                                        "To specify that you're giving a particular mode of access to all " +
                                        "authenticated users, you can use acl:agentClass acl:AuthenticatedAgent to " +
                                        "denote that you're giving access to the class of all authenticated agents.",
                                        "https://github" +
                                        ".com/solid/web-access-control-spec#authenticated-agents-anyone-logged-on",
                                        ps);

        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "all-authenticated-read-only.ttl", "");
        doGet(resourceUri, false);
    }

    /**
     * 5.0-F - Access to a specific resource
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void resourceSingle(final String uri) {
        final TestInfo info = setupTest("5.0-F", "resourceSingle",
                                        "The acl:accessTo predicate specifies which resources you're giving access " +
                                        "to, using their URLs as " +
                                        "the subjects.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        final String parentResource = createResource(uri, info.getId());
        final String child1resource = createResource(parentResource, "child1");
        final String child2resource = createResource(parentResource, "child2");

        final Map<String, String> params = new HashMap<>();
        params.put("accessTo", child1resource);
        params.put("user", this.username);
        params.put("defaultResource", parentResource);

        createAclForResource(parentResource, "user-read-only-access-to-child.ttl", params);

        doGet(child1resource, false);
        doGetUnverified(child2resource, false).then().statusCode(403);
    }

}
