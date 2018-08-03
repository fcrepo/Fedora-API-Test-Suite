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

import static org.fcrepo.spec.testsuite.Constants.ORIGINAL_RESOURCE_LINK_HEADER;
import static org.testng.AssertJUnit.assertEquals;

import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Tests for DELETE requests on LDP Version Container Resources
 *
 * @author Daniel Bernstein
 */
public class LdpcvHttpDelete extends AbstractVersioningTest {

    /**
     * Authentication
     *
     * @param username The repository username
     * @param password The repository password
     */
    @Parameters({"param2", "param3"})
    public LdpcvHttpDelete(final String username, final String password) {
        super(username, password);
    }

    /**
     * Tests section 4.3.6
     *
     * @param uri The repository root URI
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void ldpcvThatAdvertisesDeleteShouldRemoveContainerAndMementos(final String uri) {
        final TestInfo info = setupTest("4.3.6", "ldpcvThatAdvertisesDeleteShouldRemoveContainerAndMementos",
                                        "An implementation that does support DELETE should do so by both " +
                                        "removing the LDPCv and removing the versioning interaction model from the " +
                                        "original LDPRv.",
                                        "https://fcrepo.github.io/fcrepo-specification/#ldpcv-delete",
                                        ps);

        //create a versioned resource
        final Response response = createVersionedResource(uri, info);
        final String versionedResource = getLocation(response);
        final String timeMap = getTimeMapUri(response).toString();

        final Response optionsResponse = doOptions(timeMap);

        //check that delete is supported
        if (hasHeaderValueInMultiValueHeader("Allow", "DELETE", optionsResponse)) {
            //delete the ldpcv
            final Response deleteResponse = doDelete(timeMap);
            assertEquals("Timemap DELETE command failed.", deleteResponse.getStatusCode(), 204);
            //verify that ldpcv is gone
            final Response timeMapGet = doGetUnverified(timeMap);
            assertEquals("Timemap was not deleted", timeMapGet.getStatusCode(), 404);
            //verify that versioning model is gone
            final Response getResponse = doGet(versionedResource);
            confirmAbsenceOfLinkValue(ORIGINAL_RESOURCE_LINK_HEADER, getResponse);
        }
    }

}