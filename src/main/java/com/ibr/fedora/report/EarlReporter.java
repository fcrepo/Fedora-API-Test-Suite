package com.ibr.fedora.report;

import com.ibr.fedora.TestSuiteGlobals;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.testng.*;
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
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
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

    private void createAssertions(List<ISuite> suites) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        for (ISuite suite : suites) {
            // Make the Assertor Resource (the thing doing the testing)
            Resource assertorRes = model.createResource(TestSuiteGlobals.earlReportAssertor);
            assertorRes.addProperty(RDF.type, Assertor);

            Map<String, ISuiteResult> tests = suite.getResults();
            for (ISuiteResult results : tests.values()) {
                ITestContext testContext = results.getTestContext();
                passedTests = testContext.getPassedTests();
                failedTests = testContext.getFailedTests();
                skippedTests = testContext.getSkippedTests();
            }

            String[][] r = TestSuiteGlobals.orderTestsResults(passedTests, skippedTests, failedTests);
            getResultProperties(r);
        }
    }

    private void getResultProperties(String[][] tests) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (String[] r : tests) {
            makeResultResource(r);
        }
    }

    private void makeResultResource(String[] result) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Resource assertionResource = model.createResource(null, EarlCoreReporter.Assertion);

        Resource resultResource = model.createResource(null, EarlCoreReporter.TestResult);

        Resource subjectResource = model.getResource(result[0]);
        Resource assertorResource = model.getResource(TestSuiteGlobals.earlReportAssertor);

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

    private void createExceptionProperty(String stackTrace, Resource resource) {
        resource.addProperty(DCTerms.description, stackTrace);
    }
}
