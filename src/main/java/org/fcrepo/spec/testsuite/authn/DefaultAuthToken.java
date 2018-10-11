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
 * @author dbernstein
 */
public class DefaultAuthToken implements AuthenticationToken {
    private String username;
    private String password;

    /**
     * Constructor
     * @param username basic auth username
     * @param password basic auth password
     */
    public DefaultAuthToken(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public RequestSpecification addAuthInfo(final RequestSpecification requestSpecification) {
        return requestSpecification.auth().basic(this.username, this.password);
    }

    @Override
    public boolean isExpired() {
        return false;
    }
}
