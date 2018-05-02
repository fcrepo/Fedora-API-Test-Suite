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

import static org.fcrepo.spec.testsuite.test.Constants.BASIC_CONTAINER_BODY;
import static org.fcrepo.spec.testsuite.test.Constants.BASIC_CONTAINER_LINK_HEADER;

import java.io.PrintStream;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;
import org.fcrepo.spec.testsuite.TestSuiteGlobals;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * A test base class for common test functions.
 *
 * @author Daniel Berntein
 */
public class AbstractTest {

    protected PrintStream ps;

    protected String username;
    protected String password;

    /**
     * Constructor
     *
     * @param username username
     * @param password password
     */
    public AbstractTest(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * setup
     */
    @BeforeMethod
    public void setup() {
        ps = TestSuiteGlobals.logFile();
        ps.append("************************************************\n");
        ps.append("**** Test Start ********************************\n");
        ps.append("************************************************\n");

    }

    /**
     * tearDown
     */
    @AfterMethod
    public void tearDown() {
        ps.append("\n************************************************");
        ps.append("\n**** Test End **********************************");
        ps.append("\n************************************************\n\n\n\n").close();

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
        return new TestInfo(id, getClass(), title, description, specLink);
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
        ps.append("Class: " + info.getTestClass().getName()).append("\n");
        ps.append("Method: " + info.getTitle()).append("\n");
        ps.append("Description: " + info.getDescription()).append("\n");
        ps.append("Request:\n");
        return info;
    }

    protected Response createBasicContainer(final String uri, final TestInfo info) {
        return createBasicContainer(uri, info.getId());
    }

    protected Response createBasicContainer(final String uri, final TestInfo info, final String body) {
        return createBasicContainer(uri, info.getId(), body);
    }

    protected Response createBasicContainer(final String uri, final String slug) {
        return createBasicContainer(uri, slug, BASIC_CONTAINER_BODY);
    }

    protected Response createBasicContainer(final String uri, final String slug, final String body) {
        return RestAssured.given()
                          .auth().basic(this.username, this.password)
                          .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                          .contentType("text/turtle")
                          .header("Link", BASIC_CONTAINER_LINK_HEADER)
                          .header("slug", slug)
                          .body(body)
                          .log().all()
                          .when()
                          .post(uri);
    }
}
