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

import static org.hamcrest.Matchers.containsString;

import java.io.FileNotFoundException;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class HttpPost extends AbstractTest {
    public static String body = "@prefix ldp: <http://www.w3.org/ns/ldp#> ."
                                + "@prefix dcterms: <http://purl.org/dc/terms/> ."
                                + "<> a ldp:Container, ldp:BasicContainer;"
                                + "dcterms:title 'Post class Container' ;"
                                + "dcterms:description 'This is a test container for the Fedora API Test Suite.' . ";
    public String username;
    public String password;
    public String resource = "";
    public String binary = "https://www.w3.org/StyleSheets/TR/2016/logos/UD-watermark";

    /**
     * Authentication
     *
     * @param username
     * @param password
     */
    @Parameters({"param2", "param3"})
    public HttpPost(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * 3.5-A
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void httpPost(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.5-A", "httpPost",
                                        "Any LDPC (except Version Containers (LDPCv)) must support POST ([LDP] 4.2.3 " +
                                        "/ 5.2.3). ",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-post", ps);

        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .contentType("text/turtle")
                   .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                   .header("slug", info.getId())
                   .body(body)
                   .log().all()
                   .when()
                   .post(uri)
                   .then()
                   .log().all()
                   .statusCode(201);
        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.5-B
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void constrainedByResponseHeader(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.5-B", "constrainedByResponseHeader",
                                        "The default interaction model that will be assigned when there is no " +
                                        "explicit Link "
                                        + "header in the request must be recorded in the constraints"
                                        +
                                        " document referenced in the Link: rel=\"http://www" +
                                        ".w3.org/ns/ldp#constrainedBy\" "
                                        + "header ([LDP] 4.2.1.6 clarification).",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-post", ps);
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .contentType("text/turtle")
                   .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                   .header("slug", "Post-3.5-B")
                   .body(body)
                   .log().all()
                   .when()
                   .post(uri)
                   .then()
                   .log().all()
                   .statusCode(201).header("Link", containsString("constrainedBy"));
        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.5.1-A
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void postNonRDFSource(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.5.1-A", "postNonRDFSource",
                                        "Any LDPC must support creation of LDP-NRs on POST ([LDP] 5.2.3.3 may becomes" +
                                        " must).",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-post", ps);
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .header("Content-Disposition", "attachment; filename=\"postNonRDFSource.txt\"")
                   .header("slug", "Post-3.5.1-A")
                   .body("TestString.")
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .post(uri)
                   .then()
                   .log().all()
                   .statusCode(201);
        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.5.1-B
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void postResourceAndCheckAssociatedResource(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.5.1-B", "postResourceAndCheckAssociatedResource",
                                        "On creation of an LDP-NR, an implementation must create an associated LDP-RS" +
                                        " describing"
                                        + " that LDP-NR ([LDP] 5.2.3.12 may becomes must).",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-post", ps);
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .header("Content-Disposition", "attachment; filename=\"postResourceAndCheckAssociatedResource.txt\"")
                   .header("slug", info.getId())
                   .body("TestString.")
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .post(uri)
                   .then()
                   .log().all()
                   .statusCode(201).header("Link", containsString("describedby"));

        ps.append("\n -Case End- \n").close();
    }

    /**
     * 3.5.1-C
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void postDigestResponseHeaderAuthentication(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.5.1-C", "postDigestResponseHeaderAuthentication",
                                        "An HTTP POST request that would create an LDP-NR and includes a Digest " +
                                        "header (as described"
                                        +
                                        " in [RFC3230]) for which the instance-digest in that header does not match " +
                                        "that of the "
                                        + "new LDP-NR must be rejected with a 409 Conflict response.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-post-ldpnr", ps);
        final String checksum = "md5=1234";
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .header("Content-Disposition",
                           "attachment; filename=\"test1digesttext.txt\"")
                   .header("slug", info.getId())
                   .body("TestString.")
                   .header("Digest", checksum)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .post(uri)
                   .then()
                   .log().all()
                   .statusCode(409);

        ps.append("-Case End- \n").close();
    }

    /**
     * 3.5.1-D
     *
     * @param uri
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void postDigestResponseHeaderVerification(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.5.1-D", "postDigestResponseHeaderVerification",
                                        "An HTTP POST request that includes an unsupported Digest type (as described " +
                                        "in [RFC3230]), "
                                        + "should be rejected with a 400 Bad Request response.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-post-ldpnr", ps);
        final String checksum = "abc=abc";
        RestAssured.given()
                   .auth().basic(this.username, this.password)
                   .header("Content-Disposition",
                           "attachment; filename=\"test1digesttext2.txt\"")
                   .header("slug", info.getId())
                   .body("TestString.")
                   .header("Digest", checksum)
                   .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
                   .log().all()
                   .when()
                   .post(uri)
                   .then()
                   .log().all()
                   .statusCode(400);

        ps.append("\n -Case End- \n").close();
    }
}
