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

import java.util.HashMap;
import java.util.Map;

/**
 * A class that holds static description information related to tests.
 *
 * @author Daniel Bernstein
 */
public class TestInfo {

    private final static Map<String, TestInfo> TEST_INFO = new HashMap<>();

    private final String id;
    private final Class testClass;
    private final String title;
    private final String description;
    private final String specLink;

    /**
     * Default constructor
     *
     * @param id
     * @param testClass
     * @param title
     * @param description
     * @param specLink
     */
    public TestInfo(final String id, final Class testClass, final String title, final String description,
                    final String specLink) {
        this.id = id;
        this.testClass = testClass;
        this.title = title;
        this.description = description;
        this.specLink = specLink;

        if (TEST_INFO.containsKey(title)) {
            throw new RuntimeException(
                "The crud titles must be unique across the crud suite: " + title + " is duplicated.");
        }
        TEST_INFO.put(title, this);
    }

    /**
     * @param methodName
     * @return
     */
    public static TestInfo getByMethodName(final String methodName) {
        return TEST_INFO.get(methodName);
    }

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @return testClass
     */
    public Class getTestClass() {
        return testClass;
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * The reference to the specification
     *
     * @return specLink
     */
    public String getSpecLink() {
        return specLink;
    }

}
