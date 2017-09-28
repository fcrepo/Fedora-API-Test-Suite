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
package com.ibr.fedora;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        TestNG testng = new TestNG();
        XmlSuite suite = new XmlSuite();
        suite.setName("ldptest");

        XmlTest test = new XmlTest(suite);

        Map<String, String> params = new HashMap<String, String>();

        List<XmlClass> classes = new ArrayList<XmlClass>();

        int i = 0;
        for(String arg : args){
            params.put("param" + ++i, arg);
        }

        classes.add(new XmlClass("com.ibr.fedora.testsuite.HttpPost"));
        classes.add(new XmlClass("com.ibr.fedora.testsuite.HttpPut"));
        classes.add(new XmlClass("com.ibr.fedora.testsuite.HttpGet"));
        classes.add(new XmlClass("com.ibr.fedora.testsuite.HttpHead"));
        classes.add(new XmlClass("com.ibr.fedora.testsuite.HttpDelete"));
        classes.add(new XmlClass("com.ibr.fedora.testsuite.ExternalBinaryContent"));
        //classes.add(new XmlClass("com.ibr.fedora.testsuite.HttpPatch"));

        test.setParameters(params);
        test.setXmlClasses(classes);

        List<XmlTest> tests = new ArrayList<XmlTest>();
        tests.add(test);

        suite.setTests(tests);

        List<XmlSuite> suites = new ArrayList<XmlSuite>();
        suites.add(suite);

        testng.setXmlSuites(suites);
        testng.run();
    }
}
