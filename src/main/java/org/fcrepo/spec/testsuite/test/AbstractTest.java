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
package org.fcrepo.spec.testsuite.test;

import java.io.PrintStream;

import org.fcrepo.spec.testsuite.TestInfo;
import org.fcrepo.spec.testsuite.TestSuiteGlobals;

/**
 * A test base class for common test functions.
 *
 * @author Daniel Berntein
 */
public class AbstractTest {

    protected final PrintStream ps = TestSuiteGlobals.logFile();

    protected String username;
    protected String password;

    /**
     * Constructor
     * @param username username
     * @param password password
     */
    public AbstractTest(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * A convenience method for creating TestInfo instances.
     *
     * @param id
     * @param title
     * @param description
     * @param specLink
     * @return
     */
    protected TestInfo createTestInfo(final String id, final String title, final String description,
                                      final String specLink) {
        return new TestInfo(id, getClass().getSimpleName(), title, description, specLink);
    }

    /**
     * A convenience method for setup boilerplate
     *
     * @param id
     * @param title
     * @param description
     * @param specLink
     * @param ps
     * @return
     */
    protected TestInfo setupTest(final String id, final String title, final String description, final String specLink,
                                 final PrintStream ps) {
        final TestInfo info = createTestInfo(id, title, description, specLink);
        ps.append("\n" + info.getDescription()).append("\n");
        ps.append("Request:\n");
        return info;
    }
}
