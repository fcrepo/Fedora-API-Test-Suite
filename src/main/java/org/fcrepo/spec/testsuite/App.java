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
        final Option testngxml = new Option("x", "testngxml", true, "TestNG XML file");
        testngxml.setRequired(false);
        options.addOption(testngxml);
        final Option reqs = new Option("r", "requirements", true, "Requirement levels. One or more of the following, " +
                                                                  "separated by ',': " +
                                                                  "[ALL|MUST|SHOULD|MAY|MUSTNOT|SHOULDNOT]");
        reqs.setRequired(false);
        reqs.setValueSeparator(',');
        options.addOption(reqs);

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
        final String inputXml = cmd.getOptionValue("testngxml");
        final String[] requirements = cmd.getOptionValues("requirements");

        //Create the default container
        final Map<String, String> params = new HashMap<>();
        params.put("param1", TestSuiteGlobals.containerTestSuite(inputUrl, inputUser, inputPassword));
        params.put("param2", inputUser);
        params.put("param3", inputPassword);

        InputStream inputStream = null;
        if (inputXml == null) {
            inputStream = ClassLoader.getSystemResourceAsStream("testng.xml");
        } else {
            try {
                inputStream = new FileInputStream(inputXml);
            } catch (FileNotFoundException e) {
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

        testng.run();
    }
}
