/*
 *
 *  * The contents of this file are subject to the license and copyright
 *  * detailed in the LICENSE and NOTICE files at the root of the source
 *  * tree and available online at
 *  *
 *  *     http://duracloud.org/license/
 *
 */

/**
 * @author Jorge Abrego, Fernando Cardoza
 */

package org.fcrepo.spec.testsuite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        classes.add(new XmlClass("SetUpSuite"));
        classes.add(new XmlClass("Container"));
        classes.add(new XmlClass("Ldpnr"));
        classes.add(new XmlClass("HttpGet"));
        classes.add(new XmlClass("HttpHead"));
        classes.add(new XmlClass("HttpOptions"));
        classes.add(new XmlClass("HttpPost"));
        classes.add(new XmlClass("HttpPut"));
        classes.add(new XmlClass("HttpPatch"));
        classes.add(new XmlClass("HttpDelete"));
        classes.add(new XmlClass("ExternalBinaryContent"));
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
        testng.run();
    }
}
