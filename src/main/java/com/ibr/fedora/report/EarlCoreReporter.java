package com.ibr.fedora.report;

import com.ibr.fedora.TestSuiteGlobals;
import org.apache.jena.rdf.model.*;
import org.openrdf.model.vocabulary.EARL;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public abstract class EarlCoreReporter {
    protected BufferedWriter writer;
    protected Model model;
    protected static final HashMap<String, String> prefixes = new HashMap<String, String>();

    //Earl resources
    public final static Resource Assertion = ResourceFactory.createResource(EARL.ASSERTION.toString());
    public final static Resource Assertor = ResourceFactory.createResource(EARL.ASSERTOR.toString());
    public final static Resource TestSubject = ResourceFactory.createResource(EARL.TEST_SUBJECT.toString());
    public final static Resource TestResult = ResourceFactory.createResource(EARL.TESTRESULT.toString());

    //Earl properties
    public final static Property testResult = ResourceFactory.createProperty(EARL.RESULT.toString());
    public final static Property testSubject = ResourceFactory.createProperty(EARL.SUBJECT.toString());
    public final static Property assertedBy = ResourceFactory.createProperty(EARL.ASSERTEDBY.toString());
    public final static Property test = ResourceFactory.createProperty(EARL.TEST.toString());
    public final static Property outcome = ResourceFactory.createProperty(EARL.OUTCOME.toString());
    public final static Property passed = ResourceFactory.createProperty(EARL.NAMESPACE + "passed");
    public final static Property failed = ResourceFactory.createProperty(EARL.NAMESPACE + "failed");
    public final static Property untested = ResourceFactory.createProperty(EARL.NAMESPACE + "untested");

    static {
        prefixes.put("earl", "http://www.w3.org/ns/earl#");
        prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        prefixes.put("dcterms", "http://purl.org/dc/terms/");
        prefixes.put("ldpt", TestSuiteGlobals.ldptNamespace);
    }

    protected void createWriter(String directory) throws IOException {
        File dir = new File(directory);
        dir.mkdirs();
        System.out.println("Writing EARL results:");
        String fileName = TestSuiteGlobals.outputName + "-execution-report-earl.ttl";
        File file = new File(dir, fileName);
        writer = new BufferedWriter(new FileWriter(file));
        System.out.println("\t"+file.getAbsolutePath());
    }

    protected void write() {
        model.write(writer, TestSuiteGlobals.earlReportSyntax);
    }

    protected void endWriter() throws IOException {
        writer.flush();
        writer.close();
    }

    protected void createModel() {
        model = ModelFactory.createDefaultModel();
        writePrefixes(model);
    }

    public void writePrefixes(Model model) {
        for (Entry<String, String> prefix : prefixes.entrySet()) {
            model.setNsPrefix(prefix.getKey(), prefix.getValue());
        }
    }
}
