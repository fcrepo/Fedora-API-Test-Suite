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

import static org.testng.AssertJUnit.fail;

import java.util.HashMap;
import java.util.Map;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * @author awoods
 * @since 2018-07-16
 */
public class WebACCrossDomain extends AbstractAuthzTest {

    /**
     * 5.5-A - Restrict ACLs to local resources
     */
    @Test(groups = {"MAY"})
    public void restrictAclToLocal() {
        final TestInfo info = setupTest("5.5-A",
                                        "Implementations may restrict support for ACLs to local resources.",
                                        SPEC_BASE_URL + "#cross-domain-acls", ps);

        final Response resource = createResourceWithRemoteAcl();
        if (successRange().matches(resource.getStatusCode())) {
            fail("This implementation does not restrict ACLs to local resources.");
        } else {
            resource.then().statusCode(clientErrorRange());
        }

    }

    private Response createResourceWithRemoteAcl() {
        if (!isClientAclLinkingSupported()) {
            throw new SkipException("Client specified ACL locations not supported.");

        }
        //create a resource to be used for the acl link
        final String aclUri = "http://example.org/my-acl";
        //PUT the new Resource
        final String aclLinkValue = "<" + aclUri + ">; rel=\"acl\"";
        return doPutUnverified(uri, Headers.headers(new Header("Link", aclLinkValue)), "test body");
    }

    /**
     * 5.5-B - Rejected requests to have 4xx range status code
     */
    @Test(groups = {"MUST"})
    public void rejectRemoteAclStatus() {
        final TestInfo info = setupTest("5.5-B",
                                        "If an implementation chooses to reject requests concerning remote ACLs, it " +
                                        "must respond with a " +
                                        "4xx range status code.",
                                        SPEC_BASE_URL + "#cross-domain-acls", ps);

        final Response resource = createResourceWithRemoteAcl();

        if (!successRange().matches(resource.getStatusCode())) {
            resource.then().statusCode(clientErrorRange());
        }
    }

    /**
     * 5.5-C - Rejected requests to have constrainedBy Link header
     */
    @Test(groups = {"MUST"})
    public void rejectRemoteAclConstraint() {
        final TestInfo info = setupTest("5.5-C",
                                        "If an implementation chooses to reject requests concerning remote ACLs, it " +
                                        "must advertise the " +
                                        "restriction with a rel=\"http://www.w3.org/ns/ldp#constrainedBy\" link in " +
                                        "the Link " +
                                        "response header.",
                                        SPEC_BASE_URL + "#cross-domain-acls", ps);
        final Response resource = createResourceWithRemoteAcl();

        if (!successRange().matches(resource.getStatusCode())) {
            confirmPresenceOfConstrainedByLink(resource);
        } else {
            resource.then().statusCode(successRange());
        }

    }

    /**
     * 5.6-A - Restrict ACL group to local resources
     */
    @Test(groups = {"MAY"})
    public void restrictGroupToLocal() {
        final TestInfo info = setupTest("5.6-A",
                                        "Implementations may restrict support for groups of agents to local Group " +
                                        "Listing documents.",
                                        SPEC_BASE_URL + "#cross-domain-groups", ps);

        final Response response = createResourceWithRemoteGroupListInAcl(info);

        if (successRange().matches(response.getStatusCode())) {
            throw new SkipException("This implementation does not restrict group lists to local resources.");
        } else {
            response.then().statusCode(clientErrorRange());
        }
    }

    private Response createResourceWithRemoteGroupListInAcl(final TestInfo info) {
        final String resourceUri = createResource(uri, info.getId());
        final Map<String, String> params = new HashMap<>();
        final String groupUri = "http://example.org/group-list";
        params.put("groupListResource", groupUri);
        params.put("resource", resourceUri);

        //get acl handle
        final String aclUri = getAclLocation(resourceUri);
        //create read acl for user role
        return doPutUnverified(aclUri, new Headers(new Header("Content-Type", "text/turtle")),
                               filterFileAndConvertToString("group-authorization.ttl", params));
    }

    /**
     * 5.6-B - Rejected requests to have 4xx range status code
     */
    @Test(groups = {"MUST"})
    public void rejectRemoteGroupStatus() {
        final TestInfo info = setupTest("5.6-B",
                                        "If an implementation chooses to reject requests concerning remote Group " +
                                        "Listings, it must respond " +
                                        "with a 4xx range status code.",
                                        SPEC_BASE_URL + "#cross-domain-groups", ps);

        final Response aclResponse = createResourceWithRemoteGroupListInAcl(info);

        if (!successRange().matches(aclResponse.getStatusCode())) {
            aclResponse.then().statusCode(clientErrorRange());
        } else {
            throw new SkipException("Implementation does not reject requests concerning remote Group Listings.");
        }
    }

    /**
     * 5.6-C - Rejected requests to have constrainedBy Link header
     */
    @Test(groups = {"MUST"})
    public void rejectRemoteGroupConstraint() {
        final TestInfo info = setupTest("5.6-C",
                                        "If an implementation chooses to reject requests concerning remote Group " +
                                        "Listings, it must advertise " +
                                        "the restriction with a rel=\"http://www.w3.org/ns/ldp#constrainedBy\" link " +
                                        "in the Link " +
                                        "response header.",
                                        SPEC_BASE_URL + "#cross-domain-groups", ps);

        final Response aclResponse = createResourceWithRemoteGroupListInAcl(info);
        if (!successRange().matches(aclResponse.getStatusCode())) {
            aclResponse.then().statusCode(clientErrorRange());
            confirmPresenceOfConstrainedByLink(aclResponse);
        } else {
            throw new SkipException("Implementation does not reject requests concerning remote Group Listings.");
        }
    }

}
