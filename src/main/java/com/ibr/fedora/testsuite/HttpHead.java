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
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class HttpHead {
    public String username;
    public String password;
    public TestsLabels tl = new TestsLabels();
    public static String body = "@prefix ldp: <http://www.w3.org/ns/ldp#> ."
    + "@prefix dcterms: <http://purl.org/dc/terms/> ."
    + "<> a ldp:Container, ldp:BasicContainer;"
    + "dcterms:title 'Head class Container' ;"
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
     * 3.3-A
     * @param uri
     */
    @Test(priority = 15)
    @Parameters({"param1"})
    public void httpHeadResponseNoBody(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n15." + tl.httpHeadResponseNoBody()[1]).append("\n");
        ps.append("Request:\n");
        final String resource =
                RestAssured.given()
    .auth().basic(this.username, this.password)
                        .contentType("text/turtle")
                        .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                        .body(body)
                        .when()
                        .post(uri).asString();
        RestAssured.given()
    .auth().basic(this.username, this.password)
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .when()
                .head(resource)
                .then()
                .log().all()
                .statusCode(200).assertThat().body(equalTo(""));

        ps.append("\n -Case End- \n").close();
    }
    /**
     * 3.3-B
     * @param uri
     */
    @Test(priority = 16)
    @Parameters({"param1"})
    public void httpHeadResponseDigest(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n16." + tl.httpHeadResponseDigest()[1]).append("\n");
        ps.append("Request:\n");
        final String resource =
        RestAssured.given()
                .auth().basic(this.username, this.password)
                .header("Content-Disposition", "attachment; filename=\"headerwantdigest.txt\"")
                .body("TestString.")
                .when()
                .post(uri).asString();


         final Response resget =
         RestAssured.given()
                 .auth().basic(this.username, this.password)
                 .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                 .log().all()
                 .when()
                 .get(resource);

                 ps.append(resget.getStatusLine().toString() + "\n");
                 final Headers headersGet = resget.getHeaders();
                 for (Header h : headersGet) {
                      ps.append(h.getName().toString() + ": ");
                      ps.append(h.getValue().toString() + "\n");
                }

        final Response reshead =
        RestAssured.given()
                .auth().basic(this.username, this.password)
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .when()
                .head(resource);
                ps.append(reshead.getStatusLine().toString() + "\n");
                final Headers headers = reshead.getHeaders();
                for (Header h : headers) {
                     ps.append(h.getName().toString() + ": ");
                     ps.append(h.getValue().toString() + "\n");
                }

        ps.append("\n -Case End- \n").close();

        if (resget.getStatusCode() == 200 && reshead.getStatusCode() == 200) {

            if (resget.getHeader("Digest") == null) {
                if (reshead.getHeader("Digest") == null) {
                    Assert.assertTrue(true, "OK");
                } else {
                    Assert.assertTrue(false, "FAIL");
                }
            } else {
                if (reshead.getHeader("Digest") == null) {
                    Assert.assertTrue(false, "FAIL");
                } else {
                    Assert.assertTrue(true, "OK");
                }
            }
        } else {
           Assert.assertTrue(false, "FAIL");
        }
    }
    /**
     * 3.3-C
     * @param uri
     */
    @Test(priority = 17)
    @Parameters({"param1"})
    public void httpHeadResponseHeadersSameAsHttpGet(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n17." + tl.httpHeadResponseHeadersSameAsHttpGet()[1]).append("\n");
        ps.append("Request:\n");
        final String resource = RestAssured.given()
            .auth().basic(this.username, this.password)
            .contentType("text/turtle")
            .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
            .header("slug", "Head-3.3-C")
            .body(body)
            .when()
            .post(uri).asString();

        final Response resget = RestAssured.given()
            .auth().basic(this.username, this.password)
            .when()
            .get(resource);

        ps.append(resget.getStatusLine().toString() + "\n");
        final Headers headersGet = resget.getHeaders();
        for (Header h : headersGet) {
            ps.append(h.getName().toString() + ": ");
            ps.append(h.getValue().toString() + "\n");
        }

        final List<Header> h1 = new ArrayList<>();
        for (Header h : headersGet) {
            if (!TestSuiteGlobals.checkPayloadHeader(h.getName())) {
                h1.add(h);
            }
        }

     final Response reshead = RestAssured.given()
                        .auth().basic(this.username, this.password)
                        .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                        .log().all()
                        .when()
                        .head(resource);

        ps.append(reshead.getStatusLine().toString() + "\n");
        final Headers headershead = reshead.getHeaders();
              for (Header h : headershead) {
                 ps.append(h.getName().toString() + ": ");
                 ps.append(h.getValue().toString() + "\n");
               }
       final List<Header> h2 = new ArrayList<>();
              for (Header h : headershead) {
                  if (!TestSuiteGlobals.checkPayloadHeader(h.getName())) {
                      h2.add(h);
                  }
        }
        // Compares if both lists have the same size
        if (h2.equals(h1)) {
            Assert.assertTrue(true, "OK");
        } else {
             Assert.assertTrue(false, "FAIL");
        }
        ps.append("\n -Case End- \n").close();
     }

    }
