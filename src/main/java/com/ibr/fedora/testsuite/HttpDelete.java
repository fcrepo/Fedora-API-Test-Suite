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

import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibr.fedora.TestSuiteGlobals;
import com.ibr.fedora.TestsLabels;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;

    public class HttpDelete {
    public String username;
    public String password;
    public TestsLabels tl = new TestsLabels();
    public static String body = "@prefix ldp: <http://www.w3.org/ns/ldp#> ."
    + "@prefix dcterms: <http://purl.org/dc/terms/> ."
    + "<> a ldp:Container, ldp:BasicContainer;"
    + "dcterms:title 'Delete class Container' ;"
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
     * 3.8.1-A
     * @param uri
     */
    @Test(priority = 42)
    @Parameters({"param1"})
    public void httpDeleteOptionsCheck(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n42." + tl.httpDeleteOptionsCheck()[1]).append("\n");
        ps.append("Request:\n");

    final String resourceOp =
    RestAssured.given()
           .auth().basic(this.username, this.password)
           .contentType("text/turtle")
           .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
           .header("slug", "Delete-3.8.1-A")
           .body(body)
           .when()
           .post(uri).asString();

    final String resourceSonOp =
    RestAssured.given()
           .auth().basic(this.username, this.password)
           .contentType("text/turtle")
           .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
           .header("slug", "Delete-3.8.1-A")
           .body(body)
           .when()
           .post(resourceOp).asString();

    final String rdf01 =
    RestAssured.given()
           .auth().basic(this.username, this.password)
           .header("Content-Disposition", "attachment; filename=\"rdf01.txt\"")
           .body("TestString.")
           .when()
           .post(resourceOp).asString();

    final String rdf02 =
    RestAssured.given()
           .auth().basic(this.username, this.password)
           .header("Content-Disposition", "attachment; filename=\"rdf02.txt\"")
           .body("TestString.")
           .when()
           .post(resourceSonOp).asString();

    final String rdf03 =
    RestAssured.given()
           .auth().basic(this.username, this.password)
           .header("Content-Disposition", "attachment; filename=\"rdf03.txt\"")
           .body("TestString.")
           .when()
           .post(resourceSonOp).asString();

    ps.append("Request method:\tDELETE\n");
    ps.append("Request URI:\t" + uri + "\n");
    ps.append("Headers:\tAccept=*/*\n");
    ps.append("Body:\n");

    // Options to resourceOp
    final Response responseOptions = RestAssured.given()
        .auth().basic(this.username, this.password)
        .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
        .log().all()
        .when()
        .options(resourceOp);

    final String allowHeader = responseOptions.getHeader("Allow");

    // Delete to resourceOp
    final Response response = RestAssured.given()
           .auth().basic(this.username, this.password)
           .when()
           .delete(resourceOp);

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
    .get(resourceOp);

    final Response resResourceSon = RestAssured.given()
    .auth().basic(this.username, this.password)
    .when()
          .get(resourceSonOp);

    final Response resRdf01 = RestAssured.given()
    .auth().basic(this.username, this.password)
    .when()
    .get(rdf01);

    final Response resRdf02 = RestAssured.given()
    .auth().basic(this.username, this.password)
    .when()
    .get(rdf02);

    final Response resRdf03 = RestAssured.given()
    .auth().basic(this.username, this.password)
    .when()
    .get(rdf03);


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
     * @param uri
     */
    @Test(priority = 43)
    @Parameters({"param1"})
    public void httpDeleteStatusCheck(final String uri) throws FileNotFoundException {
           final PrintStream ps = TestSuiteGlobals.logFile();
           ps.append("\n43." + tl.httpDeleteStatusCheck()[1]).append("\n");
           ps.append("Request:\n");

    // Create resources
    final String rootres =
    RestAssured.given()
           .auth().basic(this.username, this.password)
           .contentType("text/turtle")
           .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
           .header("slug", "Delete-3.8.1-C")
           .body(body)
           .when()
           .post(uri).asString();

    final String resourceSon =
    RestAssured.given()
            .auth().basic(this.username, this.password)
            .contentType("text/turtle")
            .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
            .header("slug", "Delete-3.8.1-C")
            .body(body)
            .when()
            .post(rootres).asString();

    final String nrdf01 =
    RestAssured.given()
           .auth().basic(this.username, this.password)
           .header("Content-Disposition", "attachment; filename=\"nrdf01.txt\"")
           .body("TestString.")
           .when()
           .post(rootres).asString();

    final String nrdf02 =
    RestAssured.given()
           .auth().basic(this.username, this.password)
           .header("Content-Disposition", "attachment; filename=\"nrdf02.txt\"")
           .body("TestString.")
           .when()
           .post(resourceSon).asString();

    final String nrdf03 =
    RestAssured.given()
           .auth().basic(this.username, this.password)
           .header("Content-Disposition", "attachment; filename=\"nrdf03.txt\"")
           .body("TestString.")
           .when()
           .post(resourceSon).asString();

    ps.append("Request method:\tDELETE\n");
    ps.append("Request URI:\t" + uri + "\n");
    ps.append("Headers:\tAccept=*/*\n");
    ps.append("Body:\n");

    // Delete root folder
    final Response response = RestAssured.given()
          .auth().basic(this.username, this.password)
          .when()
          .delete(rootres);

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
          .get(rootres);

    final Response resResourceSon = RestAssured.given()
          .auth().basic(this.username, this.password)
          .when()
          .get(resourceSon);

    final Response resRdf01 = RestAssured.given()
           .auth().basic(this.username, this.password)
           .when()
           .get(nrdf01);

    final Response resRdf02 = RestAssured.given()
           .auth().basic(this.username, this.password)
           .when()
           .get(nrdf02);

    final Response resRdf03 = RestAssured.given()
           .auth().basic(this.username, this.password)
           .when()
           .get(nrdf03);

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
     * @param uri
     */
    @Test(priority = 44)
    @Parameters({"param1"})
    public void httpDeleteStatusCheckTwo(final String uri) throws FileNotFoundException {
           final PrintStream ps = TestSuiteGlobals.logFile();
           ps.append("\n44." + tl.httpDeleteStatusCheckTwo()[1]).append("\n");
           ps.append("Request:\n");

    // Create resources
    final String rootres =
    RestAssured.given()
           .auth().basic(this.username, this.password)
           .contentType("text/turtle")
           .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
           .header("slug", "Delete-3.8.1-D")
           .body(body)
           .when()
           .post(uri).asString();

    final String resourceSon =
    RestAssured.given()
            .auth().basic(this.username, this.password)
            .contentType("text/turtle")
            .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
            .header("slug", "Delete-3.8.1-D")
            .body(body)
            .when()
            .post(rootres).asString();

    final String nrdf01 =
    RestAssured.given()
           .auth().basic(this.username, this.password)
           .header("Content-Disposition", "attachment; filename=\"nrdf01.txt\"")
           .body("TestString.")
           .when()
           .post(rootres).asString();

    final String nrdf02 =
    RestAssured.given()
           .auth().basic(this.username, this.password)
           .header("Content-Disposition", "attachment; filename=\"nrdf02.txt\"")
           .body("TestString.")
           .when()
           .post(resourceSon).asString();

    final String nrdf03 =
    RestAssured.given()
           .auth().basic(this.username, this.password)
           .header("Content-Disposition", "attachment; filename=\"nrdf03.txt\"")
           .body("TestString.")
           .when()
           .post(resourceSon).asString();

    ps.append("Request method:\tDELETE\n");
    ps.append("Request URI:\t" + uri + "\n");
    ps.append("Headers:\tAccept=*/*\n");
    ps.append("Body:\n");

    // Delete root folder
    final Response response = RestAssured.given()
          .auth().basic(this.username, this.password)
          .when()
          .delete(rootres);

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
          .get(rootres);

    final Response resResourceSon = RestAssured.given()
          .auth().basic(this.username, this.password)
          .when()
          .get(resourceSon);

    final Response resRdf01 = RestAssured.given()
           .auth().basic(this.username, this.password)
           .when()
           .get(nrdf01);

    final Response resRdf02 = RestAssured.given()
           .auth().basic(this.username, this.password)
           .when()
           .get(nrdf02);

    final Response resRdf03 = RestAssured.given()
           .auth().basic(this.username, this.password)
           .when()
           .get(nrdf03);

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
