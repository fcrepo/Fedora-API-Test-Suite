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

//import static org.hamcrest.Matchers.containsString;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibr.fedora.TestSuiteGlobals;
import com.ibr.fedora.TestsLabels;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;

    public class HttpDelete {
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
     * 4.2.6
     * @param host
     */
    @Test(priority = 42)
    @Parameters({"param1"})
    public void httpDelete(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        boolean errDelete = false;
        boolean errResources = false;
        ps.append("\n40." + tl.httpDeleteResource()[1]).append("\n");
        ps.append("Request:\n");

    final String resource =
    RestAssured.given()
    .auth().basic(this.username, this.password)
    .contentType("text/turtle")
    .when()
    .post(host).asString();

    final String resourceSon =
    RestAssured.given()
    .auth().basic(this.username, this.password)
    .contentType("text/turtle")
    .when()
    .post(resource).asString();

    final String rdf01 =
    RestAssured.given()
    .auth().basic(this.username, this.password)
    .header("Content-Disposition", "attachment; filename=\"rdf01.txt\"")
    .body("TestString.")
    .when()
    .post(resource).asString();

    final String rdf02 =
    RestAssured.given()
    .auth().basic(this.username, this.password)
    .header("Content-Disposition", "attachment; filename=\"rdf02.txt\"")
    .body("TestString.")
    .when()
    .post(resourceSon).asString();

    final String rdf03 =
    RestAssured.given()
    .auth().basic(this.username, this.password)
    .header("Content-Disposition", "attachment; filename=\"rdf03.txt\"")
    .body("TestString.")
    .when()
    .post(resourceSon).asString();
        ps.append("Request method:\tDELETE\n");
        ps.append("Request URI:\t" + host + "\n");
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("Body:\n");

     final Response response = RestAssured.given()
    .auth().basic(this.username, this.password)
    .when()
    .delete(resource);

    final int statusDelete = response.getStatusCode();
    final Headers headers = response.getHeaders();
    ps.append("HTTP/1.1 " + statusDelete + "\n");
    for (Header h : headers) {
    ps.append(h.getName() + ": " + h.getValue() + "\n");
    }
    String str = "";
    if (statusDelete == 200 || statusDelete == 202 || statusDelete == 204) {
    str = "\n" + response.asString();
    } else {
    errDelete = true;
    str = "\nThe request does not return a success status, the fedora server may not support DELETE.\n\n";
        }
        ps.append(str);
    //GET deleted resources
    final Response resResource = RestAssured.given()
    .auth().basic(this.username, this.password)
    .when()
    .get(resource);

    final Response resResourceSon = RestAssured.given()
    .auth().basic(this.username, this.password)
    .when()
    .get(resourceSon);

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
    if (resResource.getStatusCode() != 410 || resResourceSon.getStatusCode() != 410 || resRdf01.getStatusCode() != 410
    || resRdf02.getStatusCode() != 410 || resRdf03.getStatusCode() != 410) {
    errResources = true;
    }
    if (errDelete) {
    ps.append("\n -Case End- \n").close();
    throw new AssertionError("\nThe request does not return a success status for the DELETE request, "
    + "the fedora server may not support DELETE.");
        } else if (errResources) {
    ps.append("\n -Case End- \n").close();
    throw new AssertionError("\nThe request failed to delete the next resources: " + resource + ", "
    + resourceSon + ", " + rdf01 + ", " + rdf02 + ", " + rdf03 + ", the fedora server may not support DELETE.");
        }
        ps.append("\n -Case End- \n").close();
    }

}