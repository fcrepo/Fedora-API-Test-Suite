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
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.yamlbeans.YamlReader;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.fcrepo.spec.testsuite.authn.AuthenticatorResolver;
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
     * Configuration parameter names for consistency.
     */
    public static final String TEST_CONTAINER_URL_PARAM = "testContainerURL";

    public final static String ROOT_URL_PARAM = "rooturl";

    public final static String PERMISSIONLESS_USER_WEBID_PARAM = "permissionless-user-webid";

    public final static String PERMISSIONLESS_USER_PASSWORD_PARAM = "permissionless-user-password";

    public final static String ROOT_CONTROLLER_USER_WEBID_PARAM = "root-controller-user-webid";

    public final static String ROOT_CONTROLLER_USER_PASSWORD_PARAM = "root-controller-user-password";

    private final static String TESTNGXML_PARAM = "testngxml";

    private final static String REQUIREMENTS_PARAM = "requirements";

    public final static String BROKER_URL_PARAM = "broker-url";

    public final static String QUEUE_NAME_PARAM = "queue-name";

    public final static String TOPIC_NAME_PARAM = "topic-name";

    private final static String CONFIG_FILE_PARAM = "config-file";

    private final static String SITE_NAME_PARAM = "site-name";
    public final static String CONSTRAINT_ERROR_GENERATOR_PARAM = "constraint-error-generator";

    public final static String AUTHENTICATOR_CLASS_PARAM = "auth-class";
    /*
     * Map of configuration options and whether they are required.
     */
    private static Map<String, Boolean> configArgs = new HashMap<>();

    static {
        configArgs.put(ROOT_URL_PARAM, true);
        configArgs.put(PERMISSIONLESS_USER_WEBID_PARAM, true);
        configArgs.put(ROOT_CONTROLLER_USER_WEBID_PARAM, true);
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
        try {
            final Options options = new Options();
            final Option rootUrl = new Option("b", ROOT_URL_PARAM, true, "The root URL of the repository");
            options.addOption(rootUrl);
            final Option user = new Option("u", PERMISSIONLESS_USER_WEBID_PARAM, true,
                                           "A URI representing the WebID of a user with no permissions.");
            options.addOption(user);
            final Option password =
                new Option("p", PERMISSIONLESS_USER_PASSWORD_PARAM, true, "Password of user with basic user role");
            options.addOption(password);
            final Option rootControllerUserWebId = new Option("a", ROOT_CONTROLLER_USER_WEBID_PARAM, true,
                                                "A URI representing the WebID of a user with read, write, and control" +
                                                " permissions on root container.");
            options.addOption(rootControllerUserWebId);
            final Option adminPassword =
                new Option("s", ROOT_CONTROLLER_USER_PASSWORD_PARAM, true, "Password of user with admin role");
            options.addOption(adminPassword);

            final Option testngxml = new Option("x", TESTNGXML_PARAM, true, "TestNG XML file");
            testngxml.setRequired(false);
            options.addOption(testngxml);
            final Option reqs = new Option("r", REQUIREMENTS_PARAM, true,
                                           "Requirement levels. One or more of the following, " +
                                           "separated by ',': [ALL|MUST|SHOULD|MAY]");
            reqs.setRequired(false);
            options.addOption(reqs);

            final Option configFile =
                new Option("c", CONFIG_FILE_PARAM, true, "Configuration file of test parameters.");
            configFile.setRequired(false);
            options.addOption(configFile);
            final Option configFileSite = new Option("n", SITE_NAME_PARAM, true,
                                                     "Site name from configuration file (defaults to \"default\")");
            configFileSite.setRequired(false);
            options.addOption(configFileSite);

            options.addOption(new Option("k", BROKER_URL_PARAM, true, "The URL of the JMS broker."));
            options.addOption(new Option("q", QUEUE_NAME_PARAM, true, "Queue name for events (if applicable)"));
            options.addOption(new Option("t", TOPIC_NAME_PARAM, true, "Topic name for events (if applicable)"));
            options.addOption(new Option("g", CONSTRAINT_ERROR_GENERATOR_PARAM, true,
                                         "A file containing a SPARQL query that will trigger a constraint error."));
            options
                .addOption(new Option("A", AUTHENTICATOR_CLASS_PARAM, true,
                                      "The class name of the Authenticator implementation. This class must be " +
                                      "packaged in" +
                                      " a jar file that is placed in an 'authenticators' directory adjacent to the " +
                                      "testsuite jar, '" +
                                      System.getProperty("user.dir") + "', or '" + System.getProperty("user.home") +
                                      "'. If there is only one implementation any of the specified locations, it will" +
                                      " be " +
                                      "discovered and used automatically; ie you don't need to use this optionpal " +
                                      "parameter."));

            final CommandLineParser parser = new BasicParser();
            final CommandLine cmd;
            try {
                cmd = parser.parse(options, args);
            } catch (final ParseException e) {
                printHelpAndExit(e.getMessage(), options);
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

            //validate that the webids are URIs
            final String rootControllerWebId = params.get(ROOT_CONTROLLER_USER_WEBID_PARAM);
            final String permissionlessUserWebId = params.get(PERMISSIONLESS_USER_WEBID_PARAM);
            try {
                URI.create(rootControllerWebId);
                URI.create(permissionlessUserWebId);
            } catch (Exception ex) {
                printHelpAndExit("WebID parameters must be well-formed URIs: " + ex.getMessage(),
                                 options);
            }

            //set the passwords in the system property to be used by default authenticator
            //if necessary
            final String rootControllerPassword = params.get(ROOT_CONTROLLER_USER_PASSWORD_PARAM);
            final String permissionlessUserPassword = params.get(PERMISSIONLESS_USER_PASSWORD_PARAM);
            System.setProperty(rootControllerWebId, rootControllerPassword);
            System.setProperty(permissionlessUserWebId, permissionlessUserPassword);

            try {
                //initialize the resolver
                AuthenticatorResolver.initialize(cmd.getOptionValue(AUTHENTICATOR_CLASS_PARAM, null));
            } catch (final Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
                return;
            }

            //Create the default container
            final Map<String, String> testParams = new HashMap<>();
            testParams.put(ROOT_URL_PARAM, params.get(ROOT_URL_PARAM));
            testParams.put(TEST_CONTAINER_URL_PARAM,
                           TestSuiteGlobals.containerTestSuite(params.get(ROOT_URL_PARAM), params.get(
                               ROOT_CONTROLLER_USER_WEBID_PARAM), params.get(ROOT_CONTROLLER_USER_PASSWORD_PARAM)));
            testParams.put(ROOT_CONTROLLER_USER_WEBID_PARAM, rootControllerWebId);
            testParams.put(PERMISSIONLESS_USER_WEBID_PARAM, permissionlessUserWebId);
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

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    private static void printHelpAndExit(final String errorMessage, final Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        System.err.println(errorMessage);
        formatter.printHelp("Fedora Test Suite", options);
        System.exit(1);

    }

    /**
     * This method parses the provided configFile into its equivalent command-line args
     *
     * @param configFile containing config args
     * @param siteName   the site name from the config file to use
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
