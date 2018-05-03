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
package org.fcrepo.spec.testsuite.crud;

import java.io.FileNotFoundException;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.fcrepo.spec.testsuite.TestSuiteGlobals;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class Container extends AbstractTest {
    public String pythagorasContainer = "@prefix dc: <http://purl.org/dc/terms/> . "
                                        + "@prefix foaf: <http://xmlns.com/foaf/0.1/> . "
                                        + "<> dc:title 'Pythagoras Collection'; "
                                        + "dc:abstract 'A collection of materials and facts about Pythagoras' .";
    public String personBody = "@prefix dc: <http://purl.org/dc/terms/> . "
                               + "@prefix foaf: <http://xmlns.com/foaf/0.1/> . "
                               + "<> a foaf:Person; "
                               + "foaf:name \"Pythagoras\" ; "
                               + "foaf:based_near \"Croton\" ; "
                               + "foaf:interest [ dc:title \"Geometry\" ] .";
    public String portraitContainer = "@prefix ldp: <http://www.w3.org/ns/ldp#> . "
                                      + "@prefix dcterms: <http://purl.org/dc/terms/> . "
                                      + "@prefix foaf: <http://xmlns.com/foaf/0.1/> . "
                                      + "<> a ldp:DirectContainer; "
                                      + "ldp:membershipResource <%person%>; "
                                      + "ldp:hasMemberRelation foaf:depiction; "
                                      + "dcterms:title \"Portraits of Pythagoras\" .";

    /**
     * Authentication
     *
     * @param username
     * @param password
     */
    @Parameters({"param2", "param3"})
    public Container(final String username, final String password) {
        super(username, password);
    }

    /**
     * 3.1.1-A
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void createLDPC(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.1.1-A", "createLDPC",
                                        "Implementations must support the creation and management of [LDP] Containers.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpc", ps);
        createBasicContainer(uri, info).then()
                                       .log().all()
                                       .statusCode(201);

    }

    /**
     * 3.1.1-B
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpcContainmentTriples(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.1.1-B",
                                        "ldpcContainmentTriples",
                                        "LDP Containers must distinguish [containment triples]",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpc",
                                        ps);
        final Response pythagoras = createBasicContainer(uri, info, pythagorasContainer);
        final String pythagorasLocationHeader = getLocation(pythagoras);
        final String person = createBasicContainer(pythagorasLocationHeader, "person", personBody).asString();

        final Response portraits =
            createBasicContainer(pythagorasLocationHeader, "portraits", portraitContainer.replace("%person%", person));

        final String portraitsLocationHeader = getLocation(portraits);

        createRequest("JpgPortrait", "image/jpeg")
            .when()
            .post(portraitsLocationHeader).asString();

        final Response resP = createRequest()
            .header("Prefer",
                    "return=representation; include=\"http://www" +
                    ".w3.org/ns/ldp#PreferContainment\"")
            .when()
            .get(portraitsLocationHeader);

        ps.append(resP.getStatusLine().toString() + "\n");
        final Headers headers = resP.getHeaders();
        for (Header h : headers) {
            ps.append(h.getName().toString() + ": ");
            ps.append(h.getValue().toString() + "\n");
        }
        final String body = resP.getBody().asString();
        ps.append(body);

        final boolean triple = TestSuiteGlobals.checkMembershipTriple(body);

        if (triple) {
            Assert.assertTrue(false, "FAIL");
        } else {
            if (body.contains("ldp:contains")) {
                Assert.assertTrue(true, "OK");
            }
        }
    }

    /**
     * 3.1.1-C
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpcMembershipTriples(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.1.1-C", "ldpcMembershipTriples",
                                        "LDP Containers must distinguish [membership] triples.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpc",
                                        ps);
        final Response pythagoras = createBasicContainer(uri, info.getId(), pythagorasContainer);
        final String pythagorasLocationHeader = getLocation(pythagoras);
        final String person = createBasicContainer(pythagorasLocationHeader, "person", personBody).asString();

        final Response portraits =
            createBasicContainer(pythagorasLocationHeader, "portraits", portraitContainer.replace("%person%", person));

        final String portraitsLocationHeader = getLocation(portraits);

        createRequest("JpgPortrait", "image/jpeg")
            .when()
            .post(portraitsLocationHeader).asString();

        final Response resP = createRequest().header("Prefer",
                                                     "return=representation; include=\"http://www" +
                                                     ".w3.org/ns/ldp#PreferMembership\"")
                                             .when()
                                             .get(portraitsLocationHeader);

        ps.append(resP.getStatusLine().toString() + "\n");
        final Headers headers = resP.getHeaders();
        for (Header h : headers) {
            ps.append(h.getName().toString() + ": ");
            ps.append(h.getValue().toString() + "\n");
        }
        final String body = resP.getBody().asString();
        ps.append(body);

        if (body.contains("hasMemberRelation") && body.contains("membershipResource") &&
            !body.contains("ldp:contains")) {
            Assert.assertTrue(true, "OK");
        } else {
            Assert.assertTrue(false, "FAIL");
        }
    }

    /**
     * 3.1.1-D
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void ldpcMinimalContainerTriples(final String uri) throws FileNotFoundException {
        final TestInfo info = setupTest("3.1.1-D", "ldpcMinimalContainerTriples",
                                        "LDP Containers must distinguish [minimal-container] triples.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpc",
                                        ps);
        final Response pythagoras = createBasicContainer(uri, info, pythagorasContainer);
        final String pythagorasLocationHeader = getLocation(pythagoras);

        final String person = createBasicContainer(pythagorasLocationHeader, "person", personBody).toString();

        final Response portraits =
            createBasicContainer(pythagorasLocationHeader, "portraits", portraitContainer.replace("%person%", person));
        final String portraitsLocationHeader = getLocation(portraits);

        createRequest("JpgPortrait", "image/jpeg")
            .when()
            .post(portraitsLocationHeader).asString();

        final Response resP = createRequest().header("Prefer",
                                                     "return=representation; include=\"http://www" +
                                                     ".w3.org/ns/ldp#PreferMinimalContainer\"")
                                             .when()
                                             .get(portraitsLocationHeader);

        ps.append(resP.getStatusLine().toString() + "\n");
        final Headers headers = resP.getHeaders();
        for (Header h : headers) {
            ps.append(h.getName().toString() + ": ");
            ps.append(h.getValue().toString() + "\n");
        }
        final String body = resP.getBody().asString();
        ps.append(body);

        final boolean triple = TestSuiteGlobals.checkMembershipTriple(body);

        if (!triple && !body.contains("ldp:contains")) {
            Assert.assertTrue(true, "OK");
        } else {
            Assert.assertTrue(false, "FAIL");
        }
    }

}
