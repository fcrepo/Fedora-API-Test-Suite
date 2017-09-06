package com.ibr.fedora.testsuite;

import com.ibr.fedora.TestSuiteGlobals;
import com.ibr.fedora.TestsLabels;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import org.testng.SkipException;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
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
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append(TestsLabels.httpPost()[1]).append('\n');
        RestAssured.given()
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps))).log().uri()
                .contentType("text/turtle")
                .when()
                .post(host)
                .then().log().all()
                .statusCode(201);
        ps.close();
    }

    @Test(priority = 2)
    @Parameters({"param1"})
    public void constrainByResponseHeader(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append(TestsLabels.constrainByResponseHeader()[1]).append('\n');
        RestAssured.given()
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps))).log().uri()
                .contentType("text/turtle")
                .when()
                .post(host)
                .then().log().all()
                .statusCode(201).header("Link", containsString("constrainedBy"));
        ps.close();
    }

    @Test(priority = 3)
    @Parameters({"param1"})
    public void postNonRDFSource(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append(TestsLabels.postNonRDFSource()[1]).append('\n');
        RestAssured.given()
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps))).log().uri()
                .when()
                .post(host)
                .then().log().all()
                .statusCode(201);
        ps.close();
    }

    @Test(priority = 4)
    @Parameters({"param1"})
    public void postResourceAndCheckAssociatedResource(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append(TestsLabels.postResourceAndCheckAssociatedResource()[1]).append('\n');
        RestAssured.given()
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps))).log().uri()
                .when()
                .post(host)
                .then().log().all()
                .statusCode(201).header("Link", containsString("describedby"));
        ps.close();
    }

    @Test(priority = 5)
    @Parameters({"param1", "param2"})
    public void postDigestResponseHeaderAuthentication(String host, @Optional("") String checksum) throws FileNotFoundException {
        if(!checksum.isEmpty()){
            PrintStream ps = TestSuiteGlobals.logFile();
            ps.append(TestsLabels.postDigestResponseHeaderAuthentication()[1]).append('\n');
            String resource = RestAssured.given()
                    .when()
                    .post(host).asString();

            this.resource = resource;

            RestAssured.given()
                    .header("digest", checksum)
                    .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps))).log().uri()
                    .when()
                    .post(resource)
                    .then().log().all()
                    .statusCode(201);

            ps.close();
        }else {
            throw new SkipException("Skipping this exception");
        }
    }

    @Test(priority = 6)
    @Parameters({"param1", "param2"})
    public void postDigestResponseHeaderVerification(String host, @Optional("") String checksum) throws FileNotFoundException {
        if(!this.resource.isEmpty()){
            PrintStream ps = TestSuiteGlobals.logFile();
            ps.append(TestsLabels.postDigestResponseHeaderAuthentication()[1]).append('\n');
            String resource = RestAssured.given()
                    .when()
                    .post(host).asString();

            RestAssured.given()
                    .header("digest", checksum)
                    .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps))).log().uri()
                    .when()
                    .post(resource)
                    .then().log().all()
                    .statusCode(201);

            ps.close();
        }else {
            throw new SkipException("Skipping this exception");
        }
    }
}
