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

import static org.fcrepo.spec.testsuite.TestParameters.PERMISSIONLESS_USER_PASSWORD_PARAM;
import static org.fcrepo.spec.testsuite.TestParameters.ROOT_CONTROLLER_USER_PASSWORD_PARAM;

import java.net.URI;

import org.fcrepo.spec.testsuite.TestParameters;

/**
 * This implementation of the Authenticator interface implements Basic Auth by
 * resolving the password associated with the specified WebId based on the passwords
 * supplied via the commandline or YAML parameters file.
 * @author dbernstein
 */
public class DefaultAuthenticator implements Authenticator {
    @Override
    public AuthenticationToken createAuthToken(final URI webid) {
        //get username by taking string after the last forward slash.
        final String webidStr = webid.toString();
        final String username = webidStr.substring(webidStr.lastIndexOf('/') + 1);
        final TestParameters params = TestParameters.get();
        final String password =
            webid.toString().equals(params.getRootControllerUserWebId()) ? params.getRootControllerUserPassword() :
            params.getPermissionlessUserPassword();
        if (password == null) {
            throw new RuntimeException(
                "When using the DefaultAuthenticator you must specify the optional password parameters in the command" +
                " line, ie --" +
                ROOT_CONTROLLER_USER_PASSWORD_PARAM + " and --" + PERMISSIONLESS_USER_PASSWORD_PARAM);
        }
        return new DefaultAuthToken(username, password);
    }
}
