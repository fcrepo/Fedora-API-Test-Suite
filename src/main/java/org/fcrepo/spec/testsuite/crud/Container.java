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

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.apache.jena.rdf.model.ResourceFactory;
import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.fcrepo.spec.testsuite.Constants.DIRECT_CONTAINER_BODY;
import static org.testng.Assert.assertTrue;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class Container extends AbstractTest {

    /**
     * 3.1.1-A
     */
    @Test(groups = {"MUST"})
    public void createLDPC() {
        final TestInfo info = setupTest("3.1.1-A",
                                        "Implementations must support the creation and management of [LDP] Containers.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpc", ps);
        createBasicContainer(uri, info);
    }

    /**
     * 3.1.1-B
     */
    @Test(groups = {"MUST"})
    public void ldpcContainmentTriples() {
        final TestInfo info = setupTest("3.1.1-B",
                                        "ldpcContainmentTriples",
                                        "LDP Containers must distinguish [containment triples]",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpc",
                                        ps);
        final Response base = createBasicContainer(uri, info);

        final Response container = createBasicContainer(getLocation(base), "container");
        final Response containerChild = createBasicContainer(getLocation(container), "child");

        final Response direct = createDirectContainer(
                getLocation(base), DIRECT_CONTAINER_BODY.replace("%membershipResource%", getLocation(container)));
        final Response directMember = createBasicContainer(getLocation(direct), "member");

        final Response resP = doGet(getLocation(container),
                new Header("Prefer", "return=representation; include=\"http://www.w3.org/ns/ldp#PreferContainment\""));

        ps.append(resP.getStatusLine()).append("\n");
        final Headers headers = resP.getHeaders();
        for (Header h : headers) {
            ps.append(h.getName()).append(": ");
            ps.append(h.getValue()).append("\n");
        }
        final String body = resP.getBody().asString();
        ps.append(body);

        // Verify presence of expected triple in response body
        final org.apache.jena.rdf.model.Statement triple = ResourceFactory.createStatement(
                ResourceFactory.createResource(getLocation(container)),
                ResourceFactory.createProperty("http://www.w3.org/ns/ldp#contains"),
                ResourceFactory.createResource(getLocation(containerChild)));

        resP.then().body(new TripleMatcher(triple));

        // Verify absence of unexpected triple in response body
        final org.apache.jena.rdf.model.Statement badTriple = ResourceFactory.createStatement(
                ResourceFactory.createResource(getLocation(container)),
                ResourceFactory.createProperty("http://www.w3.org/ns/ldp#contains"),
                ResourceFactory.createResource(getLocation(directMember)));

        resP.then().body(new TripleMatcher(badTriple, false));
    }

    /**
     * 3.1.1-C
     */
    @Test(groups = {"MUST"})
    public void ldpcMembershipTriples() {
        final TestInfo info = setupTest("3.1.1-C",
                                        "LDP Containers must distinguish [membership] triples.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpc",
                                        ps);
        final Response base = createBasicContainer(uri, info);

        final Response container = createBasicContainer(getLocation(base), "container");
        final Response containerChild = createBasicContainer(getLocation(container), "child");

        final String directBody = DIRECT_CONTAINER_BODY
                .replace("%membershipResource%", getLocation(container))
                .replace("dcterms:hasPart", "ldp:contains");

        // Test if ldp:contains is an allowed `hasMemberRelation`
        final Response direct = createDirectContainerUnverifed(getLocation(base), directBody);
        if (direct.getStatusCode() == 409) {
            verifyConstraintHeader(direct);

        } else if (direct.statusCode() == 201) {
            final Response directMember = createBasicContainer(getLocation(direct), "member");

            // 1. Expect two ldp:contains triples for basic GET
            final Response resP = doGet(getLocation(container));

            ps.append(resP.getStatusLine()).append("\n");
            final Headers headers = resP.getHeaders();
            for (Header h : headers) {
                ps.append(h.getName()).append(": ");
                ps.append(h.getValue()).append("\n");
            }
            final String body = resP.getBody().asString();
            ps.append(body);

            // Contained triple
            final org.apache.jena.rdf.model.Statement tripleContained = ResourceFactory.createStatement(
                    ResourceFactory.createResource(getLocation(container)),
                    ResourceFactory.createProperty("http://www.w3.org/ns/ldp#contains"),
                    ResourceFactory.createResource(getLocation(containerChild)));

            // Member triple
            final org.apache.jena.rdf.model.Statement tripleMember = ResourceFactory.createStatement(
                    ResourceFactory.createResource(getLocation(container)),
                    ResourceFactory.createProperty("http://www.w3.org/ns/ldp#contains"),
                    ResourceFactory.createResource(getLocation(directMember)));

            // Both triples should be ldp:contained
            resP.then().body(new TripleMatcher(tripleMember));
            resP.then().body(new TripleMatcher(tripleContained));

            // 2. Expect one ldp:contains triple for GET : PreferContainment
            final Response responseContainment = doGet(getLocation(container),
                    new Header("Prefer",
                            "return=representation; include=\"http://www.w3.org/ns/ldp#PreferContainment\""));

            // Contained triple should be present
            responseContainment.then().body(new TripleMatcher(tripleContained));

            // Member triple should NOT be present
            responseContainment.then().body(new TripleMatcher(tripleMember, false));

            // 3. Expect one ldp:contains triple for GET : PreferMembership
            final Response responseMembership = doGet(getLocation(container),
                    new Header("Prefer",
                            "return=representation; include=\"http://www.w3.org/ns/ldp#PreferMembership\""));

            // Contained triple should NOT be present
            responseMembership.then().body(new TripleMatcher(tripleContained, false));

            // Member triple should be present
            responseMembership.then().body(new TripleMatcher(tripleMember));

        } else {
            Assert.fail("Unexpected response code: " + direct.getStatusCode());
        }
    }

    private void verifyConstraintHeader(final Response response) {
        assertTrue(getLinksOfRelType(response, "http://www.w3.org/ns/ldp#constrainedBy").count() >= 1,
                "No constrainedBy Link header found in " + response);
    }

    /**
     * 3.1.1-D
     */
    @Test(groups = {"MUST"})
    public void ldpcMinimalContainerTriples() {
        final TestInfo info = setupTest("3.1.1-D",
                                        "LDP Containers must distinguish [minimal-container] triples.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpc",
                                        ps);
        final Response base = createBasicContainer(uri, info);

        final Response container = createBasicContainer(getLocation(base), "container");
        final Response containerChild = createBasicContainer(getLocation(container), "child");

        final Response direct = createDirectContainer(
                getLocation(base), DIRECT_CONTAINER_BODY.replace("%membershipResource%", getLocation(container)));
        final Response directMember = createBasicContainer(getLocation(direct), "member");

        final Response resP = doGet(getLocation(container),
                new Header("Prefer",
                        "return=representation; include=\"http://www.w3.org/ns/ldp#PreferMinimalContainer\""));

        ps.append(resP.getStatusLine()).append("\n");
        final Headers headers = resP.getHeaders();
        for (Header h : headers) {
            ps.append(h.getName()).append(": ");
            ps.append(h.getValue()).append("\n");
        }
        final String body = resP.getBody().asString();
        ps.append(body);

        // Verify absence of contained triple in response body
        final org.apache.jena.rdf.model.Statement tripleContained = ResourceFactory.createStatement(
                ResourceFactory.createResource(getLocation(container)),
                ResourceFactory.createProperty("http://www.w3.org/ns/ldp#contains"),
                ResourceFactory.createResource(getLocation(containerChild)));

        resP.then().body(new TripleMatcher(tripleContained, false));

        // Verify absence of member triple in response body
        final org.apache.jena.rdf.model.Statement tripleMember = ResourceFactory.createStatement(
                ResourceFactory.createResource(getLocation(container)),
                ResourceFactory.createProperty("http://www.w3.org/ns/ldp#contains"),
                ResourceFactory.createResource(getLocation(directMember)));

        resP.then().body(new TripleMatcher(tripleMember, false));
    }

}
