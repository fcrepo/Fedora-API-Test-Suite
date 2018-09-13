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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.jena.ext.com.google.common.base.Joiner;
import org.testng.TestNG;
import org.testng.xml.SuiteXmlParser;
import org.testng.xml.XmlSuite;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class App {
    private App() {
    }

    /**
     * Main configuration and execution of crud cases
     *
     * @param args
     */
    public static void main(final String[] args) {
        final Options options = new Options();
        final Option baseurl = new Option("b", "baseurl", true, "base url");
        baseurl.setRequired(true);
        options.addOption(baseurl);
        final Option user = new Option("u", "user", true, "Username of user with basic user role");
        user.setRequired(true);
        options.addOption(user);
        final Option password = new Option("p", "password", true, "Password of user with basic user role");
        password.setRequired(true);
        options.addOption(password);
        final Option adminUser = new Option("a", "admin-user", true, "Username of user with admin role");
        adminUser.setRequired(true);
        options.addOption(adminUser);
        final Option adminPassword = new Option("s", "admin-password", true, "Password of user with admin role");
        adminPassword.setRequired(true);
        options.addOption(adminPassword);

        final Option testngxml = new Option("x", "testngxml", true, "TestNG XML file");
        testngxml.setRequired(false);
        options.addOption(testngxml);
        final Option reqs = new Option("r", "requirements", true, "Requirement levels. One or more of the following, " +
                                                                  "separated by ',': [ALL|MUST|SHOULD|MAY]");
        reqs.setRequired(false);
        reqs.setValueSeparator(',');
        options.addOption(reqs);

        final CommandLineParser parser = new BasicParser();
        final HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (final ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Fedora Test Suite", options);
            System.exit(1);
            return;
        }

        final String inputUrl = cmd.getOptionValue("baseurl");
        final String inputUser = cmd.getOptionValue("user") == null ? "" : cmd.getOptionValue("user");
        final String inputPassword = cmd.getOptionValue("password") == null ? "" : cmd.getOptionValue("password");
        final String inputAdminUser = cmd.getOptionValue("admin-user") == null ? "" : cmd.getOptionValue("admin-user");
        final String inputAdminPassword =
            cmd.getOptionValue("admin-password") == null ? "" : cmd.getOptionValue("admin-password");

        final String inputXml = cmd.getOptionValue("testngxml");
        final String[] requirements = cmd.getOptionValues("requirements");

        //Create the default container
        final Map<String, String> params = new HashMap<>();
        params.put("param1", TestSuiteGlobals.containerTestSuite(inputUrl, inputAdminUser, inputAdminPassword));
        params.put("param2", inputAdminUser);
        params.put("param3", inputAdminPassword);
        params.put("param4", inputUser);
        params.put("param5", inputPassword);

        InputStream inputStream = null;
        if (inputXml == null) {
            inputStream = ClassLoader.getSystemResourceAsStream("testng.xml");
        } else {
            try {
                inputStream = new FileInputStream(inputXml);
            } catch (final FileNotFoundException e) {
                System.err.println("Unable to open '" + inputXml + "'." + e.getMessage());
                System.exit(1);
            }
        }

        final String testFilename = inputXml == null ? "Default testng.xml" : inputXml;
        final SuiteXmlParser xmlParser = new SuiteXmlParser();
        final XmlSuite xmlSuite = xmlParser.parse(testFilename, inputStream, true);
        xmlSuite.setParameters(params);

        final TestNG testng = new TestNG();
        testng.setCommandLineSuite(xmlSuite);

        // Set requirement-level groups to be run
        if (requirements != null && requirements.length > 0) {
            testng.setGroups(Joiner.on(',').join(requirements).toLowerCase());
        }

        try {
            testng.run();
        } finally {
            TestSuiteGlobals.cleanupTestResources();
        }
    }
}
