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

import static org.fcrepo.spec.testsuite.App.ROOT_CONTROLLER_USER_WEBID_PARAM;
import static org.fcrepo.spec.testsuite.App.TEST_CONTAINER_URL_PARAM;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author awoods
 * @since 2018-07-16
 */
public class WebACLinking extends AbstractTest {

    /**
     * Constructor
     *
     * @param username username
     */
    @Parameters({ROOT_CONTROLLER_USER_WEBID_PARAM})
    public WebACLinking(final String username) {
        super(username);
    }

    /**
     * 5.4-A - Client-provided Link to preferred ACL on resource creation
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MAY"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void linkToAclOnCreation(final String uri) {
        final TestInfo info = setupTest("5.4-A",
                                        "A client HTTP POST or PUT request to create a new LDPR may include a " +
                                        "rel=\"acl\" link in the " +
                                        "Link header referencing an existing LDP-RS to use as the ACL for the new " +
                                        "LDPR.",
                                        "https://fedora.info/2018/06/25/spec/#link-acl-on-create", ps);

        //create a resource to be used for the acl link
        final Response aclResponse = createBasicContainer(uri, info.getId() + "-acl");
        final String aclUri = getLocation(aclResponse);

        //PUT the new Resource
        final String aclLinkValue = "<" + aclUri + ">; rel=\"acl\"";
        final Response resource = doPut(uri, Headers.headers(new Header("Link", aclLinkValue)), "test body");
        final String resourceUri = getLocation(resource);
        final Response getResource = doGet(resourceUri);
        confirmPresenceOfLinkValue(aclLinkValue, getResource);

    }

    /**
     * 5.4-B - Reject request if Linking to client-provided ACL is not supported
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void conflictIfNotSupportingAclLink(final String uri) {
        final TestInfo info = setupTest("5.4-B",
                                        "The server must reject the request and respond with a 4xx or 5xx range " +
                                        "status code, such as " +
                                        "409 (Conflict) if it isn't able to create the LDPR with the specified LDP-RS" +
                                        " as the ACL. " +
                                        "In that response, the restrictions causing the request to fail must be " +
                                        "described in a " +
                                        "resource indicated by a rel=\"http://www.w3.org/ns/ldp#constrainedBy\" link " +
                                        "in the Link " +
                                        "response header",
                                        "https://fedora.info/2018/06/25/spec/#link-acl-on-create", ps);

        //create a resource to be used for the acl link
        final Response aclResponse = createBasicContainer(uri, info.getId() + "-acl");
        final String aclUri = getLocation(aclResponse);

        //PUT the new Resource
        final String aclLinkValue = "<" + aclUri + ">; rel=\"acl\"";
        final Response resource = doPutUnverified(uri, Headers.headers(new Header("Link", aclLinkValue)), "test body");

        if (resource.getStatusCode() >= 400) {
            confirmPresenceOfConstrainedByLink(resource);
        } else {
            resource.then().statusCode(successRange());
        }

    }

}
