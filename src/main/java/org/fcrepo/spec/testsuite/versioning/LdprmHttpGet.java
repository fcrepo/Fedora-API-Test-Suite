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
import java.util.NoSuchElementException;

import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import io.restassured.response.Response;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import static org.fcrepo.spec.testsuite.Constants.MEMENTO_LINK_HEADER;

/**
 * Tests for GET requests on LDP Memento Resources
 *
 * @author Daniel Bernstein
 */
public class LdprmHttpGet extends AbstractVersioningTest {

    /**
     * Authentication
     *
     * @param username The repository username
     * @param password The repository password
     */
    @Parameters({"param2", "param3"})
    public LdprmHttpGet(final String username, final String password) {
        super(username, password);
    }

    /**
     * 4.2.1-A
     *
     * @param uri The repository root URI
     */
    @Test(groups = { "MUST" })
    @Parameters({"param1"})
    public void ldprmMustSupportGet(final String uri) {
        final TestInfo info = setupTest("4.2.1-A", "ldprmMustSupportGet",
                                        "LDPR mementos must support GET",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-get",
                                        ps);

        //create an LDPRm
        final Response resp = createVersionedResource(uri, info);
        final String location = getLocation(resp);
        final String mementoUri = createMemento(location);

        //verify that a subsequent GET returns 200.
        doGet(mementoUri);
    }

    /**
     * 4.2.1-B
     *
     * @param uri The repository root URI
     */
    @Test(groups = { "MUST" })
    @Parameters({"param1"})
    public void ldpnrmMustSupportGet(final String uri) {
        final TestInfo info = setupTest("4.2.1-B", "ldpnrmMustSupportGet",
                                        "LDP-NR mementos must support GET",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-get",
                                        ps);

        //create an LDP-NR memento
        final Response resp = createVersionedNonRDFResource(uri, info);
        final String location = getLocation(resp);
        final String mementoUri = createMemento(location);

        //verify that a subsequent GET returns 200.
        doGet(mementoUri);

    }

    /**
     * 4.2.1-C
     *
     * @param uri The repository root URI
     */
    @Test(groups = { "MUST" })
    @Parameters({"param1"})
    public void ldprmMustHaveCorrectTimeGate(final String uri) {
        final TestInfo info = setupTest("4.2.1-C", "ldprmMustHaveCorrectTimeGate",
                "TimeGate for an  LDP-RS memento is the original versioned LDP-RS",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-get",
                                        ps);

        //create an LDPR memento
        final Response resp = createVersionedResource(uri, info);
        final String location = getLocation(resp);
        final String mementoUri = createMemento(location);

        //verify that the timegate matches that of the original LDPR
        final Response getResp = doGet(mementoUri);
        try {
            final URI timegateUri = getLinksOfRelTypeAsUris(getResp, "timegate").findFirst().get();
            assertEquals(location, timegateUri.toString());
        } catch (final NoSuchElementException e) {
            fail("Timegate link header was not returned by memento");
        }
    }

    /**
     * 4.2.1-D
     *
     * @param uri The repository root URI
     */
    @Test(groups = { "MUST" })
    @Parameters({"param1"})
    public void ldpnrmMustHaveCorrectTimeGate(final String uri) {
        final TestInfo info = setupTest("4.2.1-D", "ldpnrmMustHaveCorrectTimeGate",
                                        "TimeGate  for an  LDP-NR memento  is the original versioned LDP-NR",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-get",
                                        ps);

        //create an LDP-NR memento
        final Response resp = createVersionedNonRDFResource(uri, info);
        final String location = getLocation(resp);
        final String mementoUri = createMemento(location);

        // verify that the timegate matches that of the original LDPR
        final Response getResp = doGet(mementoUri);
        try {
            final URI timegateUri = getLinksOfRelTypeAsUris(getResp, "timegate").findFirst().get();
            assertEquals(location, timegateUri.toString());
        } catch (final NoSuchElementException e) {
            fail("Timegate link header was not returned by memento");
        }
    }

    /**
     * 4.2.1-E
     *
     * @param uri The repository root URI
     */
    @Test(groups = { "MUST" })
    @Parameters({"param1"})
    public void ldprmMustHaveMementoLinkHeader(final String uri) {
        final TestInfo info = setupTest("4.2.1-E", "ldprmMustHaveMementoLinkHeader",
                                        "Any response to a GET request on an LDP-RS Memento must include a " +
                                        "<http://mementoweb.org/ns#Memento>; rel=\"type\" link in the Link header",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-get",
                                        ps);

        //create an LDP-NR memento
        final Response resp = createVersionedResource(uri, info);
        final String location = getLocation(resp);
        final String mementoUri = createMemento(location);

        //verify that the appropriate link header is present
        final Response getResp = doGet(mementoUri);
        confirmPresenceOfLinkValue(MEMENTO_LINK_HEADER, getResp);
    }

    /**
     * 4.2.1-F
     *
     * @param uri The repository root URI
     */
    @Test(groups = { "MUST" })
    @Parameters({"param1"})
    public void ldpnrmMustHaveMementoLinkHeader(final String uri) {
        final TestInfo info = setupTest("4.2.1-F", "ldpnrmMustHaveMementoLinkHeader",
                                        "Any response to a GET request on an LDP-NR Memento must include a " +
                                        "<http://mementoweb.org/ns#Memento>; rel=\"type\" link in the Link header",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldprm-get",
                                        ps);

        //create an LDP-NR memento
        final Response resp = createVersionedNonRDFResource(uri, info);
        final String location = getLocation(resp);
        final String mementoUri = createMemento(location);

        //verify that the appropriate link header is present
        final Response getResp = doGet(mementoUri);
        confirmPresenceOfLinkValue(MEMENTO_LINK_HEADER, getResp);

    }
}

