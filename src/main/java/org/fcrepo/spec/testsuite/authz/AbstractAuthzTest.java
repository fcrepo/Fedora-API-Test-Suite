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
package org.fcrepo.spec.testsuite.authz;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.core.Link;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.apache.commons.io.IOUtils;
import org.fcrepo.spec.testsuite.AbstractTest;
import org.testng.annotations.Parameters;

/**
 * @author Daniel Bernstein
 */
public class AbstractAuthzTest extends AbstractTest {
    /**
     * Constructor
     *
     * @param adminUsername admin username
     * @param adminPassword admin password
     * @param username      username
     * @param password      password
     */
    @Parameters({"param2", "param3", "param4", "param5"})
    public AbstractAuthzTest(final String adminUsername, final String adminPassword, final String username,
                             final String password) {
        super(adminUsername, adminPassword, username, password);
    }

    protected String getAclLocation(final String resourceUri) {
        //get or create the resource
        final Response resourceResponse = doGet(resourceUri);
        final List<Link> acls = getLinksOfRelType(resourceResponse, "acl").collect(Collectors.toList());
        if (acls.size() > 0) {
            return acls.get(0).getUri().toString();
        } else {
            throw new RuntimeException("No link of type rel=\"acl\" found on resource: " + resourceUri);
        }
    }

    protected String getAclAsString(final String fileName, final String resourceUri, final String username) {
        try (InputStream is = getClass().getResourceAsStream("/acls/" + fileName)) {
            return IOUtils.toString(is, "UTF-8").replace("${resource}", resourceUri).replace("${user}", username);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected String createResource(final String baseUri, final String resourceId) {
        final Response resourceResponse = createBasicContainer(baseUri, resourceId);
        return getLocation(resourceResponse);
    }

    protected String createAclForResource(final String resourceUri, final String aclFileName, final String username) {
        //get acl handle
        final String aclUri = getAclLocation(resourceUri);
        //create read acl for user role
        final Response response = doPutUnverified(aclUri, new Headers(new Header("Content-Type", "text/turtle")),
                                                  getAclAsString(aclFileName, resourceUri, username));
        response.then().statusCode(201);
        return aclUri;
    }



}

