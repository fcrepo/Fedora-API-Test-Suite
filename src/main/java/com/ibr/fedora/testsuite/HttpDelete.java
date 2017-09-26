package com.ibr.fedora.testsuite;

import com.ibr.fedora.TestSuiteGlobals;
import com.ibr.fedora.TestsLabels;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class HttpDelete {

    @Test(priority = 16)
    @Parameters({"param1"})
    public void httpDelete(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n16."+ TestsLabels.httpDelete()[1]).append("\n");
        ps.append("Request:\n");
        String resource =
                RestAssured.given()
                        .contentType("text/turtle")
                        .when()
                        .post(host).asString();
        RestAssured.given()
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .when()
                .delete(resource)
                .then()
                .log().all()
                .statusCode(204);

        ps.append("\n -Case End- \n").close();
    }
}