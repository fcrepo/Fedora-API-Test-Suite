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

    private String id;
    private String className;
    private String title;
    private String description;
    private String specLink;

    /**
     * Default constructor
     *
     * @param id
     * @param className
     * @param title
     * @param description
     * @param specLink
     */
    public TestInfo(final String id, final String className, final String title, final String description,
                    final String specLink) {
        this.id = id;
        this.className = className;
        this.title = title;
        this.description = description;
        this.specLink = specLink;

        if (TEST_INFO.containsKey(title)) {
            throw new RuntimeException(
                "The test titles must be unique across the test suite: " + title + " is duplicated.");
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
     * @return className
     */
    public String getClassName() {
        return className;
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
