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

import io.restassured.specification.RequestSpecification;

/**
 * An interface defining a token that can add any implementation specific authentication
 * information to the request.
 * See {@link org.fcrepo.spec.testsuite.authn.DefaultAuthToken} for an example implementation.
 * @author dbernstein
 */
public interface AuthenticationToken {
    /**
     * Adds any auth information from the token to the request specification and returns the
     * altered request specification instance
     * @param requestSpecification to be authenticated
     * @return the modified request specification
     */
    RequestSpecification addAuthInfo(RequestSpecification requestSpecification);
}
