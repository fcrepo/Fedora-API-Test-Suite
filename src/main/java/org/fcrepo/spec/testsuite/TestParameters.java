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

import java.util.Map;

/**
 * A singleton class providing access to shared test paramters.
 *
 * @author dbernstein
 */
public class TestParameters {

    /**
     * Configuration parameter names for consistency.
     */
    public static final String TEST_CONTAINER_URL_PARAM = "testContainerURL";

    public final static String ROOT_URL_PARAM = "rooturl";

    public final static String PERMISSIONLESS_USER_WEBID_PARAM = "permissionless-user-webid";

    public final static String PERMISSIONLESS_USER_PASSWORD_PARAM = "permissionless-user-password";

    public final static String PERMISSIONLESS_USER_AUTH_HEADER = "permissionless-user-auth-header-value";

    public final static String ROOT_CONTROLLER_USER_WEBID_PARAM = "root-controller-user-webid";

    public final static String ROOT_CONTROLLER_USER_AUTH_HEADER = "root-controller-user-auth-header-value";

    public final static String ROOT_CONTROLLER_USER_PASSWORD_PARAM = "root-controller-user-password";

    public final static String BROKER_URL_PARAM = "broker-url";

    public final static String QUEUE_NAME_PARAM = "queue-name";

    public final static String TOPIC_NAME_PARAM = "topic-name";

    public final static String CONFIG_FILE_PARAM = "config-file";

    public final static String CONSTRAINT_ERROR_GENERATOR_PARAM = "constraint-error-generator";

    public final static String AUTHENTICATOR_CLASS_PARAM = "auth-class";

    private static TestParameters instance;

    /**
     * Initialize the test parameters
     * @param params
     */
    public static void initialize(final Map<String, String> params) {
        if (instance != null) {
            throw new IllegalStateException("The test parameters have already been initialized.");
        }

        instance = new TestParameters(params);
    }

    /**
     * Return the singleton
     * @return
     */
    public static TestParameters get() {
        return instance;
    }

    private Map<String, String> params = null;
    private String testContainerUrl = null;

    private TestParameters(final Map<String, String> params) {
        this.params = params;
    }

    /**
     * The repository root url
     * @return
     */
    public String getRootUrl() {
        return params.get(ROOT_URL_PARAM);
    }

    /**
     * The root controller user WebID
     * @return
     */
    public String getRootControllerUserWebId() {
        return params.get(ROOT_CONTROLLER_USER_WEBID_PARAM);
    }

    /**
     * The root controller user password
     * @return
     */
    public String getRootControllerUserPassword() {
        return params.get(ROOT_CONTROLLER_USER_PASSWORD_PARAM);
    }

    /**
     * The root controller user auth header value
     * @return
     */
    public String getRootControllerUserAuthHeader() {
        return params.get(ROOT_CONTROLLER_USER_AUTH_HEADER);
    }

    /**
     * The permissionless user WebID
     * @return
     */

    public String getPermissionlessUserWebId() {
        return params.get(PERMISSIONLESS_USER_WEBID_PARAM);
    }

    /**
     * The permissionless user password
     * @return
     */
    public String getPermissionlessUserPassword() {
        return params.get(PERMISSIONLESS_USER_PASSWORD_PARAM);
    }

    /**
     * The permissionless user auth header value
     * @return
     */
    public String getPermissionlessUserAuthHeader() {
        return params.get(PERMISSIONLESS_USER_AUTH_HEADER);
    }

    /**
     * Set the test container url
     * @param testContainerUrl
     */
    public void setTestContainerUrl(final String testContainerUrl) {
        this.testContainerUrl = testContainerUrl;
    }

    /**
     * Get the test container url
     * @return
     */
    public String getTestContainerUrl() {
        return this.testContainerUrl;
    }

    /**
     * queue name
     * @return
     */
    public String getQueueName() {
        return params.get(QUEUE_NAME_PARAM);
    }

    /**
     * topic name
     * @return
     */
    public String getTopicName() {
        return params.get(TOPIC_NAME_PARAM);
    }

    /**
     * broker url
     * @return
     */
    public String getBrokerUrl() {
        return params.get(BROKER_URL_PARAM);
    }

    /**
     * constraint error generator file path
     * @return
     */
    public String getConstraintErrorGenerator() {
        return params.get(CONSTRAINT_ERROR_GENERATOR_PARAM);
    }
}
