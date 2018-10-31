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

import java.net.URI;

/**
 * An interface that provides a means to resolve any authentication-related information based on a user-identifying
 * URI (ie a WebID) that will be required by subsequent requests in order to fulfill a request.  For example in the
 * case of an implementation that uses Basic Auth,  the createAuthToken() would return an AuthToken with access to
 * valid credentials that the AuthToken would subsequently add to the request. NB:  A zero-argument constructor is
 * required for any implementation of this interface.
 * See {@link org.fcrepo.spec.testsuite.authn.DefaultAuthenticator} and
 * {@link org.fcrepo.spec.testsuite.authn.DefaultAuthToken}  for an example of how one might implement a
 * custom Authenticator class.
 *
 * @author dbernstein
 */
public interface Authenticator {

    /**
     * Returns a new AuthorizationToken to be used by a future request
     *
     * @param userUri A user-identifying URI (such as a WebID)
     * @return an authentication token
     */
    AuthenticationToken createAuthToken(final URI userUri);
}
