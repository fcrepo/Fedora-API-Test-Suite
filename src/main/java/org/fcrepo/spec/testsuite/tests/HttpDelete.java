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

public class HttpDelete {
    public static String body = "@prefix ldp: <http://www.w3.org/ns/ldp#> ."
                                + "@prefix dcterms: <http://purl.org/dc/terms/> ."
                                + "<> a ldp:Container, ldp:BasicContainer;"
                                + "dcterms:title 'Delete class Container' ;"
                                + "dcterms:description 'This is a test container for the Fedora API Test Suite.' . ";
    public String username;
    public String password;
    public TestsLabels tl = new TestsLabels();

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
     * 3.8.1-A
     *
     * @param uri
     */
    @Test(priority = 42)
    @Parameters({"param1"})
    public void httpDeleteOptionsCheck(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n42." + tl.httpDeleteOptionsCheck()[1]).append("\n");
        ps.append("Request:\n");

        final Response resourceOp =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .contentType("text/turtle")
                       .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                       .header("slug", "Delete-3.8.1-A")
                       .body(body)
                       .when()
                       .post(uri);
        final String locationHeader = resourceOp.getHeader("Location");
        final Response resourceSonOp =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .contentType("text/turtle")
                       .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                       .header("slug", "Delete-3.8.1-A")
                       .body(body)
                       .when()
                       .post(locationHeader);

        final Response rdf01 =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .header("Content-Disposition", "attachment; filename=\"rdf01.txt\"")
                       .header("slug", "Delete1-3.8.1-A")
                       .body("TestString.")
                       .when()
                       .post(locationHeader);

        final String locationHeader2 = resourceSonOp.getHeader("Location");

        final Response rdf02 =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .header("Content-Disposition", "attachment; filename=\"rdf02.txt\"")
                       .header("slug", "Delete2-3.8.1-A")
                       .body("TestString.")
                       .when()
                       .post(locationHeader2);

        final Response rdf03 =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .header("Content-Disposition", "attachment; filename=\"rdf03.txt\"")
                       .header("slug", "Delete3-3.8.1-A")
                       .body("TestString.")
                       .when()
                       .post(locationHeader2);

        final String rlocationHeader1 = rdf01.getHeader("Location");
        final String rlocationHeader2 = rdf02.getHeader("Location");
        final String rlocationHeader3 = rdf03.getHeader("Location");

        ps.append("Request method:\tDELETE\n");
        ps.append("Request URI:\t" + uri + "\n");
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("Body:\n");

        // Options to resourceOp
        final Response responseOptions = RestAssured.given()
                                                    .auth().basic(this.username, this.password)
                                                    .config(RestAssured.config()
                                                                       .logConfig(new LogConfig().defaultStream(ps)))
                                                    .log().all()
                                                    .when()
                                                    .options(locationHeader);

        final String allowHeader = responseOptions.getHeader("Allow");

        // Delete to resourceOp
        final Response response = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .when()
                                             .delete(locationHeader);

        // Print headers and status
        final Headers headers = response.getHeaders();
        ps.append(response.getStatusLine().toString());

        for (Header h : headers) {
            ps.append(h.getName().toString() + ": " + h.getValue().toString() + "\n");
        }

        //GET deleted resources
        final Response resResource = RestAssured.given()
                                                .auth().basic(this.username, this.password)
                                                .when()
                                                .get(locationHeader);

        final Response resResourceSon = RestAssured.given()
                                                   .auth().basic(this.username, this.password)
                                                   .when()
                                                   .get(locationHeader2);

        final Response resRdf01 = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .when()
                                             .get(rlocationHeader1);

        final Response resRdf02 = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .when()
                                             .get(rlocationHeader2);

        final Response resRdf03 = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .when()
                                             .get(rlocationHeader3);


        if (allowHeader.contains("OPTIONS")) {
            if (resResource.getStatusCode() == 410 || resResourceSon.getStatusCode() == 410 ||
                resRdf01.getStatusCode() == 410 || resRdf02.getStatusCode() == 410 ||
                resRdf03.getStatusCode() == 410) {
                Assert.assertTrue(true, "OK");
            } else {
                Assert.assertTrue(false, "FAIL");
            }
        } else {
            if (resResource.getStatusCode() == 410 || resResourceSon.getStatusCode() == 410 ||
                resRdf01.getStatusCode() == 410 || resRdf02.getStatusCode() == 410 ||
                resRdf03.getStatusCode() == 410) {
                Assert.assertTrue(false, "FAIL");
            } else {
                Assert.assertTrue(true, "OK");
            }

        }

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.8.1-C
     *
     * @param uri
     */
    @Test(priority = 43)
    @Parameters({"param1"})
    public void httpDeleteStatusCheck(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n43." + tl.httpDeleteStatusCheck()[1]).append("\n");
        ps.append("Request:\n");

        // Create resources
        final Response rootres =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .contentType("text/turtle")
                       .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                       .header("slug", "Delete-3.8.1-C")
                       .body(body)
                       .when()
                       .post(uri);
        final String locationHeader = rootres.getHeader("Location");
        final Response resourceSon =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .contentType("text/turtle")
                       .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                       .header("slug", "Delete-3.8.1-C")
                       .body(body)
                       .when()
                       .post(locationHeader);
        final String locationHeader2 = resourceSon.getHeader("Location");
        final Response nrdf01 =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .header("Content-Disposition", "attachment; filename=\"nrdf01.txt\"")
                       .header("slug", "Delete1-3.8.1-C")
                       .body("TestString.")
                       .when()
                       .post(locationHeader);

        final Response nrdf02 =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .header("Content-Disposition", "attachment; filename=\"nrdf02.txt\"")
                       .header("slug", "Delete2-3.8.1-C")
                       .body("TestString.")
                       .when()
                       .post(locationHeader2);

        final Response nrdf03 =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .header("Content-Disposition", "attachment; filename=\"nrdf03.txt\"")
                       .header("slug", "Delete3-3.8.1-C")
                       .body("TestString.")
                       .when()
                       .post(locationHeader2);
        final String rlocationHeader1 = nrdf01.getHeader("Location");
        final String rlocationHeader2 = nrdf02.getHeader("Location");
        final String rlocationHeader3 = nrdf03.getHeader("Location");

        ps.append("Request method:\tDELETE\n");
        ps.append("Request URI:\t" + uri + "\n");
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("Body:\n");

        // Delete root folder
        final Response response = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .when()
                                             .delete(locationHeader);

        // Print headers and status
        final int statusDelete = response.getStatusCode();
        final Headers headers = response.getHeaders();
        ps.append(response.getStatusLine().toString());

        for (Header h : headers) {
            ps.append(h.getName().toString() + ": " + h.getValue().toString() + "\n");
        }

        //GET deleted resources
        final Response resResource = RestAssured.given()
                                                .auth().basic(this.username, this.password)
                                                .when()
                                                .get(locationHeader);

        final Response resResourceSon = RestAssured.given()
                                                   .auth().basic(this.username, this.password)
                                                   .when()
                                                   .get(locationHeader2);

        final Response resRdf01 = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .when()
                                             .get(rlocationHeader1);

        final Response resRdf02 = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .when()
                                             .get(rlocationHeader2);

        final Response resRdf03 = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .when()
                                             .get(rlocationHeader3);

        if (statusDelete == 200 || statusDelete == 204) {
            if (resResource.getStatusCode() == 410 || resResourceSon.getStatusCode() == 410 ||
                resRdf01.getStatusCode() == 410 || resRdf02.getStatusCode() == 410 ||
                resRdf03.getStatusCode() == 410) {
                Assert.assertTrue(true, "OK");
            } else {
                Assert.assertTrue(false, "FAIL");
            }
        } else {
            if (resResource.getStatusCode() == 410 || resResourceSon.getStatusCode() == 410 ||
                resRdf01.getStatusCode() == 410 || resRdf02.getStatusCode() == 410 ||
                resRdf03.getStatusCode() == 410) {
                Assert.assertTrue(false, "FAIL");
            } else {
                Assert.assertTrue(true, "OK");
            }
        }
    }

    /**
     * 3.8.1-D
     *
     * @param uri
     */
    @Test(priority = 44)
    @Parameters({"param1"})
    public void httpDeleteStatusCheckTwo(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n44." + tl.httpDeleteStatusCheckTwo()[1]).append("\n");
        ps.append("Request:\n");

        // Create resources
        final Response rootres =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .contentType("text/turtle")
                       .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                       .header("slug", "Delete-3.8.1-D")
                       .body(body)
                       .when()
                       .post(uri);
        final String locationHeader = rootres.getHeader("Location");
        final Response resourceSon =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .contentType("text/turtle")
                       .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                       .header("slug", "Delete-3.8.1-D")
                       .body(body)
                       .when()
                       .post(locationHeader);
        final String locationHeader2 = resourceSon.getHeader("Location");
        final Response nrdf01 =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .header("Content-Disposition", "attachment; filename=\"nrdf01.txt\"")
                       .header("slug", "Delete1-3.8.1-D")
                       .body("TestString.")
                       .when()
                       .post(locationHeader);

        final Response nrdf02 =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .header("Content-Disposition", "attachment; filename=\"nrdf02.txt\"")
                       .header("slug", "Delete2-3.8.1-D")
                       .body("TestString.")
                       .when()
                       .post(locationHeader2);

        final Response nrdf03 =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .header("Content-Disposition", "attachment; filename=\"nrdf03.txt\"")
                       .header("slug", "Delete3-3.8.1-D")
                       .body("TestString.")
                       .when()
                       .post(locationHeader2);

        final String rlocationHeader1 = nrdf01.getHeader("Location");
        final String rlocationHeader2 = nrdf02.getHeader("Location");
        final String rlocationHeader3 = nrdf03.getHeader("Location");

        ps.append("Request method:\tDELETE\n");
        ps.append("Request URI:\t" + uri + "\n");
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("Body:\n");

        // Delete root folder
        final Response response = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .when()
                                             .delete(locationHeader);

        // Print headers and status
        final int statusDelete = response.getStatusCode();
        final Headers headers = response.getHeaders();
        ps.append(response.getStatusLine().toString());

        for (Header h : headers) {
            ps.append(h.getName().toString() + ": " + h.getValue().toString() + "\n");
        }

        //GET deleted resources
        final Response resResource = RestAssured.given()
                                                .auth().basic(this.username, this.password)
                                                .when()
                                                .get(locationHeader);

        final Response resResourceSon = RestAssured.given()
                                                   .auth().basic(this.username, this.password)
                                                   .when()
                                                   .get(locationHeader2);

        final Response resRdf01 = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .when()
                                             .get(rlocationHeader1);

        final Response resRdf02 = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .when()
                                             .get(rlocationHeader2);

        final Response resRdf03 = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .when()
                                             .get(rlocationHeader3);

        final String statusdeletestring = String.valueOf(statusDelete);
        if (statusdeletestring.charAt(0) == '2') {
            if (resResource.getStatusCode() == 410 || resResourceSon.getStatusCode() == 410 ||
                resRdf01.getStatusCode() == 410 || resRdf02.getStatusCode() == 410 ||
                resRdf03.getStatusCode() == 410) {
                Assert.assertTrue(true, "OK");
            } else {
                Assert.assertTrue(false, "FAIL");
            }
        } else {
            if (resResource.getStatusCode() == 410 || resResourceSon.getStatusCode() == 410 ||
                resRdf01.getStatusCode() == 410 || resRdf02.getStatusCode() == 410 ||
                resRdf03.getStatusCode() == 410) {
                Assert.assertTrue(false, "FAIL");
            } else {
                Assert.assertTrue(true, "OK");
            }
        }
    }

}
