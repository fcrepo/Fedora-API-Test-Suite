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

import static org.hamcrest.Matchers.containsString;

public class HttpGet {

    @Test(priority = 12)
    @Parameters({"param1"})
    public void responseDescribesHeader(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n12."+ TestsLabels.responseDescribesHeader()[1]).append("\n");
        ps.append("Request:\n");
        String resource =
                RestAssured.given()
                        .header("Content-Disposition", "attachment; filename=\"responseDescribesHeader.txt\"")
                        .when()
                        .post(host).asString();
        RestAssured.given()
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .when()
                .get(resource + "/fcr:metadata")
                .then()
                .log().all()
                .statusCode(200).header("Link", containsString("describes"));

        ps.append("\n -Case End- \n").close();
    }

    @Test(priority = 13)
    @Parameters({"param1"})
    public void responsePreferenceAppliedHeader(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n13."+ TestsLabels.responsePreferenceAppliedHeader()[1]).append("\n");
        ps.append("Request:\n");
        String resource =
                RestAssured.given()
                        .contentType("text/turtle")
                        .when()
                        .post(host).asString();
        RestAssured.given()
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .header("Prefer", "return=minimal")
                .when()
                .get(resource)
                .then()
                .log().all()
                .statusCode(200).header("preference-applied", containsString("return=minimal"));

        ps.append("\n -Case End- \n").close();
    }
}
