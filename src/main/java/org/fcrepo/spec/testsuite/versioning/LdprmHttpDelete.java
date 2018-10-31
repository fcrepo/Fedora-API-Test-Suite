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
package org.fcrepo.spec.testsuite.versioning;

import java.net.URI;

import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.SkipException;
import org.testng.annotations.Test;

import io.restassured.response.Response;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

/**
 * Tests for DELETE requests on LDP Memento Resources
 *
 * @author Daniel Bernstein
 */
public class LdprmHttpDelete extends AbstractVersioningTest {

    /**
     * 4.2.6
     */
    @Test(groups = { "MUST" })
    public void ldprmMustSupportDeleteIfAdvertised() {
        final TestInfo info = setupTest("4.2.6",
                                        "LDPRm resources must support DELETE if DELETE is advertised in OPTIONS",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-delete",
                                        ps);

        //create an LDPRm
        final Response resp = createVersionedResource(uri, info);
        final String location = getLocation(resp);
        final String mementoUri = createMemento(location);

        //check if LDPRm supports DELETE and if so perform DELETE
        final String accept = doOptions(mementoUri).header("Allow");
        if (!accept.contains("DELETE")) {
            throw new SkipException("DELETE not supported");
        }
        doDelete(mementoUri);

        //verify that the DELETE is reflected in a 404 on a subsequent GET
        doGetUnverified(mementoUri).then().statusCode(404);

        //and that the LDPCv no longer has a reference to it.
        final URI timemapUri = getTimeMapUri(resp);
        final Statement tripleMember = ResourceFactory.createStatement(
                createResource(timemapUri.toString()),
                createProperty("http://www.w3.org/ns/ldp#contains"),
                createResource(mementoUri));

        // Both triples should be ldp:contained
        doGet(timemapUri.toString()).then()
                .body(new TripleMatcher<Statement>(tripleMember, false));

    }

}

