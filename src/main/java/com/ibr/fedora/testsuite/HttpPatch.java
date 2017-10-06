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

import io.restassured.RestAssured;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class HttpPatch {

/**
 * @param host
 */
@Test(priority = 25)
@Parameters({"param1"})
public void supportPatch(final String host) throws IOException {
    //final PrintStream ps = TestSuiteGlobals.logFile();
    //ps.append("\n25." + TestsLabels.supportPatch()[1]).append('\n');
    //ps.append("Request:\n");

    final String resource =
            RestAssured.given()
                    .contentType("text/turtle")
                    .when()
                    .post(host).asString();
    System.out.println(resource);

    final InputStream is = new FileInputStream("body.rdf");
    int i;
    char c;
    while ((i = is.read()) != -1) {

        // converts integer to character
        c = (char)i;

        // prints character
        System.out.println(c);
    }

    RestAssured.given()
            .contentType("application/sparql-update")
            //.config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .when()
            .body(is)
            .patch(resource)
            .then()
            .log().all()
            .statusCode(204);
    is.close();
    //ps.append("\n -Case End- \n").close();
}

/*@Test(priority = 26)
@Parameters({"param1"})
public void failedPatch(String host) throws FileNotFoundException {
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n26." + TestsLabels.failedPatch()[1]).append('\n');
    ps.append("Request:\n");

    final String resource =
            RestAssured.given()
                    .contentType("text/turtle")
                    .when()
                    .post(host).asString();

    RestAssured.given()
            .header("Content-Type", "application/sparql-update")
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .body("PREFIX fedora: <http://fedora.info/definitions/v4/repository#>\n" +
                    "INSERT {   \n" +
                    "  <> fedora:lastModified \"2017-09-25T21:03:50.431Z\"^^
                    + "<http://www.w3.org/2001/XMLSchema#dateTime> .\n" +
                    "}\n" +
                    "WHERE { }")
            .when()
            .patch(resource)
            .then()
            .log().all()
            .statusCode(409).header("Link", containsString("constrainedBy")).body(containsString("lastModified"));

    ps.append("\n -Case End- \n").close();
}*/
}
