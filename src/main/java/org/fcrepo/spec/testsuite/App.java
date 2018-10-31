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

import static org.fcrepo.spec.testsuite.TestParameters.AUTHENTICATOR_CLASS_PARAM;
import static org.fcrepo.spec.testsuite.TestParameters.BROKER_URL_PARAM;
import static org.fcrepo.spec.testsuite.TestParameters.CONFIG_FILE_PARAM;
import static org.fcrepo.spec.testsuite.TestParameters.CONSTRAINT_ERROR_GENERATOR_PARAM;
import static org.fcrepo.spec.testsuite.TestParameters.PERMISSIONLESS_USER_AUTH_HEADER;
import static org.fcrepo.spec.testsuite.TestParameters.PERMISSIONLESS_USER_PASSWORD_PARAM;
import static org.fcrepo.spec.testsuite.TestParameters.PERMISSIONLESS_USER_WEBID_PARAM;
import static org.fcrepo.spec.testsuite.TestParameters.QUEUE_NAME_PARAM;
import static org.fcrepo.spec.testsuite.TestParameters.ROOT_CONTROLLER_USER_AUTH_HEADER;
import static org.fcrepo.spec.testsuite.TestParameters.ROOT_CONTROLLER_USER_PASSWORD_PARAM;
import static org.fcrepo.spec.testsuite.TestParameters.ROOT_CONTROLLER_USER_WEBID_PARAM;
import static org.fcrepo.spec.testsuite.TestParameters.ROOT_URL_PARAM;
import static org.fcrepo.spec.testsuite.TestParameters.TOPIC_NAME_PARAM;
import static org.testng.util.Strings.isNullOrEmpty;

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

    private final static String TESTNGXML_PARAM = "testngxml";
    private final static String REQUIREMENTS_PARAM = "requirements";
    private final static String SITE_NAME_PARAM = "site-name";


    private App() {
    }


    /*
     * Map of configuration options and whether they are required.
     */
    private static Map<String, Boolean> configArgs = new HashMap<>();

    static {
        configArgs.put(ROOT_URL_PARAM, true);
        configArgs.put(PERMISSIONLESS_USER_WEBID_PARAM, true);
        configArgs.put(PERMISSIONLESS_USER_PASSWORD_PARAM, false);
        configArgs.put(PERMISSIONLESS_USER_AUTH_HEADER, false);
        configArgs.put(ROOT_CONTROLLER_USER_WEBID_PARAM, true);
        configArgs.put(ROOT_CONTROLLER_USER_PASSWORD_PARAM, false);
        configArgs.put(ROOT_CONTROLLER_USER_AUTH_HEADER, false);
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
        options.addOption(new Option("b", ROOT_URL_PARAM, true, "The root URL of the repository"));
        options.addOption(new Option("u", PERMISSIONLESS_USER_WEBID_PARAM, true,
                                     "A URI representing the WebID of a user with no permissions."));
        options.addOption(
            new Option("p", PERMISSIONLESS_USER_PASSWORD_PARAM, true, "Password of user with basic user role"));
        options.addOption(new Option("a", ROOT_CONTROLLER_USER_WEBID_PARAM, true,
                                     "A URI representing the WebID of a user with read, write, and control" +
                                     " permissions on root container."));
        options.addOption(
            new Option("s", ROOT_CONTROLLER_USER_PASSWORD_PARAM, true, "Password of user with admin role"));

        options.addOption(new Option("R", ROOT_CONTROLLER_USER_AUTH_HEADER, true,
                                     "\"Authorization\" header value for a user with read, write, and control. " +
                                     "When present, this value will be added to the request effectively " +
                                     "overriding Authenticator implementations, custom or default, found in the " +
                                     "classpath."));

        options.addOption(new Option("P", PERMISSIONLESS_USER_AUTH_HEADER, true,
                                     "\"Authorization\" header value for a user with no permissions. " +
                                     "When present, this value will be added to the request effectively " +
                                     "overriding Authenticator implementations, custom or default, found in the " +
                                     "classpath."));
        options.addOption( new Option("x", TESTNGXML_PARAM, true, "TestNG XML file"));
        options.addOption(new Option("r", REQUIREMENTS_PARAM, true,
                                     "Requirement levels. One or more of the following, " +
                                     "separated by ',': [ALL|MUST|SHOULD|MAY]"));
        options.addOption(new Option("c", CONFIG_FILE_PARAM, true, "Configuration file of test parameters."));
        options.addOption(new Option("n", SITE_NAME_PARAM, true,
                                     "Site name from configuration file (defaults to \"default\")"));
        options.addOption(new Option("k", BROKER_URL_PARAM, true, "The URL of the JMS broker."));
        options.addOption(new Option("q", QUEUE_NAME_PARAM, true, "Queue name for events (if applicable)"));
        options.addOption(new Option("t", TOPIC_NAME_PARAM, true, "Topic name for events (if applicable)"));
        options.addOption(new Option("g", CONSTRAINT_ERROR_GENERATOR_PARAM, true,
                                     "A file containing a SPARQL query that will trigger a constraint error."));
        options
            .addOption(new Option("A", AUTHENTICATOR_CLASS_PARAM, true,
                                  "The class name of the Authenticator implementation. This class must be " +
                                  "packaged in a jar file that is placed in an 'authenticators' directory adjacent " +
                                  "to the testsuite jar.  If there is only one implementation found in the jar(s) in " +
                                  "the 'authenticators' directory, it will be discovered and used automatically.  " +
                                  "In other words, if you have only one implementation you don't need to use this " +
                                  "optional parameter."));

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

        TestParameters.initialize(params);
        final TestParameters tp = TestParameters.get();
        if (isNullOrEmpty(tp.getQueueName()) &&
            isNullOrEmpty(tp.getTopicName())) {
            throw new RuntimeException(String.format("One of %s, %s must be provided", QUEUE_NAME_PARAM,
                                                     TOPIC_NAME_PARAM));
        }

        //validate that the webids are URIs
        try {
            URI.create(tp.getRootControllerUserWebId());
            URI.create(tp.getPermissionlessUserWebId());
        } catch (Exception ex) {
            printHelpAndExit("WebID parameters must be well-formed URIs: " + ex.getMessage(),
                             options);
        }

        if (isNullOrEmpty(tp.getPermissionlessUserAuthHeader()) ||
            isNullOrEmpty(tp.getRootControllerUserAuthHeader())) {
            try {
                //initialize the resolver
                AuthenticatorResolver.initialize(cmd.getOptionValue(AUTHENTICATOR_CLASS_PARAM, null));
            } catch (final Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
                return;
            }
        }

        tp.setTestContainerUrl(TestSuiteGlobals.containerTestSuite());

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
