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
import static org.testng.AssertJUnit.assertEquals;

import java.net.URI;

import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author awoods
 * @author dbernstein
 * @since 2018-07-16
 */
public class WebACLinkHeaders extends AbstractAuthzTest {

    /**
     * Constructor
     *
     * @param rootControllerUserWebId admin user
     */
    @Parameters({ROOT_CONTROLLER_USER_WEBID_PARAM})
    public WebACLinkHeaders(final String rootControllerUserWebId) {
        super(rootControllerUserWebId);
    }

    /**
     * 5.3-A - Link header points to existing ACL
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void linkToAclExisting(final String uri) {
        final TestInfo info = setupTest("5.3-A",
                                        "A conforming server must advertise the individual resource ACL for every " +
                                        "controlled resource in HTTP responses with a rel=\"acl\" link in the Link " +
                                        "header, whether or not the ACL exists.",
                                        "https://fedora.info/2018/06/25/spec/#link-rel-acl", ps);

        // check for Link header with existing ACL.
        final String resourceUri = createResource(uri, info.getId());
        //verify that the link does not exist already
        doGetUnverified(getAclLocation(resourceUri)).then().statusCode(404);
        //create the acl for the resource
        createAclForResource(resourceUri, "user-read-only.ttl", "http://example.com/anyuser");
        //verify that it now exists.
        doGet(getAclLocation(resourceUri));
    }

    /**
     * 5.3-B - Link header points to yet uncreated ACL
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void linkToAclNonexisting(final String uri) {
        final TestInfo info = setupTest("5.3-B",
                                        "A conforming server must advertise the individual resource ACL for every " +
                                        "controlled resource in HTTP responses with a rel=\"acl\" link in the Link " +
                                        "header, whether or not the ACL exists.",
                                        "https://fedora.info/2018/06/25/spec/#link-rel-acl", ps);

        // check for Link header with existing ACL.
        final String resourceUri = createResource(uri, info.getId());
        final String aclUri = getAclLocation(resourceUri);
        doGetUnverified(aclUri).then().statusCode(404);
    }

    /**
     * 5.3-C - ACL on the same server
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({TEST_CONTAINER_URL_PARAM})
    public void aclOnSameServer(final String uri) {
        final TestInfo info = setupTest("5.3-C",
                                        "The ACL resource should be located in the same server as the controlled " +
                                        "resource.",
                                        "https://fedora.info/2018/06/25/spec/#link-rel-acl", ps);
        final String resourceUri = createResource(uri, info.getId());
        final String aclUri = getAclLocation(resourceUri);
        assertEquals("ACL should be on the same host as the controlled source", URI.create(uri).getHost(),
                     URI.create(aclUri).getHost());
    }

}
