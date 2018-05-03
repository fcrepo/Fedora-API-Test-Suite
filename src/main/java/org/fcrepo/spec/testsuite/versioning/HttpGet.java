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
package org.fcrepo.spec.testsuite.versioning;

import java.io.FileNotFoundException;

import org.fcrepo.spec.testsuite.AbstractTest;
import org.testng.annotations.Parameters;

/**
 * @author Daniel Bernstein
 */
public class HttpGet extends AbstractTest {

    /**
     * Authentication
     *
     * @param username
     * @param password
     */
    @Parameters({"param2", "param3"})
    public HttpGet(final String username, final String password) {
        super(username, password);
    }



    /**
     * First versioning test will go here.
     *
     * @param uri
     */
    //@Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void firstTest(final String uri) throws FileNotFoundException {
    }
}
