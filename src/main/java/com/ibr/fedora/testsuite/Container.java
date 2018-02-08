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
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.PrintStream;


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
    public static String body = "@prefix ldp: <http://www.w3.org/ns/ldp#> ."
        + "@prefix dcterms: <http://purl.org/dc/terms/> ."
        + "<> a ldp:Container, ldp:BasicContainer;"
        + "dcterms:title 'Container class Container' ;"
        + "dcterms:description 'This is a test container for the Fedora API Test Suite.' . ";

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
     * @param uri
     */
    @Test(priority = 1)
    @Parameters({"param1"})
    public void createLDPC(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n1." + tl.createLDPC()[1]).append("\n");
        ps.append("Request:\n");
        RestAssured.given()
        .auth().basic(this.username, this.password)
        .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
        .contentType("text/turtle")
        .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
        .header("slug", "Container-3.1.1-A")
        .body(body)
        .log().all()
        .when()
        .post(uri)
        .then()
        .log().all()
        .statusCode(201);
        ps.append("\n -Case End- \n").close();
        }
    /**
     * 3.1.1-B
     * @param uri
     */
    @Test(priority = 2)
    @Parameters({"param1"})
    public void ldpcContainmentTriples(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n2." + tl.ldpcContainmentTriples()[1]).append("\n");
        ps.append("Request:\n");
        final String pythagoras =
        RestAssured.given()
        .auth().basic(this.username, this.password)
        .contentType("text/turtle")
        .header("slug", "pythagoras-3.1.1-B")
        .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
        .when()
        .body(pythagorasContainer)
        .post(uri).asString();

        final String person = RestAssured.given()
        .auth().basic(this.username, this.password)
        .contentType("text/turtle")
        .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
        .header("slug", "person")
        .when()
        .body(personBody)
        .post(pythagoras).asString();

        final String portraits = RestAssured.given()
        .auth().basic(this.username, this.password)
        .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
        .contentType("text/turtle")
        .header("slug", "portraits")
        .when()
        .body(portraitContainer.replace("%person%", person))
        .post(pythagoras).asString();

         RestAssured.given()
        .auth().basic(this.username, this.password)
        .contentType("image/jpeg")
        .header("slug", "JpgPortrait")
        .when()
        .post(portraits).asString();

        final Response resP = RestAssured.given()
        .auth().basic(this.username, this.password)
        .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
        .log().all()
        .header("Prefer","return=representation; include=\"http://www.w3.org/ns/ldp#PreferContainment\"")
        .when()
        .get(portraits);

        ps.append(resP.getStatusLine().toString() + "\n");
        final Headers headers = resP.getHeaders();
        for (Header h : headers) {
             ps.append(h.getName().toString() + ": ");
             ps.append(h.getValue().toString() + "\n");
        }
        final String body = resP.getBody().asString();
        ps.append(body);
        ps.append("\n -Case End- \n").close();

       final boolean triple = TestSuiteGlobals.checkMembershipTriple(body);

         if (triple) {
                    Assert.assertTrue(false, "FAIL");
                } else {
                    if (body.contains("ldp:contains")) {
                        Assert.assertTrue(true, "OK");
                    }
               }
        }
    /**
     * 3.1.1-C
     * @param uri
     */
    @Test(priority = 3)
    @Parameters({"param1"})
    public void ldpcMembershipTriples(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n3." + tl.ldpcMembershipTriples()[1]).append("\n");
        ps.append("Request:\n");
        final String pythagoras =
        RestAssured.given()
        .auth().basic(this.username, this.password)
        .contentType("text/turtle")
        .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
        .header("slug", "pythagoras-3.1.1-C")
        .when()
        .body(pythagorasContainer)
        .post(uri).asString();

        final String person = RestAssured.given()
        .auth().basic(this.username, this.password)
        .contentType("text/turtle")
        .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
        .header("slug", "person")
        .when()
        .body(personBody)
        .post(pythagoras).asString();

        final String portraits = RestAssured.given()
        .auth().basic(this.username, this.password)
        .contentType("text/turtle")
        .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
        .header("slug", "portraits")
        .when()
        .body(portraitContainer.replace("%person%", person))
        .post(pythagoras).asString();

         RestAssured.given()
        .auth().basic(this.username, this.password)
        .contentType("image/jpeg")
        .header("slug", "JpgPortrait")
        .when()
        .post(portraits).asString();

        final Response resP = RestAssured.given()
        .auth().basic(this.username, this.password)
        .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
        .log().all()
        .header("Prefer","return=representation; include=\"http://www.w3.org/ns/ldp#PreferMembership\"")
        .when()
        .get(portraits);

        ps.append(resP.getStatusLine().toString() + "\n");
        final Headers headers = resP.getHeaders();
        for (Header h : headers) {
             ps.append(h.getName().toString() + ": ");
             ps.append(h.getValue().toString() + "\n");
        }
        final String body = resP.getBody().asString();
        ps.append(body);
        ps.append("\n -Case End- \n").close();

         if (body.contains("hasMemberRelation") && body.contains("membershipResource") &&
             !body.contains("ldp:contains") ) {
                    Assert.assertTrue(true, "OK");
                } else {
                    Assert.assertTrue(false, "FAIL");
               }
        }
    /**
     * 3.1.1-D
     * @param uri
     */
    @Test(priority = 4)
    @Parameters({"param1"})
    public void ldpcMinimalContainerTriples(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n4." + tl.ldpcMinimalContainerTriples()[1]).append("\n");
        ps.append("Request:\n");
        final String pythagoras =
        RestAssured.given()
        .auth().basic(this.username, this.password)
        .contentType("text/turtle")
        .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
        .header("slug", "pythagoras-3.1.1-D")
        .when()
        .body(pythagorasContainer)
        .post(uri).asString();

        final String person = RestAssured.given()
        .auth().basic(this.username, this.password)
        .contentType("text/turtle")
        .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
        .header("slug", "person")
        .when()
        .body(personBody)
        .post(pythagoras).asString();

        final String portraits = RestAssured.given()
        .auth().basic(this.username, this.password)
        .contentType("text/turtle")
        .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
        .header("slug", "portraits")
        .when()
        .body(portraitContainer.replace("%person%", person))
        .post(pythagoras).asString();

         RestAssured.given()
        .auth().basic(this.username, this.password)
        .contentType("image/jpeg")
        .header("slug", "JpgPortrait")
        .when()
        .post(portraits).asString();

        final Response resP = RestAssured.given()
        .auth().basic(this.username, this.password)
        .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
        .log().all()
        .header("Prefer","return=representation; include=\"http://www.w3.org/ns/ldp#PreferMinimalContainer\"")
        .when()
        .get(portraits);

        ps.append(resP.getStatusLine().toString() + "\n");
        final Headers headers = resP.getHeaders();
        for (Header h : headers) {
             ps.append(h.getName().toString() + ": ");
             ps.append(h.getValue().toString() + "\n");
        }
        final String body = resP.getBody().asString();
        ps.append(body);
        ps.append("\n -Case End- \n").close();

       final boolean triple = TestSuiteGlobals.checkMembershipTriple(body);

         if (!triple && !body.contains("ldp:contains")) {
                      Assert.assertTrue(true, "OK");
                } else {
                      Assert.assertTrue(false, "FAIL");
               }
        }

    }
