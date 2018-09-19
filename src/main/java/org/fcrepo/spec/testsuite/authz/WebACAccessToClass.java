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

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author dbernstein
 * @since 2018-09-19
 */
public class WebACAccessToClass extends AbstractAuthzTest {

    /**
     * Constructor
     *
     * @param adminUsername admin username
     * @param adminPassword admin password
     * @param username      username
     * @param password      password
     */
    @Parameters({"param2", "param3", "param4", "param5"})
    public WebACAccessToClass(final String adminUsername, final String adminPassword, final String username,
                              final String password) {
        super(adminUsername, adminPassword, username, password);
    }

    /**
     * 5.9-A - accessType MUST
     *
     * @param uri of base test container
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void accessTypeMustGiveAccess(final String uri) {
        final TestInfo info = setupTest("5.8-A",
                                        "When an ACL includes an acl:accessToClass statement, it MUST give access to " +
                                        "all " +
                                        "resources with the specified type, whether that type is client-managed or " +
                                        "server-managed",
                                        "https://fedora.info/2018/06/25/spec/#access-to-class", ps);

        //create an resource
        final String testContainerUri = createResource(uri, info.getId());
        //create a read acl with acl:accessToClass specified
        createAclForResource(testContainerUri, "user-read-only-access-to-class.ttl", this.username);
        //create a child resource
        final Response response =
            doPost(testContainerUri, new Headers(new Header("Content-Type", "text/plain")), "test body");
        final String resourceUri = getLocation(response);
        //verify user does not have access.
        doGetUnverified(resourceUri, false).then().statusCode(403);
        //add the accessToClass triple to the description:
        final String descriptionUri = getLdpNrDescription(resourceUri);
        final String sparql = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                              "PREFIX foaf: <http://xmlns.com/foaf/0.1/>  \n" +
                              "INSERT { <> rdf:type foaf:Document } WHERE {}";
        doPatch(descriptionUri, new Headers(new Header("Content-Type", "application/sparql-update")), sparql);
        //verify user does have access
        doGet(resourceUri, false);
    }

    /**
     * 5.9-B - accessType MAY use inference.
     *
     * @param uri of base test container
     */
    @Test(groups = {"MAY"})
    @Parameters({"param1"})
    public void accessTypeMayUseInference(final String uri) {
        final TestInfo info = setupTest("5.8-B",
                                        " Implementations may use inference to infer types not present in a " +
                                        "resource's triples or rel=\"type\" links in the Link header",
                                        "https://fedora.info/2018/06/25/spec/#access-to-class", ps);

        //create an resource
        final String testContainerUri = createResource(uri, info.getId());
        //create a read acl with acl:accessToClass specified
        createAclForResource(testContainerUri, "user-read-only-access-to-class.ttl", this.username);
        //create a child resource
        final Response response =
            doPost(testContainerUri, new Headers(new Header("Content-Type", "text/plain")), "image-bytes");
        final String resourceUri = getLocation(response);
        //verify user does not have access.
        doGetUnverified(resourceUri, false).then().statusCode(403);
        //add the accessToClass triple to the description:
        final String descriptionUri = getLdpNrDescription(resourceUri);
        final String sparql = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                              "PREFIX foaf: <http://xmlns.com/foaf/0.1/>  \n" +
                              "INSERT { <> rdf:type foaf:Image } WHERE {}";
        doPatch(descriptionUri, new Headers(new Header("Content-Type", "application/sparql-update")), sparql);
        //verify user does have access
        doGet(resourceUri, false);
    }
}
