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
package com.ibr.fedora;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.IResultMap;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.internal.Utils;

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

    public static String body = "@prefix ldp: <http://www.w3.org/ns/ldp#> ."
                                + "@prefix dcterms: <http://purl.org/dc/terms/> ."
                                + "<> a ldp:Container, ldp:BasicContainer;"
                                + "dcterms:title 'Base Container' ;"
                                + "dcterms:description 'This container is the base container for the Fedora API Test " +
                                "Suite.' . ";

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
                                        .header("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"")
                                        .header("slug", name)
                                        .body(body)
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
        for (String h : payloadHeaders) {
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
        for (String h : membershipTriples) {
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
     * @throws FileNotFoundException
     */
    public static PrintStream logFile() throws FileNotFoundException {
        final FileOutputStream fos = new FileOutputStream(new File(TestSuiteGlobals.outputDirectory + "/" +
                                                                   TestSuiteGlobals.outputName + "-execution.log"),
                                                          true);
        final PrintStream ps = new PrintStream(fos);
        return ps;
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
    public static String[][] orderTestsResults(final IResultMap passed,
                                               final IResultMap skipped, final IResultMap failed)
        throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        final int size = passed.size() + skipped.size() + failed.size();
        final String[][] results = new String[size][5];//second value according the total tests
        for (ITestResult result : passed.getAllResults()) {
            final ITestNGMethod method = result.getMethod();

            final Object o = TestsLabels.class.newInstance();
            final Method m = TestsLabels.class.getDeclaredMethod(method.getMethodName());
            final Object[] normalizedName = (Object[]) m.invoke(o);

            results[(method.getPriority() - 1)][0] = normalizedName[2].toString();
            results[(method.getPriority() - 1)][1] = "PASS";
            results[(method.getPriority() - 1)][2] = normalizedName[1].toString();
            results[(method.getPriority() - 1)][3] = normalizedName[0].toString();
            results[(method.getPriority() - 1)][4] = getStackTrace(result.getThrowable());
        }
        for (ITestResult result : skipped.getAllResults()) {
            final ITestNGMethod method = result.getMethod();

            final Object o = TestsLabels.class.newInstance();
            final Method m = TestsLabels.class.getDeclaredMethod(method.getMethodName());
            final Object[] normalizedName = (Object[]) m.invoke(o);

            results[(method.getPriority() - 1)][0] = normalizedName[2].toString();
            results[(method.getPriority() - 1)][1] = "SKIPPED";
            results[(method.getPriority() - 1)][2] = normalizedName[1].toString();
            results[(method.getPriority() - 1)][3] = normalizedName[0].toString();
            results[(method.getPriority() - 1)][4] = getStackTrace(result.getThrowable());
        }
        for (ITestResult result : failed.getAllResults()) {
            final ITestNGMethod method = result.getMethod();

            final Object o = TestsLabels.class.newInstance();
            final Method m = TestsLabels.class.getDeclaredMethod(method.getMethodName());
            final Object[] normalizedName = (Object[]) m.invoke(o);

            results[(method.getPriority() - 1)][0] = normalizedName[2].toString();
            results[(method.getPriority() - 1)][1] = "FAIL";
            results[(method.getPriority() - 1)][2] = normalizedName[1].toString();
            results[(method.getPriority() - 1)][3] = normalizedName[0].toString();
            results[(method.getPriority() - 1)][4] = getStackTrace(result.getThrowable());
        }

        return results;
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
                msg = Utils.stackTrace(thrown, false)[0];
            }
        }

        return msg;
    }
}
