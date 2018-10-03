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

    /**
     * Configuration parameter names for consistency.
     */
    private final static String ROOT_URL_PARAM = "rooturl";

    private final static String USER_NAME_PARAM = "user";

    private final static String USER_PASS_PARAM = "password";

    private final static String ADMIN_NAME_PARAM = "admin-user";

    private final static String ADMIN_PASS_PARAM = "admin-password";

    private final static String TESTNGXML_PARAM = "testngxml";

    private final static String REQUIREMENTS_PARAM = "requirements";

    public final static String BROKER_URL_PARAM = "broker-url";

    public final static String QUEUE_NAME_PARAM = "queue-name";

    public final static String TOPIC_NAME_PARAM = "topic-name";

    private final static String CONFIG_FILE_PARAM = "config-file";

    private final static String SITE_NAME_PARAM = "site-name";
    public  final static String CONSTRAINT_ERROR_GENERATOR_PARAM = "constraint-error-generator";

    /*
     * Map of configuration options and whether they are required.
     */
    private static Map<String, Boolean> configArgs = new HashMap<>();

    static {
        configArgs.put(ROOT_URL_PARAM, true);
        configArgs.put(USER_NAME_PARAM, true);
        configArgs.put(USER_PASS_PARAM, true);
        configArgs.put(ADMIN_NAME_PARAM, true);
        configArgs.put(ADMIN_PASS_PARAM, true);
        configArgs.put(TESTNGXML_PARAM, false);
        configArgs.put(REQUIREMENTS_PARAM, false);
        configArgs.put(BROKER_URL_PARAM, true);
        configArgs.put(QUEUE_NAME_PARAM, false);
        configArgs.put(TOPIC_NAME_PARAM, false);
        configArgs.put(CONSTRAINT_ERROR_GENERATOR_PARAM, false);

    }

    /**
     * Main configuration and execution of crud cases
     *
     * @param args the command line arguments.
     */
    public static void main(final String[] args) {
        final Options options = new Options();
        final Option rootUrl = new Option("b", ROOT_URL_PARAM, true, "The root URL of the repository");
        options.addOption(rootUrl);
        final Option user = new Option("u", USER_NAME_PARAM, true, "Username of user with basic user role");
        options.addOption(user);
        final Option password = new Option("p", USER_PASS_PARAM, true, "Password of user with basic user role");
        options.addOption(password);
        final Option adminUser = new Option("a", ADMIN_NAME_PARAM, true, "Username of user with admin role");
        options.addOption(adminUser);
        final Option adminPassword = new Option("s", ADMIN_PASS_PARAM, true, "Password of user with admin role");
        options.addOption(adminPassword);

        final Option testngxml = new Option("x", TESTNGXML_PARAM, true, "TestNG XML file");
        testngxml.setRequired(false);
        options.addOption(testngxml);
        final Option reqs = new Option("r", REQUIREMENTS_PARAM, true,
                "Requirement levels. One or more of the following, " +
                                                                  "separated by ',': [ALL|MUST|SHOULD|MAY]");
        reqs.setRequired(false);
        options.addOption(reqs);

        final Option configFile = new Option("c", CONFIG_FILE_PARAM, true, "Configuration file of test parameters.");
        configFile.setRequired(false);
        options.addOption(configFile);
        final Option configFileSite = new Option("n", SITE_NAME_PARAM, true,
                "Site name from configuration file (defaults to \"default\")");
        configFileSite.setRequired(false);
        options.addOption(configFileSite);

        options.addOption(new Option("k", BROKER_URL_PARAM, true, "The URL of the JMS broker."));
        options.addOption(new Option("q", QUEUE_NAME_PARAM, true, "Queue name for events (if applicable)"));
        options.addOption(new Option("t", TOPIC_NAME_PARAM, true, "Topic name for events (if applicable)"));
        options.addOption(new Option("c", CONSTRAINT_ERROR_GENERATOR_PARAM, true,
                                     "A file containing a SPARQL query that will trigger a constraint error."));

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
        if (cmd.hasOption(CONFIG_FILE_PARAM)) {
            final File configurationFile = new File(cmd.getOptionValue(CONFIG_FILE_PARAM));
            if (configurationFile.exists()) {
                final String sitename = cmd.getOptionValue(SITE_NAME_PARAM) == null ? "default" : cmd
                        .getOptionValue(SITE_NAME_PARAM);
                params = retrieveConfig(configurationFile, sitename);
            }
        }

        for (final String opt : configArgs.keySet()) {
            // Allow command line overriding of config file arguments
            if (cmd.getOptionValue(opt) != null) {
                params.put(opt, cmd.getOptionValue(opt));
            }
            if (!params.containsKey(opt) || params.get(opt).isEmpty()) {
                if (configArgs.get(opt)) {
                    throw new RuntimeException("Argument \"" + opt + "\" is required");
                }
                // Fill in missing parts with blanks
                params.put(opt, "");
            }
        }

        if ((!params.containsKey(QUEUE_NAME_PARAM) || params.get(QUEUE_NAME_PARAM).isEmpty()) &&
                (!params.containsKey(TOPIC_NAME_PARAM) || params.get(TOPIC_NAME_PARAM).isEmpty())) {
            throw new RuntimeException(String.format("One of %s, %s must be provided", QUEUE_NAME_PARAM,
                    TOPIC_NAME_PARAM));
        }

        //Create the default container
        final Map<String, String> testParams = new HashMap<>();
        testParams.put("param0", params.get(ROOT_URL_PARAM));
        testParams.put("param1", TestSuiteGlobals.containerTestSuite(params.get(ROOT_URL_PARAM), params.get(
                ADMIN_NAME_PARAM), params.get(ADMIN_PASS_PARAM)));
        testParams.put("param2", params.get(ADMIN_NAME_PARAM));
        testParams.put("param3", params.get(ADMIN_PASS_PARAM));
        testParams.put("param4", params.get(USER_NAME_PARAM));
        testParams.put("param5", params.get(USER_PASS_PARAM));
        testParams.put(BROKER_URL_PARAM, params.get(BROKER_URL_PARAM));
        testParams.put(QUEUE_NAME_PARAM, params.get(QUEUE_NAME_PARAM));
        testParams.put(TOPIC_NAME_PARAM, params.get(TOPIC_NAME_PARAM));
        testParams.put(CONSTRAINT_ERROR_GENERATOR_PARAM, params.get(CONSTRAINT_ERROR_GENERATOR_PARAM));

        InputStream inputStream = null;
        if (params.get(TESTNGXML_PARAM).toString().isEmpty()) {
            inputStream = ClassLoader.getSystemResourceAsStream("testng.xml");
        } else {
            try {
                inputStream = new FileInputStream(params.get(TESTNGXML_PARAM));
            } catch (final FileNotFoundException e) {
                System.err.println("Unable to open '" + params.get(TESTNGXML_PARAM) + "'." + e.getMessage());
                System.exit(1);
            }
        }

        final String testFilename = params.get(TESTNGXML_PARAM).isEmpty() ? "Default testng.xml" : params.get(
                TESTNGXML_PARAM);
        final SuiteXmlParser xmlParser = new SuiteXmlParser();
        final XmlSuite xmlSuite = xmlParser.parse(testFilename, inputStream, true);
        xmlSuite.setParameters(testParams);

        final TestNG testng = new TestNG();
        testng.setCommandLineSuite(xmlSuite);

        // Set requirement-level groups to be run
        if (!params.get(REQUIREMENTS_PARAM).isEmpty()) {
            testng.setGroups(params.get(REQUIREMENTS_PARAM).toLowerCase());
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
