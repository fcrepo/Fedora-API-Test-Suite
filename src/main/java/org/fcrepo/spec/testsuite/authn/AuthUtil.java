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

package org.fcrepo.spec.testsuite.authn;

import static org.testng.util.Strings.isNullOrEmpty;

import java.net.URI;

import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;
import org.fcrepo.spec.testsuite.TestParameters;

/**
 * @author dbernstein
 */
public class AuthUtil {

    private AuthUtil() {
    }

    /**
     * A global authentication function.  Adds Authorization headers if present, otherwise uses resolved Authenticator.
     *
     * @param request The request specification
     * @param webId   The webId of the user to be authenticated
     * @return The authenticated request specification.
     */
    public static RequestSpecification auth(final RequestSpecification request, final String webId) {
        final TestParameters params = TestParameters.get();
        final boolean isRootController = webId.equals(params.getRootControllerUserWebId());
        final String authHeader = isRootController ? params.getRootControllerUserAuthHeader() :
                                  params.getPermissionlessUserAuthHeader();

        if (isNullOrEmpty(authHeader)) {
            return AuthenticatorResolver.getAuthenticator()
                                        .createAuthToken(URI.create(webId))
                                        .addAuthInfo(request);
        } else {
            return request.header(new Header("Authorization", authHeader));
        }
    }
}
