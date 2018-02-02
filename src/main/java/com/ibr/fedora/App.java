/**
 * @author Jorge Abrego, Fernando Cardoza
 */
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

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class App {
     private App() { }

    /**
     * Main configuration and execution of test cases
     * 
     * @param args
     */
    public static void main(final String[] args) {
    final Options options = new Options();
    final Option baseurl = new Option("b", "baseurl", true, "base url");
    baseurl.setRequired(true);
    options.addOption(baseurl);
    final Option user = new Option("u", "user", true, "user login");
    user.setRequired(false);
    options.addOption(user);
    final Option password = new Option("p", "password", true, "user's password");
    password.setRequired(false);
    options.addOption(password);

    final CommandLineParser parser = new BasicParser();
    final HelpFormatter formatter = new HelpFormatter();
    CommandLine cmd;
    try {
    cmd = parser.parse(options, args);
    } catch (ParseException e) {
        System.out.println(e.getMessage());
        formatter.printHelp("Fedora Test Suite", options);
        System.exit(1);
        return;
    }

    final String inputUrl = cmd.getOptionValue("baseurl");
    final String inputUser = cmd.getOptionValue("user") == null ? "" : cmd.getOptionValue("user");
    final String inputPassword = cmd.getOptionValue("password") == null ? "" : cmd.getOptionValue("password");

    final TestNG testng = new TestNG();
    final XmlSuite suite = new XmlSuite();
    suite.setName("ldptest");
    final XmlTest test = new XmlTest(suite);
    final Map<String, String> params = new HashMap<String, String>();
    final List<XmlClass> classes = new ArrayList<XmlClass>();

    classes.add(new XmlClass("com.ibr.fedora.testsuite.SetUpSuite"));
    classes.add(new XmlClass("com.ibr.fedora.testsuite.Container"));
    classes.add(new XmlClass("com.ibr.fedora.testsuite.Ldpnr"));
    classes.add(new XmlClass("com.ibr.fedora.testsuite.HttpGet"));
    classes.add(new XmlClass("com.ibr.fedora.testsuite.HttpHead"));
    classes.add(new XmlClass("com.ibr.fedora.testsuite.HttpOptions"));
    classes.add(new XmlClass("com.ibr.fedora.testsuite.HttpPost"));
    classes.add(new XmlClass("com.ibr.fedora.testsuite.HttpPut"));
    classes.add(new XmlClass("com.ibr.fedora.testsuite.HttpPatch"));
    classes.add(new XmlClass("com.ibr.fedora.testsuite.HttpDelete"));
    classes.add(new XmlClass("com.ibr.fedora.testsuite.ExternalBinaryContent"));
    //Create the default container
    params.put("param1", TestSuiteGlobals.containerTestSuite(inputUrl, inputUser, inputPassword));
    params.put("param2", inputUser);
    params.put("param3", inputPassword);

    test.setParameters(params);
    test.setXmlClasses(classes);

    final List<XmlTest> tests = new ArrayList<XmlTest>();
    tests.add(test);

    suite.setTests(tests);

    final List<XmlSuite> suites = new ArrayList<XmlSuite>();
    suites.add(suite);

    testng.setXmlSuites(suites);
    testng.run();    }
    }
