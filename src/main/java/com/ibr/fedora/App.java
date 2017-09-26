package com.ibr.fedora;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        TestNG testng = new TestNG();
        XmlSuite suite = new XmlSuite();
        suite.setName("ldptest");

        XmlTest test = new XmlTest(suite);

        Map<String, String> params = new HashMap<String, String>();

        List<XmlClass> classes = new ArrayList<XmlClass>();

        int i = 0;
        for(String arg : args){
            params.put("param" + ++i, arg);
        }

        classes.add(new XmlClass("com.ibr.fedora.testsuite.HttpPost"));
        classes.add(new XmlClass("com.ibr.fedora.testsuite.HttpPut"));
        classes.add(new XmlClass("com.ibr.fedora.testsuite.HttpGet"));
        classes.add(new XmlClass("com.ibr.fedora.testsuite.HttpHead"));
        classes.add(new XmlClass("com.ibr.fedora.testsuite.HttpDelete"));
        classes.add(new XmlClass("com.ibr.fedora.testsuite.ExternalBinaryContent"));
        //classes.add(new XmlClass("com.ibr.fedora.testsuite.HttpPatch"));

        test.setParameters(params);
        test.setXmlClasses(classes);

        List<XmlTest> tests = new ArrayList<XmlTest>();
        tests.add(test);

        suite.setTests(tests);

        List<XmlSuite> suites = new ArrayList<XmlSuite>();
        suites.add(suite);

        testng.setXmlSuites(suites);
        testng.run();
    }
}
