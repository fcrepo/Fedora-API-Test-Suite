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
package org.fcrepo.spec.testsuite.test;

import static org.fcrepo.spec.testsuite.test.Constants.BASIC_CONTAINER_BODY;
import static org.hamcrest.Matchers.equalTo;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;
import org.fcrepo.spec.testsuite.TestSuiteGlobals;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class HttpHead extends AbstractTest {

    /**
     * Authentication
     *
     * @param username
     * @param password
     */
    @Parameters({"param2", "param3"})
    public HttpHead(final String username, final String password) {
        super(username, password);
    }

    /**
     * 3.3-A
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void httpHeadResponseNoBody(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.3-A", "httpHeadResponseNoBody",
                                        "The HEAD method is identical to GET except that the server must not return a "
                                        + "message-body in the response, as "
                                        + "specified in [RFC7231] section 4.3.2.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-head",
                                        ps);
        final Response resource =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                       .contentType("text/turtle")
                       .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                       .header("slug", "Head-3.3-A")
                       .body(BASIC_CONTAINER_BODY)
                       .when()
                       .post(uri);
        final String locationHeader = resource.getHeader("Location");
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .head(locationHeader)
                   .then()
                   .log().all()
                   .statusCode(200).assertThat().body(equalTo(""));

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.3-B
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void httpHeadResponseDigest(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.3-B", "httpHeadResponseDigest",
                                        "The server must send the same Digest header in the response as it"
                                        +
                                        " would have sent if the request had been a GET (or omit it if it would have " +
                                        "been omitted for a GET).",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-head",
                                        ps);
        final Response resource =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                       .header("Content-Disposition", "attachment; filename=\"headerwantdigest.txt\"")
                       .header("slug", info.getId())
                       .body("TestString.")
                       .when()
                       .post(uri);

        final String locationHeader = resource.getHeader("Location");
        final Response resget =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                       .log().all()
                       .when()
                       .get(locationHeader);

        ps.append(resget.getStatusLine().toString() + "\n");
        final Headers headersGet = resget.getHeaders();
        for (Header h : headersGet) {
            ps.append(h.getName().toString() + ": ");
            ps.append(h.getValue().toString() + "\n");
        }

        final Response reshead =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                       .log().all()
                       .when()
                       .head(locationHeader);
        ps.append(reshead.getStatusLine().toString() + "\n");
        final Headers headers = reshead.getHeaders();
        for (Header h : headers) {
            ps.append(h.getName().toString() + ": ");
            ps.append(h.getValue().toString() + "\n");
        }

        ps.append("\n -Case End- \n").close();

        if (resget.getStatusCode() == 200 && reshead.getStatusCode() == 200) {

            if (resget.getHeader("Digest") == null) {
                if (reshead.getHeader("Digest") == null) {
                    Assert.assertTrue(true, "OK");
                } else {
                    Assert.assertTrue(false, "FAIL");
                }
            } else {
                if (reshead.getHeader("Digest") == null) {
                    Assert.assertTrue(false, "FAIL");
                } else {
                    Assert.assertTrue(true, "OK");
                }
            }
        } else {
            Assert.assertTrue(false, "FAIL");
        }
    }

    /**
     * 3.3-C
     *
     * @param uri
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void httpHeadResponseHeadersSameAsHttpGet(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.3-C", "httpHeadResponseHeadersSameAsHttpGet",
                                        "In other cases, The server should send the same headers in response to a " +
                                        "HEAD request "
                                        + "as it would have sent if the request had "
                                        +
                                        "been a GET, except that the payload headers (defined in [RFC7231] section " +
                                        "3.3) may be omitted.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-head",
                                        ps);
        final Response resource = RestAssured.given()
                                             .auth().basic(this.username, this.password)
                                             .contentType("text/turtle")
                                             .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                                             .header("slug", info.getId())
                                             .body(BASIC_CONTAINER_BODY)
                                             .when()
                                             .post(uri);
        final String locationHeader = resource.getHeader("Location");
        final Response resget = RestAssured.given()
                                           .auth().basic(this.username, this.password)
                                           .when()
                                           .get(locationHeader);

        ps.append(resget.getStatusLine().toString() + "\n");
        final Headers headersGet = resget.getHeaders();
        for (Header h : headersGet) {
            ps.append(h.getName().toString() + ": ");
            ps.append(h.getValue().toString() + "\n");
        }

        final List<Header> h1 = new ArrayList<>();
        for (Header h : headersGet) {
            if (!TestSuiteGlobals.checkPayloadHeader(h.getName())) {
                h1.add(h);
            }
        }

        final Response reshead = RestAssured.given()
                                            .auth().basic(this.username, this.password)
                                            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                                            .log().all()
                                            .when()
                                            .head(locationHeader);

        ps.append(reshead.getStatusLine().toString() + "\n");
        final Headers headershead = reshead.getHeaders();
        for (Header h : headershead) {
            ps.append(h.getName().toString() + ": ");
            ps.append(h.getValue().toString() + "\n");
        }
        final List<Header> h2 = new ArrayList<>();
        for (Header h : headershead) {
            if (!TestSuiteGlobals.checkPayloadHeader(h.getName())) {
                h2.add(h);
            }
        }
        // Compares if both lists have the same size
        if (h2.equals(h1)) {
            Assert.assertTrue(true, "OK");
        } else {
            Assert.assertTrue(false, "FAIL");
        }
        ps.append("\n -Case End- \n").close();
    }

}
