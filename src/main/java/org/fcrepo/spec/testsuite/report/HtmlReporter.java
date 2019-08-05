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
package org.fcrepo.spec.testsuite.report;

import static org.fcrepo.spec.testsuite.TestSuiteGlobals.orderTestsResults;
import static org.rendersnake.HtmlAttributesFactory.NO_ESCAPE;
import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.fcrepo.spec.testsuite.App;
import org.fcrepo.spec.testsuite.TestParameters;
import org.fcrepo.spec.testsuite.TestSuiteGlobals;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.StringResource;
import org.springframework.format.number.PercentFormatter;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class HtmlReporter implements IReporter {

    private IResultMap passedTests;
    private IResultMap failedTests;
    private IResultMap skippedTests;
    private HtmlCanvas html;

    /**
     * Generate HTML report main method
     *
     * @param xmlSuites
     * @param suites
     * @param outputDirectory
     */
    @Override
    public void generateReport(final List<XmlSuite> xmlSuites,
                               final List<ISuite> suites, final String outputDirectory) {
        try {
            for (ISuite suite : suites) {
                html = new HtmlCanvas();
                html.html().head();

                writeCss();
                html.title().content("Fedora API Test Suite Report")._head()
                    .body();
                html.h1().content("Fedora API Test Suite Summary");

                html.h2().content("for " + TestParameters.get().getImplementationName() + " " +
                                  TestParameters.get().getImplementationVersion());

                // Getting the results for the said suite
                final Map<String, ISuiteResult> suiteResults = suite.getResults();
                final ISuiteResult suiteResult = suiteResults.get("Fedora API Specification Tests");

                if (suiteResult == null) {
                    throw new RuntimeException("Unable to find expected test-suite: " +
                            "'Fedora API Specification Tests', configured in the <suite> tag of 'testng.xml'!");
                }

                final ITestContext tc = suiteResult.getTestContext();
                passedTests = tc.getPassedTests();
                failedTests = tc.getFailedTests();
                skippedTests = tc.getSkippedTests();

                // Display results summary
                displayResultsSummary();

                //Display methods summary
                makeMethodSummaryTable();

                // Add footer
                displayFooter();

                // send html to a file
                createWriter(html.toHtml());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayResultsSummary() throws IOException {
        html.table(class_("indented"));
        html.tr().th().content("Req Level");
        html.th().content("Num Pass");
        html.th().content("Num Fail");
        html.th().content("Num Skip");
        html.th().content("% Pass")._tr();

        // Get all three requirement levels
        final int passMust = getNumTestsByRequirement(passedTests, "MUST");
        final int passShould = getNumTestsByRequirement(passedTests, "SHOULD");
        final int passMay = getNumTestsByRequirement(passedTests, "MAY");

        final int failMust = getNumTestsByRequirement(failedTests, "MUST");
        final int failShould = getNumTestsByRequirement(failedTests, "SHOULD");
        final int failMay = getNumTestsByRequirement(failedTests, "MAY");

        final int skipMust = getNumTestsByRequirement(skippedTests, "MUST");
        final int skipShould = getNumTestsByRequirement(skippedTests, "SHOULD");
        final int skipMay = getNumTestsByRequirement(skippedTests, "MAY");

        final String rateMust = getPassPercentage(passMust, failMust);
        final String rateShould = getPassPercentage(passShould, failShould);
        final String rateMay = getPassPercentage(passMay, failMay);

        displayRequirementRow(passMust, failMust, skipMust, rateMust, "MUST");
        displayRequirementRow(passShould, failShould, skipShould, rateShould, "SHOULD");
        displayRequirementRow(passMay, failMay, skipMay, rateMay, "MAY");

        final int passTotal = passMust + passShould + passMay;
        final int failTotal = failMust + failShould + failMay;
        final int skipTotal = skipMust + skipShould + skipMay;
        final String rateTotal = getPassPercentage(passTotal, failTotal);

        displayRequirementRow(passTotal, failTotal, skipTotal, rateTotal, "Total");

        html._table();
        html.br();
    }

    private int getNumTestsByRequirement(final IResultMap results, final String req) {
        int numResults = 0;
        for (final ITestResult result : results.getAllResults()) {
            if (req.equalsIgnoreCase(result.getMethod().getGroups()[0])) {
                numResults++;
            }
        }
        return numResults;
    }

    private String getPassPercentage(final int pass, final int fail) {
        if (pass + fail > 0) {
            final Float number = pass / new Float(pass + fail);
            final PercentFormatter formatter = new PercentFormatter();
            return formatter.print(number, Locale.getDefault());
        }
        return "0";
    }

    private void displayRequirementRow(final int pass,
                                       final int fail,
                                       final int skip,
                                       final String rate,
                                       final String reqLevel) throws IOException {
        html.tr();
        html.td().content(reqLevel);
        html.td().span(class_("PASS")).content(pass)._td();
        html.td().span(class_("FAIL")).content(fail)._td();
        html.td().span(class_("SKIPPED")).content(skip)._td();
        html.td().content(rate);
        html._tr();
    }

    private void displayFooter() throws IOException {
        final Properties properties = new Properties();
        properties.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));
        final String version = properties.getProperty("version");
        final String buildNumber = properties.getProperty("buildNumber");
        final String buildTimestamp = properties.getProperty("buildTimestamp");

        html.footer();
        html.p().content("Release: " + version + " | #" + buildNumber + " (" + buildTimestamp + ")");
        html._footer();
    }

    private void writeCss() throws IOException {

        html.style().write(StringResource.get(TestSuiteGlobals.cssReport), NO_ESCAPE)
            ._style();
    }

    private void createWriter(final String output) {
        final File dir = new File(TestSuiteGlobals.outputDirectory);
        dir.mkdirs();

        final String fileName = TestSuiteGlobals.outputName + "-execution-report.html";
        System.out.println("Writing HTML results:");
        final File file = new File(dir, fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(output);
            System.out.println("\t" + file.getAbsolutePath());

        } catch (IOException e) {
        }
    }

    private void makeMethodSummaryTable()
        throws IOException {
        html.table(class_("indented"));
        html.tr().th().content("Specification Section");
        html.th().content("Req Level");
        html.th().content("Result");
        html.th().content("Test Description");
        html.th().content("Implementation Note")._tr();
        final Map<String, String[]> results = orderTestsResults(passedTests, skippedTests, failedTests);

        final Map<String, String> implNotes = App.getImplementationNotes();
        for (String[] r : results.values()) {
            html.tr();
            html.td().a(href(r[0]).target("_blank")).write(r[3])._a()._td();
            html.td().content(r[5]);
            html.td().span(class_(r[1])).content(r[1])._td();
            html.td().content(r[2]);
            html.td().content(implNotes.getOrDefault(r[3], ""));
            html._tr();
        }

        html._table();
        html.br();
    }
}
