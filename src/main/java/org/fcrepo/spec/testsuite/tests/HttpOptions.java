/*
 *
 *  * The contents of this file are subject to the license and copyright
 *  * detailed in the LICENSE and NOTICE files at the root of the source
 *  * tree and available online at
 *  *
 *  *     http://duracloud.org/license/
 *
 */

/**
 * @author Jorge Abrego, Fernando Cardoza
 */

package org.fcrepo.spec.testsuite.tests;

import static org.hamcrest.Matchers.containsString;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.fcrepo.spec.testsuite.TestSuiteGlobals;
import org.fcrepo.spec.testsuite.TestsLabels;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class HttpOptions {
    public String username;
    public String password;
    public TestsLabels tl = new TestsLabels();

    /**
     * Authentication
     *
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
     * 3.4-A
     *
     * @param uri
     */
    @Test(priority = 18)
    @Parameters({"param1"})
    public void httpOptionsSupport(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n18." + tl.httpOptionsSupport()[1]).append("\n");
        ps.append("Request:\n");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .options(uri)
                   .then()
                   .log().all()
                   .statusCode(200);

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.4-B
     *
     * @param uri
     */
    @Test(priority = 19)
    @Parameters({"param1"})
    public void httpOptionsSupportAllow(final String uri) throws FileNotFoundException {
        final PrintStream ps = TestSuiteGlobals.logFile();
        ps.append("\n19." + tl.httpOptionsSupportAllow()[1]).append("\n");
        ps.append("Request:\n");

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .options(uri)
                   .then()
                   .log().all()
                   .statusCode(200).header("Allow", containsString("GET"));

        ps.append("\n -Case End- \n").close();
    }
}
