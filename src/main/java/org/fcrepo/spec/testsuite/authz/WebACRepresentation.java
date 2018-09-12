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
 * @since 2018-07-15
 */
public class WebACRepresentation extends AbstractAuthzTest {

    /**
     * Constructor
     *
     * @param adminUsername admin username
     * @param adminPassword admin password
     * @param username      username
     * @param password      password
     */
    @Parameters({"param2", "param3", "param4", "param5"})
    public WebACRepresentation(final String adminUsername, final String adminPassword, final String username,
                      final String password) {
        super(adminUsername, adminPassword, username, password);
    }
    /**
     * 5.2-A - Authz type and URI as subject of triples
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void aclRepresentation(final String uri) {
        final TestInfo info = setupTest("5.2-A", "aclRepresentation",
                                        "Implementations must inspect the ACL RDF for authorizations. Authorizations " +
                                        "are identified by type definition triples of the form authorization_N " +
                                        "rdf:type acl:Authorization, where authorization_N is the URI of an " +
                                        "authorization.",
                                        "https://fedora.info/2018/06/25/spec/#acl-representation", ps);

        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-only.ttl", this.username);
        //perform GET as non-admin
        doGet(resourceUri, false);
    }

    /**
     * 5.2-B - Only authz statements used to determine access
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void onlyAclStatements(final String uri) {
        final TestInfo info = setupTest("5.2-B", "onlyAclStatements",
                                        "Implementations must use only statements associated with an authorization in" +
                                        " the ACL RDF to determine access, except in the case of acl:agentGroup " +
                                        "statements where the group listing document is dereferenced.",
                                        "https://fedora.info/2018/06/25/spec/#acl-representation", ps);

        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "acl-without-authorization-type-triple.ttl", this.username);
        //perform GET as non-admin
        final Response getResponse = doGetUnverified(resourceUri, false);
        getResponse.then().statusCode(403);
    }

    /**
     * 5.2-C - Group membership determined by dereferencing resource containing members
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void dereferencingGroups(final String uri) {
        final TestInfo info = setupTest("5.2-C", "dereferencingGroups",
                "Implementations must use only statements associated with an authorization in the ACL RDF to " +
                        "determine access, except in the case of acl:agentGroup statements where the group listing " +
                        "document is dereferenced.",
                "https://fedora.info/2018/06/25/spec/#acl-representation", ps);

        //create test container
        final String testContainerUri = createResource(uri, info.getId());

        //create agent-group list
        final String groupListUri = testContainerUri + "/agent-group";
        final Map<String,String> params = new HashMap<>();
        params.put("user", "testuser");
        final Response response  = doPutUnverified(groupListUri,
            new Headers(new Header("Content-Type", "text/turtle")),
            filterFileAndConvertToString("agent-group.ttl", params));
        response.then().statusCode(201);

        final String resourceUri = createResource(testContainerUri, "group-test");
        //create a resource
        final Map<String,String> aclParams = new HashMap<>();
        aclParams.put("resource", resourceUri);
        aclParams.put("groupListResource", groupListUri + "#allowed-users");
        createAclForResource(resourceUri, "group-authorization.ttl", aclParams);
        doGet(resourceUri, false);
    }

    /**
     * 5.2-D - Access granted by examining all authorizations
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void aclExamined(final String uri) {
        final TestInfo info = setupTest("5.2-D", "aclExamined",
                                        "The authorizations must be examined to see whether they grant the requested " +
                                        "access to the controlled resource.",
                                        "https://fedora.info/2018/06/25/spec/#acl-representation", ps);
        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-write-in-multiple-authorizations.ttl", this.username);
        //perform GET as non-admin
        doGet(resourceUri, false);
        doPost(resourceUri, new Headers(new Header("Content-Type", "text/plain")), "test body", false).then()
                                                                                                      .statusCode(201);
    }

    /**
     * 5.2-E - Deny access if no authorizations grant access
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void accessDenied(final String uri) {
        final TestInfo info = setupTest("5.2-E", "accessDenied",
                "If none of the authorizations grant the requested access then the request must be denied.",
                "https://fedora.info/2018/06/25/spec/#acl-representation", ps);

        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "empty-acl.ttl", this.username);
        //perform GET as non-admin
        final Response getResponse = doGetUnverified(resourceUri, false);
        //verify unauthorized
        getResponse.then().statusCode(403);
    }

}
