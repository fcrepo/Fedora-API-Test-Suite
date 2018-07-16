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

import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author awoods
 * @since 2018-07-14
 */
public class WebACModes extends AbstractTest {


    /**
     * Constructor
     *
     * @param username username
     * @param password password
     */
    @Parameters({"param2", "param3"})
    public WebACModes(final String username, final String password) {
        super(username, password);
    }


    /**
     * 5.0-F -Read access on HEAD
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void readAllowedHEAD(final String uri) {
        final TestInfo info = setupTest("5.0-F", "readAllowedHEAD",
                "acl:Read gives access to a class of operations that can be described as \"Read Access\". " +
                        "In a typical REST API, this includes access to HTTP verbs HEAD.",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

    /**
     * 5.0-G -Read access on GET
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void readAllowedGET(final String uri) {
        final TestInfo info = setupTest("5.0-G", "readAllowedGET",
                "acl:Read gives access to a class of operations that can be described as \"Read Access\". " +
                        "In a typical REST API, this includes access to HTTP verbs GET.",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

    /**
     * 5.0-H -Read access disallowed
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void readDisallowed(final String uri) {
        final TestInfo info = setupTest("5.0-H", "readDisallowed",
                "acl:Read gives access to a class of operations that can be described as \"Read Access\". " +
                        "In a typical REST API, this includes access to HTTP verbs GET. Its absence must prevent reads",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

    /**
     * 5.0-I - Write access PUT
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void writeAllowedPUT(final String uri) {
        final TestInfo info = setupTest("5.0-I", "writeAllowedPUT",
                "acl:Write gives access to a class of operations that can modify the resource. In a REST API " +
                        "context, this would include PUT.",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

    /**
     * 5.0-J - Write access POST
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void writeAllowedPOST(final String uri) {
        final TestInfo info = setupTest("5.0-J", "writeAllowedPOST",
                "acl:Write gives access to a class of operations that can modify the resource. In a REST API " +
                        "context, this would include POST.",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

    /**
     * 5.0-K - Write access DELETE
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void writeAllowedDELETE(final String uri) {
        final TestInfo info = setupTest("5.0-K", "writeAllowedDELETE",
                "acl:Write gives access to a class of operations that can modify the resource. In a REST API " +
                        "context, this would include DELETE",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

    /**
     * 5.0-L - Write access PATCH
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void writeAllowedPATCH(final String uri) {
        final TestInfo info = setupTest("5.0-L", "writeAllowedPATCH",
                "acl:Write gives access to a class of operations that can modify the resource. In a REST API " +
                        "context, this would include PATCH.",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

    /**
     * 5.0-M - Write access disallowed
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void writeDisallowed(final String uri) {
        final TestInfo info = setupTest("5.0-M", "writeDisallowed",
                "acl:Write gives access to a class of operations that can modify the resource. In a REST API " +
                        "context, this would include PUT, POST, DELETE and PATCH. Its absence must prevent writes",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

    /**
     * 5.0-N - Append access on POST
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void appendAllowedPOST(final String uri) {
        final TestInfo info = setupTest("5.0-N", "appendAllowedPOST",
                "acl:Append gives a more limited ability to write to a resource -- Append-Only. " +
                        "This generally includes the HTTP verb POST.",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

    /**
     * 5.0-O - Append access on PATCH
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void appendAllowedPATCH(final String uri) {
        final TestInfo info = setupTest("5.0-O", "appendAllowedPATCH",
                "acl:Append gives a more limited ability to write to a resource -- Append-Only. " +
                        "This generally includes the INSERT-only portion of SPARQL-based PATCHes.",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }
    /**
     * 5.0-P - Append access disallowed
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void appendDisallowed(final String uri) {
        final TestInfo info = setupTest("5.0-P", "appendDisallowed",
                "acl:Append gives a more limited ability to write to a resource -- Append-Only. " +
                        "This generally includes the HTTP verb POST, although some implementations may also extend " +
                        "this mode to cover non-overwriting PUTs, as well as the INSERT-only portion of " +
                        "SPARQL-based PATCHes. Its absence must prevent append updates.",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

    /**
     * 5.0-Q - Control access on GET
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void controlAllowedGET(final String uri) {
        final TestInfo info = setupTest("5.0-Q", "controlAllowedGET",
                "acl:Control is a special-case access mode that gives an agent the ability to view the ACL of a " +
                        "resource.",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

    /**
     * 5.0-R - Control access on PATCH
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void controlAllowedPATCH(final String uri) {
        final TestInfo info = setupTest("5.0-R", "controlAllowedPATCH",
                "acl:Control is a special-case access mode that gives an agent the ability to modify the ACL of a " +
                        "resource.",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

    /**
     * 5.0-S - Control access on PUT
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void controlAllowedPUT(final String uri) {
        final TestInfo info = setupTest("5.0-S", "controlAllowedPUT",
                "acl:Control is a special-case access mode that gives an agent the ability to modify the ACL of a " +
                        "resource.",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

    /**
     * 5.0-T - Control access disallowed on GET
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void controlDisallowedGET(final String uri) {
        final TestInfo info = setupTest("5.0-T", "controlDisallowedGET",
                "acl:Control is a special-case access mode that gives an agent the ability to view and modify the " +
                        "ACL of a resource. Its absence must prevent viewing the ACL.",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

    /**
     * 5.0-U - Control access disallowed on PATCH
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void controlDisallowedPATCH(final String uri) {
        final TestInfo info = setupTest("5.0-U", "controlDisallowedPATCH",
                "acl:Control is a special-case access mode that gives an agent the ability to view and modify the " +
                        "ACL of a resource. Its absence must prevent updating the ACL.",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

    /**
     * 5.0-V - Control access disallowed on PUT
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void controlDisallowedPUT(final String uri) {
        final TestInfo info = setupTest("5.0-U", "controlDisallowedPUT",
                "acl:Control is a special-case access mode that gives an agent the ability to view and modify the " +
                        "ACL of a resource. Its absence must prevent updating the ACL.",
                "https://fedora.info/2018/06/25/spec/#resource-authorization", ps);
    }

}
