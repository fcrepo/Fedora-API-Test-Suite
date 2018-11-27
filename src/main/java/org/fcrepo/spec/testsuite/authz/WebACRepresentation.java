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
import org.testng.annotations.Test;

/**
 * @author awoods
 * @since 2018-07-15
 */
public class WebACRepresentation extends AbstractAuthzTest {

    /**
     * 5.2-A - Authz type and URI as subject of triples
     */
    @Test(groups = {"MUST"})
    public void aclRepresentation() {
        final TestInfo info = setupTest("5.2-A",
                                        "Implementations must inspect the ACL RDF for authorizations. Authorizations " +
                                        "are identified by type definition triples of the form authorization_N " +
                                        "rdf:type acl:Authorization, where authorization_N is the URI of an " +
                                        "authorization.",
                                        SPEC_BASE_URL + "#acl-representation", ps);

        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-only.ttl", this.permissionlessUserWebId);
        //perform GET as non-admin
        doGet(resourceUri, false);
    }

    /**
     * 5.2-B - Only authz statements used to determine access
     */
    @Test(groups = {"MUST"})
    public void onlyAuthorizationStatementsUsed() {
        final TestInfo info = setupTest("5.2-B",
                                        "Implementations must use only statements associated with an authorization in" +
                                        " the ACL RDF to determine access, except in the case of acl:agentGroup " +
                                        "statements where the group listing document is dereferenced.",
                                        SPEC_BASE_URL + "#acl-representation", ps);

        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "acl-without-authorization-type-triple.ttl", this.permissionlessUserWebId);
        //GET as non-admin should succeed
        doGet(resourceUri);
        //POST as non-admin return 403
        final Response postResponse = doPostUnverified(resourceUri, new Headers(), "test-body", false);
        postResponse.then().statusCode(403);
    }

    /**
     * 5.2-C - Group membership determined by dereferencing resource containing members
     */
    @Test(groups = {"MUST"})
    public void dereferencingGroups() {
        final TestInfo info = setupTest("5.2-C",
                "Implementations must use only statements associated with an authorization in the ACL RDF to " +
                        "determine access, except in the case of acl:agentGroup statements where the group listing " +
                        "document is dereferenced.",
                SPEC_BASE_URL + "#acl-representation", ps);

        //create test container
        final String testContainerUri = createResource(uri, info.getId());

        //create agent-group list
        final String groupListUri = testContainerUri + "/agent-group";
        final Map<String,String> params = new HashMap<>();
        params.put("user", this.permissionlessUserWebId);
        final Response response  = doPutUnverified(groupListUri,
            new Headers(new Header("Content-Type", "text/turtle")),
            filterFileAndConvertToString("agent-group.ttl", params));
        response.then().statusCode(201);

        final String resourceUri = createResource(testContainerUri, "group-test");
        //create a resource
        final Map<String,String> aclParams = new HashMap<>();
        aclParams.put("resource", resourceUri);
        aclParams.put("groupListResource", groupListUri);
        createAclForResource(resourceUri, "group-authorization.ttl", aclParams);
        doGet(resourceUri, false);
    }

    /**
     * 5.2-D - Access granted by examining all authorizations
     */
    @Test(groups = {"MUST"})
    public void aclExamined() {
        final TestInfo info = setupTest("5.2-D",
                                        "The authorizations must be examined to see whether they grant the requested " +
                                        "access to the controlled resource.",
                                        SPEC_BASE_URL + "#acl-representation", ps);
        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-write-in-multiple-authorizations.ttl",
                             this.permissionlessUserWebId);
        //perform GET as non-admin
        doGet(resourceUri, false);
        doPost(resourceUri, new Headers(new Header("Content-Type", "text/plain")), "test body", false).then()
                                                                                                      .statusCode(201);
    }

    /**
     * 5.2-E - Deny access if no authorizations grant access
     */
    @Test(groups = {"MUST"})
    public void accessDenied() {
        final TestInfo info = setupTest("5.2-E",
                "If none of the authorizations grant the requested access then the request must be denied.",
                SPEC_BASE_URL + "#acl-representation", ps);

        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "empty-acl.ttl", this.permissionlessUserWebId);
        //perform GET as non-admin
        final Response getResponse = doGetUnverified(resourceUri, false);
        //verify unauthorized
        getResponse.then().statusCode(403);
    }

}
