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
import static org.fcrepo.spec.testsuite.Constants.TIME_GATE_LINK_HEADER;
import java.net.URI;

import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.SkipException;
import org.testng.annotations.Test;

import io.restassured.response.Response;

/**
 * @author bbpennel
 */
public class ResourceVersioning extends AbstractVersioningTest {


    /**
     * 4.0-A ldprv with type via post
     */
    @Test(groups = {"MUST"})
    public void postLdprWithType() {
        final TestInfo info = setupTest("4.0-A",
                                        "When an LDPR is created with a rel=\"type\" link in the Link " +
                                        "header specifying type http://mementoweb.org/ns#OriginalResource " +
                                        "to indicate versioning, it MUST be created as an LDPRv and a version " +
                                        "container (LDPCv) MUST be created to contain Memento resources",
                "https://fcrepo.github.io/fcrepo-specification/#resource-versioning",
                ps);

        // create an LDPRv using a post
        final Response response = createVersionedResource(uri, info);
        // is a LDPRv: URI-R and Timegate
        confirmPresenceOfLinkValue(ORIGINAL_RESOURCE_LINK_HEADER, response);
        confirmPresenceOfLinkValue(TIME_GATE_LINK_HEADER, response);

        // Verify timemap created
        final URI timemapUri = getTimeMapUri(response);
        doGet(timemapUri.toString());
    }

    /**
     * 4.0-B ldprv with type via put
     */
    @Test(groups = { "MAY" })
    public void convertToLDPRViaPutWithType() {
        final TestInfo info = setupTest("4.0-B",
                "Implementations MAY allow a subsequent PUT request with a rel=\"type\" link in the Link header " +
                "specifying type http://mementoweb.org/ns#OriginalResource to convert an existing LDPR into an LDPRv." +
                " If such a conversion from an LDPR to an LDPRv is supported, it MUST be accompanied by the creation " +
                "of a version container (LDPCv), as noted above.",
                "https://fcrepo.github.io/fcrepo-specification/#resource-versioning",
                                        ps);

        //create a container
        final String resourceUri = getLocation(createBasicContainer(uri, info));
        //create an LDPRv using a put
        final Response response = putVersionedResourceUnverified(resourceUri);
        // PUT creation operation not supported
        if (clientErrorRange().matches(response.getStatusCode())) {
            throw new SkipException("External Binaries are supported");
        }

        // is a LDPRv: URI-R and Timegate
        confirmPresenceOfLinkValue(ORIGINAL_RESOURCE_LINK_HEADER, response);
        confirmPresenceOfLinkValue(TIME_GATE_LINK_HEADER, response);
        // Verify timemap created
        final URI timemapUri = getTimeMapUri(response);
        doGet(timemapUri.toString());
    }
}
