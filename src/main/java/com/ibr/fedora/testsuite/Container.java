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

import static org.hamcrest.CoreMatchers.containsString;

public class Container {
    public String username;
    public String password;
    public TestsLabels tl = new TestsLabels();
    public String pythagorasContainer = "@prefix dc: <http://purl.org/dc/terms/> . "
    + "@prefix foaf: <http://xmlns.com/foaf/0.1/> . "
    + "<> dc:title 'Pythagoras Collection'; "
    + "dc:abstract 'A collection of materials and facts about Pythagoras' .";
    public String personBody = "@prefix dc: <http://purl.org/dc/terms/> . "
    + "@prefix foaf: <http://xmlns.com/foaf/0.1/> . "
    + "<> a foaf:Person; "
    + "foaf:name \"Pythagoras\" ; "
    + "foaf:based_near \"Croton\" ; "
    + "foaf:interest [ dc:title \"Geometry\" ] .";
    public String portraitContainer = "@prefix ldp: <http://www.w3.org/ns/ldp#> . "
    + "@prefix dcterms: <http://purl.org/dc/terms/> . "
    + "@prefix foaf: <http://xmlns.com/foaf/0.1/> . "
    + "<> a ldp:DirectContainer; "
    + "ldp:membershipResource <%person%>; "
    + "ldp:hasMemberRelation foaf:depiction; "
    + "dcterms:title \"Portraits of Pythagoras\" .";

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
     * 3.1.1-A
     * @param host
     */
    @Test(priority = 1)
    @Parameters({"param1"})
    public void createLDPC(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n1." + tl.createLDPC()[1]).append("\n");
        ps.append("Request:\n");
        RestAssured.given()
        .auth().basic(this.username, this.password)
        .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
        .contentType("text/turtle")
        .log().all()
        .when()
        .post(host)
        .then()
        .log().all()
        .statusCode(201);
        ps.append("\n -Case End- \n").close();
        }

    /**
     * 3.1.1-B
     * @param host
     */
    @Test(priority = 2)
    @Parameters({"param1"})
    public void ldpcContainmentTriples(final String host) throws FileNotFoundException {
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n2." + tl.ldpcContainmentTriples()[1]).append("\n");
    ps.append("Request:\n");

    final String container = RestAssured.given()
    .auth().basic(this.username, this.password)
    .contentType("text/turtle")
    .when()
    .post(host).asString();

    RestAssured.given()
    .auth().basic(this.username, this.password)
    .contentType("text/turtle")
    .when()
    .post(container).asString();

    RestAssured.given()
    .auth().basic(this.username, this.password)
    .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
    .log().all()
    .when()
    .get(container)
    .then()
    .log().all()
    .body(containsString("ldp:contains"));
    ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.1.1-C
     * @param host
     */
    @Test(priority = 3)
    @Parameters({"param1"})
    public void ldpcMembership(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n3." + tl.ldpcMembership()[1]).append("\n");
        ps.append("Request:\n");

        final String pythagoras =
        RestAssured.given()
        .auth().basic(this.username, this.password)
        .contentType("text/turtle")
        .header("slug", "pythagoras")
        .when()
        .body(pythagorasContainer)
        .post(host).asString();

        final String person = RestAssured.given()
        .auth().basic(this.username, this.password)
        .contentType("text/turtle")
        .header("slug", "person")
        .when()
        .body(personBody)
        .post(pythagoras).asString();

        final String portraits = RestAssured.given()
        .auth().basic(this.username, this.password)
        .contentType("text/turtle")
        .header("slug", "portraits")
        .when()
        .body(portraitContainer.replace("%person%", person))
        .post(pythagoras).asString();

        final String portrait = RestAssured.given()
        .auth().basic(this.username, this.password)
        .contentType("image/jpeg")
        .header("slug", "JpgPortrait")
        .when()
        .post(portraits).asString();

        RestAssured.given()
        .auth().basic(this.username, this.password)
        .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
        .log().all()
        .when()
        .get(person)
        .then()
        .log().all()
        .body(containsString(portrait));

        ps.append("\n -Case End- \n").close();
        }
    }