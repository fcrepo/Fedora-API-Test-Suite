package com.ibr.fedora.report;

import com.ibr.fedora.TestSuiteGlobals;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.testng.*;
import org.testng.internal.Utils;
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
                getResultProperties(failedTests, FAIL);
                getResultProperties(skippedTests, SKIP);
                getResultProperties(passedTests, PASS);
            }
        }
    }

    private void getResultProperties(IResultMap tests, String status) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (ITestResult result : tests.getAllResults()) {
            makeResultResource(result, status);
        }
    }

    private void makeResultResource(ITestResult result, String status) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        //String className = result.getTestClass().getName();
        //className = className.substring(className.lastIndexOf(".") + 1);

        Resource assertionResource = model.createResource(null, EarlCoreReporter.Assertion);

        Resource resultResource = model.createResource(null, EarlCoreReporter.TestResult);

        Resource subjectResource = model.getResource(createTestCaseURL(result.getName()));
        Resource assertorResource = model.getResource(TestSuiteGlobals.earlReportAssertor);

        assertionResource.addProperty(EarlCoreReporter.testSubject, subjectResource);

        assertionResource.addProperty(EarlCoreReporter.test, model.getResource(createTestCaseTitle(result.getName())));

        switch (status) {
            case FAIL:
                resultResource.addProperty(EarlCoreReporter.outcome, EarlCoreReporter.failed);
                break;
            case PASS:
                resultResource.addProperty(EarlCoreReporter.outcome, EarlCoreReporter.passed);
                break;
            case SKIP:
                resultResource.addProperty(EarlCoreReporter.outcome, EarlCoreReporter.untested);
                break;
            default:
                break;
        }

        if (result.getThrowable() != null) {
            createExceptionProperty(result.getThrowable(), resultResource);
        }


        assertionResource.addProperty(EarlCoreReporter.assertedBy, assertorResource);

        resultResource.addProperty(DCTerms.date, model.createTypedLiteral(GregorianCalendar.getInstance()));

		/*
		 * Add the above resources to the Assertion Resource
		 */
        assertionResource.addProperty(EarlCoreReporter.testResult, resultResource);
    }

    private void createExceptionProperty(Throwable thrown, Resource resource) {
        if (thrown.getClass().getName().contains(SKIP))
            resource.addProperty(DCTerms.description, thrown.getMessage());
        else
            resource.addLiteral(DCTerms.description,  Utils.stackTrace(thrown, false)[0]);
    }
}
