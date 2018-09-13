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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.Link;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * Service which cleans up Fedora resources created during test runs.
 *
 * @author bbpennel
 */
public class ResourceCleanupManager {

    private String testContainerUrl;

    private final String user;

    private final String password;

    private List<String> createdResources = new ArrayList<>();

    /**
     * Construct manager
     *
     * @param user user
     * @param pass password
     */
    public ResourceCleanupManager(final String user, final String pass) {
        this.user = user;
        this.password = pass;
    }

    /**
     * Add a url to the list of created fedora resources
     *
     * @param url url to add
     */
    public void registerResource(final String url) {
        if (url != null) {
            createdResources.add(url);
        }
    }

    /**
     * Cleanup the created Fedora resources.
     */
    public void cleanup() {
        // Attempt to recursively delete test container to save time
        if (testContainerUrl != null) {
            if (cleanupTestContainer()) {
                return;
            }
        }
        // Either there was no test container, or it couldn't be recursively deleted
        // So delete each resource in reverse order added to avoid recursion
        Collections.reverse(createdResources);
        // if present, register test container for deletion after all its children
        if (testContainerUrl != null) {
            createdResources.add(testContainerUrl);
        }
        cleanupResources();
    }

    private boolean cleanupTestContainer() {
        // Determine if recursive delete allowed
        final Response resp = RestAssured.given()
                .auth().basic(user, password)
                .when()
                .options(testContainerUrl);

        final String allowHeader = resp.header("Allow");
        if (allowHeader == null || !allowHeader.contains("DELETE")) {
            return false;
        }
        return deleteResource(testContainerUrl);
    }

    private void cleanupResources() {
        for (final String resourceUrl : createdResources) {
            deleteResource(resourceUrl);
        }
    }

    private boolean deleteResource(final String url) {
        final Response resp = RestAssured.given()
                .auth().basic(user, password)
                .when()
                .delete(url);

        if (resp.statusCode() != 204 && resp.statusCode() != 200) {
            final PrintStream log = TestSuiteGlobals.logFile();
            log.append("Failed to cleanup test resource:\n").append(url).append('\n');
            return false;
        } else {
            final Response headResp = RestAssured.given()
                    .auth().basic(user, password)
                    .when()
                    .head(url);

            final Optional<Link> tombstoneOption = headResp.headers().getValues("Link").stream()
                    .map(l -> Link.valueOf(l))
                    .filter(l -> l.getRel().equals("hasTombstone"))
                    .findFirst();

            if (tombstoneOption.isPresent()) {
                // Also delete the tombstone if it exists
                String path = tombstoneOption.get().getUri().toString();
                // Remove duplicate slashes (aside from ://) due to RestAssured issue #867
                path = path.replaceAll("(?<!:)//", "/");
                final Response dResp = RestAssured.given()
                        .auth().basic(user, password)
                        .when()
                        .delete(path);
            }
        }
        return true;
    }

    /**
     * Set the url of the base test container.
     *
     * @param testContainerUrl url of the base test container
     */
    public void setTestContainerUrl(final String testContainerUrl) {
        this.testContainerUrl = testContainerUrl;
    }
}
