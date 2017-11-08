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
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class HttpPut {
public TestsLabels tl = new TestsLabels();
/**
 * @param host
 */
@Test(priority = 8)
@Parameters({"param1"})
public void httpPut(final String host) throws FileNotFoundException {
    final PrintStream ps = TestSuiteGlobals.logFile();

    ps.append("\n8." + tl.httpPut()[0] + "-" + tl.httpPut()[1]).append("\n");
    ps.append("Request:\n");
    final String resource =
            RestAssured.given()
                    .header("Content-Disposition", "attachment; filename=\"postCreate.txt\"")
                    .body("TestString.")
                    .when()
                    .post(host).asString();

    RestAssured.given()
            .header("Content-Disposition", "attachment; filename=\"putUpdate.txt\"")
            .header("Link", "<http://www.w3.org/ns/ldp#RDFSource>; rel=\"type\"")
            .body("TestString2.")
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .when()
            .put(resource)
            .then()
            .log().all()
            .statusCode(409);

    ps.append("\n -Case End- \n").close();
}

/**
 * @param host
 */
@Test(priority = 9)
@Parameters({"param1"})
public void httpPutNR(final String host) throws FileNotFoundException {
    final PrintStream ps = TestSuiteGlobals.logFile();

    ps.append("\n9." + tl.httpPutNR()[0] + "-" + tl.httpPutNR()[1]).append("\n");
    ps.append("Request:\n");
    final String resource =
            RestAssured.given()
                    .header("Content-Disposition", "attachment; filename=\"postCreate.txt\"")
                    .body("TestString.")
                    .when()
                    .post(host).asString();

    RestAssured.given()
            .header("Content-Disposition", "attachment; filename=\"putUpdate.txt\"")
            .body("TestString2.")
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .when()
            .put(resource)
            .then()
            .log().all()
            .statusCode(204);

    ps.append("\n -Case End- \n").close();
}

/**
 * @param host
 */
@Test(priority = 10)
@Parameters({"param1"})
public void putDigestResponseHeaderAuthentication(final String host) throws FileNotFoundException {
    final String checksum = "sha1=cb1a576f22e8e3e110611b616e3e2f5ce9bdb941";
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n10." + tl.putDigestResponseHeaderAuthentication()[0] + "-" +
    tl.putDigestResponseHeaderAuthentication()[1]).append("\n");
    ps.append("Request:\n");
    final String resource =
            RestAssured.given()
                    .header("Content-Disposition", "attachment; filename=\"postCreate.txt\"")
                    .body("TestString.")
                    .when()
                    .post(host).asString();

    RestAssured.given()
            .header("digest", checksum)
            .header("Content-Disposition", "attachment; filename=\"putUpdate.txt\"")
            .body("TestString2.")
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .when()
            .put(resource)
            .then()
            .log().all()
            .statusCode(409);

    ps.append("\n -Case End- \n").close();
}

/**
 * @param host
 */
@Test(priority = 11)
@Parameters({"param1"})
public void putDigestResponseHeaderVerification(final String host) throws FileNotFoundException {
    final String checksum = "abc=abc";
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n11." + tl.putDigestResponseHeaderVerification()[0] + "-" +
    tl.putDigestResponseHeaderVerification()[1]).append("\n");
    ps.append("Request:\n");
    final String resource =
            RestAssured.given()
                    .header("Content-Disposition", "attachment; filename=\"postCreate.txt\"")
                    .body("TestString.")
                    .when()
                    .post(host).asString();

    RestAssured.given()
            .header("digest", checksum)
            .header("Content-Disposition", "attachment; filename=\"putUpdate.txt\"")
            .body("TestString2.")
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .when()
            .put(resource)
            .then()
            .log().all()
            .statusCode(400);

    ps.append("\n -Case End- \n").close();
}


}