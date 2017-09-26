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
