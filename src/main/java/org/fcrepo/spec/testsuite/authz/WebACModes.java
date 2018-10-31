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
import org.testng.annotations.Test;

/**
 * @author awoods
 * @author dbernstein
 * @since 2018-07-14
 */
public class WebACModes extends AbstractAuthzTest {

    /**
     * 5.0-F -Read access on HEAD
     */
    @Test(groups = {"MUST"})
    public void readAllowedHEAD() {
        final TestInfo info = setupTest("5.0-F",
                                        "acl:Read gives access to a class of operations that can be described as " +
                                        "\"Read Access\". " +
                                        "In a typical REST API, this includes access to HTTP verbs HEAD.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-only.ttl", this.permissionlessUserWebId);
        //perform verified head as non-admin
        doHead(resourceUri, false);
    }

    /**
     * 5.0-G -Read access on GET
     */
    @Test(groups = {"MUST"})
    public void readAllowedGET() {
        final TestInfo info = setupTest("5.0-G",
                                        "acl:Read gives access to a class of operations that can be described as " +
                                        "\"Read Access\". " +
                                        "In a typical REST API, this includes access to HTTP verbs GET.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-only.ttl", this.permissionlessUserWebId);
        //perform GET as non-admin
        doGet(resourceUri, false);
    }

    /**
     * 5.0-H -Read access disallowed
     */
    @Test(groups = {"MUST"})
    public void readDisallowed() {
        final TestInfo info = setupTest("5.0-H",
                                        "acl:Read gives access to a class of operations that can be described as " +
                                        "\"Read Access\". " +
                                        "In a typical REST API, this includes access to HTTP verbs GET. Its absence " +
                                        "must prevent reads",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-only.ttl", "not-" + this.permissionlessUserWebId);
        //perform GET as non-admin
        final Response getResponse = doGetUnverified(resourceUri, false);
        //verify unauthorized
        getResponse.then().statusCode(403);
    }

    /**
     * 5.0-I - Write access PUT
     */
    @Test(groups = {"MUST"})
    public void writeAllowedPUT() {
        final TestInfo info = setupTest("5.0-I",
                                        "acl:Write gives access to a class of operations that can modify the resource" +
                                        ". In a REST API " +
                                        "context, this would include PUT.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-write.ttl", this.permissionlessUserWebId);
        //perform PUT  to child resource as non-admin
        final Response putResponse =
            doPutUnverified(resourceUri + "/child1", new Headers(new Header("Content-Type", "text/plain")),
                            "test", false);
        //verify successful
        putResponse.then().statusCode(201);

    }

    /**
     * 5.0-J - Write access POST
     */
    @Test(groups = {"MUST"})
    public void writeAllowedPOST() {
        final TestInfo info = setupTest("5.0-J",
                                        "acl:Write gives access to a class of operations that can modify the resource" +
                                        ". In a REST API " +
                                        "context, this would include POST.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-write.ttl", this.permissionlessUserWebId);
        //perform POST  to child resource as non-admin
        doPost(resourceUri, new Headers(new Header("Content-Type", "text/plain")),
               "test", false);
    }

    /**
     * 5.0-K - Write access DELETE
     */
    @Test(groups = {"MUST"})
    public void writeAllowedDELETE() {
        final TestInfo info = setupTest("5.0-K",
                                        "acl:Write gives access to a class of operations that can modify the resource" +
                                        ". In a REST API " +
                                        "context, this would include DELETE",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-write.ttl", this.permissionlessUserWebId);
        //perform DELETE  to child resource as non-admin
        doDelete(resourceUri, false);
    }

    /**
     * 5.0-L - Write access PATCH
     */
    @Test(groups = {"MUST"})
    public void writeAllowedPATCH() {
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
        createAclForResource(resourceUri, "user-read-write.ttl", this.permissionlessUserWebId);
        //perform PATCH  to child resource as non-admin
        doPatch(resourceUri, new Headers(new Header("Content-Type", APPLICATION_SPARQL_UPDATE)), body, false);

    }

    /**
     * 5.0-M-1 - Write access disallowed on PUT
     */
    @Test(groups = {"MUST"})
    public void writeDisallowedPut() {
        final TestInfo info = setupTest("5.0-M-1",
                                        "acl:Write gives access to PUT a resource. When not present, " +
                                        "writes should be disallowed.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-only.ttl", this.permissionlessUserWebId);
        //perform PUT  to child resource as non-admin with read only prives
        final Response putResponse =
            doPutUnverified(resourceUri + "/child1", new Headers(new Header("Content-Type", "text/plain")),
                            "test", false);
        //verify unauthorized
        putResponse.then().statusCode(403);
    }

    /**
     * 5.0-M - Write access disallowed POST
     */
    @Test(groups = {"MUST"})
    public void writeDisallowedPost() {
        final TestInfo info = setupTest("5.0-M-2",
                                        "acl:Write gives access to POST a resource. When not present, " +
                                        "writes should be disallowed.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-only.ttl", this.permissionlessUserWebId);
        //perform POST  to child resource as non-admin
        final Response postResponse = doPostUnverified(resourceUri, new Headers(),
                                                       "test", false);
        postResponse.then().statusCode(403);
    }

    /**
     * 5.0-M - Write access disallowed on DELETE
     */
    @Test(groups = {"MUST"})
    public void writeDisallowedDelete() {
        final TestInfo info = setupTest("5.0-M-3",
                                        "acl:Write gives access to DELETE a resource. When not present, " +
                                        "writes should be disallowed.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-only.ttl", this.permissionlessUserWebId);
        //perform DELETE  to child resource as non-admin
        final Response deleteResponse = doDeleteUnverified(resourceUri, false);
        deleteResponse.then().statusCode(403);
    }

    /**
     * 5.0-M-4 - Write access disallowed on PATCH
     */
    @Test(groups = {"MUST"})
    public void writeDisallowedPatch() {
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
        createAclForResource(resourceUri, "user-read-only.ttl", this.permissionlessUserWebId);
        //perform PATCH  to child resource as non-admin
        final Response patchResponse =
            doPatchUnverified(resourceUri, new Headers(new Header("Content-Type", APPLICATION_SPARQL_UPDATE)), body,
                              false);
        patchResponse.then().statusCode(403);
    }

    /**
     * 5.0-N - Append access on POST
     */
    @Test(groups = {"MUST"})
    public void appendAllowedPOST() {
        final TestInfo info = setupTest("5.0-N",
                                        "acl:Append gives a more limited ability to write to a resource -- " +
                                        "Append-Only. " +
                                        "This generally includes the HTTP verb POST.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        //create a resource
        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-append.ttl", this.permissionlessUserWebId);
        //perform POST  to child resource as non-admin
        doPost(resourceUri, new Headers(),
               "test", false);

    }

    /**
     * 5.0-O - Append access on PATCH
     */
    @Test(groups = {"MUST"})
    public void appendAllowedPATCH() {
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
        createAclForResource(resourceUri, "user-read-append.ttl", this.permissionlessUserWebId);
        //perform PATCH  to child resource as non-admin
        doPatch(resourceUri, new Headers(new Header("Content-Type", APPLICATION_SPARQL_UPDATE)), body, false);
    }

    /**
     * 5.0-P - Append access disallowed
     */
    @Test(groups = {"MUST"})
    public void appendDisallowed() {
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
        createAclForResource(resourceUri, "user-read-append.ttl", this.permissionlessUserWebId);
        //perform PATCH  to child resource as non-admin
        final Response patchResponse =
            doPatchUnverified(resourceUri, new Headers(new Header("Content-Type", APPLICATION_SPARQL_UPDATE)), body,
                              false);
        //verify unauthorized
        patchResponse.then().statusCode(403);
    }

    /**
     * 5.0-Q - Control access on GET
     */
    @Test(groups = {"MUST"})
    public void controlAllowedGET() {
        final TestInfo info = setupTest("5.0-Q",
                                        "acl:Control is a special-case access mode that gives an agent the ability to" +
                                        " view the ACL of a " +
                                        "resource.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
        final String resourceUri = createResource(uri, info.getId());
        final String aclUri = createAclForResource(resourceUri, "user-control.ttl", this.permissionlessUserWebId);
        //perform verified get as non-admin
        doGet(aclUri, false);

    }

    /**
     * 5.0-R - Control access on PATCH
     */
    @Test(groups = {"MUST"})
    public void controlAllowedPATCH() {
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
        final String aclUri = createAclForResource(resourceUri, "user-control.ttl", this.permissionlessUserWebId);
        //perform PATCH  to resource's acl as non-admin
        doPatch(aclUri, new Headers(new Header("Content-Type", APPLICATION_SPARQL_UPDATE)), body, false);
    }

    /**
     * 5.0-S - Control access on PUT
     */
    @Test(groups = {"MUST"})
    public void controlAllowedPUT() {
        final TestInfo info = setupTest("5.0-S",
                                        "acl:Control is a special-case access mode that gives an agent the ability to" +
                                        " modify the ACL of a " +
                                        "resource.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        final String resourceUri = createResource(uri, info.getId());

        // ACL allows control but not read/write
        final String aclUri = createAclForResource(resourceUri, "user-control.ttl", this.permissionlessUserWebId);

        // Verify that non-admin user can not read the resource
        doGetUnverified(resourceUri, false).then().statusCode(403);

        final String body = "PREFIX acl: <http://www.w3.org/ns/auth/acl#>\n"
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
                + " INSERT {\n"
                + " <#openaccess> a acl:Authorization ;"
                + " acl:mode acl:Read ;\n"
                + " acl:agentClass foaf:Agent ; \n"
                + " acl:accessTo <" + resourceUri + "> .\n"
                + "}"
                + " WHERE { }";

        // Verify that non-admin user can update ACL
        doPatch(aclUri, new Headers(new Header("Content-Type", APPLICATION_SPARQL_UPDATE)), body, false);

        // Verify that non-admin user can now read the resource
        doGet(resourceUri, false);
    }

    /**
     * 5.0-T - Control access disallowed on GET
     */
    @Test(groups = {"MUST"})
    public void controlDisallowedGET() {
        final TestInfo info = setupTest("5.0-T",
                                        "acl:Control is a special-case access mode that gives an agent the ability to" +
                                        " view and modify the " +
                                        "ACL of a resource. Its absence must prevent viewing the ACL.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        final String resourceUri = createResource(uri, info.getId());
        final String aclUri = createAclForResource(resourceUri, "user-read-only.ttl", this.permissionlessUserWebId);
        //perform a GET on the acl using the non-admin user
        final Response getResponse = doGetUnverified(aclUri, false);
        getResponse.then().statusCode(403);
    }

    /**
     * 5.0-U - Control access disallowed on PATCH
     */
    @Test(groups = {"MUST"})
    public void controlDisallowedPATCH() {
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
        final String aclUri = createAclForResource(resourceUri, "user-read-only.ttl", this.permissionlessUserWebId);
        //perform PATCH  to resource's acl as non-admin
        final Response patchResponse =
            doPatchUnverified(aclUri, new Headers(new Header("Content-Type", APPLICATION_SPARQL_UPDATE)), body, false);
        //verify unauthorized
        patchResponse.then().statusCode(403);
    }

    /**
     * 5.0-V - Control access disallowed on PUT
     */
    @Test(groups = {"MUST"})
    public void controlDisallowedPUT() {
        final TestInfo info = setupTest("5.0-V",
                                        "acl:Control is a special-case access mode that gives an agent the ability to" +
                                        " view and modify the " +
                                        "ACL of a resource. Its absence must prevent updating the ACL.",
                                        "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);

        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-write.ttl", this.permissionlessUserWebId);
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
                            getAclAsString("user-read-write.ttl", childResourceUri, this.permissionlessUserWebId),
                            false);
        childAclResponse.then().statusCode(403);
    }

    /**
     * 5.7.1-A acl:Append for LDP-RS MUST test conditions
     */
    @Test(groups = { "MUST" })
    public void appendNotWriteLdpRsMust() {
        final TestInfo info = setupTest("5.7.1-A",
                "When a client has acl:Append but not acl:Write for an LDP-RS they MUST " +
                        "not DELETE, not PATCH that deletes triples, not PUT on the resource",
                "https://fedora.info/2018/06/25/spec/#append-ldprs", ps);

        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-append.ttl", this.permissionlessUserWebId);

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
     */
    @Test(groups = { "MUST" })
    public void appendNotWritePutToCreate() {
        final TestInfo info = setupTest("5.7.1-B",
                "When a client has acl:Append but not acl:Write for an LDP-RS and the " +
                        "implementation supports PUT to create they MUST " +
                        "allow the addition of a new child resource.",
                "https://fedora.info/2018/06/25/spec/#append-ldprs", ps);

        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-append.ttl", this.permissionlessUserWebId);

        // perform PUT to child resource as non-admin succeeds.
        final Response putChildResponse =
                doPutUnverified(resourceUri + "/child1", new Headers(new Header("Content-Type", "text/plain")),
                        "test", false);
        // verify successful
        putChildResponse.then().statusCode(201);
    }

    /**
     * 5.7.1-C acl:Append for LDP-RS SHOULD test conditions
     */
    @Test(groups = { "SHOULD" })
    public void appendNotWriteLdpRsShould() {
        final TestInfo info = setupTest("5.7.1-C",
                "When a client has acl:Append but not acl:Write for an LDP-RS they SHOULD " +
                        "allow a PATCH request that only adds triples.",
                "https://fedora.info/2018/06/25/spec/#append-ldprs", ps);

        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-append.ttl", this.permissionlessUserWebId);

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
     */
    @Test(groups = { "MUST" })
    public void appendNotWriteLdpCMust() {
        final TestInfo info = setupTest("5.7.2",
                "When a client has acl:Append but not acl:Write for an LDPC they MUST " +
                        "allow a POST request.",
                "https://fedora.info/2018/06/25/spec/#append-ldpc", ps);

        final String resourceUri = createResource(uri, info.getId());
        createAclForResource(resourceUri, "user-read-append.ttl", this.permissionlessUserWebId);

        final Response getInfo = doGet(resourceUri, false);
        if (confirmLDPContainer(getInfo)) {
            doPost(resourceUri, null, null, false);
        } else {
            throw new SkipException("Cannot confirm resource is a LDPC.");
        }
    }

    /**
     * 5.7.3 acl:Append for LDP-NR MUST test conditions
     */
    @Test(groups = { "MUST" })
    public void appendNotWriteLdpNr() {
        final TestInfo info = setupTest("5.7.3",
                "When a client has acl:Append but not acl:Write for an LDP-NR they MUST " +
                        "deny all DELETE, POST, and PUT requests.",
                "https://fedora.info/2018/06/25/spec/#append-ldpnr", ps);

        final Headers headers = new Headers(new Header("Content-type", "text/plain"));
        final Response postLdpNr = doPost(uri, headers, "test image");
        final String resourceUri = getLocation(postLdpNr);

        createAclForResource(resourceUri, "user-read-append.ttl", this.permissionlessUserWebId);

        final String description = getLdpNrDescription(resourceUri);

        // POST requests to a LDP-NR with acl:Append only MUST be denied
        final Response postRequest = doPostUnverified(resourceUri, null, null, false);
        postRequest.then().statusCode(403);

        // PUT requests to a LDP-NR with acl:Append only MUST be denied
        final Response putRequest = doPutUnverified(resourceUri, null, null, false);
        putRequest.then().statusCode(403);

        // DELETE requests to a LDP-NR with acl:Append only MUST be denied
        final Response deleteRequest = doDeleteUnverified(resourceUri, false);
        deleteRequest.then().statusCode(403);

        // Also perform the tests against an associated LDP-RS.
        if (description != null) {
            // POST requests to a LDP-NR with acl:Append only MUST be denied
            final Response postRequest2 = doPostUnverified(description, null, null, false);
            postRequest2.then().statusCode(403);

            // PUT requests to a LDP-NR with acl:Append only MUST be denied
            final Response putRequest2 = doPutUnverified(description, null, null, false);
            putRequest2.then().statusCode(403);

            // DELETE requests to a LDP-NR with acl:Append only MUST be denied
            final Response deleteRequest2 = doDeleteUnverified(description, false);
            deleteRequest2.then().statusCode(403);
        }

    }

}
