/**
 * @author Jorge Abrego, Fernando Cardoza
 */
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
package com.ibr.fedora.testsuite;

import static org.hamcrest.Matchers.containsString;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import com.ibr.fedora.TestSuiteGlobals;
import com.ibr.fedora.TestsLabels;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class HttpOptions {
    public String username;
    public String password;
    public TestsLabels tl = new TestsLabels();

    /**
     * Authentication
     *
     * @param username
     * @param password
     */
    @Parameters({"param2", "param3"})
    public HttpOptions(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * 3.4-A
     *
     * @param uri
     */
    @Test(priority = 18, groups = {"MUST"})
    @Parameters({"param1"})
    public void httpOptionsSupport(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n18." + tl.httpOptionsSupport()[1]).append("\n");
        ps.append("Request:\n");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .options(uri)
                   .then()
                   .log().all()
                   .statusCode(200);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.4-B
     *
     * @param uri
     */
    @Test(priority = 19, groups = {"MUST"})
    @Parameters({"param1"})
    public void httpOptionsSupportAllow(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n19." + tl.httpOptionsSupportAllow()[1]).append("\n");
        ps.append("Request:\n");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .options(uri)
                   .then()
                   .log().all()
                   .statusCode(200).header("Allow", containsString("GET"));

        ps.append("\n -Case End- \n").close();
    }
}
