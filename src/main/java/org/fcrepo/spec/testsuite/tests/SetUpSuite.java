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
 * @author Fernando Cardoza
 */

package org.fcrepo.spec.testsuite.tests;

import java.io.FileNotFoundException;

import org.fcrepo.spec.testsuite.TestSuiteGlobals;
import org.fcrepo.spec.testsuite.report.EarlReporter;
import org.fcrepo.spec.testsuite.report.HtmlReporter;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

@Listeners({HtmlReporter.class, EarlReporter.class})
public class SetUpSuite {

    /**
     * @throws FileNotFoundException
     */
    @BeforeSuite
    public void setUp() throws FileNotFoundException {
        TestSuiteGlobals.resetFile();
    }
}
