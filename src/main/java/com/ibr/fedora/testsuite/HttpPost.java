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
import org.testng.SkipException;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import static org.hamcrest.Matchers.containsString;

@Listeners({com.ibr.fedora.report.HtmlReporter.class, com.ibr.fedora.report.EarlReporter.class})
public class HttpPost {
    public String resource = "";

    @Test(priority = 1)
    @Parameters({"param1"})
    public void httpPost(String host) throws FileNotFoundException {
    	TestSuiteGlobals.resetFile();
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n1."+TestsLabels.httpPost()[1]).append("\n");
        ps.append("Request:\n");
        RestAssured.given()
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

    @Test(priority = 2)
    @Parameters({"param1"})
    public void constrainedByResponseHeader(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n2."+TestsLabels.constrainedByResponseHeader()[1]).append("\n");
        ps.append("Request:\n");

        String resource =
                RestAssured.given()
                        .header("Content-Disposition", "attachment; filename=\"constrainedByResponseHeader.txt\"")
                        .body("TestString.")
                        .when()
                        .post(host).asString();

        RestAssured.given()
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .contentType("text/turtle")
                .log().all()
                .when()
                .post(resource)
                .then()
                .log().all()
                .statusCode(409).header("Link", containsString("constrainedBy"));
        
        ps.append("\n -Case End- \n").close();
    }

    @Test(priority = 3)
    @Parameters({"param1"})
    public void postNonRDFSource(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n3."+TestsLabels.postNonRDFSource()[1]).append('\n');
        ps.append("Request:\n");
        RestAssured.given()
                .header("Content-Disposition", "attachment; filename=\"postNonRDFSource.txt\"")
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .when()
                .post(host)
                .then()
                .log().all()
                .statusCode(201);
        ps.append("\n -Case End- \n").close();
    }

    @Test(priority = 4)
    @Parameters({"param1"})
    public void postResourceAndCheckAssociatedResource(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n4."+TestsLabels.postResourceAndCheckAssociatedResource()[1]).append('\n');
        ps.append("Request:\n");
        RestAssured.given()
                .header("Content-Disposition", "attachment; filename=\"postResourceAndCheckAssociatedResource.txt\"")
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .when()
                .post(host)
                .then()
                .log().all()
                .statusCode(201).header("Link", containsString("describedby"));
        
        ps.append("\n -Case End- \n").close();
    }

    @Test(priority = 5)
    @Parameters({"param1"})
    public void postDigestResponseHeaderAuthentication(String host) throws FileNotFoundException {
        String checksum = "sha1=cb1a576f22e8e3e110611b616e3e2f5ce9bdb941";
        if(!checksum.isEmpty()){
            PrintStream ps = TestSuiteGlobals.logFile();
            ps.append("\n5."+TestsLabels.postDigestResponseHeaderAuthentication()[1]).append('\n');
            ps.append("Request:\n");
            String resource =
                    RestAssured.given()
                            .header("Content-Disposition", "attachment; filename=\"postDigestResponseHeaderAuthentication.txt\"")
                            .when()
                            .post(host).asString();

            this.resource = resource;

            RestAssured.given()
                    .header("digest", checksum)
                    .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                    .log().all()
                    .when()
                    .post(resource)
                    .then()
                    .log().all()
                    .statusCode(409);

            ps.append("-Case End- \n").close();
        }else {
            throw new SkipException("Skipping this exception");
        }
    }

    @Test(priority = 6)
    @Parameters({"param1"})
    public void postDigestResponseHeaderVerification(String host) throws FileNotFoundException {
        String checksum = "abc=abc";
        if(!this.resource.isEmpty()){
            PrintStream ps = TestSuiteGlobals.logFile();
            ps.append("\n6."+TestsLabels.postDigestResponseHeaderVerification()[1]).append('\n');
            ps.append("Request:\n");

            RestAssured.given()
                    .header("digest", checksum)
                    .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                    .log().all()
                    .when()
                    .post(resource)
                    .then()
                    .log().all()
                    .statusCode(400);

            ps.append("\n -Case End- \n").close();
        }else {
            throw new SkipException("Skipping this exception");
        }
    }
}
