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
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;

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
     * @param host
     */
    @Test(priority = 15)
    @Parameters({"param1"})
    public void httpHeadResponseNoBody(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n15." + tl.httpHeadResponseNoBody()[1]).append("\n");
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
                .when()
                .head(resource)
                .then()
                .log().all()
                .statusCode(200).assertThat().body(equalTo(""));

        ps.append("\n -Case End- \n").close();
    }
    /**
     * 3.3-B
     * @param host
     */
    @Test(priority = 16)
    @Parameters({"param1"})
    public void httpHeadResponseDigest(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n16." + tl.httpHeadResponseDigest()[1]).append("\n");
        ps.append("Request:\n");
        final String resource =
        RestAssured.given()
                .auth().basic(this.username, this.password)
                .header("Content-Disposition", "attachment; filename=\"headerwantdigest.txt\"")
                .body("TestString.")
                .when()
                .post(host).asString();


         final Response resget =
         RestAssured.given()
                 .auth().basic(this.username, this.password)
                 .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                 .log().all()
                 .when()
                 .get(resource);

                 ps.append(resget.getStatusLine().toString() + "\n");
                 final Headers heade = resget.getHeaders();
                 for (Header h : heade) {
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
     * @param host
     */
    @Test(priority = 17)
    @Parameters({"param1"})
    public void httpHeadResponseHeadersSameAsHttpGet(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n17." + tl.httpHeadResponseHeadersSameAsHttpGet()[1]).append("\n");
        ps.append("Request:\n");
        final String resource =
                RestAssured.given()
    .auth().basic(this.username, this.password)
                        .contentType("text/turtle")
                        .when()
                        .post(host).asString();

        final Headers headers =
                RestAssured.given()
    .auth().basic(this.username, this.password)
                        .when()
                        .get(resource).getHeaders();
        final List<Header> hl = new ArrayList<>();
        for (Header h : headers) {
            if (!TestSuiteGlobals.checkPayloadHeader(h.getName())) {
                hl.add(h);
            }
        }

        final ResponseSpecBuilder spec = new ResponseSpecBuilder();
        for (Header h : hl) {
            spec.expectHeader(h.getName(), h.getValue());
        }
        final ResponseSpecification rs = spec.build();

        RestAssured.given()
    .auth().basic(this.username, this.password)
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .when()
                .head(resource)
                .then()
                .spec(rs)
                .log().all();

        ps.append("\n -Case End- \n").close();
     }

    }