package com.ibr.fedora.testsuite;

import com.ibr.fedora.TestSuiteGlobals;
import com.ibr.fedora.TestsLabels;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import org.testng.SkipException;
import org.testng.annotations.Listeners;
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
    	TestSuiteGlobals.resetFile();
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n1."+TestsLabels.httpPost()[1]).append("\n");
        ps.append("Request:\n");
        RestAssured.given()
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .contentType("text/turtle")
                .log().all()
                .when()
                .post(host)
                .then()
                .log().all()
                .statusCode(201);
        
        ps.append("\n -Case End- \n").close();
    }

    @Test(priority = 2)
    @Parameters({"param1"})
    public void constrainByResponseHeader(String host) throws FileNotFoundException {   	
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n2."+TestsLabels.constrainByResponseHeader()[1]).append("\n");
        ps.append("Request:\n");

        String resource =
                RestAssured.given()
                        .header("Content-Disposition", "attachment; filename=\"constrainByResponseHeader.txt\"")
                        .body("TestString.")
                        .when()
                        .post(host).asString();

        RestAssured.given()
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .contentType("text/turtle")
                .log().all()
                .when()
                .post(resource)
                .then()
                .log().all()
                .statusCode(409).header("Link", containsString("constrainedBy"));
        
        ps.append("\n -Case End- \n").close();
    }

    @Test(priority = 3)
    @Parameters({"param1"})
    public void postNonRDFSource(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n3."+TestsLabels.postNonRDFSource()[1]).append('\n');
        ps.append("Request:\n");
        RestAssured.given()
                .header("Content-Disposition", "attachment; filename=\"postNonRDFSource.txt\"")
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .when()
                .post(host)
                .then()
                .log().all()
                .statusCode(201);
        ps.append("\n -Case End- \n").close();
    }

    @Test(priority = 4)
    @Parameters({"param1"})
    public void postResourceAndCheckAssociatedResource(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n4."+TestsLabels.postResourceAndCheckAssociatedResource()[1]).append('\n');
        ps.append("Request:\n");
        RestAssured.given()
                .header("Content-Disposition", "attachment; filename=\"postResourceAndCheckAssociatedResource.txt\"")
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .when()
                .post(host)
                .then()
                .log().all()
                .statusCode(201).header("Link", containsString("describedby"));
        
        ps.append("\n -Case End- \n").close();
    }

    @Test(priority = 5)
    @Parameters({"param1"})
    public void postDigestResponseHeaderAuthentication(String host) throws FileNotFoundException {
        String checksum = "sha1=cb1a576f22e8e3e110611b616e3e2f5ce9bdb941";
        if(!checksum.isEmpty()){
            PrintStream ps = TestSuiteGlobals.logFile();
            ps.append("\n5."+TestsLabels.postDigestResponseHeaderAuthentication()[1]).append('\n');
            ps.append("Request:\n");
            String resource =
                    RestAssured.given()
                            .header("Content-Disposition", "attachment; filename=\"postDigestResponseHeaderAuthentication.txt\"")
                            .when()
                            .post(host).asString();

            this.resource = resource;

            RestAssured.given()
                    .header("digest", checksum)
                    .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                    .log().all()
                    .when()
                    .post(resource)
                    .then()
                    .log().all()
                    .statusCode(409);

            ps.append("-Case End- \n").close();
        }else {
            throw new SkipException("Skipping this exception");
        }
    }

    @Test(priority = 6)
    @Parameters({"param1"})
    public void postDigestResponseHeaderVerification(String host) throws FileNotFoundException {
        String checksum = "abc=abc";
        if(!this.resource.isEmpty()){
            PrintStream ps = TestSuiteGlobals.logFile();
            ps.append("\n6."+TestsLabels.postDigestResponseHeaderVerification()[1]).append('\n');
            ps.append("Request:\n");

            RestAssured.given()
                    .header("digest", checksum)
                    .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                    .log().all()
                    .when()
                    .post(resource)
                    .then()
                    .log().all()
                    .statusCode(400);

            ps.append("\n -Case End- \n").close();
        }else {
            throw new SkipException("Skipping this exception");
        }
    }
    
    @Test(priority = 7)
    @Parameters({"param1"})
    public void httpPut(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        
        ps.append("\n7."+TestsLabels.httpPut()[1]).append("\n");
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
    
    @Test(priority = 8)
    @Parameters({"param1"})
    public void putDigestResponseHeaderAuthentication(String host) throws FileNotFoundException {
    	String checksum = "sha1=cb1a576f22e8e3e110611b616e3e2f5ce9bdb941";
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n8."+TestsLabels.putDigestResponseHeaderAuthentication()[1]).append("\n");
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
    
    @Test(priority = 9)
    @Parameters({"param1"})
    public void putDigestResponseHeaderVerification(String host) throws FileNotFoundException {
    	String checksum = "abc=abc";
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n9."+TestsLabels.putDigestResponseHeaderVerification()[1]).append("\n");
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
}
