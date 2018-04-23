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

import static org.hamcrest.Matchers.containsString;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.fcrepo.spec.testsuite.TestSuiteGlobals;
import org.fcrepo.spec.testsuite.TestsLabels;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


public class HttpPost {
    public static String body = "@prefix ldp: <http://www.w3.org/ns/ldp#> ."
                                + "@prefix dcterms: <http://purl.org/dc/terms/> ."
                                + "<> a ldp:Container, ldp:BasicContainer;"
                                + "dcterms:title 'Post class Container' ;"
                                + "dcterms:description 'This is a test container for the Fedora API Test Suite.' . ";
    public String username;
    public String password;
    public TestsLabels tl = new TestsLabels();
    public String resource = "";
    public String binary = "https://www.w3.org/StyleSheets/TR/2016/logos/UD-watermark";

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
     * 3.5-A
     *
     * @param uri
     */
    @Test(priority = 20)
    @Parameters({"param1"})
    public void httpPost(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n20." + tl.httpPost()[1]).append("\n");
        ps.append("Request:\n");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .contentType("text/turtle")
                   .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                   .header("slug", "Post-3.5-A")
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
     * 3.5-B
     *
     * @param uri
     */
    @Test(priority = 21)
    @Parameters({"param1"})
    public void constrainedByResponseHeader(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n21." + tl.constrainedByResponseHeader()[1]).append("\n");
        ps.append("Request:\n");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .contentType("text/turtle")
                   .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                   .header("slug", "Post-3.5-B")
                   .body(body)
                   .log().all()
                   .when()
                   .post(uri)
                   .then()
                   .log().all()
                   .statusCode(201).header("Link", containsString("constrainedBy"));
        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.5.1-A
     *
     * @param uri
     */
    @Test(priority = 22)
    @Parameters({"param1"})
    public void postNonRDFSource(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n22." + tl.postNonRDFSource()[1]).append('\n');
        ps.append("Request:\n");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .header("Content-Disposition", "attachment; filename=\"postNonRDFSource.txt\"")
                   .header("slug", "Post-3.5.1-A")
                   .body("TestString.")
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .post(uri)
                   .then()
                   .log().all()
                   .statusCode(201);
        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.5.1-B
     *
     * @param uri
     */
    @Test(priority = 23)
    @Parameters({"param1"})
    public void postResourceAndCheckAssociatedResource(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n23." + tl.postResourceAndCheckAssociatedResource()[1]).append('\n');
        ps.append("Request:\n");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .header("Content-Disposition", "attachment; filename=\"postResourceAndCheckAssociatedResource.txt\"")
                   .header("slug", "Post-3.5.1-B")
                   .body("TestString.")
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .post(uri)
                   .then()
                   .log().all()
                   .statusCode(201).header("Link", containsString("describedby"));

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.5.1-C
     *
     * @param uri
     */
    @Test(priority = 24)
    @Parameters({"param1"})
    public void postDigestResponseHeaderAuthentication(final String uri) throws FileNotFoundException {
        final String checksum = "md5=1234";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n24." + tl.postDigestResponseHeaderAuthentication()[1]).append('\n');
        ps.append("Request:\n");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .header("Content-Disposition",
                           "attachment; filename=\"test1digesttext.txt\"")
                   .header("slug", "Post-3.5.1-C")
                   .body("TestString.")
                   .header("Digest", checksum)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .post(uri)
                   .then()
                   .log().all()
                   .statusCode(409);

        ps.append("-Case End- \n").close();
    }

    /**
     * 3.5.1-D
     *
     * @param uri
     */
    @Test(priority = 25)
    @Parameters({"param1"})
    public void postDigestResponseHeaderVerification(final String uri) throws FileNotFoundException {
        final String checksum = "abc=abc";
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n25." + tl.postDigestResponseHeaderVerification()[1]).append('\n');
        ps.append("Request:\n");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .header("Content-Disposition",
                           "attachment; filename=\"test1digesttext2.txt\"")
                   .header("slug", "Post-3.5.1-D")
                   .body("TestString.")
                   .header("Digest", checksum)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .post(uri)
                   .then()
                   .log().all()
                   .statusCode(400);

        ps.append("\n -Case End- \n").close();
    }
}
