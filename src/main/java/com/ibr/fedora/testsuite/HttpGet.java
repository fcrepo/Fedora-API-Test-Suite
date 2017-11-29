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

import com.ibr.fedora.TestSuiteGlobals;
import com.ibr.fedora.TestsLabels;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import static org.hamcrest.Matchers.containsString;

public class HttpGet {
public String username;
public String password;
public TestsLabels tl = new TestsLabels();

/**
 * Authentication
 * @param username
 * @param password
 */
@BeforeClass
@Parameters({"param2", "param3"})
public void auth(final String username, final String password) {
this.username = username;
this.password = password;
}

/**
 * 3.5
 * @param host
 */
@Test(priority = 28)
@Parameters({"param1"})
public void responseDescribesHeader(final String host) throws FileNotFoundException {
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n28." + tl.responseDescribesHeader()[1] + "-" + tl.responseDescribesHeader()[1]).append("\n");
    ps.append("Request:\n");
    final String resource =
            RestAssured.given()
.auth().basic(this.username, this.password)
                    .header("Content-Disposition", "attachment; filename=\"responseDescribesHeader.txt\"")
                    .when()
                    .post(host).asString();
    RestAssured.given()
.auth().basic(this.username, this.password)
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .when()
            .get(resource + "/fcr:metadata")
            .then()
            .log().all()
            .statusCode(200).header("Link", containsString("describes"));

    ps.append("\n -Case End- \n").close();
}


/**
 * 3.5.1-A
 * @param host
 */
@Test(priority = 29)
@Parameters({"param1"})
public void additionalValuesForPreferHeader(final String host) throws FileNotFoundException {
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n29." + tl.additionalValuesForPreferHeader()[1]).append("\n");
    ps.append("Request:\n");
    final String resource =
            RestAssured.given()
.auth().basic(this.username, this.password)
                    .contentType("text/turtle")
                    .when()
                    .post(host).asString();
    RestAssured.given()
.auth().basic(this.username, this.password)
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .header("Prefer", "return=representation; "
            + "include=\"http://fedora.info/definitions/v4/repository#InboundReferences\"")
            .when()
            .get(resource)
            .then()
            .log().all()
            .statusCode(200).header("preference-applied",
            containsString("http://fedora.info/definitions/v4/repository#InboundReferences"));

    ps.append("\n -Case End- \n").close();
}

/**
 * 3.5.2
 * @param host
 */
@Test(priority = 30)
@Parameters({"param1"})
public void responsePreferenceAppliedHeader(final String host) throws FileNotFoundException {
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n30." + tl.responsePreferenceAppliedHeader()[1]).append("\n");
    ps.append("Request:\n");
    final String resource =
            RestAssured.given()
.auth().basic(this.username, this.password)
                    .contentType("text/turtle")
                    .when()
                    .post(host).asString();
    RestAssured.given()
.auth().basic(this.username, this.password)
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .header("Prefer", "return=minimal")
            .when()
            .get(resource)
            .then()
            .log().all()
            .statusCode(200).header("preference-applied", containsString("return=minimal"));

    ps.append("\n -Case End- \n").close();
}


}
