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
package com.ibr.fedora.report;

import com.ibr.fedora.TestSuiteGlobals;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.xml.XmlSuite;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class EarlReporter extends EarlCoreReporter implements IReporter {
    private static final String PASS = "TEST PASSED";
    private static final String FAIL = "TEST FAILED";
    private static final String SKIP = "TEST SKIPPED";
    private IResultMap passedTests;
    private IResultMap failedTests;
    private IResultMap skippedTests;

    @Override
    public void generateReport(final List<XmlSuite> xmlSuites, final List<ISuite> suites,
    final String outputDirectory) {
    try {
        createWriter(TestSuiteGlobals.outputDirectory);
    } catch (IOException e) {
        e.printStackTrace(System.err);
        System.exit(1);
    }
    createModel();
    try {
        createAssertions(suites);
    } catch (NoSuchMethodException e) {
        e.printStackTrace();
    } catch (InstantiationException e) {
        e.printStackTrace();
    } catch (IllegalAccessException e) {
        e.printStackTrace();
    } catch (InvocationTargetException e) {
        e.printStackTrace();
    }
    write();
    try {
        endWriter();
    } catch (IOException e) {
        e.printStackTrace(System.err);
        System.exit(1);
    }
    }

    private void createAssertions(final List<ISuite> suites) throws
    NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    for (ISuite suite : suites) {
        // Make the Assertor Resource (the thing doing the testing)
        final Resource assertorRes = model.createResource(TestSuiteGlobals.earlReportAssertor);
        assertorRes.addProperty(RDF.type, Assertor);

        final Map<String, ISuiteResult> tests = suite.getResults();
        for (ISuiteResult results : tests.values()) {
            final ITestContext testContext = results.getTestContext();
            passedTests = testContext.getPassedTests();
            failedTests = testContext.getFailedTests();
            skippedTests = testContext.getSkippedTests();
        }

        final String[][] r = TestSuiteGlobals.orderTestsResults(passedTests, skippedTests, failedTests);
        getResultProperties(r);
    }
    }

    private void getResultProperties(final String[][] tests) throws
    InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    for (String[] r : tests) {
        makeResultResource(r);
    }
    }

    private void makeResultResource(final String[] result) throws
    NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    final Resource assertionResource = model.createResource(null, EarlCoreReporter.Assertion);

    final  Resource resultResource = model.createResource(null, EarlCoreReporter.TestResult);

    final Resource subjectResource = model.getResource(result[0]);
    final Resource assertorResource = model.getResource(TestSuiteGlobals.earlReportAssertor);

    assertionResource.addProperty(EarlCoreReporter.testSubject, subjectResource);

    assertionResource.addProperty(EarlCoreReporter.test, model.getResource(result[3]));

    switch (result[1]) {
        case "FAIL":
            resultResource.addProperty(EarlCoreReporter.outcome, EarlCoreReporter.failed);
            break;
        case "PASS":
            resultResource.addProperty(EarlCoreReporter.outcome, EarlCoreReporter.passed);
            break;
        case "SKIPPED":
            resultResource.addProperty(EarlCoreReporter.outcome, EarlCoreReporter.untested);
            break;
        default:
            break;
    }

    if (!result[4].isEmpty()) {
        createExceptionProperty(result[4], resultResource);
    }

    assertionResource.addProperty(EarlCoreReporter.assertedBy, assertorResource);

    resultResource.addProperty(DCTerms.date, model.createTypedLiteral(GregorianCalendar.getInstance()));

    /*
     * Add the above resources to the Assertion Resource
     */
    assertionResource.addProperty(EarlCoreReporter.testResult, resultResource);
    }

    private void createExceptionProperty(final String stackTrace, final Resource resource) {
    resource.addProperty(DCTerms.description, stackTrace);
    }
    }
