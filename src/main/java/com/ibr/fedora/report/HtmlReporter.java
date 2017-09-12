package com.ibr.fedora.report;

import com.ibr.fedora.TestSuiteGlobals;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.StringResource;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.rendersnake.HtmlAttributesFactory.*;

public class HtmlReporter implements IReporter {

    private IResultMap passedTests;
    private IResultMap failedTests;
    private IResultMap skippedTests;

    HashMap<String, Integer> passClasses;
    HashMap<String, Integer> failClasses;
    HashMap<String, Integer> skipClasses;

    private HtmlCanvas html;

    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        try {
            for (ISuite suite : suites) {
                html = new HtmlCanvas();
                html.html().head();

                writeCss();
                html.title().content("LDP Test Suite Report")._head()
                        .body();
                html.h1().content("LDP Test Suite Summary");

                // Getting the results for the said suite
                Map<String, ISuiteResult> suiteResults = suite.getResults();
                for (ISuiteResult sr : suiteResults.values()) {

                    ITestContext tc = sr.getTestContext();
                    passedTests = tc.getPassedTests();
                    failedTests = tc.getFailedTests();
                    skippedTests = tc.getSkippedTests();
                }

                passClasses = getClasses(passedTests);
                failClasses = getClasses(failedTests);
                skipClasses = getClasses(skippedTests);

                html.br();

                //Display methods summary
                displayMethodsSummary(suites);

                // send html to a file
                createWriter(html.toHtml());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void writeCss() throws IOException {

        html.style().write(StringResource.get(TestSuiteGlobals.cssReport), NO_ESCAPE)
                ._style();
    }

    private void createWriter(String output) {
        BufferedWriter writer = null;
        String date = TestSuiteGlobals.today();
        new File(TestSuiteGlobals.outputDirectory).mkdirs();
        try {
            writer = new BufferedWriter(new FileWriter(TestSuiteGlobals.outputDirectory + "/" + TestSuiteGlobals.outputName + "-execution-report.html"));
            writer.write(output);

        } catch (IOException e) {
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
            }
        }
    }

    private HashMap<String, Integer> getClasses(IResultMap tests) {
        HashMap<String, Integer> classes = new HashMap<String, Integer>();
        Iterator<ITestResult> results = tests.getAllResults().iterator();
        while (results.hasNext()) {
            String name = results.next().getTestClass().getName().toString();
            name = name.substring(name.lastIndexOf(".") + 1);

            if (!classes.containsKey(name))
                classes.put(name, 1);
            else
                classes.put(name, classes.get(name) + 1);
        }
        return classes;
    }

    private void displayMethodsSummary(List<ISuite> suites) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (ISuite suite : suites) {
            Map<String, ISuiteResult> r = suite.getResults();
            for (ISuiteResult r2 : r.values()) {
                ITestContext testContext = r2.getTestContext();
                makeMethodsList(testContext);
            }
        }
    }

    private void makeMethodsList(ITestContext testContext) throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        IResultMap failed = testContext.getFailedTests();
        IResultMap passed = testContext.getPassedTests();
        IResultMap skipped = testContext.getSkippedTests();

        //html.h2().content("3.3 HTTP POST");
        //html.h3().content("3.3.1 LDP-NRs").small().content("(must)");
        //html.br();

        makeMethodSummaryTable(passed, skipped, failed);
        html.br();
    }

    private void makeMethodSummaryTable(IResultMap passed, IResultMap skipped, IResultMap failed) throws IOException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        html.table(class_("indented"));
        html.tr().th().content("Test");
        html.th().content("Result");
        html.th().content("")._tr();
        String[][] results = TestSuiteGlobals.orderTestsResults(passed, skipped, failed);

        for(String[] r : results){
            html.tr();
            html.td().a(href(r[0]).target("_blank")).write(r[3])._a()._td();
            html.td().span(class_(r[1])).content(r[1])._td();
            html.td().content(r[2]);
            html._tr();
        }

        html._table();
    }
}
