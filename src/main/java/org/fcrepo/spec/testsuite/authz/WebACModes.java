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

import static org.fcrepo.spec.testsuite.Constants.APPLICATION_SPARQL_UPDATE;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;

import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author awoods
 * @author dbernstein
 * @since 2018-07-14
 */
public class WebACModes extends AbstractAuthzTest {

    /**
     * Constructor
     *
     * @param adminUsername admin username
     * @param adminPassword admin password
     * @param username      username
     * @param password      password
     */
    @Parameters({"param2", "param3", "param4", "param5"})
    public WebACModes(final String adminUsername, final String adminPassword, final String username,
                      final String password) {
        super(adminUsername, adminPassword, username, password);
    }

    /**
     * 5.0-F -Read access on HEAD
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void readAllowedHEAD(final String uri) {
        final TestInfo info = setupTest("5.0-F",
                                        "acl:Read gives access to a class of operations that can be described as " +
                                        "\"Read Access\". " +
                                        "In a typical REST API, this includes access to HTTP verbs HEAD.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-only.ttl", this.username);
        //perform verified head as non-admin
        doHead(resourceUri, false);
    }

    /**
     * 5.0-G -Read access on GET
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void readAllowedGET(final String uri) {
        final TestInfo info = setupTest("5.0-G",
                                        "acl:Read gives access to a class of operations that can be described as " +
                                        "\"Read Access\". " +
                                        "In a typical REST API, this includes access to HTTP verbs GET.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-only.ttl", this.username);
        //perform GET as non-admin
        doGet(resourceUri, false);
    }

    /**
     * 5.0-H -Read access disallowed
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void readDisallowed(final String uri) {
        final TestInfo info = setupTest("5.0-H",
                                        "acl:Read gives access to a class of operations that can be described as " +
                                        "\"Read Access\". " +
                                        "In a typical REST API, this includes access to HTTP verbs GET. Its absence " +
                                        "must prevent reads",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-only.ttl", "not-" + this.username);
        //perform GET as non-admin
        final Response getResponse = doGetUnverified(resourceUri, false);
        //verify unauthorized
        getResponse.then().statusCode(403);
    }

    /**
     * 5.0-I - Write access PUT
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void writeAllowedPUT(final String uri) {
        final TestInfo info = setupTest("5.0-I",
                                        "acl:Write gives access to a class of operations that can modify the resource" +
                                        ". In a REST API " +
                                        "context, this would include PUT.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-write.ttl", this.username);
        //perform PUT  to child resource as non-admin
        final Response putResponse =
            doPutUnverified(resourceUri + "/child1", new Headers(new Header("Content-Type", "text/plain")),
                            "test", false);
        //verify successful
        putResponse.then().statusCode(201);

    }

    /**
     * 5.0-J - Write access POST
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void writeAllowedPOST(final String uri) {
        final TestInfo info = setupTest("5.0-J",
                                        "acl:Write gives access to a class of operations that can modify the resource" +
                                        ". In a REST API " +
                                        "context, this would include POST.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-write.ttl", this.username);
        //perform POST  to child resource as non-admin
        doPost(resourceUri, new Headers(new Header("Content-Type", "text/plain")),
               "test", false);
    }

    /**
     * 5.0-K - Write access DELETE
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void writeAllowedDELETE(final String uri) {
        final TestInfo info = setupTest("5.0-K",
                                        "acl:Write gives access to a class of operations that can modify the resource" +
                                        ". In a REST API " +
                                        "context, this would include DELETE",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-write.ttl", this.username);
        //perform DELETE  to child resource as non-admin
        doDelete(resourceUri, false);
    }

    /**
     * 5.0-L - Write access PATCH
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void writeAllowedPATCH(final String uri) {
        final TestInfo info = setupTest("5.0-L",
                                        "acl:Write gives access to a class of operations that can modify the resource" +
                                        ". In a REST API " +
                                        "context, this would include PATCH.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        final String body = "PREFIX dcterms: <http://purl.org/dc/terms/>"
                            + " INSERT {"
                            + " <> dcterms:description \"Patch Updated Description\" ."
                            + "}"
                            + " WHERE { }";

        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-write.ttl", this.username);
        //perform PATCH  to child resource as non-admin
        doPatch(resourceUri, new Headers(new Header("Content-Type", APPLICATION_SPARQL_UPDATE)), body, false);

    }

    /**
     * 5.0-M-1 - Write access disallowed on PUT
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void writeDisallowedPut(final String uri) {
        final TestInfo info = setupTest("5.0-M-1",
                                        "acl:Write gives access to PUT a resource. When not present, " +
                                        "writes should be disallowed.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-only.ttl", this.username);
        //perform PUT  to child resource as non-admin with read only prives
        final Response putResponse =
            doPutUnverified(resourceUri + "/child1", new Headers(new Header("Content-Type", "text/plain")),
                            "test", false);
        //verify unauthorized
        putResponse.then().statusCode(403);
    }

    /**
     * 5.0-M - Write access disallowed POST
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void writeDisallowedPost(final String uri) {
        final TestInfo info = setupTest("5.0-M-2",
                                        "acl:Write gives access to POST a resource. When not present, " +
                                        "writes should be disallowed.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-only.ttl", this.username);
        //perform POST  to child resource as non-admin
        final Response postResponse = doPostUnverified(resourceUri, new Headers(),
                                                       "test", false);
        postResponse.then().statusCode(403);
    }

    /**
     * 5.0-M - Write access disallowed on DELETE
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void writeDisallowedDelete(final String uri) {
        final TestInfo info = setupTest("5.0-M-3",
                                        "acl:Write gives access to DELETE a resource. When not present, " +
                                        "writes should be disallowed.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-only.ttl", this.username);
        //perform DELETE  to child resource as non-admin
        final Response deleteResponse = doDeleteUnverified(resourceUri, false);
        deleteResponse.then().statusCode(403);
    }

    /**
     * 5.0-M-4 - Write access disallowed on PATCH
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void writeDisallowedPatch(final String uri) {
        final TestInfo info = setupTest("5.0-M-4",
                                        "acl:Write gives access to PATCH a resource. When not present, " +
                                        "writes should be disallowed.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        final String body = "PREFIX dcterms: <http://purl.org/dc/terms/>"
                            + " INSERT {"
                            + " <> dcterms:description \"Patch Updated Description\" ."
                            + "}"
                            + " WHERE { }";

        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-only.ttl", this.username);
        //perform PATCH  to child resource as non-admin
        final Response patchResponse =
            doPatchUnverified(resourceUri, new Headers(new Header("Content-Type", APPLICATION_SPARQL_UPDATE)), body,
                              false);
        patchResponse.then().statusCode(403);
    }

    /**
     * 5.0-N - Append access on POST
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void appendAllowedPOST(final String uri) {
        final TestInfo info = setupTest("5.0-N",
                                        "acl:Append gives a more limited ability to write to a resource -- " +
                                        "Append-Only. " +
                                        "This generally includes the HTTP verb POST.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-append.ttl", this.username);
        //perform POST  to child resource as non-admin
        doPost(resourceUri, new Headers(),
               "test", false);

    }

    /**
     * 5.0-O - Append access on PATCH
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void appendAllowedPATCH(final String uri) {
        final TestInfo info = setupTest("5.0-O",
                                        "acl:Append gives a more limited ability to write to a resource -- " +
                                        "Append-Only. " +
                                        "This generally includes the INSERT-only portion of SPARQL-based PATCHes.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        final String body = "PREFIX dcterms: <http://purl.org/dc/terms/>"
                            + " INSERT {"
                            + " <> dcterms:description \"Patch Updated Description\" ."
                            + "}"
                            + " WHERE { }";

        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-append.ttl", this.username);
        //perform PATCH  to child resource as non-admin
        doPatch(resourceUri, new Headers(new Header("Content-Type", APPLICATION_SPARQL_UPDATE)), body, false);
    }

    /**
     * 5.0-P - Append access disallowed
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void appendDisallowed(final String uri) {
        final TestInfo info = setupTest("5.0-P",
                                        "acl:Append gives a more limited ability to write to a resource -- " +
                                        "Append-Only. " +
                                        "This generally includes the HTTP verb POST, although some implementations " +
                                        "may also extend " +
                                        "this mode to cover non-overwriting PUTs, as well as the INSERT-only portion " +
                                        "of SPARQL-based PATCHes. Its absence must prevent append updates.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        final String body = "PREFIX acl: <http://www.w3.org/ns/auth/acl#>"
                            + " DELETE {"
                            + " <#restricted> acl:mode acl:Read ."
                            + "}"
                            + " WHERE { }";

        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-append.ttl", this.username);
        //perform PATCH  to child resource as non-admin
        final Response patchResponse =
            doPatchUnverified(resourceUri, new Headers(new Header("Content-Type", APPLICATION_SPARQL_UPDATE)), body,
                              false);
        //verify unauthorized
        patchResponse.then().statusCode(403);
    }

    /**
     * 5.0-Q - Control access on GET
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void controlAllowedGET(final String uri) {
        final TestInfo info = setupTest("5.0-Q",
                                        "acl:Control is a special-case access mode that gives an agent the ability to" +
                                        " view the ACL of a " +
                                        "resource.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        final String resourceUri = createResource(uri, info.getId());
        final String aclUri = createAclForResource(resourceUri, "user-control.ttl", this.username);
        //perform verified get as non-admin
        doGet(aclUri, false);

    }

    /**
     * 5.0-R - Control access on PATCH
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void controlAllowedPATCH(final String uri) {
        final TestInfo info = setupTest("5.0-R",
                                        "acl:Control is a special-case access mode that gives an agent the ability to" +
                                        " modify the ACL of a " +
                                        "resource.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        final String body = "PREFIX acl: <http://www.w3.org/ns/auth/acl#>"
                            + " INSERT {"
                            + " <#restricted> acl:mode acl:Read ."
                            + "}"
                            + " WHERE { }";

        final String resourceUri = createResource(uri, info.getId());
        final String aclUri = createAclForResource(resourceUri, "user-control.ttl", this.username);
        //perform PATCH  to resource's acl as non-admin
        doPatch(aclUri, new Headers(new Header("Content-Type", APPLICATION_SPARQL_UPDATE)), body, false);
    }

    /**
     * 5.0-S - Control access on PUT
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void controlAllowedPUT(final String uri) {
        final TestInfo info = setupTest("5.0-S",
                                        "acl:Control is a special-case access mode that gives an agent the ability to" +
                                        " modify the ACL of a " +
                                        "resource.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-control.ttl", this.username);
        //perform PUT  to child resource as non-admin
        final Response putResponse =
            doPutUnverified(resourceUri + "/child1", new Headers(new Header("Content-Type", "text/plain")),
                            "test", false);
        //verify successful
        putResponse.then().statusCode(201);

        //get acl location of child
        final String childResourceUri = getLocation(putResponse);
        final String childAclUri = getAclLocation(childResourceUri);

        doPut(childAclUri, new Headers(new Header("Content-Type", "text/turtle")),
              getAclAsString("user-read-only.ttl", childResourceUri, this.username));
    }

    /**
     * 5.0-T - Control access disallowed on GET
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void controlDisallowedGET(final String uri) {
        final TestInfo info = setupTest("5.0-T",
                                        "acl:Control is a special-case access mode that gives an agent the ability to" +
                                        " view and modify the " +
                                        "ACL of a resource. Its absence must prevent viewing the ACL.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        final String resourceUri = createResource(uri, info.getId());
        final String aclUri = createAclForResource(resourceUri, "user-read-only.ttl", this.username);
        //perform a GET on the acl using the non-admin user
        final Response getResponse = doGetUnverified(aclUri, false);
        getResponse.then().statusCode(403);
    }

    /**
     * 5.0-U - Control access disallowed on PATCH
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void controlDisallowedPATCH(final String uri) {
        final TestInfo info = setupTest("5.0-U",
                                        "acl:Control is a special-case access mode that gives an agent the ability to" +
                                        " view and modify the " +
                                        "ACL of a resource. Its absence must prevent updating the ACL.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        final String body = "PREFIX acl: <http://www.w3.org/ns/auth/acl#>"
                            + " INSERT {"
                            + " <#restricted> acl:mode acl:Read ."
                            + "}"
                            + " WHERE { }";
        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        final String aclUri = createAclForResource(resourceUri, "user-read-only.ttl", this.username);
        //perform PATCH  to resource's acl as non-admin
        final Response patchResponse =
            doPatchUnverified(aclUri, new Headers(new Header("Content-Type", APPLICATION_SPARQL_UPDATE)), body, false);
        //verify unauthorized
        patchResponse.then().statusCode(403);
    }

    /**
     * 5.0-V - Control access disallowed on PUT
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void controlDisallowedPUT(final String uri) {
        final TestInfo info = setupTest("5.0-U",
                                        "acl:Control is a special-case access mode that gives an agent the ability to" +
                                        " view and modify the " +
                                        "ACL of a resource. Its absence must prevent updating the ACL.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        final String resourceUri = createResource(uri, info.getId());
        final String aclUri = createAclForResource(resourceUri, "user-read-write.ttl", this.username);
        //perform PUT  to child resource as non-admin
        final Response putResponse =
            doPutUnverified(resourceUri + "/child1", new Headers(new Header("Content-Type", "text/plain")),
                            "test", false);
        //verify successful
        putResponse.then().statusCode(201);

        //create child resource acl
        final String childResourceUri = getLocation(putResponse);
        final String childAclUri = getAclLocation(childResourceUri);
        final Response childAclResponse =
            doPutUnverified(childAclUri, new Headers(new Header("Content-Type", "text/turtle")),
                            getAclAsString("user-read-write.ttl", childResourceUri, this.username));

        childAclResponse.then().statusCode(403);
    }

    /**
     * 5.7.1-A acl:Append for LDP-RS MUST test conditions
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = { "MUST" })
    @Parameters({ "param1" })
    public void appendNotWriteLdpRsMust(final String uri) {
        final TestInfo info = setupTest("5.7.1-A",
                "When a client has acl:Append but not acl:Write for an LDP-RS they MUST " +
                        "not DELETE, not PATCH that deletes triples, not PUT on the resource",
                "https://fedora.info/2018/06/25/spec/#append-ldprs", ps);

        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-append.ttl", this.username);

        // perform DELETE to resource as non-admin
        final Response deleteResponse = doDeleteUnverified(resourceUri, false);
        // verify failure.
        deleteResponse.then().statusCode(403);

        // perform PUT to resource as non-admin
        final Response putResponse =
                doPutUnverified(resourceUri, new Headers(new Header("Content-Type", "text/plain")),
                        "test", false);
        // verify failure.
        putResponse.then().statusCode(403);

        // perform PATCH which also deletes.
        final Response patchDelete = doPatchUnverified(resourceUri, new Headers(new Header("Content-type",
                "application/sparql-update")),
                "prefix dc: <http://purl.org/dc/elements/1.1/> DELETE { <> dc:title ?o1 .}" +
                        " INSERT { <> dc:title \"I made a change\" .} WHERE { <> dc:title ?o1 .}",
                false);
        // Verify failure.
        patchDelete.then().statusCode(403);

        final Response patchDeleteData = doPatchUnverified(resourceUri, new Headers(new Header("Content-type",
                "application/sparql-update")),
                "prefix dc: <http://purl.org/dc/elements/1.1/> DELETE DATA { <> dc:title \"Some title\" .}",
                false);
        // Verify failure.
        patchDeleteData.then().statusCode(403);
    }

    /**
     * 5.7.1-B acl:Append for LDP-RS MUST if PUT to create test conditions
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = { "MUST" })
    @Parameters({ "param1" })
    public void appendNotWritePutToCreate(final String uri) {
        final TestInfo info = setupTest("5.7.1-B",
                "When a client has acl:Append but not acl:Write for an LDP-RS and the " +
                        "implementation supports PUT to create they MUST " +
                        "allow the addition of a new child resource.",
                "https://fedora.info/2018/06/25/spec/#append-ldprs", ps);

        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-append.ttl", this.username);

        // perform PUT to child resource as non-admin succeeds.
        final Response putChildResponse =
                doPutUnverified(resourceUri + "/child1", new Headers(new Header("Content-Type", "text/plain")),
                        "test", false);
        // verify successful
        putChildResponse.then().statusCode(201);
    }

    /**
     * 5.7.1-C acl:Append for LDP-RS SHOULD test conditions
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = { "SHOULD" })
    @Parameters({ "param1" })
    public void appendNotWriteLdpRsShould(final String uri) {
        final TestInfo info = setupTest("5.7.1-C",
                "When a client has acl:Append but not acl:Write for an LDP-RS they SHOULD " +
                        "allow a PATCH request that only adds triples.",
                "https://fedora.info/2018/06/25/spec/#append-ldprs", ps);

        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-append.ttl", this.username);

        // perform PATCH which only adds.
        final Response patchAdd = doPatchUnverified(resourceUri, new Headers(new Header("Content-type",
                "application/sparql-update")),
                "prefix dc: <http://purl.org/dc/elements/1.1/> DELETE { } INSERT { <> dc:title \"I made a change\" .}" +
                        " WHERE { }",
                false);
        // Verify success.
        patchAdd.then().statusCode(204);

        // perform PATCH which only adds.
        final Response patchAddData = doPatchUnverified(resourceUri, new Headers(new Header("Content-type",
                "application/sparql-update")),
                "prefix dc: <http://purl.org/dc/elements/1.1/> INSERT DATA { <> dc:title \"I made a change\" .}",
                false);
        // Verify success.
        patchAddData.then().statusCode(204);
    }

    /**
     * 5.7.2 acl:Append for LDP-C MUST test conditions
     *
     * @param uri
     */
    @Test(groups = { "MUST" })
    @Parameters({ "param1" })
    public void appendNotWriteLdpCMust(final String uri) {
        final TestInfo info = setupTest("5.7.2",
                "When a client has acl:Append but not acl:Write for an LDPC they MUST " +
                        "allow a POST request.",
                "https://fedora.info/2018/06/25/spec/#append-ldpc", ps);

        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-append.ttl", this.username);

        final Response getInfo = doGet(resourceUri, false);
        if (confirmLDPContainer(getInfo)) {
            doPost(resourceUri, null, null, false);
        } else {
            throw new SkipException("Cannot confirm resource is a LDPC.");
        }
    }

}
