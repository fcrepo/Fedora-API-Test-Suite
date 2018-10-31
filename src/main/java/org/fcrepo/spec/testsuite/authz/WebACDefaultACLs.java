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

import static org.testng.AssertJUnit.assertEquals;

import java.net.URI;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.annotations.Test;

/**
 * @author dbernstein
 * @since 2018-09-17
 */
public class WebACDefaultACLs extends AbstractAuthzTest {

    /**
     * 5.9-A - Inheritance of ACLs in Fedora implementations is defined by the [SOLIDWEBAC]
     * ACL Inheritance Algorithm and must be reckoned along the [LDP] containment
     * relationships linking controlled resources
     */
    @Test(groups = {"MUST"})
    public void aclInheritanceMustUseLdpContainmentRelationships() {
        final TestInfo info =
            setupTest("5.9-A", "Inheritance of ACLs in Fedora implementations is defined by the [SOLIDWEBAC]" +
                               "ACL Inheritance Algorithm and must be reckoned along the [LDP] containment " +
                               "relationships linking controlled resources",
                      "https://fedora.info/2018/06/25/spec/#inheritance", ps);
        final String simpleRDFBody = "@prefix dc: <http://purl.org/dc/terms/> \n" +
                                     "<> dc:title \"Test\".";
        final Headers headers = new Headers(new Header("Content-Type", "text/turtle"));

        //create a resource
        final String grandParentUri = createResource(rootUri, info.getId());
        //create an acl with acl:default with write privileges
        createAclForResource(grandParentUri, "user-read-write.ttl", this.permissionlessUserWebId);
        //create a child  and child acl with read privileges and no acl:default
        final Response childPost = doPost(grandParentUri, headers, simpleRDFBody);
        final String childUri = getLocation(childPost);
        createAclForResource(childUri, "user-read-only-no-default.ttl", this.permissionlessUserWebId);

        //create a grandchild  with no acl.
        final Response grandChildPost = doPost(childUri, headers, simpleRDFBody);
        final String grandChildUri = getLocation(grandChildPost);

        // verify that the user can read and write to the grandparent
        doGet(grandParentUri, false);
        doPost(grandParentUri, headers, simpleRDFBody, false);

        // verify that the user can read but not write to the child
        doPostUnverified(childUri, headers, simpleRDFBody, false).then().statusCode(403);
        doGet(childUri, false);

        //verify that the grandchild can read and write.
        doGet(grandChildUri, false);
        doPost(grandChildUri, headers, simpleRDFBody, false);
    }

    /**
     * 5.9-B - In the case that the controlled resource is uncontained and has no ACL, or that there is no ACL at any
     * point in the containment hierarchy of the controlled resource, then the server must supply a default ACL.
     */
    @Test(groups = {"SHOULD"})
    public void serverSuppliedDefaultAcl() {
        setupTest("5.9-B", "In the case that the controlled resource is uncontained and has no ACL, or " +
                           "that there is no ACL at any point in the containment hierarchy of the " +
                           "controlled resource, then the server must supply a default ACL.",
                  "https://fedora.info/2018/06/25/spec/#inheritance", ps);

        //retrieve the default acl resource link from the root resource
        final String aclUri = getAclLocation(rootUri);
        //GET the default acl and verify success
        doGet(aclUri);
    }

    /**
     * 5.9-C - Default ACL should be on the same server as the controlled resource.
     */
    @Test(groups = {"SHOULD"})
    public void defaultAclOnSameServer() {
        setupTest("5.9-C", "The default ACL resource should be located in the same server (host and port) as the " +
                           "controlled resource.",
                  "https://fedora.info/2018/06/25/spec/#inheritance", ps);
        //GET the default acl and verify that it is on the same server as the root uri.
        final String aclUri = getAclLocation(rootUri);
        assertEquals("The default ACL resource is not located on the same host as the controlled resource.",
                     URI.create(rootUri).getHost(), URI.create(aclUri).getHost());

        assertEquals("The default ACL resource is not located on the same port as the controlled resource.",
                     URI.create(rootUri).getPort(), URI.create(aclUri).getPort());

    }

}
