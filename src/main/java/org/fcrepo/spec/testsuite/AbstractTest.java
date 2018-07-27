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
package org.fcrepo.spec.testsuite;

import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.fcrepo.spec.testsuite.Constants.APPLICATION_SPARQL_UPDATE;
import static org.fcrepo.spec.testsuite.Constants.BASIC_CONTAINER_BODY;
import static org.fcrepo.spec.testsuite.Constants.BASIC_CONTAINER_LINK_HEADER;
import static org.fcrepo.spec.testsuite.Constants.SLUG;

import java.io.PrintStream;
import java.io.StringReader;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.LogConfig;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * A crud base class for common crud functions.
 *
 * @author Daniel Berntein
 */
public class AbstractTest {

    protected PrintStream ps;

    protected final String username;
    protected final String password;

    /**
     * Constructor
     *
     * @param username username
     * @param password password
     */
    public AbstractTest(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * setup
     */
    @BeforeMethod
    public void setup() {
        ps = TestSuiteGlobals.logFile();
        ps.append("************************************************\n");
        ps.append("**** Test Start ********************************\n");
        ps.append("************************************************\n");

    }

    /**
     * tearDown
     */
    @AfterMethod
    public void tearDown() {
        ps.append("\n************************************************");
        ps.append("\n**** Test End **********************************");
        ps.append("\n************************************************\n\n\n\n").close();

    }

    /**
     * A convenience method for creating TestInfo instances.
     */
    private TestInfo createTestInfo(final String id, final String title, final String description,
                                    final String specLink) {
        return new TestInfo(id, getClass(), title, description, specLink);
    }

    /**
     * A convenience method for setup boilerplate
     */
    protected TestInfo setupTest(final String id, final String title, final String description, final String specLink,
                                 final PrintStream ps) {
        final TestInfo info = createTestInfo(id, title, description, specLink);
        ps.append("Class: ");
        ps.append(info.getTestClass().getName());
        ps.append("\n");
        ps.append("Method: ");
        ps.append(info.getTitle());
        ps.append("\n");
        ps.append("Description: ");
        ps.append(info.getDescription());
        ps.append("\n");
        ps.append("Request:\n");
        return info;
    }

    protected Response createBasicContainer(final String uri, final TestInfo info) {
        return createBasicContainer(uri, info.getId());
    }

    protected Response createBasicContainer(final String uri, final TestInfo info, final String body) {
        return createBasicContainer(uri, info.getId(), body);
    }

    protected Response createBasicContainer(final String uri, final String slug) {
        return createBasicContainer(uri, slug, BASIC_CONTAINER_BODY);
    }

    protected Response createBasicContainer(final String uri, final String slug, final String body) {
        final Response response = createRequest(slug, "text/turtle")
            .header("Link", BASIC_CONTAINER_LINK_HEADER)
            .body(body)
            .when()
            .post(uri);

        response.then().statusCode(201);

        return response;
    }

    protected Response createDirectContainer(final String uri, final String body) {
        final Response response = createDirectContainerUnverifed(uri, body);

        response.then().statusCode(201);

        return response;
    }

    protected Response createDirectContainerUnverifed(final String uri, final String body) {
        final Headers headers = new Headers(
                new Header("Link", "<http://www.w3.org/ns/ldp#DirectContainer>; rel=\"type\""),
                new Header("Content-Type", "text/turtle"));
        return doPostUnverified(uri, headers, body);
    }

    protected Response doPostUnverified(final String uri, final Headers headers, final String body) {
        return createRequest().headers(headers)
                .body(body)
                .when()
                .post(uri);
    }

    private Response doPostUnverified(final String uri, final Headers headers) {
        return createRequest().headers(headers)
                .when()
                .post(uri);
    }

    private Response doPostUnverified(final String uri) {
        return createRequest()
                .when()
                .post(uri);
    }

    protected Response doPost(final String uri, final Headers headers) {
        final Response response = doPostUnverified(uri, headers);

        response.then().statusCode(201);

        return response;
    }

    protected Response doPost(final String uri) {
        final Response response = doPostUnverified(uri);

        response.then().statusCode(201);

        return response;
    }

    protected Response doPost(final String uri, final Headers headers, final String body) {
        final Response response = doPostUnverified(uri, headers, body);

        response.then().statusCode(201);

        return response;
    }

    protected Response doPutUnverified(final String uri, final Headers headers, final String body) {
        return createRequest().headers(headers)
                .body(body)
                .when()
                .put(uri);
    }

    protected Response doPut(final String uri, final Headers headers, final String body) {
        final Response response = doPutUnverified(uri, headers, body);

        response.then().statusCode(204);

        return response;
    }

    private Response doOptionsUnverified(final String uri) {
        return createRequest().when().options(uri);
    }

    protected Response doOptions(final String uri) {
        final Response response = doOptionsUnverified(uri);

        response.then().statusCode(200);

        return response;
    }


    private RequestSpecification createRequest() {
        return createRequestAuthOnly().config(RestAssured.config().redirect(redirectConfig().followRedirects(false))
                                      .logConfig(new LogConfig().defaultStream(ps)
                                                                .enableLoggingOfRequestAndResponseIfValidationFails()))
                                      .log().all();
    }

    private RequestSpecification createRequest(final String slug, final String contentType) {
        return createRequest().header(SLUG, slug).contentType(contentType);
    }

    private RequestSpecification createRequestAuthOnly() {
        return RestAssured.given()
                          .auth().basic(this.username, this.password).urlEncodingEnabled(false);
    }

    protected Response doGet(final String uri, final Header header) {
        final Response response = doGetUnverified(uri, header);

        response.then().statusCode(200);

        return response;
    }

    protected Response doGet(final String uri) {
        final Response response = doGetUnverified(uri);

        response.then().statusCode(200);

        return response;
    }

    protected Response doGetUnverified(final String uri, final Header header) {
        return createRequest().header(header).when().get(uri);
    }

    protected Response doGetUnverified(final String uri) {
        return createRequest().when().get(uri);
    }

    private Response doDeleteUnverified(final String uri) {
        return createRequest().when().delete(uri);
    }

    protected Response doDelete(final String uri) {
        final Response response = doDeleteUnverified(uri);

        response.then().statusCode(204);

        return response;
    }

    private Response doHeadUnverified(final String uri) {
        return createRequest().when().head(uri);
    }

    protected Response doHead(final String uri) {
        final Response response = doHeadUnverified(uri);

        response.then().statusCode(200);

        return response;
    }

    protected Response doPatch(final String uri, final Headers headers, final String body) {
        final Response response = doPatchUnverified(uri, headers, body);

        response.then().statusCode(successRange());

        return response;
    }

    protected Response doPatchUnverified(final String uri, final Headers headers, final String body) {
        return createRequest().config(
                RestAssured.config().encoderConfig(
                        new EncoderConfig().encodeContentTypeAs(APPLICATION_SPARQL_UPDATE, ContentType.TEXT)))
                .headers(headers).body(body).when().patch(uri);
    }

    protected String getLocation(final Response response) {
        return response.getHeader("Location");
    }

    protected String getETag(final Response response) {
        return response.getHeader("ETag");
    }

    protected class TripleMatcher<T> extends BaseMatcher<T> {

        private final Statement triple;
        private final boolean expectMatch;

        public TripleMatcher(final Statement t) {
            this(t, true);
        }

        public TripleMatcher(final Statement t, final boolean expect) {
            triple = t;
            expectMatch = expect;
        }

        @Override
        public boolean matches(final Object item) {
            final Model model = ModelFactory.createDefaultModel();
            model.read(new StringReader(item.toString()), "", "TURTLE");

            return model.contains(triple) == expectMatch;
        }

        @Override
        public void describeTo(final Description description) {
            final String msg = expectMatch ? "To contain triple: " : "Not to contain triple: ";
            description.appendText(msg).appendText(triple.toString());
        }
    }

    // Matcher for 2xx status codes
    private Matcher<Integer> successRange() {
        return CoreMatchers.both(Matchers.greaterThanOrEqualTo(200)).and(Matchers.lessThan(300));
    }

    // Matcher for 4xx status codes
    protected Matcher<Integer> clientErrorRange() {
        return CoreMatchers.both(Matchers.greaterThanOrEqualTo(400)).and(Matchers.lessThan(500));
    }

}