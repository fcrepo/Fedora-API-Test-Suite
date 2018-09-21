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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.testng.TestNG;
import org.testng.xml.SuiteXmlParser;
import org.testng.xml.XmlSuite;

import com.esotericsoftware.yamlbeans.YamlReader;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class App {
    private App() {
    }

    /*
     * Map of configuration options and whether they are required.
     */
    private static Map<String, Boolean> configArgs = new HashMap<>();

    static {
        configArgs.put("rooturl", true);
        configArgs.put("user", true);
        configArgs.put("password", true);
        configArgs.put("admin-user", true);
        configArgs.put("admin-password", true);
        configArgs.put("testngxml", false);
        configArgs.put("requirements", false);
    }

    /**
     * Main configuration and execution of crud cases
     *
     * @param args
     */
    public static void main(final String[] args) {
        final Options options = new Options();
        final Option rootUrl = new Option("b", "rooturl", true, "The root URL of the repository");
        options.addOption(rootUrl);
        final Option user = new Option("u", "user", true, "Username of user with basic user role");
        options.addOption(user);
        final Option password = new Option("p", "password", true, "Password of user with basic user role");
        options.addOption(password);
        final Option adminUser = new Option("a", "admin-user", true, "Username of user with admin role");
        options.addOption(adminUser);
        final Option adminPassword = new Option("s", "admin-password", true, "Password of user with admin role");
        options.addOption(adminPassword);

        final Option testngxml = new Option("x", "testngxml", true, "TestNG XML file");
        testngxml.setRequired(false);
        options.addOption(testngxml);
        final Option reqs = new Option("r", "requirements", true, "Requirement levels. One or more of the following, " +
                                                                  "separated by ',': [ALL|MUST|SHOULD|MAY]");
        reqs.setRequired(false);
        options.addOption(reqs);

        final Option configFile = new Option("c", "configFile", true, "Configuration file of test parameters.");
        configFile.setRequired(false);
        options.addOption(configFile);
        final Option configFileSite = new Option("n", "configFileSitename", true,
                "Site name from configuration file (defaults to \"default\")");
        configFileSite.setRequired(false);
        options.addOption(configFileSite);

        final CommandLineParser parser = new BasicParser();
        final HelpFormatter formatter = new HelpFormatter();
        final CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (final ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Fedora Test Suite", options);
            System.exit(1);
            return;
        }

        Map<String, String> params = new HashMap<>();
        if (cmd.hasOption("configFile")) {
            final File configurationFile = new File(cmd.getOptionValue("configFile"));
            if (configurationFile.exists()) {
                final String sitename = cmd.getOptionValue("configFileSitename") == null ? "default" : cmd
                        .getOptionValue("configFileSitename");
                params = retrieveConfig(configurationFile, sitename);
            }
        }

        for (final String opt : configArgs.keySet()) {
            // Allow command line overriding of config file arguments
            if (cmd.getOptionValue(opt) != null) {
                params.put(opt, cmd.getOptionValue(opt));
            }
            if (!params.containsKey(opt)) {
                if (configArgs.get(opt)) {
                    throw new RuntimeException("Argument \"" + opt + "\" is required");
                }
                // Fill in missing parts with blanks
                params.put(opt, "");
            }
        }

        //Create the default container
        final Map<String, String> testParams = new HashMap<>();
        testParams.put("param0", params.get("rooturl"));
        testParams.put("param1", TestSuiteGlobals.containerTestSuite(params.get("rooturl"), params.get(
                "admin-user"), params.get("admin-password")));
        testParams.put("param2", params.get("admin-user"));
        testParams.put("param3", params.get("admin-password"));
        testParams.put("param4", params.get("user"));
        testParams.put("param5", params.get("password"));

        InputStream inputStream = null;
        if (params.get("testngxml").toString().isEmpty()) {
            inputStream = ClassLoader.getSystemResourceAsStream("testng.xml");
        } else {
            try {
                inputStream = new FileInputStream(params.get("testngxml").toString());
            } catch (final FileNotFoundException e) {
                System.err.println("Unable to open '" + params.get("testngxml").toString() + "'." + e.getMessage());
                System.exit(1);
            }
        }

        final String testFilename = params.get("testngxml").toString().isEmpty() ? "Default testng.xml" : params.get(
                "testngxml").toString();
        final SuiteXmlParser xmlParser = new SuiteXmlParser();
        final XmlSuite xmlSuite = xmlParser.parse(testFilename, inputStream, true);
        xmlSuite.setParameters(testParams);

        final TestNG testng = new TestNG();
        testng.setCommandLineSuite(xmlSuite);

        // Set requirement-level groups to be run
        if (!params.get("requirements").isEmpty()) {
            testng.setGroups(params.get("requirements").toLowerCase());
        }

        try {
            testng.run();
        } finally {
            TestSuiteGlobals.cleanupTestResources();
        }
    }

    /**
     * This method parses the provided configFile into its equivalent command-line args
     *
     * @param configFile containing config args
     * @param siteName the site name from the config file to use
     * @return Array of args
     */
    @SuppressWarnings("unchecked")
    protected static Map<String, String> retrieveConfig(final File configFile, final String siteName) {
        if (!configFile.exists()) {
            printHelp("Configuration file does not exist: " + configFile);
        }
        try {
            final YamlReader reader = new YamlReader(new FileReader(configFile));
            final Map<String, Map<String, String>> config = (Map<String, Map<String, String>>) reader.read();
            if (config.containsKey(siteName)) {
                return config.get(siteName);
            } else {
                throw new RuntimeException("Unable to find site \"" + siteName + "\" in configuration file.");
            }
        } catch (final IOException e) {
            throw new RuntimeException("Unable to read configuration file due to: " + e.getMessage(), e);
        }
    }

    /**
     * Print help/usage information
     *
     * @param message the message or null for none
     */
    private static void printHelp(final String message) {
        final HelpFormatter formatter = new HelpFormatter();
        final PrintWriter writer = new PrintWriter(System.out);
        if (message != null) {
            writer.println("\n-----------------------\n" + message + "\n-----------------------\n");
        }

        writer.println("Running Fedora API Test Suite from command line arguments");
        // formatter.printHelp(writer, 80, "java -jar testSuite-shaded.jar", "", options, 4, 4, "", true);

        writer.println("\n");
        writer.flush();

        if (message != null) {
            throw new RuntimeException(message);
        } else {
            throw new RuntimeException();
        }
    }
}
