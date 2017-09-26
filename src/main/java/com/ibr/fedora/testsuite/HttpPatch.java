package com.ibr.fedora.testsuite;

import com.ibr.fedora.TestSuiteGlobals;
import com.ibr.fedora.TestsLabels;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class HttpPatch {

    @Test(priority = 25)
    @Parameters({"param1"})
    public void supportPatch(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n25."+ TestsLabels.supportPatch()[1]).append('\n');
        ps.append("Request:\n");

        String resource =
                RestAssured.given()
                        .contentType("text/turtle")
                        .when()
                        .post(host).asString();
        System.out.println(resource);
        RestAssured.given()
                .header("Content-Type", "application/sparql-update")
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .body("PREFIX dc: <http://purl.org/dc/elements/1.1/> INSERT { <> dc:title \"some-resource-title\" . } WHERE { }")
                .when()
                .patch(resource)
                .then()
                .log().all()
                .statusCode(204);

        ps.append("\n -Case End- \n").close();
    }

    /*@Test(priority = 26)
    @Parameters({"param1"})
    public void failedPatch(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n26."+ TestsLabels.failedPatch()[1]).append('\n');
        ps.append("Request:\n");

        String resource =
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
                        "  <> fedora:lastModified \"2017-09-25T21:03:50.431Z\"^^<http://www.w3.org/2001/XMLSchema#dateTime> .\n" +
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
