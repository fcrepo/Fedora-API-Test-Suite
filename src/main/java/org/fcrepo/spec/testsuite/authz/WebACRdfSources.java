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
 * @since 2018-07-15
 */
public class WebACRdfSources extends AbstractTest {


    /**
     * Constructor
     *
     * @param username username
     * @param password password
     */
    @Parameters({"param2", "param3"})
    public WebACRdfSources(final String username, final String password) {
        super(username, password);
    }


    /**
     * 5.1 - ACL is LDPRS
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void aclIsLDPRS(final String uri) {
        final TestInfo info = setupTest("5.1", "aclIsLDPRS",
                "An ACL for a controlled resource on a conforming server must itself be an LDP-RS.",
                "https://fedora.info/2018/06/25/spec/#solid-ldp-acls", ps);
    }

}
