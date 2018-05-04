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
package org.fcrepo.spec.testsuite;

import static org.fcrepo.spec.testsuite.Constants.BASIC_CONTAINER_LINK_HEADER;
import static org.fcrepo.spec.testsuite.Constants.SLUG;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.IResultMap;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.internal.Utils;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public abstract class TestSuiteGlobals {
    public static String cssReport = "reportStyle.css";
    public static String outputDirectory = "report";
    public static String outputName = "testsuite";
    public static String earlReportSyntax = "TURTLE";
    public static String ldptNamespace = "http://fedora.info/2017/06/30/spec/#";
    public static String earlReportAssertor = "https://wiki.duraspace.org/display/FF";
    public static String resourcePointer;
    public static String[] payloadHeaders = {"Content-Length", "Content-Range", "Trailer", "Transfer-Encoding"};
    public static String[] membershipTriples = {"hasMemberRelation", "isMemberOfRelation", "membershipResource",
                                                "insertedContentRelation"};

    /**
     * Get or create the default container for all tests resources to be created
     *
     * @param baseurl
     * @return containerUrl
     */
    public static String containerTestSuite(final String baseurl, final String user, final String pass) {
        final String name = outputName + "container" + today();
        String containerUrl = baseurl + "/" + name;
        containerUrl = containerUrl.replaceAll("(?<!http:)//", "/");
        final Response res = RestAssured.given()
                                        .auth().basic(user, pass)
                                        .contentType("text/turtle")
                                        .header("Link", BASIC_CONTAINER_LINK_HEADER)
                                        .header(SLUG, name)
                                        .body(BASIC_CONTAINER_LINK_HEADER)
                                        .when()
                                        .post(baseurl);
        if (res.getStatusCode() == 201) {
            return containerUrl;
        } else {
            return baseurl;
        }
    }

    /**
     * @param header
     * @return isPayloadHeader
     */
    public static boolean checkPayloadHeader(final String header) {
        boolean isPayloadHeader = false;
        for (final String h : payloadHeaders) {
            if (h.equals(header)) {
                isPayloadHeader = true;
                break;
            }
        }
        return isPayloadHeader;
    }

    /**
     * @param body
     * @return isMembershipTriple
     */
    public static boolean checkMembershipTriple(final String body) {
        boolean isMembershipTriple = false;
        for (final String h : membershipTriples) {
            if (body.contains(h)) {
                isMembershipTriple = true;
            }
        }
        return isMembershipTriple;
    }

    /**
     * @return date
     */
    public static String today() {
        final String date = new SimpleDateFormat("MMddyyyyHHmmss").format(new Date());
        return date;
    }

    /**
     * @param format
     * @return date
     */
    public static String today(final String format) {
        final String date = new SimpleDateFormat(format).format(new Date());
        return date;
    }

    /**
     * @throws FileNotFoundException
     */
    public static void resetFile() throws FileNotFoundException {
        final File dir = new File("report");
        dir.mkdirs();
        final File f = new File(
            TestSuiteGlobals.outputDirectory + "/" + TestSuiteGlobals.outputName + "-execution.log");
        if (f.exists()) {
            f.delete();
        }
    }

    /**
     * @return ps
     */
    public static PrintStream logFile() {
        try {
            final FileOutputStream fos = new FileOutputStream(new File(TestSuiteGlobals.outputDirectory + "/" +
                                                                       TestSuiteGlobals.outputName + "-execution.log"),
                                                              true);
            final PrintStream ps = new PrintStream(fos);
            return ps;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @param passed
     * @param skipped
     * @param failed
     * @return results
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public static Map<String, String[]> orderTestsResults(final IResultMap passed,
                                                          final IResultMap skipped, final IResultMap failed)
        throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        final TreeMap<String, String[]> results = new TreeMap<>();
        addToResults(results, passed, "PASS");
        addToResults(results, skipped, "SKIPPED");
        addToResults(results, failed, "FAIL");
        return results;
    }

    private static void addToResults(final TreeMap<String, String[]> results, final IResultMap resultMap,
                                     final String outcome) {
        for (final ITestResult result : resultMap.getAllResults()) {
            final ITestNGMethod method = result.getMethod();
            final TestInfo info = TestInfo.getByMethodName(method.getMethodName());
            final String[] details = new String[6];
            details[0] = info.getSpecLink().toString();
            details[1] = outcome;
            details[2] = info.getDescription().toString();
            details[3] = formatTestLinkText(info);
            details[4] = getStackTrace(result.getThrowable());
            details[5] = method.getGroups()[0];
            results.put(details[3], details);
        }
    }

    private static String formatTestLinkText(final TestInfo info) {
        return info.getId();
    }

    /**
     * @param thrown
     * @return msg
     */
    public static String getStackTrace(final Throwable thrown) {
        String msg = "";
        if (thrown != null) {
            if (thrown.getClass().getName().contains("TEST SKIPPED")) {
                msg = thrown.getMessage();
            } else {
                msg = Utils.shortStackTrace(thrown, false);
            }
        }

        return msg;
    }
}
