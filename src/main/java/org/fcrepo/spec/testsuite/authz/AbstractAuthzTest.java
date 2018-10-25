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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.core.Link;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.AbstractTest;

/**
 * @author Daniel Bernstein
 */
public class AbstractAuthzTest extends AbstractTest {

    protected String filterFileAndConvertToString(final String fileName, final Map<String, String> params) {
        String str = fileToString("/acls/" + fileName);
        for (String key : params.keySet()) {
            str = str.replace("${" + key + "}", params.get(key));
        }
        return str;
    }

    protected String getAclAsString(final String fileName, final String resourceUri, final String user) {
        final Map<String, String> params = new HashMap<>();
        params.put("resource", resourceUri);
        params.put("user", user);
        return filterFileAndConvertToString(fileName, params);
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
        response.then().statusCode(successRange());
        return aclUri;
    }

    protected String createAclForResource(final String resourceUri, final String aclFileName,
                                          final Map<String, String> aclParams) {
        //get acl handle
        final String aclUri = getAclLocation(resourceUri);
        //create read acl for user role
        final Response response = doPutUnverified(aclUri, new Headers(new Header("Content-Type", "text/turtle")),
                                                  filterFileAndConvertToString(aclFileName, aclParams));
        response.then().statusCode(successRange());
        return aclUri;
    }

}

