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

import static org.hamcrest.CoreMatchers.containsString;

import java.io.IOException;
import java.io.PrintStream;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibr.fedora.TestSuiteGlobals;
import com.ibr.fedora.TestsLabels;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.LogConfig;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;

public class HttpPatch {
public TestsLabels tl = new TestsLabels();
public String body = "PREFIX dcterms: <http://purl.org/dc/terms/>"
+ " INSERT {"
+ " <> dcterms:description \"Patch Updated Description\" ."
+ "}"
+ " WHERE { }";
public String ldpatch = "@prefix dcterms: <http://purl.org/dc/terms/>"
+ "Add {"
+ " <#> dcterms:description \"Patch LDP Updated Description\" ;"
+ "} .";
public String serverProps = "PREFIX fedora: <http://fedora.info/definitions/v4/repository#>"
+ " INSERT {"
+ " <> fedora:lastModifiedBy \"User\" ."
+ "}"
+ " WHERE { }";
public String resourceType = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
+ " PREFIX ldp: <http://www.w3.org/ns/ldp#>"
+ " INSERT {"
+ " <> rdf:type ldp:NonRDFSource ."
+ "}"
+ " WHERE { }";
/**
 * @param host
 */
@Test(priority = 26)
@Parameters({"param1"})
public void supportPatch(final String host) throws IOException {
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n26." + tl.supportPatch()[1]).append('\n');
    ps.append("Request:\n");

    final String resource =
            RestAssured.given()
                    .contentType("text/turtle")
                    .when()
                    .post(host).asString();

    RestAssured.given()
.contentType("application/sparql-update")
.config(RestAssured.config()
.encoderConfig(new EncoderConfig().encodeContentTypeAs("application/sparql-update", ContentType.TEXT))
.logConfig(new LogConfig().defaultStream(ps)))
.log().all()
.when()
.request().body(body)
.patch(resource)
.then()
.log().all()
.statusCode(204);

    ps.append("\n -Case End- \n").close();
}
/**
 * @param host
 */
@Test(priority = 27)
@Parameters({"param1"})
public void ldpPatchSupport(final String host) throws IOException {
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n27." + tl.ldpPatchSupport()[1]).append('\n');
    ps.append("Request:\n");

    final String resource =
            RestAssured.given()
                    .contentType("text/turtle")
                    .when()
                    .post(host).asString();

    RestAssured.given()
.contentType("text/ldpatch")
.config(RestAssured.config().encoderConfig(new EncoderConfig()
.encodeContentTypeAs("text/ldpatch", ContentType.TEXT))
.logConfig(new LogConfig().defaultStream(ps)))
.log().all()
.body(ldpatch)
.when()
.patch(resource)
.then()
.log().all()
.statusCode(204);

    ps.append("\n -Case End- \n").close();
}
/**
 * @param host
 */
@Test(priority = 28)
@Parameters({"param1"})
public void serverManagedPropertiesModification(final String host) throws IOException {
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n28." + tl.serverManagedPropertiesModification()[1]).append('\n');
    ps.append("Request:\n");

    final String resource =
            RestAssured.given()
                    .contentType("text/turtle")
                    .when()
                    .post(host).asString();

    RestAssured.given()
.contentType("application/sparql-update")
.config(RestAssured.config().encoderConfig(new EncoderConfig()
.encodeContentTypeAs("application/sparql-update", ContentType.TEXT))
.logConfig(new LogConfig().defaultStream(ps)))
.log().all()
.body(serverProps)
.when()
.patch(resource)
.then()
.log().all()
.statusCode(409);

    ps.append("\n -Case End- \n").close();
}
/**
 * @param host
 */
@Test(priority = 29)
@Parameters({"param1"})
public void statementNotPersistedResponseBody(final String host) throws IOException {
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n29." + tl.statementNotPersistedResponseBody()[1]).append('\n');
    ps.append("Request:\n");

    final String resource =
            RestAssured.given()
                    .contentType("text/turtle")
                    .when()
                    .post(host).asString();

    RestAssured.given()
.contentType("application/sparql-update")
.config(RestAssured.config().encoderConfig(new EncoderConfig()
.encodeContentTypeAs("application/sparql-update", ContentType.TEXT))
.logConfig(new LogConfig().defaultStream(ps)))
.log().all()
.body(serverProps)
.when()
.patch(resource)
.then()
.log().all()
.statusCode(409).body(containsString("lastModified"));

    ps.append("\n -Case End- \n").close();
}
/**
 * @param host
 */
@Test(priority = 30)
@Parameters({"param1"})
public void statementNotPersistedConstrainedBy(final String host) throws IOException {
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n30." + tl.statementNotPersistedConstrainedBy()[1]).append('\n');
    ps.append("Request:\n");

    final String resource =
            RestAssured.given()
                    .contentType("text/turtle")
                    .when()
                    .post(host).asString();

    RestAssured.given()
.contentType("application/sparql-update")
.config(RestAssured.config().encoderConfig(new EncoderConfig()
.encodeContentTypeAs("application/sparql-update", ContentType.TEXT))
.logConfig(new LogConfig().defaultStream(ps)))
.log().all()
.body(serverProps)
.when()
.patch(resource)
.then()
.log().all()
.statusCode(409).header("Link", containsString("constrainedBy"));

    ps.append("\n -Case End- \n").close();
}
/**
 * @param host
 */
@Test(priority = 31)
@Parameters({"param1"})
public void successfulPatchStatusCode(final String host) throws IOException {
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n31." + tl.successfulPatchStatusCode()[1]).append('\n');
    ps.append("Request:\n");

    final String resource =
            RestAssured.given()
                    .contentType("text/turtle")
                    .when()
                    .post(host).asString();

    ps.append("Request method:\tPATCH\n");
    ps.append("Request URI:\t" + host);
    ps.append("Headers:\tAccept=*/*\n");
    ps.append("\t\t\t\tContent-Type=application/sparql-update; charset=ISO-8859-1\n");
    ps.append("Body:\n");
    ps.append(body + "\n");

    final Response response = RestAssured.given()
.contentType("application/sparql-update")
.config(RestAssured.config().encoderConfig(new EncoderConfig()
.encodeContentTypeAs("application/sparql-update", ContentType.TEXT)))
.body(body)
.when()
.patch(resource);
    final int statusCode = response.getStatusCode();
    final Headers headers = response.getHeaders();

    ps.append("HTTP/1.1 " + statusCode + "\n");
    for (Header h : headers) {
ps.append(h.getName() + ": " + h.getValue() + "\n");
    }

    String str = "";
    boolean err = false;
    if (statusCode >= 200 && statusCode < 300) {
str = "\n" + response.asString();
    } else {
err = true;
str = "\nThe response status code is not a valid successful status code.\n\n";
str += response.asString();
    }

    ps.append(str);
    if (err) {
ps.append("\n -Case End- \n").close();
throw new AssertionError("\nThe response status code is not a valid successful status code for PATCH.");
    }

    ps.append("\n -Case End- \n").close();
}
/**
 * @param host
 */
@Test(priority = 32)
@Parameters({"param1"})
public void disallowChangeResourceType(final String host) throws IOException {
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n32." + tl.disallowChangeResourceType()[1]).append('\n');
    ps.append("Request:\n");

    final String resource =
            RestAssured.given()
                    .contentType("text/turtle")
                    .when()
                    .post(host).asString();

    RestAssured.given()
.contentType("application/sparql-update")
.config(RestAssured.config().encoderConfig(new EncoderConfig()
.encodeContentTypeAs("application/sparql-update", ContentType.TEXT))
.logConfig(new LogConfig().defaultStream(ps)))
.log().all()
.body(resourceType)
.when()
.patch(resource)
.then()
.log().all()
.statusCode(409);

    ps.append("\n -Case End- \n").close();
}
}
