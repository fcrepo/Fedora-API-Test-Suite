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
import io.restassured.http.Header;
import io.restassured.response.Response;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Ldpnr {
    public TestsLabels tl = new TestsLabels();
    public String username;
    public String password;

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
     * 3.1.2.-A
     * @param host
     */
    @Test(priority = 5)
    @Parameters({"param1"})
    public void ldpnrCreationLinkType(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n5." + tl.ldpnrCreationLinkType()[1]).append("\n");
        ps.append("Request:\n");

        final Response res = RestAssured.given()
        .auth().basic(this.username, this.password)
        .header("Content-Disposition", "attachment; filename=\"sample.txt\"")
        .header("Link", "<http://www.w3.org/ns/ldp#NonRDFSource>; rel=\"type\"")
        .when()
        .post(host);
        ps.append("Request method:\tPOST\n");
        ps.append("Request URI:\t" + host + "\n");
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("Body:\n");
        ps.append("HTTP/1.1 " + res.getStatusCode() + "\n");
        for (Header h : res.getHeaders()) {
        ps.append(h.getName() + ": " + h.getValue() + "\n");
        }
        ps.append("\n" + res.asString() + "\n");

        if (res.getStatusCode() == 201) {
        final Response nonr = RestAssured.given()
        .when()
        .get(res.asString());
        boolean noHeader = false;
        for (Header h : nonr.getHeaders()) {
        if (h.getName().equals("Link") && h.getValue().contains("NonRDFSource")) {
        noHeader = false;
        break;
        } else {
        noHeader = true;
        }
    }

    if (noHeader) {
    ps.append("\nExpected a Link: rel=\"type\" http://www.w3.org/ns/ldp#NonRDFSource.\n");
    ps.append("\n -Case End- \n").close();
    throw new AssertionError("Expected a Link: rel=\"type\" http://www.w3.org/ns/ldp#NonRDFSource.");
    }
    } else {
    ps.append("\nExpected response with a 2xx range status code.\n");
    ps.append("\n -Case End- \n").close();
    throw new AssertionError("Expected response with a 2xx range status code.");
    }

    ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.1.2.-B
     * @param host
     */
    @Test(priority = 6)
    @Parameters({"param1"})
    public void ldpnrCreationWrongLinkType(final String host) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n6." + tl.ldpnrCreationWrongLinkType()[1]).append("\n");
        ps.append("Request:\n");

        final Response res = RestAssured.given()
        .auth().basic(this.username, this.password)
        .header("Content-Disposition", "attachment; filename=\"sample.txt\"")
        .header("Link", "<http://www.w3.org/ns/ldp#RDFSource>; rel=\"type\"")
        .when()
        .post(host);

        ps.append("Request method:\tPOST\n");
        ps.append("Request URI:\t" + host + "\n");
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("Body:\n");

        ps.append("HTTP/1.1 " + res.getStatusCode() + "\n");
        for (Header h : res.getHeaders()) {
        ps.append(h.getName() + ": " + h.getValue() + "\n");
    }

    ps.append("\n" + res.asString() + "\n");

    if (res.getStatusCode() >= 200 && res.getStatusCode() < 300) {
        ps.append("\nExpected response with a 4xx range status code.\n");
        ps.append("\n -Case End- \n").close();
        throw new AssertionError("Expected response with a 4xx range status code.");
        }

        ps.append("\n -Case End- \n").close();
        }
    }
