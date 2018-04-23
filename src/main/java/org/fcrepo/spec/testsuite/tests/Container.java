/*
 *
 *  * The contents of this file are subject to the license and copyright
 *  * detailed in the LICENSE and NOTICE files at the root of the source
 *  * tree and available online at
 *  *
 *  *     http://duracloud.org/license/
 *
 */

/**
 * @author Jorge Abrego, Fernando Cardoza
 */

package org.fcrepo.spec.testsuite.tests;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.fcrepo.spec.testsuite.TestSuiteGlobals;
import org.fcrepo.spec.testsuite.TestsLabels;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


public class Container {
    public static String body = "@prefix ldp: <http://www.w3.org/ns/ldp#> ."
                                + "@prefix dcterms: <http://purl.org/dc/terms/> ."
                                + "<> a ldp:Container, ldp:BasicContainer;"
                                + "dcterms:title 'Container class Container' ;"
                                + "dcterms:description 'This is a test container for the Fedora API Test Suite.' . ";
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
     *
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
     *
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
     *
     * @param uri
     */
    @Test(priority = 2)
    @Parameters({"param1"})
    public void ldpcContainmentTriples(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n2." + tl.ldpcContainmentTriples()[1]).append("\n");
        ps.append("Request:\n");
        final Response pythagoras =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .contentType("text/turtle")
                       .header("slug", "pythagoras-3.1.1-B")
                       .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                       .when()
                       .body(pythagorasContainer)
                       .post(uri);
        final String pythagorasLocationHeader = pythagoras.getHeader("Location");

        final String person = RestAssured.given()
                                         .auth().basic(this.username, this.password)
                                         .contentType("text/turtle")
                                         .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                                         .header("slug", "person")
                                         .when()
                                         .body(personBody)
                                         .post(pythagorasLocationHeader).asString();

        final Response portraits = RestAssured.given()
                                              .auth().basic(this.username, this.password)
                                              .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                                              .contentType("text/turtle")
                                              .header("slug", "portraits")
                                              .when()
                                              .body(portraitContainer.replace("%person%", person))
                                              .post(pythagorasLocationHeader);
        final String portraitsLocationHeader = portraits.getHeader("Location");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("image/jpeg")
                   .header("slug", "JpgPortrait")
                   .when()
                   .post(portraitsLocationHeader).asString();

        final Response resP = RestAssured.given()
                                         .auth().basic(this.username, this.password)
                                         .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                                         .log().all()
                                         .header("Prefer",
                                                 "return=representation; include=\"http://www" +
                                                 ".w3.org/ns/ldp#PreferContainment\"")
                                         .when()
                                         .get(portraitsLocationHeader);

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
     *
     * @param uri
     */
    @Test(priority = 3)
    @Parameters({"param1"})
    public void ldpcMembershipTriples(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n3." + tl.ldpcMembershipTriples()[1]).append("\n");
        ps.append("Request:\n");
        final Response pythagoras =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .contentType("text/turtle")
                       .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                       .header("slug", "pythagoras-3.1.1-C")
                       .when()
                       .body(pythagorasContainer)
                       .post(uri);
        final String pythagorasLocationHeader = pythagoras.getHeader("Location");

        final String person = RestAssured.given()
                                         .auth().basic(this.username, this.password)
                                         .contentType("text/turtle")
                                         .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                                         .header("slug", "person")
                                         .when()
                                         .body(personBody)
                                         .post(pythagorasLocationHeader).asString();

        final Response portraits = RestAssured.given()
                                              .auth().basic(this.username, this.password)
                                              .contentType("text/turtle")
                                              .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                                              .header("slug", "portraits")
                                              .when()
                                              .body(portraitContainer.replace("%person%", person))
                                              .post(pythagorasLocationHeader);
        final String portraitsLocationHeader = portraits.getHeader("Location");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("image/jpeg")
                   .header("slug", "JpgPortrait")
                   .when()
                   .post(portraitsLocationHeader).asString();

        final Response resP = RestAssured.given()
                                         .auth().basic(this.username, this.password)
                                         .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                                         .log().all()
                                         .header("Prefer",
                                                 "return=representation; include=\"http://www" +
                                                 ".w3.org/ns/ldp#PreferMembership\"")
                                         .when()
                                         .get(portraitsLocationHeader);

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
            !body.contains("ldp:contains")) {
            Assert.assertTrue(true, "OK");
        } else {
            Assert.assertTrue(false, "FAIL");
        }
    }

    /**
     * 3.1.1-D
     *
     * @param uri
     */
    @Test(priority = 4)
    @Parameters({"param1"})
    public void ldpcMinimalContainerTriples(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n4." + tl.ldpcMinimalContainerTriples()[1]).append("\n");
        ps.append("Request:\n");
        final Response pythagoras =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .contentType("text/turtle")
                       .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                       .header("slug", "pythagoras-3.1.1-D")
                       .when()
                       .body(pythagorasContainer)
                       .post(uri);
        final String pythagorasLocationHeader = pythagoras.getHeader("Location");

        final String person = RestAssured.given()
                                         .auth().basic(this.username, this.password)
                                         .contentType("text/turtle")
                                         .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                                         .header("slug", "person")
                                         .when()
                                         .body(personBody)
                                         .post(pythagorasLocationHeader).asString();

        final Response portraits = RestAssured.given()
                                              .auth().basic(this.username, this.password)
                                              .contentType("text/turtle")
                                              .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                                              .header("slug", "portraits")
                                              .when()
                                              .body(portraitContainer.replace("%person%", person))
                                              .post(pythagorasLocationHeader);
        final String portraitsLocationHeader = portraits.getHeader("Location");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .contentType("image/jpeg")
                   .header("slug", "JpgPortrait")
                   .when()
                   .post(portraitsLocationHeader).asString();

        final Response resP = RestAssured.given()
                                         .auth().basic(this.username, this.password)
                                         .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                                         .log().all()
                                         .header("Prefer",
                                                 "return=representation; include=\"http://www" +
                                                 ".w3.org/ns/ldp#PreferMinimalContainer\"")
                                         .when()
                                         .get(portraitsLocationHeader);

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
