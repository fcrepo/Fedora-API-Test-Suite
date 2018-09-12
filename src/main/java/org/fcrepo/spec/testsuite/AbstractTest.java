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
import static org.testng.AssertJUnit.assertTrue;

import java.io.PrintStream;
import java.io.StringReader;
import java.net.URI;
import java.util.Arrays;
import java.util.stream.Stream;
import javax.ws.rs.core.Link;

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
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * A crud base class for common crud functions.
 *
 * @author dbernstein
 */
public class AbstractTest {

    protected PrintStream ps;

    private final String adminUsername;
    private final String adminPassword;
    protected final String username;
    protected final String password;

    /**
     * Constructor
     *
     * @param adminUsername admin password
     * @param adminPassword admin username
     * @param username username
     * @param password password
     */
    public AbstractTest(final String adminUsername, final String adminPassword, final String username,
                        final String password) {
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.username = username;
        this.password = password;
    }

    /**
     * Constructor
     *
     * @param adminUsername admin username
     * @param adminPassword admin password
     */
    public AbstractTest(final String adminUsername, final String adminPassword) {
        this(adminPassword, adminPassword, null, null);
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
        return doPostUnverified(uri,headers, body, true);
    }

    protected Response doPostUnverified(final String uri, final Headers headers, final String body,
                                        final boolean admin) {
        return createRequest(admin).headers(headers)
                                   .body(body)
                                   .when()
                                   .post(uri);
    }

    private Response doPostUnverified(final String uri, final String body) {
        return createRequest()
                .body(body)
                .when()
                .post(uri);
    }

    protected Response doPostUnverified(final String uri, final Headers headers) {
        return createRequest().headers(headers)
                .when()
                .post(uri);
    }

    protected Response doPostUnverified(final String uri) {
        return createRequest()
                .when()
                .post(uri);
    }

    protected Response doPost(final String uri, final Headers headers) {
        final Response response = doPostUnverified(uri, headers);

        response.then().statusCode(201);

        return response;
    }

    protected Response doPost(final String uri, final String body) {
        final Response response = doPostUnverified(uri, body);

        response.then().statusCode(201);
        return response;
    }



    protected Response doPost(final String uri) {
        final Response response = doPostUnverified(uri);

        response.then().statusCode(201);

        return response;
    }

    protected Response doPost(final String uri, final Headers headers, final String body) {
        return doPost(uri, headers, body, true);
    }

    protected Response doPost(final String uri, final Headers headers, final String body, final boolean admin) {
        final Response response = doPostUnverified(uri, headers, body, admin);
        response.then().statusCode(201);
        return response;
    }

    protected Response doPutUnverified(final String uri, final Headers headers, final String body) {
        return doPutUnverified(uri, headers, body, true);
    }

    protected Response doPutUnverified(final String uri, final Headers headers, final String body,
                                       final boolean admin) {
        return createRequest(admin).headers(headers)
                                   .body(body)
                                   .when()
                                   .put(uri);
    }

    protected Response doPutUnverified(final String uri, final Headers headers) {
        return createRequest().headers(headers)
                              .when()
                              .put(uri);
    }

    protected Response doPutUnverified(final String uri) {
        return createRequest().when()
                              .put(uri);
    }

    protected Response doPut(final String uri, final Headers headers) {
        final Response response = doPutUnverified(uri, headers);

        response.then().statusCode(204);

        return response;
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
        return createRequest(true);
    }

    private RequestSpecification createRequest(final boolean admin) {
        return createRequestAuthOnly(admin)
            .config(RestAssured.config().redirect(redirectConfig().followRedirects(false))
                               .logConfig(new LogConfig().defaultStream(ps)
                                                         .enableLoggingOfRequestAndResponseIfValidationFails()))
            .log().all();
    }

    private RequestSpecification createRequest(final String slug, final String contentType) {
        return createRequest().header(SLUG, slug).contentType(contentType);
    }

    private RequestSpecification createRequestAuthOnly(final boolean admin) {
        if (admin) {
            return createRequestAuthOnly(this.adminUsername, this.adminPassword);
        } else {
            return createRequestAuthOnly(this.username, this.password);
        }
    }

    private RequestSpecification createRequestAuthOnly(final String username, final String password) {
        return RestAssured.given()
                          .auth().basic(username, password).urlEncodingEnabled(false);
    }

    protected Response doGet(final String uri, final Header header) {
        final Response response = doGetUnverified(uri, header);

        response.then().statusCode(200);

        return response;
    }

    protected Response doGet(final String uri, final boolean admin) {
        final Response response = doGetUnverified(uri, admin);

        response.then().statusCode(200);

        return response;
    }

    protected Response doGet(final String uri) {
        return doGet(uri, true);
    }

    protected Response doGetUnverified(final String uri, final Header header) {
        return createRequest().header(header).when().get(uri);
    }

    protected Response doGetUnverified(final String uri) {
        return doGetUnverified(uri,true);
    }

    protected Response doGetUnverified(final String uri, final boolean admin) {
        return createRequest(admin).when().get(uri);
    }

    protected Response doDeleteUnverified(final String uri, final boolean admin) {
        return createRequest(admin).when().delete(uri);
    }

    protected Response doDelete(final String uri) {
        return doDelete(uri, true);
    }

    protected Response doDelete(final String uri, final boolean admin) {
        final Response response = doDeleteUnverified(uri, admin);

        response.then().statusCode(204);

        return response;
    }

    protected Response doHeadUnverified(final String uri, final boolean admin) {
        return createRequest(admin).when().head(uri);
    }

    protected Response doHead(final String uri, final boolean admin) {
        final Response response = doHeadUnverified(uri, admin);

        response.then().statusCode(200);

        return response;
    }

    protected Response doHead(final String uri) {
        return doHead(uri, true);
    }

    protected Response doPatch(final String uri, final Headers headers, final String body) {
        return doPatch(uri, headers, body, true);
    }

    protected Response doPatch(final String uri, final Headers headers, final String body, final boolean admin) {
        final Response response = doPatchUnverified(uri, headers, body, admin);

        response.then().statusCode(successRange());

        return response;
    }

    protected Response doPatchUnverified(final String uri, final Headers headers, final String body) {
        return doPatchUnverified(uri, headers, body, true);
    }

    protected Response doPatchUnverified(final String uri, final Headers headers, final String body,
                                         final boolean admin) {
        return createRequest(admin).config(
            RestAssured.config().encoderConfig(
                new EncoderConfig().encodeContentTypeAs(APPLICATION_SPARQL_UPDATE, ContentType.TEXT)))
                                   .headers(headers).body(body).when().patch(uri);
    }

    protected Response doPatchUnverified(final String uri) {
        return createRequest().config(
            RestAssured.config().encoderConfig(
                new EncoderConfig().encodeContentTypeAs(APPLICATION_SPARQL_UPDATE, ContentType.TEXT)))
                              .when().patch(uri);
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
    protected Matcher<Integer> successRange() {
        return CoreMatchers.both(Matchers.greaterThanOrEqualTo(200)).and(Matchers.lessThan(300));
    }

    // Matcher for 4xx status codes
    protected Matcher<Integer> clientErrorRange() {
        return CoreMatchers.both(Matchers.greaterThanOrEqualTo(400)).and(Matchers.lessThan(500));
    }

    protected void confirmPresenceOfLinkValue(final String linkValue, final Response response) {
        final Link link = Link.valueOf(linkValue);
        Assert.assertEquals(getLinksOfRelType(response, link.getRel()).filter(l -> l.equals(link))
                                                                      .count(),
                            1,
                            "Link header with a value of " + linkValue + " must be present but is not!");
    }

    protected Stream<Link> getLinksOfRelType(final Response response, final String relType) {
        return getHeaders(response, "Link")
            // Link header may include multiple, comma-separated link values
            .flatMap(header -> Arrays.stream(header.getValue().split(",")).map(linkStr -> Link.valueOf(linkStr)))
            // Each link value may contain multiple "rel" values
            .filter(link -> link.getRels().stream().anyMatch(rel -> rel.equalsIgnoreCase(relType)));
    }

    protected Stream<URI> getLinksOfRelTypeAsUris(final Response response, final String relType) {
        return getLinksOfRelType(response, relType)
            .map(link -> link.getUri());
    }

    protected Stream<Header> getHeaders(final Response response, final String headerName) {
        return response.getHeaders()
                       .getList(headerName)
                       .stream();
    }

    protected void confirmPresenceOfConstrainedByLink(final Response response) {
        final String contrainedByUri = "http://www.w3.org/ns/ldp#constrainedBy";
        assertTrue("Response does not contain link of rel type = " + contrainedByUri,
                   getLinksOfRelType(response, contrainedByUri).count() > 0);
    }

    protected String joinLocation(final String uri, final String... subpaths) {
        final StringBuilder builder = new StringBuilder(uri);
        if (!uri.endsWith("/")) {
            builder.append('/');
        }
        builder.append(String.join("/", subpaths));
        return builder.toString();
    }
}
