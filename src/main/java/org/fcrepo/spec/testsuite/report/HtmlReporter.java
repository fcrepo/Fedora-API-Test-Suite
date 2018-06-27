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

import static org.rendersnake.HtmlAttributesFactory.NO_ESCAPE;
import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fcrepo.spec.testsuite.TestSuiteGlobals;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.StringResource;
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

    private HashMap<String, Integer> failClasses;
    private HashMap<String, Integer> skipClasses;
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

                // Getting the results for the said suite
                final Map<String, ISuiteResult> suiteResults = suite.getResults();
                for (ISuiteResult sr : suiteResults.values()) {
                    final ITestContext tc = sr.getTestContext();
                    passedTests = tc.getPassedTests();
                    failedTests = tc.getFailedTests();
                    skippedTests = tc.getSkippedTests();
                }

                final HashMap<String, Integer> passClasses = getClasses(passedTests);
                failClasses = getClasses(failedTests);
                skipClasses = getClasses(skippedTests);

                html.br();

                //Display methods summary
                displayMethodsSummary(suites);

                // send html to a file
                createWriter(html.toHtml());
            }
        } catch (IOException | InvocationTargetException | IllegalAccessException | InstantiationException |
                NoSuchMethodException e) {
            e.printStackTrace();
        }
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

    private HashMap<String, Integer> getClasses(final IResultMap tests) {
        final HashMap<String, Integer> classes = new HashMap<>();
        for (ITestResult iTestResult : tests.getAllResults()) {
            String name = iTestResult.getTestClass().getName();
            name = name.substring(name.lastIndexOf(".") + 1);

            if (!classes.containsKey(name)) {
                classes.put(name, 1);
            } else {
                classes.put(name, classes.get(name) + 1);
            }
        }
        return classes;
    }

    private void displayMethodsSummary(final List<ISuite> suites) throws
        IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (ISuite suite : suites) {
            final Map<String, ISuiteResult> r = suite.getResults();
            for (ISuiteResult r2 : r.values()) {
                final ITestContext testContext = r2.getTestContext();
                makeMethodsList(testContext);
            }
        }
    }

    private void makeMethodsList(final ITestContext testContext) throws
        IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final IResultMap failed = testContext.getFailedTests();
        final IResultMap passed = testContext.getPassedTests();
        final IResultMap skipped = testContext.getSkippedTests();
        makeMethodSummaryTable(passed, skipped, failed);
        html.br();
    }

    private void makeMethodSummaryTable(final IResultMap passed, final IResultMap skipped, final IResultMap failed)
        throws IOException {
        html.table(class_("indented"));
        html.tr().th().content("Specification Section");
        html.th().content("Req Level");
        html.th().content("Result");
        html.th().content("Test Description")._tr();
        final Map<String, String[]> results = TestSuiteGlobals.orderTestsResults(passed, skipped, failed);

        for (String[] r : results.values()) {
            html.tr();
            html.td().a(href(r[0]).target("_blank")).write(r[3])._a()._td();
            html.td().content(r[5]);
            html.td().span(class_(r[1])).content(r[1])._td();
            html.td().content(r[2]);
            html._tr();
        }

        html._table();
    }
}
