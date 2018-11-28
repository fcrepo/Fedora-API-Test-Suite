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
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetImpl;
import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import static org.apache.jena.graph.Node.ANY;
import static org.apache.jena.graph.NodeFactory.createURI;
import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.fcrepo.spec.testsuite.Constants.DIRECT_CONTAINER_BODY;
import static org.fcrepo.spec.testsuite.Constants.INDIRECT_CONTAINER_BODY;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.io.InputStream;

/**
 * @author Jorge Abrego, Fernando Cardoza, dbernstein
 */
public class Container extends AbstractTest {

    private static final String LDP_CONTAINS_PREDICATE = "http://www.w3.org/ns/ldp#contains";
    private static final String LDP_MEMBERSHIP_RESOURCE_PREDICATE = "http://www.w3.org/ns/ldp#membershipResource";
    private static final String LDP_HAS_MEMBER_RELATION_PREDICATE = "http://www.w3.org/ns/ldp#hasMemberRelation";
    private static final String LDP_IS_MEMBER_OF_RELATION_PREDICATE = "http://www.w3.org/ns/ldp#isMemberOfRelation";
    private static final String LDP_MEMBER = "http://www.w3.org/ns/ldp#member";

    private Boolean directContainersSupported = null;
    private Boolean indirectContainersSupported = null;
    private static final String LDP_INSERTED_CONTENT_RELATION_PREDICATE =
        "http://www.w3.org/ns/ldp#insertedContentRelation";
    private static final String LDP_MEMBER_SUBJECT = "http://www.w3.org/ns/ldp#MemberSubject";
    private static final String CONTAINER_SPEC_LINK = SPEC_BASE_URL + "#ldpc";
    private static final String DIRECT_CONTAINER_SPEC_LINK = SPEC_BASE_URL + "#ldpc";
    private static final String INDIRECT_CONTAINER_SPEC_LINK = SPEC_BASE_URL + "#ldpic";

    /**
     * 3.1.1-A-1
     */
    @Test(groups = {"MUST"})
    public void createLDPC() {
        final TestInfo info = setupTest("3.1.1-A-1",
                                        "Implementations must support the creation and management of [LDP] Containers.",
                                        CONTAINER_SPEC_LINK, ps);
        createBasicContainer(uri, info);
    }

    /**
     * 3.1.1-A-2
     */
    @Test(groups = {"MAY"})
    public void createLDPDirectContainer() {
        final TestInfo info = setupTest("3.1.1-A-2",
                                        "Implementations may support the creation and management of [LDP] Direct " +
                                        "Containers",
                                        CONTAINER_SPEC_LINK, ps);
        skipIfDirectContainersNotSupported();
    }

    /**
     * 3.1.1-A-3
     */
    @Test(groups = {"MAY"})
    public void createLDPIndirectContainer() {
        final TestInfo info = setupTest("3.1.1-A-3",
                                        "Implementations may support the creation and management of [LDP] Indirect " +
                                        "Containers",
                                        CONTAINER_SPEC_LINK, ps);
        skipIfIndirectContainersNotSupported();
    }

    private void skipIfDirectContainersNotSupported() {
        if (directContainersSupported == null) {
            final String membershipResource = getLocation(doPost(uri));
            directContainersSupported =
                (successRange().matches(createDirectContainerUnverified(uri, DIRECT_CONTAINER_BODY
                    .replace("%membershipResource%", membershipResource)).getStatusCode()));
        }

        if (!directContainersSupported) {
            throw new SkipException("This implementation does not support DirectContainers");
        }
    }

    private void skipIfIndirectContainersNotSupported() {
        if (indirectContainersSupported == null) {
            final String membershipResource = getLocation(doPost(uri));
            indirectContainersSupported =
                (successRange().matches(createIndirectContainerUnverified(uri, INDIRECT_CONTAINER_BODY
                    .replace("%membershipResource%", membershipResource)).getStatusCode()));
        }

        if (!indirectContainersSupported) {
            throw new SkipException("This implementation does not support IndirectContainers");
        }
    }


    /**
     * 3.1.1-B
     */
    @Test(groups = {"MUST"})
    public void ldpcContainmentTriples() {
        final TestInfo info = setupTest("3.1.1-B",
                                        "ldpcContainmentTriples",
                                        "LDP Containers must distinguish [containment triples]",
                                        CONTAINER_SPEC_LINK,
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
        confirmPresenceOrAbsenceOfTripleInResponse(resP, getLocation(container), LDP_CONTAINS_PREDICATE,
                                                   getLocation(containerChild), true);

        // Verify absence of unexpected triple in response body
        confirmPresenceOrAbsenceOfTripleInResponse(resP, getLocation(container), LDP_CONTAINS_PREDICATE,
                                                   getLocation(directMember), false);
    }

    /**
     * 3.1.1-C
     */
    @Test(groups = {"MUST"})
    public void ldpcMembershipTriples() {
        final TestInfo info = setupTest("3.1.1-C",
                                        "LDP Containers must distinguish [membership] triples.",
                                        CONTAINER_SPEC_LINK,
                                        ps);
        final Response base = createBasicContainer(uri, info);

        final Response container = createBasicContainer(getLocation(base), "container");
        final Response containerChild = createBasicContainer(getLocation(container), "child");

        final String directBody = DIRECT_CONTAINER_BODY
                .replace("%membershipResource%", getLocation(container))
                .replace("dcterms:hasPart", "ldp:contains");

        // Test if ldp:contains is an allowed `hasMemberRelation`
        final Response direct = createDirectContainerUnverified(getLocation(base), directBody);
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
                    ResourceFactory.createProperty(LDP_CONTAINS_PREDICATE),
                    ResourceFactory.createResource(getLocation(containerChild)));

            // Member triple
            final org.apache.jena.rdf.model.Statement tripleMember = ResourceFactory.createStatement(
                    ResourceFactory.createResource(getLocation(container)),
                    ResourceFactory.createProperty(LDP_CONTAINS_PREDICATE),
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
                                        CONTAINER_SPEC_LINK,
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
        confirmPresenceOrAbsenceOfTripleInResponse(resP, getLocation(container), LDP_CONTAINS_PREDICATE,
                                                   getLocation(containerChild), false);

        confirmPresenceOrAbsenceOfTripleInResponse(resP, getLocation(container), LDP_CONTAINS_PREDICATE,
                                                   getLocation(directMember), false);
    }

    private void confirmPresenceOrAbsenceOfTripleInResponse(final Response response, final String subjectUri,
                                                            final String predicateUri, final String objectUri,
                                                            final boolean present) {

        if (!testForPresenceOfTrip(response, subjectUri, predicateUri, objectUri, present)) {
            if (present) {
                fail("Triple should have been present but wasn't.");
            } else {
                fail("Triple should not have been present but was.");
            }
        }
    }

    private boolean testForPresenceOfTrip(final Response response, final String subjectUri,
                                          final String predicateUri, final String objectUri, final boolean present) {
        // Verify absence of member triple in response body
        final org.apache.jena.rdf.model.Statement tripleMember = ResourceFactory.createStatement(
            ResourceFactory.createResource(subjectUri),
            ResourceFactory.createProperty(predicateUri),
            ResourceFactory.createResource(objectUri));
        return new TripleMatcher(tripleMember, present).matches(response.getBody().asString());
    }

    /**
     * 3.1.2-A
     */
    @Test(groups = {"MUST"})
    public void ldpDirectContainerMustAllowMembershipConstantURIToBeSet() {
        setupTest("3.1.2-A",
                  "Implementations " +
                  "MUST allow the membership constant URI to be set via the " +
                  "ldp:membershipResource property of the content RDF on container creation.",
                  DIRECT_CONTAINER_SPEC_LINK,
                  ps);
        //throw skip exception if direct containers not supported
        skipIfDirectContainersNotSupported();

        final String membershipResource = getLocation(doPost(uri));

        final String body = "@prefix ldp: <http://www.w3.org/ns/ldp#> .\n"
                            + "<> ldp:membershipResource <" + membershipResource + "> ;";
        final Response directContainer = createDirectContainer(uri, body);
        final String directContainerResource = getLocation(directContainer);
        confirmPresenceOrAbsenceOfTripleInResponse(doGet(directContainerResource), directContainerResource,
                                                   LDP_MEMBERSHIP_RESOURCE_PREDICATE, membershipResource, true);
    }

    /**
     * 3.1.2-B
     */
    @Test(groups = {"MUST"})
    public void ldpDirectContainerMustSetLdpMembershipResourceByDefault() {
        setupTest("3.1.2-B",
                  "Implementations MUST set the ldp:membershipResource by default when" +
                  " not specified on creation.",
                  DIRECT_CONTAINER_SPEC_LINK,
                  ps);
        //throw skip exception if direct containers not supported
        skipIfDirectContainersNotSupported();
        final String body = "";
        final Response directContainer = createDirectContainer(uri, body);
        final String directContainerResource = getLocation(directContainer);
        final String responseBody = doGet(getLocation(directContainer)).getBody().asString();
        try (CloseableDataset dataset = parseTriples(IOUtils.toInputStream(responseBody))) {
            final DatasetGraph graph = dataset.asDatasetGraph();
            assertTrue(graph.contains(ANY, createURI(directContainerResource),
                                      createURI(LDP_MEMBERSHIP_RESOURCE_PREDICATE), ANY));
        }
    }

    /**
     * 3.1.2-C
     */
    @Test(groups = {"SHOULD"})
    public void ldpDirectContainerMustSetLdpMembershipResourceValueToLDPCByDefault() {
        setupTest("3.1.2-C",
                  "Implementations SHOULD set the ldp:membershipResource to the LDPC " +
                  " by default when not specified on creation.",
                  DIRECT_CONTAINER_SPEC_LINK,
                  ps);
        //throw skip exception if direct containers not supported
        skipIfDirectContainersNotSupported();
        final String membershipResource = getLocation(doPost(uri));
        final String body = "";
        final Response directContainer = createDirectContainer(uri, body);
        final String directContainerResource = getLocation(directContainer);
        confirmPresenceOrAbsenceOfTripleInResponse(doGet(directContainerResource), directContainerResource,
                                                   LDP_MEMBERSHIP_RESOURCE_PREDICATE, membershipResource, true);
    }

    private CloseableDataset parseTriples(final InputStream content) {
        final Model model = createDefaultModel();
        model.read(content, "", "text/turtle");
        return new CloseableDataset(model);
    }

    private class CloseableDataset extends DatasetImpl implements AutoCloseable {
        private CloseableDataset(final Model model) {
            super(model);
        }
    }

    /**
     * 3.1.2-D
     */
    @Test(groups = {"MAY"})
    public void ldpDirectContainerMayAllowLdpMembershipToBeUpdatedByPut() {
        setupTest("3.1.2-D",
                  "Implementations may allow the membership constant URI to be updated by " +
                  "subsequent PUT requests that change the ldp:membershipResource " +
                  "property of the resource content.",
                  DIRECT_CONTAINER_SPEC_LINK,
                  ps);

        //throw skip exception if direct containers not supported
        skipIfDirectContainersNotSupported();

        final String membershipResource1 = getLocation(doPost(uri));
        final String membershipResource2 = getLocation(doPost(uri));

        final Response response = createDirectContainer(uri, DIRECT_CONTAINER_BODY
            .replace("%membershipResource%", membershipResource1));
        final String directContainerResource = getLocation(response);
        final String body = doGet(directContainerResource).getBody().asString();
        final String newBody = body.replace(membershipResource1, membershipResource2);
        final Response updateResponse =
            doPutUnverified(directContainerResource, new Headers(new Header("Content-Type", "text/turtle")), newBody);
        if (clientErrorRange().matches(updateResponse.statusCode())) {
            throw new SkipException("This implementation does not support PUT updates on ldp:membershipResource");
        }

        final Response getUpdatedResource = doGet(directContainerResource);
        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, directContainerResource,
                                   LDP_MEMBERSHIP_RESOURCE_PREDICATE, membershipResource2, true);
        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, directContainerResource,
                                                   LDP_MEMBERSHIP_RESOURCE_PREDICATE, membershipResource1, false);
    }

    /**
     * 3.1.2-E
     */
    @Test(groups = {"MAY"})
    public void ldpDirectContainerMayAllowLdpMembershipToBeUpdatedByPatch() {
        setupTest("3.1.2-E",
                  "Implementations may allow the membership constant URI to be updated by " +
                  "subsequent PATCH requests that change the ldp:membershipResource " +
                  "property of the resource content.",
                  DIRECT_CONTAINER_SPEC_LINK,
                  ps);
        //throw skip exception if direct containers not supported
        skipIfDirectContainersNotSupported();

        final String membershipResource1 = getLocation(doPost(uri));
        final String membershipResource2 = getLocation(doPost(uri));

        final Response response = createDirectContainer(uri, DIRECT_CONTAINER_BODY
            .replace("%membershipResource%", membershipResource1));
        final String directContainerResource = getLocation(response);

        final String sparqlUpdate =
            "DELETE { <> <" + LDP_MEMBERSHIP_RESOURCE_PREDICATE + "> <" + membershipResource1 + ">  } \n" +
            "INSERT { <> <" + LDP_MEMBERSHIP_RESOURCE_PREDICATE + "> <" + membershipResource2 + ">  } \n" +
            "WHERE {}";

        final Response updateResponse =
            doPatchUnverified(directContainerResource,
                              new Headers(new Header("Content-Type", "application/sparql-update")),
                              sparqlUpdate);
        if (clientErrorRange().matches(updateResponse.statusCode())) {
            throw new SkipException("This implementation does not support PATCH updates on ldp:membershipResource");
        }

        final Response getUpdatedResource = doGet(directContainerResource);
        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, directContainerResource,
                                   LDP_MEMBERSHIP_RESOURCE_PREDICATE, membershipResource2, true);
        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, directContainerResource,
                                                   LDP_MEMBERSHIP_RESOURCE_PREDICATE, membershipResource1, false);
    }

    /**
     * 3.1.2-G-1
     */
    @Test(groups = {"MUST"})
    public void ldpDirectContainerMustAllowHasMemberRelationPredicateToBeSetOnCreate() {
        setupTest("3.1.2-G-1",
                  "Implementations must allow the membership predicate  to be set " +
                  "via either the ldp:hasMemberRelation or ldp:isMemberOfRelation property " +
                  "of the content RDF on container creation, or otherwise default to an " +
                  "implementation defined value. Implementations should use the default <> " +
                  "ldp:hasMemberRelation ldp:member",
                  DIRECT_CONTAINER_SPEC_LINK,
                  ps);
        //throw skip exception if direct containers not supported
        skipIfDirectContainersNotSupported();

        final String hasMemberPredicate = "http://example.org/ldp/member";
        final String body = "<> <" + LDP_HAS_MEMBER_RELATION_PREDICATE + "> <" + hasMemberPredicate + "> ;";
        final Response directContainer = createDirectContainer(uri, body);
        final String directContainerResource = getLocation(directContainer);
        confirmPresenceOrAbsenceOfTripleInResponse(doGet(directContainerResource), directContainerResource,
                                                   LDP_HAS_MEMBER_RELATION_PREDICATE, hasMemberPredicate, true);
    }

    /**
     * 3.1.2-G-2
     */
    @Test(groups = {"MUST"})
    public void ldpDirectContainerMustAllowIsMemberOfRelationPredicateToBeSetOnCreate() {
        setupTest("3.1.2-G-2",
                  "Implementations must allow the membership predicate  to be set " +
                  "via ldp:isMemberOfRelation property " +
                  "of the content RDF on container creation, or otherwise default to an " +
                  "implementation defined value. Implementations should use the default <> " +
                  "ldp:hasMemberRelation ldp:member",
                  DIRECT_CONTAINER_SPEC_LINK,
                  ps);
        //throw skip exception if direct containers not supported
        skipIfDirectContainersNotSupported();

        final String isMemberPredicate = "http://example.org/ldp/isMemberOf";
        final String body = "<> <" + LDP_IS_MEMBER_OF_RELATION_PREDICATE + "> <" + isMemberPredicate + "> ;";
        final Response directContainer = createDirectContainer(uri, body);
        final String directContainerResource = getLocation(directContainer);
        confirmPresenceOrAbsenceOfTripleInResponse(doGet(directContainerResource), directContainerResource,
                                                   LDP_IS_MEMBER_OF_RELATION_PREDICATE, isMemberPredicate, true);
    }

    /**
     * 3.1.2-H
     */
    @Test(groups = {"MUST"})
    public void ldpDirectContainerMustAllowMembershipPredicateToBeSetByDefault() {
        setupTest("3.1.2-H",
                  "Implementations must allow the membership predicate  to be set by " +
                  "default to an implementation defined value. ",
                  DIRECT_CONTAINER_SPEC_LINK,
                  ps);
        //throw skip exception if direct containers not supported
        skipIfDirectContainersNotSupported();

        final String body = "";
        final Response directContainer = createDirectContainer(uri, body);
        final String directContainerResource = getLocation(directContainer);
        final String responseBody = doGet(getLocation(directContainer)).getBody().asString();
        try (CloseableDataset dataset = parseTriples(IOUtils.toInputStream(responseBody))) {
            final DatasetGraph graph = dataset.asDatasetGraph();
            if (!graph
                .contains(ANY, createURI(directContainerResource), createURI(LDP_HAS_MEMBER_RELATION_PREDICATE), ANY) &&
                !graph.contains(ANY, createURI(directContainerResource), createURI(LDP_IS_MEMBER_OF_RELATION_PREDICATE),
                                ANY)) {
                fail("Neither the " + LDP_HAS_MEMBER_RELATION_PREDICATE + " nor the " +
                     LDP_IS_MEMBER_OF_RELATION_PREDICATE + " predicate found");
            }
        }
    }

    /**
     * 3.1.2-I
     */
    @Test(groups = {"SHOULD"})
    public void ldpDirectContainerShouldUseLdpMemberByDefault() {
        setupTest("3.1.2-I",
                  "Implementations should use the default <> ldp:hasMemberRelation ldp:member",
                  DIRECT_CONTAINER_SPEC_LINK,
                  ps);
        //throw skip exception if direct containers not supported
        skipIfDirectContainersNotSupported();

        final String body = "";
        final Response directContainer = createDirectContainer(uri, body);
        final String directContainerResource = getLocation(directContainer);
        final Response response = doGet(getLocation(directContainer));
        confirmPresenceOrAbsenceOfTripleInResponse(response, directContainerResource, LDP_HAS_MEMBER_RELATION_PREDICATE,
                                                   LDP_MEMBER, true);
    }

    /**
     * 3.1.2-J
     */
    @Test(groups = {"MAY"})
    public void ldpDirectContainerMayAllowLdpHasMemberRelationPredicateToBeUpdatedByPut() {
        setupTest("3.1.2-J",
                  "Implementations may allow the membership predicate to be updated by " +
                  "subsequent PUT requests that change the ldp:hasMemberRelation " +
                  "property of the resource content.",
                  DIRECT_CONTAINER_SPEC_LINK,
                  ps);
        //throw skip exception if direct containers not supported
        skipIfDirectContainersNotSupported();

        final String hasMemberPredicate1 = "http://example.org/ldp/member1";
        final String hasMemberPredicate2 = "http://example.org/ldp/member2";

        final String requestBody = "<> <" + LDP_HAS_MEMBER_RELATION_PREDICATE + "> <" + hasMemberPredicate1 + "> ;\n";

        final Response response = createDirectContainer(uri, requestBody);
        final String directContainerResource = getLocation(response);
        final String body = doGet(directContainerResource).getBody().asString();
        final String newBody = body.replace(hasMemberPredicate1, hasMemberPredicate2);
        final Response updateResponse =
            doPutUnverified(directContainerResource, new Headers(new Header("Content-Type", "text/turtle")), newBody);
        if (clientErrorRange().matches(updateResponse.statusCode())) {
            throw new SkipException(
                "This implementation does not support PUT updates on " + LDP_HAS_MEMBER_RELATION_PREDICATE);
        }

        final Response getUpdatedResource = doGet(directContainerResource);
        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, directContainerResource,
                                   LDP_HAS_MEMBER_RELATION_PREDICATE, hasMemberPredicate2, true);
        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, directContainerResource,
                                                   LDP_HAS_MEMBER_RELATION_PREDICATE, hasMemberPredicate1, false);
    }

    /**
     * 3.1.2-K
     */
    @Test(groups = {"MAY"})
    public void ldpDirectContainerMayAllowLdpHasMemberRelationToBeUpdatedByPatch() {
        setupTest("3.1.2-K",
                  "Implementations may allow the membership predicate to be updated by " +
                  "subsequent PATCH requests that change the ldp:hasMemberRelation " +
                  "property of the resource content.",
                  DIRECT_CONTAINER_SPEC_LINK,
                  ps);
        //throw skip exception if direct containers not supported
        skipIfDirectContainersNotSupported();

        final String hasMemberPredicate = "http://example.org/ldp/member";
        final String requestBody = "<> <" + LDP_HAS_MEMBER_RELATION_PREDICATE + "> <" + LDP_MEMBER + "> ;\n";
        final Response response = createDirectContainer(uri, requestBody);
        final String directContainerResource = getLocation(response);

        final String sparqlUpdate =
            "DELETE { <> <" + LDP_HAS_MEMBER_RELATION_PREDICATE + "> <" + LDP_MEMBER + ">  } \n" +
            "INSERT { <> <" + LDP_HAS_MEMBER_RELATION_PREDICATE + "> <" + hasMemberPredicate + ">  } \n" +
            "WHERE {}";

        final Response updateResponse =
            doPatchUnverified(directContainerResource,
                              new Headers(new Header("Content-Type", "application/sparql-update")),
                              sparqlUpdate);
        if (clientErrorRange().matches(updateResponse.statusCode())) {
            throw new SkipException(
                "This implementation does not support PATCH updates on " + LDP_HAS_MEMBER_RELATION_PREDICATE);
        }

        final Response getUpdatedResource = doGet(directContainerResource);
        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, directContainerResource,
                                   LDP_HAS_MEMBER_RELATION_PREDICATE, hasMemberPredicate, true);

        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, directContainerResource,
                                                   LDP_HAS_MEMBER_RELATION_PREDICATE, LDP_MEMBER, false);

    }

    /**
     * 3.1.2-L
     */
    @Test(groups = {"MAY"})
    public void ldpDirectContainerMayAllowLdpIsMemberRelationPredicateToBeUpdatedByPut() {
        setupTest("3.1.2-L",
                  "Implementations may allow the membership predicate to be updated by " +
                  "subsequent PUT requests that change the ldp:isMemberOfRelation property of " +
                  "the resource content.",
                  DIRECT_CONTAINER_SPEC_LINK,
                  ps);

        //throw skip exception if direct containers not supported
        skipIfDirectContainersNotSupported();

        final String isMemberOfPredicate1 = "http://example.org/ldp/isMemberOf1";
        final String isMemberOfPredicate2 = "http://example.org/ldp/isMemberOf2";

        final String requestBody =
            "<> <" + LDP_IS_MEMBER_OF_RELATION_PREDICATE + "> <" + isMemberOfPredicate1 + "> ;\n";

        final Response response = createDirectContainer(uri, requestBody);
        final String directContainerResource = getLocation(response);
        final String body = doGet(directContainerResource).getBody().asString();

        final String newBody = body.replace(isMemberOfPredicate1, isMemberOfPredicate2);
        final Response updateResponse =
            doPutUnverified(directContainerResource, new Headers(new Header("Content-Type", "text/turtle")), newBody);
        if (clientErrorRange().matches(updateResponse.statusCode())) {
            throw new SkipException(
                "This implementation does not support PUT updates on " + LDP_IS_MEMBER_OF_RELATION_PREDICATE);
        }

        final Response getUpdatedResource = doGet(directContainerResource);
        if (!testForPresenceOfTrip(getUpdatedResource, directContainerResource,
                                   LDP_IS_MEMBER_OF_RELATION_PREDICATE, isMemberOfPredicate2, true)) {
            throw new SkipException(
                "This implementation does not support PUT updates on " + LDP_IS_MEMBER_OF_RELATION_PREDICATE);
        }

        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, directContainerResource,
                                                   LDP_IS_MEMBER_OF_RELATION_PREDICATE, isMemberOfPredicate1, false);

    }

    /**
     * 3.1.2-M
     */
    @Test(groups = {"MAY"})
    public void ldpDirectContainerMayAllowLdpIsMemberRelationToBeUpdatedByPatch() {
        setupTest("3.1.2-M",
                  "Implementations may allow the membership predicate to be updated by " +
                  "subsequent PATCH requests that change the ldp:isMemberOfRelation " +
                  "property of the resource content.",
                  DIRECT_CONTAINER_SPEC_LINK,
                  ps);

        //throw skip exception if direct containers not supported
        skipIfDirectContainersNotSupported();

        final String isMemberOfPredicate1 = "http://example.org/ldp/isMemberOf1";
        final String isMemberOfPredicate2 = "http://example.org/ldp/isMemberOf2";
        final String requestBody =
            "<> <" + LDP_IS_MEMBER_OF_RELATION_PREDICATE + "> <" + isMemberOfPredicate1 + "> ;\n";
        final Response response = createDirectContainer(uri, requestBody);
        final String directContainerResource = getLocation(response);

        final String sparqlUpdate =
            "DELETE { <> <" + LDP_IS_MEMBER_OF_RELATION_PREDICATE + "> <" + isMemberOfPredicate1 + ">  } \n" +
            "INSERT { <> <" + LDP_IS_MEMBER_OF_RELATION_PREDICATE + "> <" + isMemberOfPredicate2 + ">  } \n" +
            "WHERE {}";

        final Response updateResponse =
            doPatchUnverified(directContainerResource,
                              new Headers(new Header("Content-Type", "application/sparql-update")),
                              sparqlUpdate);
        if (clientErrorRange().matches(updateResponse.statusCode())) {
            throw new SkipException(
                "This implementation does not support PATCH updates on " + LDP_IS_MEMBER_OF_RELATION_PREDICATE);
        }

        final Response getUpdatedResource = doGet(directContainerResource);
        if (!testForPresenceOfTrip(getUpdatedResource, directContainerResource,
                                   LDP_IS_MEMBER_OF_RELATION_PREDICATE, isMemberOfPredicate2, true)) {
            throw new SkipException(
                "This implementation does not support PATCH updates on " + LDP_IS_MEMBER_OF_RELATION_PREDICATE);
        }

        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, directContainerResource,
                                                   LDP_IS_MEMBER_OF_RELATION_PREDICATE, isMemberOfPredicate1, false);

    }


    /**
     * 3.1.3-A
     */
    @Test(groups = {"MUST"})
    public void ldpIndirectContainerMustAllowMembershipConstantURIToBeSet() {
        setupTest("3.1.3-A",
                  "Implementations " +
                  "MUST allow the indirect container's membership constant URI to be set via the " +
                  "ldp:membershipResource property of the content RDF on container creation.",
                  INDIRECT_CONTAINER_SPEC_LINK,
                  ps);

        //throw skip exception if indirect containers not supported
        skipIfIndirectContainersNotSupported();

        final String membershipResource = getLocation(doPost(uri));

        final String body = "@prefix ldp: <http://www.w3.org/ns/ldp#> .\n"
                            + "<> ldp:membershipResource <" + membershipResource + "> ;";
        final Response container = createIndirectContainer(uri, body);
        final String containerResource = getLocation(container);
        confirmPresenceOrAbsenceOfTripleInResponse(doGet(containerResource), containerResource,
                                                   LDP_MEMBERSHIP_RESOURCE_PREDICATE, membershipResource, true);
    }

    /**
     * 3.1.3-B
     */
    @Test(groups = {"MUST"})
    public void ldpIndirectContainerMustSetLdpMembershipResourceByDefault() {
        setupTest("3.1.3-B",
                  "Implementations MUST set the indirect container's ldp:membershipResource by default when" +
                  " not specified on creation.",
                  INDIRECT_CONTAINER_SPEC_LINK,
                  ps);

        //throw skip exception if indirect containers not supported
        skipIfIndirectContainersNotSupported();

        final String body = "";
        final Response directContainer = createIndirectContainer(uri, body);
        final String directContainerResource = getLocation(directContainer);
        final String responseBody = doGet(getLocation(directContainer)).getBody().asString();
        try (CloseableDataset dataset = parseTriples(IOUtils.toInputStream(responseBody))) {
            final DatasetGraph graph = dataset.asDatasetGraph();
            assertTrue(graph.contains(ANY, createURI(directContainerResource),
                                      createURI(LDP_MEMBERSHIP_RESOURCE_PREDICATE), ANY));
        }
    }

    /**
     * 3.1.3-C
     */
    @Test(groups = {"SHOULD"})
    public void ldpIndirectContainerMustSetLdpMembershipResourceValueToLDPCByDefault() {
        setupTest("3.1.3-C",
                  "Implementations SHOULD set the indirect container's ldp:membershipResource to the LDPC " +
                  " by default when not specified on creation.",
                  INDIRECT_CONTAINER_SPEC_LINK,
                  ps);

        //throw skip exception if indirect containers not supported
        skipIfIndirectContainersNotSupported();

        final String membershipResource = getLocation(doPost(uri));
        final String body = "";
        final Response container = createIndirectContainer(uri, body);
        final String containerResource = getLocation(container);
        confirmPresenceOrAbsenceOfTripleInResponse(doGet(containerResource), containerResource,
                                                   LDP_MEMBERSHIP_RESOURCE_PREDICATE, membershipResource, true);
    }

    /**
     * 3.1.3-D
     */
    @Test(groups = {"MAY"})
    public void ldpIndirectContainerMayAllowLdpMembershipToBeUpdatedByPut() {
        setupTest("3.1.3-D",
                  "Implementations may allow the indirect container's membership constant URI to be updated by " +
                  "subsequent PUT requests that change the ldp:membershipResource " +
                  "property of the resource content.",
                  INDIRECT_CONTAINER_SPEC_LINK,
                  ps);

        //throw skip exception if indirect containers not supported
        skipIfIndirectContainersNotSupported();

        final String membershipResource1 = getLocation(doPost(uri));
        final String membershipResource2 = getLocation(doPost(uri));

        final Response response = createIndirectContainer(uri, INDIRECT_CONTAINER_BODY
            .replace("%membershipResource%", membershipResource1));
        final String containerResource = getLocation(response);
        final String body = doGet(containerResource).getBody().asString();
        final String newBody = body.replace(membershipResource1, membershipResource2);
        final Response updateResponse =
            doPutUnverified(containerResource, new Headers(new Header("Content-Type", "text/turtle")), newBody);
        if (clientErrorRange().matches(updateResponse.statusCode())) {
            throw new SkipException("This implementation does not support PUT updates on ldp:membershipResource");
        }

        final Response getUpdatedResource = doGet(containerResource);
        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, containerResource,
                                                   LDP_MEMBERSHIP_RESOURCE_PREDICATE, membershipResource2, true);
        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, containerResource,
                                                   LDP_MEMBERSHIP_RESOURCE_PREDICATE, membershipResource1, false);
    }

    /**
     * 3.1.3-E
     */
    @Test(groups = {"MAY"})
    public void ldpIndirectContainerMayAllowLdpMembershipToBeUpdatedByPatch() {
        setupTest("3.1.3-E",
                  "Implementations may allow the indirect container's membership constant URI to be updated by " +
                  "subsequent PATCH requests that change the ldp:membershipResource " +
                  "property of the resource content.",
                  INDIRECT_CONTAINER_SPEC_LINK,
                  ps);
        //throw skip exception if indirect containers not supported
        skipIfIndirectContainersNotSupported();

        final String membershipResource1 = getLocation(doPost(uri));
        final String membershipResource2 = getLocation(doPost(uri));

        final Response response = createIndirectContainer(uri, INDIRECT_CONTAINER_BODY
            .replace("%membershipResource%", membershipResource1));
        final String containerResource = getLocation(response);

        final String sparqlUpdate =
            "DELETE { <> <" + LDP_MEMBERSHIP_RESOURCE_PREDICATE + "> <" + membershipResource1 + ">  } \n" +
            "INSERT { <> <" + LDP_MEMBERSHIP_RESOURCE_PREDICATE + "> <" + membershipResource2 + ">  } \n" +
            "WHERE {}";

        final Response updateResponse =
            doPatchUnverified(containerResource,
                              new Headers(new Header("Content-Type", "application/sparql-update")),
                              sparqlUpdate);
        if (clientErrorRange().matches(updateResponse.statusCode())) {
            throw new SkipException("This implementation does not support PATCH updates on ldp:membershipResource");
        }

        final Response getUpdatedResource = doGet(containerResource);
        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, containerResource,
                                                   LDP_MEMBERSHIP_RESOURCE_PREDICATE, membershipResource2, true);
        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, containerResource,
                                                   LDP_MEMBERSHIP_RESOURCE_PREDICATE, membershipResource1, false);
    }

    /**
     * 3.1.3-F
     */
    @Test(groups = {"MUST"})
    public void ldpIndirectContainerMustAllowHasMemberRelationPredicateToBeSetOnCreate() {
        setupTest("3.1.3-F",
                  "Implementations must allow the membership predicate to be set on indirect containers " +
                  "via either the ldp:hasMemberRelation or ldp:isMemberOfRelation property " +
                  "of the content RDF on container creation.",
                  INDIRECT_CONTAINER_SPEC_LINK,
                  ps);
        //throw skip exception if indirect containers not supported
        skipIfIndirectContainersNotSupported();

        final String hasMemberPredicate = "http://example.org/ldp/member";
        final String body = "<> <" + LDP_HAS_MEMBER_RELATION_PREDICATE + "> <" + hasMemberPredicate + "> ;";
        final Response container = createIndirectContainer(uri, body);
        final String containerResource = getLocation(container);
        confirmPresenceOrAbsenceOfTripleInResponse(doGet(containerResource), containerResource,
                                                   LDP_HAS_MEMBER_RELATION_PREDICATE, hasMemberPredicate, true);
    }

    /**
     * 3.1.3-G
     */
    @Test(groups = {"MUST"})
    public void ldpIndirectContainerMustAllowIsMemberOfRelationPredicateToBeSetOnCreate() {
        setupTest("3.1.3-G",
                  "Implementations must allow the membership predicate to be set on indirect containers" +
                  "via ldp:isMemberOfRelation property " +
                  "of the content RDF on container creation.",
                  INDIRECT_CONTAINER_SPEC_LINK,
                  ps);
        //throw skip exception if indirect containers not supported
        skipIfIndirectContainersNotSupported();

        final String isMemberPredicate = "http://example.org/ldp/isMemberOf";
        final String body = "<> <" + LDP_IS_MEMBER_OF_RELATION_PREDICATE + "> <" + isMemberPredicate + "> ;";
        final Response container = createIndirectContainer(uri, body);
        final String containerResource = getLocation(container);
        confirmPresenceOrAbsenceOfTripleInResponse(doGet(containerResource), containerResource,
                                                   LDP_IS_MEMBER_OF_RELATION_PREDICATE, isMemberPredicate, true);
    }

    /**
     * 3.1.3-H
     */
    @Test(groups = {"MUST"})
    public void ldpIndirectContainerMustAllowMembershipPredicateToBeSetByDefault() {
        setupTest("3.1.3-H",
                  "Implementations must allow the indirect container's membership predicate to be set by " +
                  "default to an implementation defined value. ",
                  INDIRECT_CONTAINER_SPEC_LINK,
                  ps);
        //throw skip exception if indirect containers not supported
        skipIfIndirectContainersNotSupported();

        final String body = "";
        final Response container = createIndirectContainer(uri, body);
        final String containerResource = getLocation(container);
        final String responseBody = doGet(getLocation(container)).getBody().asString();
        try (CloseableDataset dataset = parseTriples(IOUtils.toInputStream(responseBody))) {
            final DatasetGraph graph = dataset.asDatasetGraph();
            if (!graph.contains(ANY, createURI(containerResource), createURI(LDP_HAS_MEMBER_RELATION_PREDICATE), ANY) &&
                !graph.contains(ANY, createURI(containerResource), createURI(LDP_IS_MEMBER_OF_RELATION_PREDICATE),
                                ANY)) {
                fail("Neither the " + LDP_HAS_MEMBER_RELATION_PREDICATE + " nor the " +
                     LDP_IS_MEMBER_OF_RELATION_PREDICATE + " predicate found");
            }
        }
    }


    /**
     * 3.1.3-I
     */
    @Test(groups = {"SHOULD"})
    public void ldpIndirectContainerShouldUseLdpMemberByDefault() {
        setupTest("3.1.3-I",
                  "Implementations should use the default <> ldp:hasMemberRelation ldp:member",
                  INDIRECT_CONTAINER_SPEC_LINK,
                  ps);
        //throw skip exception if indirect containers not supported
        skipIfIndirectContainersNotSupported();

        final String body = "";
        final Response container = createIndirectContainer(uri, body);
        final String containerResource = getLocation(container);
        final Response response = doGet(getLocation(container));
        confirmPresenceOrAbsenceOfTripleInResponse(response, containerResource, LDP_HAS_MEMBER_RELATION_PREDICATE,
                                                   LDP_MEMBER, true);
    }

    /**
     * 3.1.3-J
     */
    @Test(groups = {"MAY"})
    public void ldpIndirectContainerMayAllowLdpHasMemberRelationPredicateToBeUpdatedByPut() {
        setupTest("3.1.3-J",
                  "Implementations may allow the membership predicate to be updated by " +
                  "subsequent PUT requests that change the ldp:hasMemberRelation " +
                  "property of the resource content.",
                  INDIRECT_CONTAINER_SPEC_LINK,
                  ps);
        //throw skip exception if indirect containers not supported
        skipIfIndirectContainersNotSupported();

        final String hasMemberPredicate1 = "http://example.org/ldp/member1";
        final String hasMemberPredicate2 = "http://example.org/ldp/member2";

        final String requestBody = "<> <" + LDP_HAS_MEMBER_RELATION_PREDICATE + "> <" + hasMemberPredicate1 + "> ;\n";

        final Response response = createIndirectContainer(uri, requestBody);
        final String containerResource = getLocation(response);
        final String body = doGet(containerResource).getBody().asString();
        final String newBody = body.replace(hasMemberPredicate1, hasMemberPredicate2);
        final Response updateResponse =
            doPutUnverified(containerResource, new Headers(new Header("Content-Type", "text/turtle")), newBody);
        if (clientErrorRange().matches(updateResponse.statusCode())) {
            throw new SkipException(
                "This implementation does not support PUT updates on " + LDP_HAS_MEMBER_RELATION_PREDICATE);
        }

        final Response getUpdatedResource = doGet(containerResource);
        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, containerResource,
                                                   LDP_HAS_MEMBER_RELATION_PREDICATE, hasMemberPredicate2, true);
        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, containerResource,
                                                   LDP_HAS_MEMBER_RELATION_PREDICATE, hasMemberPredicate1, false);
    }

    /**
     * 3.1.3-K
     */
    @Test(groups = {"MAY"})
    public void ldpIndirectContainerMayAllowLdpHasMemberRelationToBeUpdatedByPatch() {
        setupTest("3.1.3-K",
                  "Implementations may allow the membership predicate to be updated by " +
                  "subsequent PATCH requests that change the ldp:hasMemberRelation " +
                  "property of the resource content.",
                  INDIRECT_CONTAINER_SPEC_LINK,
                  ps);
        //throw skip exception if indirect containers not supported
        skipIfIndirectContainersNotSupported();

        final String hasMemberPredicate = "http://example.org/ldp/member";
        final String requestBody = "<> <" + LDP_HAS_MEMBER_RELATION_PREDICATE + "> <" + LDP_MEMBER + "> ;\n";
        final Response response = createIndirectContainer(uri, requestBody);
        final String containerResource = getLocation(response);

        final String sparqlUpdate =
            "DELETE { <> <" + LDP_HAS_MEMBER_RELATION_PREDICATE + "> <" + LDP_MEMBER + ">  } \n" +
            "INSERT { <> <" + LDP_HAS_MEMBER_RELATION_PREDICATE + "> <" + hasMemberPredicate + ">  } \n" +
            "WHERE {}";

        final Response updateResponse =
            doPatchUnverified(containerResource,
                              new Headers(new Header("Content-Type", "application/sparql-update")),
                              sparqlUpdate);
        if (clientErrorRange().matches(updateResponse.statusCode())) {
            throw new SkipException(
                "This implementation does not support PATCH updates on " + LDP_HAS_MEMBER_RELATION_PREDICATE);
        }

        final Response getUpdatedResource = doGet(containerResource);
        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, containerResource,
                                                   LDP_HAS_MEMBER_RELATION_PREDICATE, hasMemberPredicate, true);

        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, containerResource,
                                                   LDP_HAS_MEMBER_RELATION_PREDICATE, LDP_MEMBER, false);

    }

    /**
     * 3.1.3-L
     */
    @Test(groups = {"MAY"})
    public void ldpIndirectContainerMayAllowLdpIsMemberRelationPredicateToBeUpdatedByPut() {
        setupTest("3.1.3-L",
                  "Implementations may allow the membership predicate to be updated by " +
                  "subsequent PUT requests that change the ldp:isMemberOfRelation property of " +
                  "the resource content.",
                  INDIRECT_CONTAINER_SPEC_LINK,
                  ps);

        //throw skip exception if indirect containers not supported
        skipIfIndirectContainersNotSupported();

        final String isMemberOfPredicate1 = "http://example.org/ldp/isMemberOf1";
        final String isMemberOfPredicate2 = "http://example.org/ldp/isMemberOf2";

        final String requestBody =
            "<> <" + LDP_IS_MEMBER_OF_RELATION_PREDICATE + "> <" + isMemberOfPredicate1 + "> ;\n";

        final Response response = createIndirectContainer(uri, requestBody);
        final String containerResource = getLocation(response);
        final String body = doGet(containerResource).getBody().asString();

        final String newBody = body.replace(isMemberOfPredicate1, isMemberOfPredicate2);
        final Response updateResponse =
            doPutUnverified(containerResource, new Headers(new Header("Content-Type", "text/turtle")), newBody);
        if (clientErrorRange().matches(updateResponse.statusCode())) {
            throw new SkipException(
                "This implementation does not support PUT updates on " + LDP_IS_MEMBER_OF_RELATION_PREDICATE);
        }

        final Response getUpdatedResource = doGet(containerResource);
        if (!testForPresenceOfTrip(getUpdatedResource, containerResource,
                                   LDP_IS_MEMBER_OF_RELATION_PREDICATE, isMemberOfPredicate2, true)) {
            throw new SkipException(
                "This implementation does not support PUT updates on " + LDP_IS_MEMBER_OF_RELATION_PREDICATE);
        }

        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, containerResource,
                                                   LDP_IS_MEMBER_OF_RELATION_PREDICATE, isMemberOfPredicate1, false);

    }

    /**
     * 3.1.3-M
     */
    @Test(groups = {"MAY"})
    public void ldpIndirectContainerMayAllowLdpIsMemberRelationToBeUpdatedByPatch() {
        setupTest("3.1.3-M",
                  "Implementations may allow the membership predicate to be updated by " +
                  "subsequent PATCH requests that change the ldp:isMemberOfRelation " +
                  "property of the resource content.",
                  INDIRECT_CONTAINER_SPEC_LINK,
                  ps);

        //throw skip exception if direct containers not supported
        skipIfIndirectContainersNotSupported();

        final String isMemberOfPredicate1 = "http://example.org/ldp/isMemberOf1";
        final String isMemberOfPredicate2 = "http://example.org/ldp/isMemberOf2";
        final String requestBody =
            "<> <" + LDP_IS_MEMBER_OF_RELATION_PREDICATE + "> <" + isMemberOfPredicate1 + "> ;\n";
        final Response response = createIndirectContainer(uri, requestBody);
        final String containerResource = getLocation(response);

        final String sparqlUpdate =
            "DELETE { <> <" + LDP_IS_MEMBER_OF_RELATION_PREDICATE + "> <" + isMemberOfPredicate1 + ">  } \n" +
            "INSERT { <> <" + LDP_IS_MEMBER_OF_RELATION_PREDICATE + "> <" + isMemberOfPredicate2 + ">  } \n" +
            "WHERE {}";

        final Response updateResponse =
            doPatchUnverified(containerResource,
                              new Headers(new Header("Content-Type", "application/sparql-update")),
                              sparqlUpdate);
        if (clientErrorRange().matches(updateResponse.statusCode())) {
            throw new SkipException(
                "This implementation does not support PATCH updates on " + LDP_IS_MEMBER_OF_RELATION_PREDICATE);
        }

        final Response getUpdatedResource = doGet(containerResource);
        if (!testForPresenceOfTrip(getUpdatedResource, containerResource,
                                   LDP_IS_MEMBER_OF_RELATION_PREDICATE, isMemberOfPredicate2, true)) {
            throw new SkipException(
                "This implementation does not support PATCH updates on " + LDP_IS_MEMBER_OF_RELATION_PREDICATE);
        }

        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, containerResource,
                                                   LDP_IS_MEMBER_OF_RELATION_PREDICATE, isMemberOfPredicate1, false);

    }

    /**
     * 3.1.3-N
     */
    @Test(groups = {"MUST"})
    public void ldpIndirectContainerMustAllowInsertedContentRelationPredicateToBeSetOnCreate() {
        setupTest("3.1.3-N",
                  "Implementations must allow the ldp:insertedContentRelation property to be set via the content RDF " +
                  "on container creation",
                  INDIRECT_CONTAINER_SPEC_LINK,
                  ps);

        //throw skip exception if indirect containers not supported
        skipIfIndirectContainersNotSupported();

        final String memberSubject = "http://example.org/ldp/MemberSubject";
        final String body = "<> <" + LDP_INSERTED_CONTENT_RELATION_PREDICATE + "> <" + memberSubject + "> ;";
        final Response container = createIndirectContainer(uri, body);
        final String containerResource = getLocation(container);
        confirmPresenceOrAbsenceOfTripleInResponse(doGet(containerResource), containerResource,
                                                   LDP_INSERTED_CONTENT_RELATION_PREDICATE, memberSubject, true);
    }

    /**
     * 3.1.3-O
     */
    @Test(groups = {"MUST"})
    public void ldpIndirectContainerMustAllowInsertedContentRelationToBeSetByDefault() {
        setupTest("3.1.3-O",
                  "Implementations must allow the ldp:insertedContentRelation property to be set by default to an " +
                  "implementation defined value.",
                  INDIRECT_CONTAINER_SPEC_LINK,
                  ps);

        //throw skip exception if indirect containers not supported
        skipIfIndirectContainersNotSupported();

        final String body = "";
        final Response container = createIndirectContainer(uri, body);
        final String containerResource = getLocation(container);
        final String responseBody = doGet(getLocation(container)).getBody().asString();
        try (CloseableDataset dataset = parseTriples(IOUtils.toInputStream(responseBody))) {
            final DatasetGraph graph = dataset.asDatasetGraph();
            if (!graph
                .contains(ANY, createURI(containerResource), createURI(LDP_INSERTED_CONTENT_RELATION_PREDICATE), ANY)) {
                fail("The " + LDP_INSERTED_CONTENT_RELATION_PREDICATE + " predicate was expected but not found");
            }
        }
    }

    /**
     * 3.1.3-P
     */
    @Test(groups = {"SHOULD"})
    public void ldpIndirectContainerShouldUseLdpMemberSubjectByDefault() {
        setupTest("3.1.3-P",
                  "Implementations SHOULD allow the ldp:insertedContentRelation property to be set by default to " +
                  "ldp:MemberSubject.",
                  INDIRECT_CONTAINER_SPEC_LINK,
                  ps);

        //throw skip exception if indirect containers not supported
        skipIfIndirectContainersNotSupported();

        final String body = "";
        final Response container = createIndirectContainer(uri, body);
        final String containerResource = getLocation(container);
        final Response response = doGet(getLocation(container));
        confirmPresenceOrAbsenceOfTripleInResponse(response, containerResource, LDP_INSERTED_CONTENT_RELATION_PREDICATE,
                                                   LDP_MEMBER_SUBJECT, true);
    }

    /**
     * 3.1.3-Q
     */
    @Test(groups = {"MAY"})
    public void ldpIndirectContainerMayAllowLdpInsertedContentRelationPredicateToBeUpdatedByPut() {
        setupTest("3.1.3-Q",
                  "Implementations may allow the ldp:insertedContentRelation property to be updated via the content " +
                  "RDF by subsequent PUT requests.",
                  INDIRECT_CONTAINER_SPEC_LINK,
                  ps);
        //throw skip exception if indirect containers not supported
        skipIfIndirectContainersNotSupported();

        final String value1 = "http://example.org/ldp/MemberSubject1";
        final String value2 = "http://example.org/ldp/MemberSubject2";

        final String requestBody = "<> <" + LDP_INSERTED_CONTENT_RELATION_PREDICATE + "> <" + value1 + "> ;\n";

        final Response response = createIndirectContainer(uri, requestBody);
        final String containerResource = getLocation(response);
        final String body = doGet(containerResource).getBody().asString();
        final String newBody = body.replace(value1, value2);
        final Response updateResponse =
            doPutUnverified(containerResource, new Headers(new Header("Content-Type", "text/turtle")), newBody);
        if (clientErrorRange().matches(updateResponse.statusCode())) {
            throw new SkipException(
                "This implementation does not support PUT updates on " + LDP_INSERTED_CONTENT_RELATION_PREDICATE);
        }

        final Response getUpdatedResource = doGet(containerResource);
        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, containerResource,
                                                   LDP_INSERTED_CONTENT_RELATION_PREDICATE, value2, true);
        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, containerResource,
                                                   LDP_INSERTED_CONTENT_RELATION_PREDICATE, value1, false);
    }

    /**
     * 3.1.3-R
     */
    @Test(groups = {"MAY"})
    public void ldpIndirectContainerMayAllowLdpInsertedContentRelationToBeUpdatedByPatch() {
        setupTest("3.1.3-R",
                  "Implementations may allow the ldp:insertedContentRelation property to be updated via the content " +
                  "RDF by subsequent PATCH requests.",
                  INDIRECT_CONTAINER_SPEC_LINK,
                  ps);
        //throw skip exception if indirect containers not supported
        skipIfIndirectContainersNotSupported();

        final String memberSubject = "http://example.org/ldp/MemberSubject";
        final String requestBody =
            "<> <" + LDP_INSERTED_CONTENT_RELATION_PREDICATE + "> <" + LDP_MEMBER_SUBJECT + "> ;\n";
        final Response response = createIndirectContainer(uri, requestBody);
        final String containerResource = getLocation(response);

        final String sparqlUpdate =
            "DELETE { <> <" + LDP_INSERTED_CONTENT_RELATION_PREDICATE + "> <" + LDP_MEMBER_SUBJECT + ">  } \n" +
            "INSERT { <> <" + LDP_INSERTED_CONTENT_RELATION_PREDICATE + "> <" + memberSubject + ">  } \n" +
            "WHERE {}";

        final Response updateResponse =
            doPatchUnverified(containerResource,
                              new Headers(new Header("Content-Type", "application/sparql-update")),
                              sparqlUpdate);
        if (clientErrorRange().matches(updateResponse.statusCode())) {
            throw new SkipException(
                "This implementation does not support PATCH updates on " + LDP_INSERTED_CONTENT_RELATION_PREDICATE);
        }

        final Response getUpdatedResource = doGet(containerResource);
        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, containerResource,
                                                   LDP_INSERTED_CONTENT_RELATION_PREDICATE, memberSubject, true);

        confirmPresenceOrAbsenceOfTripleInResponse(getUpdatedResource, containerResource,
                                                   LDP_INSERTED_CONTENT_RELATION_PREDICATE, LDP_MEMBER_SUBJECT, false);

    }


}
