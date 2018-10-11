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

import static org.fcrepo.spec.testsuite.App.PERMISSIONLESS_USER_WEBID_PARAM;
import static org.fcrepo.spec.testsuite.App.ROOT_CONTROLLER_USER_WEBID_PARAM;
import static org.fcrepo.spec.testsuite.App.TEST_CONTAINER_URL_PARAM;

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
     * @param rootControllerUserWebId root container controller WebID
     * @param permissionlessUserWebId      permissionless User WebId
     */
    @Parameters({ROOT_CONTROLLER_USER_WEBID_PARAM,PERMISSIONLESS_USER_WEBID_PARAM})
    public WebACGeneral(final String rootControllerUserWebId, final String permissionlessUserWebId) {
        super(rootControllerUserWebId, permissionlessUserWebId);
    }

    /**
     * 5.0-A - Access to a single agent
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void agentSingle(final String uri) {
        final TestInfo info = setupTest("5.0-A",
                                        "An authorization may list any number of individual agents (that are being " +
                                        "given access) by using " +
                                        "the acl:agent predicate",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-only.ttl", this.permissionlessUserWebId);
        doGet(resourceUri, false);
    }

    /**
     * 5.0-B - Different access to different agents
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void agentDouble(final String uri) {
        final TestInfo info = setupTest("5.0-B",
                                        "An authorization may list any number of individual agents (that are being " +
                                        "given access) by using " +
                                        "the acl:agent predicate.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-only-multiple-agents.ttl", this.permissionlessUserWebId);
        doGet(resourceUri, false);
    }

    /**
     * 5.0-C-1 - Access to an agent group
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void agentGroup(final String uri) {
        final TestInfo info = setupTest("5.0-C-1",
                                        "To give access to a group of agents, use the acl:agentGroup predicate. The " +
                                        "object of an " +
                                        "agentGroup statement is a link to a Group Listing document. The group " +
                                        "members are " +
                                        "listed in it, using the vcard:hasMember predicate.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        //create test container
        final String testContainerUri = createResource(uri, info.getId());

        //create agent-group list
        final String groupListUri = joinLocation(testContainerUri, "agent-group");
        final Map<String, String> params = new HashMap<>();
        params.put("user", this.permissionlessUserWebId);
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
     * 5.0-C-2 - Access to an agent group with hash uris
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void agentGroupWithHashUris(final String uri) {
        final TestInfo info = setupTest("5.0-C-2",
                                        "To give access to a group of agents, use the acl:agentGroup predicate. The " +
                                        "object of an agentGroup statement is a link with a hash URI to a Group " +
                                        "Listing document. The group members are listed in it, using the " +
                                        "vcard:hasMember predicate. ",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        //create test container
        final String testContainerUri = createResource(uri, info.getId());

        //create agent-group list
        final String groupListUri = joinLocation(testContainerUri, "agent-group");
        final Map<String, String> params = new HashMap<>();
        params.put("user", this.permissionlessUserWebId);
        final Response response = doPutUnverified(groupListUri,
                                                  new Headers(new Header("Content-Type", "text/turtle")),
                                                  filterFileAndConvertToString("agent-group-using-hash-uris.ttl",
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
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void agentAll(final String uri) {
        final TestInfo info = setupTest("5.0-D",
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
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void allAuthenticatedAgents(final String uri) {
        final TestInfo info = setupTest("5.0-E",
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
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void resourceSingle(final String uri) {
        final TestInfo info = setupTest("5.0-F",
                                        "The acl:accessTo predicate specifies which resources you're giving access " +
                                        "to, using their URLs as " +
                                        "the subjects.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        final String parentResource = createResource(uri, info.getId());
        final String child1resource = createResource(parentResource, "child1");
        final String child2resource = createResource(parentResource, "child2");

        final Map<String, String> params = new HashMap<>();
        params.put("accessTo", child1resource);
        params.put("user", this.permissionlessUserWebId);
        params.put("defaultResource", parentResource);

        createAclForResource(parentResource, "user-read-only-access-to-child.ttl", params);

        doGet(child1resource, false);
        doGetUnverified(child2resource, false).then().statusCode(403);
    }

}
