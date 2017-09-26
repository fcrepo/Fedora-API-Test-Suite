package com.ibr.fedora.testsuite;

import com.ibr.fedora.TestSuiteGlobals;
import com.ibr.fedora.TestsLabels;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class ExternalBinaryContent {

    public String binary = "https://www.w3.org/StyleSheets/TR/2016/logos/UD-watermark";
    public String binary2 = "https://wiki.duraspace.org/download/attachments/4980737/atl.site.logo?version=3&modificationDate=1383695533307&api=v2";

    @Test(priority = 17)
    @Parameters({"param1"})
    public void postCreateExternalBinaryContent(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n17."+ TestsLabels.postCreateExternalBinaryContent()[1]).append('\n');
        ps.append("Request:\n");

        RestAssured.given()
                .header("Content-Type", "message/external-body; access-type=URL; URL=\"" + binary + "\"")
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .when()
                .post(host)
                .then()
                .log().all()
                .statusCode(201);

        ps.append("\n -Case End- \n").close();
    }

    @Test(priority = 18)
    @Parameters({"param1"})
    public void putCreateExternalBinaryContent(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n18."+ TestsLabels.putCreateExternalBinaryContent()[1]).append('\n');
        ps.append("Request:\n");

        RestAssured.given()
                .header("Content-Type", "message/external-body; access-type=URL; URL=\"" + binary + "\"")
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .when()
                .put(host)
                .then()
                .log().all()
                .statusCode(201);

        ps.append("\n -Case End- \n").close();
    }

    @Test(priority = 19)
    @Parameters({"param1"})
    public void putUpdateExternalBinaryContent(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n19."+ TestsLabels.putUpdateExternalBinaryContent()[1]).append('\n');
        ps.append("Request:\n");

        String resource =
                RestAssured.given()
                        .header("Content-Type", "message/external-body; access-type=URL; URL=\"" + binary + "\"")
                        .when()
                        .post(host).asString();
        RestAssured.given()
                .header("Content-Type", "message/external-body; access-type=URL; URL=\"" + binary2 + "\"")
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .when()
                .put(resource)
                .then()
                .log().all()
                .statusCode(204);

        ps.append("\n -Case End- \n").close();
    }

    @Test(priority = 20)
    @Parameters({"param1"})
    public void checkAcceptPostHeader(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n20."+ TestsLabels.checkAcceptPostHeader()[1]).append('\n');
        ps.append("Request:\n");

        String resource =
                RestAssured.given()
                        .header("Content-Type", "message/external-body; access-type=URL; URL=\"" + binary + "\"")
                        .when()
                        .post(host).asString();

        ps.append("Request method:\tPOST\n");
        ps.append("Request URI:\t" + host);
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("\t\t\t\tContent-Type=message/external-body; access-type=URL; URL=\"" + binary + "\"\n\n");

        Headers headers =
                RestAssured.given()
                        .when()
                        .get(resource).getHeaders();

        if(resource.indexOf("http") == 0){
            boolean isPresent = false;
            for(Header h : headers){

                if(h.getName().equals("Accept-Post")){
                    isPresent = true;
                }
            }

            if(!isPresent){
                ps.append("Accept-Post Header was not sent in the response.");
                ps.append("\n -Case End- \n").close();
                throw new AssertionError("Accept-Post header was not set in the response.");
            }
        }else {
            throw new SkipException("Skipping this exception");
        }
    }

    @Test(priority = 21)
    @Parameters({"param1"})
    public void postCheckUnsupportedMediaType(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n21."+ TestsLabels.postCheckUnsupportedMediaType()[1]).append('\n');
        ps.append("Request:\n");

        RestAssured.given()
                .header("Content-Type", "abc/abc; access-type=URL; URL=\"" + binary + "\"")
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .when()
                .post(host)
                .then()
                .log().all()
                .statusCode(415);

        ps.append("\n -Case End- \n").close();
    }

    @Test(priority = 22)
    @Parameters({"param1"})
    public void putCheckUnsupportedMediaType(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n22."+ TestsLabels.putCheckUnsupportedMediaType()[1]).append('\n');
        ps.append("Request:\n");

        RestAssured.given()
                .header("Content-Type", "abc/abc; access-type=URL; URL=\"" + binary + "\"")
                .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                .log().all()
                .when()
                .put(host)
                .then()
                .log().all()
                .statusCode(415);

        ps.append("\n -Case End- \n").close();
    }

    @Test(priority = 23)
    @Parameters({"param1"})
    public void getCheckContentLocationHeader(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n23."+ TestsLabels.getCheckContentLocationHeader()[1]).append('\n');
        ps.append("Request:\n");

        String resource =
                RestAssured.given()
                        .header("Content-Type", "message/external-body; access-type=URL; URL=\"" + binary2 + "\"")
                        .when()
                        .post(host).asString();

        ps.append("Request method:\tGET\n");
        ps.append("Request URI:\t" + host);
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("\t\t\t\tContent-Type=message/external-body; access-type=URL; URL=\"" + binary2 + "\"\n\n");

        Headers headers =
                RestAssured.given()
                        .when()
                        .get(resource).getHeaders();

        if(resource.indexOf("http") == 0){
            boolean isPresent = false;
            for(Header h : headers){

                if(h.getName().equals("Content-Location")){
                    isPresent = true;
                }
            }

            if(!isPresent){
                ps.append("Content-Location header was not sent in the response.");
                ps.append("\n -Case End- \n").close();
                throw new AssertionError("Content-Location header was not set in the response.");
            }
        }else {
            throw new SkipException("Skipping this exception");
        }

        ps.append("\n -Case End- \n").close();
    }

    @Test(priority = 24)
    @Parameters({"param1"})
    public void headCheckContentLocationHeader(String host) throws FileNotFoundException {
        PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n24."+ TestsLabels.headCheckContentLocationHeader()[1]).append('\n');
        ps.append("Request:\n");

        String resource =
                RestAssured.given()
                        .header("Content-Type", "message/external-body; access-type=URL; URL=\"" + binary2 + "\"")
                        .when()
                        .post(host).asString();

        ps.append("Request method:\tHEAD\n");
        ps.append("Request URI:\t" + host);
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("\t\t\t\tContent-Type=message/external-body; access-type=URL; URL=\"" + binary2 + "\"\n\n");

        Headers headers =
                RestAssured.given()
                        .when()
                        .head(resource).getHeaders();

        if(resource.indexOf("http") == 0){
            boolean isPresent = false;
            for(Header h : headers){

                if(h.getName().equals("Content-Location")){
                    isPresent = true;
                }
            }

            if(!isPresent){
                ps.append("Content-Location header was not sent in the response.");
                ps.append("\n -Case End- \n").close();
                throw new AssertionError("Content-Location header was not set in the response.");
            }
        }else {
            throw new SkipException("Skipping this exception");
        }

        ps.append("\n -Case End- \n").close();
    }
}
