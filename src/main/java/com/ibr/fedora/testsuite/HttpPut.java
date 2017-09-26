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

    @Test(priority = 7)
    @Parameters({"param1"})
    public void httpPut(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();

        ps.append("\n7."+TestsLabels.httpPut()[0]+"-"+TestsLabels.httpPut()[1]).append("\n");
        ps.append("Request:\n");
        String resource =
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
    
    @Test(priority = 8)
    @Parameters({"param1"})
    public void httpPutNR(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();

        ps.append("\n8."+TestsLabels.httpPutNR()[0]+"-"+TestsLabels.httpPutNR()[1]).append("\n");
        ps.append("Request:\n");
        String resource =
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

    @Test(priority = 9)
    @Parameters({"param1"})
    public void putDigestResponseHeaderAuthentication(String host) throws FileNotFoundException {
        String checksum = "sha1=cb1a576f22e8e3e110611b616e3e2f5ce9bdb941";
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n9."+TestsLabels.putDigestResponseHeaderAuthentication()[0]+"-"+TestsLabels.putDigestResponseHeaderAuthentication()[1]).append("\n");
        ps.append("Request:\n");
        String resource =
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

    @Test(priority = 10)
    @Parameters({"param1"})
    public void putDigestResponseHeaderVerification(String host) throws FileNotFoundException {
        String checksum = "abc=abc";
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n10."+TestsLabels.putDigestResponseHeaderVerification()[0]+"-"+ TestsLabels.putDigestResponseHeaderVerification()[1]).append("\n");
        ps.append("Request:\n");
        String resource =
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
    
    @Test(priority = 11)
    @Parameters({"param1"})
    public void httpPutExternalBody(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();

        ps.append("\n11."+TestsLabels.httpPutExternalBody()[0]+"-"+TestsLabels.httpPutExternalBody()[1]).append("\n");
        ps.append("Request:\n");
        String resource =
                RestAssured.given()
                        .header("Content-Disposition", "attachment; filename=\"postCreate.txt\"")
                        .body("TestString.")
                        .when()
                        .post(host).asString();

        RestAssured.given()
                .header("Content-Type", "message/external-body; access-type=URL; URL=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\"")
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .when()
                .put(resource)
                .then()
                .log().all()
                .statusCode(204);

        ps.append("\n -Case End- \n").close();
    }
}
