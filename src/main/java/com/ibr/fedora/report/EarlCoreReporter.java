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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.ibr.fedora.TestSuiteGlobals;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.openrdf.model.vocabulary.EARL;

public abstract class EarlCoreReporter {
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
    protected static final HashMap<String, String> prefixes = new HashMap<String, String>();

    static {
        prefixes.put("earl", "http://www.w3.org/ns/earl#");
        prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        prefixes.put("dcterms", "http://purl.org/dc/terms/");
        prefixes.put("ldpt", TestSuiteGlobals.ldptNamespace);
    }

    protected BufferedWriter writer;
    protected Model model;

    protected void createWriter(final String directory) throws IOException {
        final File dir = new File(directory);
        dir.mkdirs();

        final String fileName = TestSuiteGlobals.outputName + "-execution-report-earl.ttl";
        final File file = new File(dir, fileName);
        writer = new BufferedWriter(new FileWriter(file));
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

    /**
     * Write RDF prefixes
     *
     * @param model
     */
    public void writePrefixes(final Model model) {
        for (Entry<String, String> prefix : prefixes.entrySet()) {
            model.setNsPrefix(prefix.getKey(), prefix.getValue());
        }
    }
}
