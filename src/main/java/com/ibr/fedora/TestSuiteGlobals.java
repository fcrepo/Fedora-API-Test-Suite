package com.ibr.fedora;

import org.testng.IResultMap;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.internal.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class TestSuiteGlobals {
    public static String cssReport = "reportStyle.css";
    public static String outputDirectory = "report";
    public static String outputName = "testsuite";
    public static String earlReportSyntax = "TURTLE";
    public static String ldptNamespace = "http://fedora.info/2017/06/30/spec/#";
    public static String earlReportAssertor = "https://wiki.duraspace.org/display/FF";
    public static String resourcePointer;
    public static String[] payloadHeaders = {"Content-Length", "Content-Range", "Trailer", "Transfer-Encoding"};

    public static boolean checkPayloadHeader(String header){
        boolean isPayloadHeader = false;
        for(String h : payloadHeaders){
            if(h.equals(header)) {
                isPayloadHeader = true;
                break;
            }
        }
        return isPayloadHeader;
    }

    public static String today(){
        String date = new SimpleDateFormat("MM-dd-yyyy HH-mm-ss").format(new Date());
        return date;
    }
    public static String today(String format){
        String date = new SimpleDateFormat(format).format(new Date());
        return date;
    }
    public static void resetFile() throws FileNotFoundException {
    	File f = new File(TestSuiteGlobals.outputDirectory + "/" + TestSuiteGlobals.outputName + "-execution.log");
    	if (f.exists())
    	{
    	   f.delete();
    	}
    }
    public static PrintStream logFile() throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(new File(TestSuiteGlobals.outputDirectory + "/" + TestSuiteGlobals.outputName + "-execution.log"), true);
        PrintStream ps = new PrintStream(fos);
        return ps;
    }

    public static String[][] orderTestsResults(IResultMap passed, IResultMap skipped, IResultMap failed) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        int size = passed.size() + skipped.size() + failed.size();
        String[][] results = new String[size][5];//second value according the total tests
        for (ITestResult result : passed.getAllResults()) {
            ITestNGMethod method = result.getMethod();

            Object o = TestsLabels.class.newInstance();
            Method m = TestsLabels.class.getDeclaredMethod(method.getMethodName());
            Object[] normalizedName = (Object[]) m.invoke(o);

            results[(method.getPriority()-1)][0] = normalizedName[2].toString();
            results[(method.getPriority()-1)][1] = "PASS";
            results[(method.getPriority()-1)][2] = normalizedName[1].toString();
            results[(method.getPriority()-1)][3] = normalizedName[0].toString();
            results[(method.getPriority()-1)][4] = getStackTrace(result.getThrowable());
        }
        for(ITestResult result : skipped.getAllResults()){
            ITestNGMethod method = result.getMethod();

            Object o = TestsLabels.class.newInstance();
            Method m = TestsLabels.class.getDeclaredMethod(method.getMethodName());
            Object[] normalizedName = (Object[]) m.invoke(o);

            results[(method.getPriority()-1)][0] = normalizedName[2].toString();
            results[(method.getPriority()-1)][1] = "SKIPPED";
            results[(method.getPriority()-1)][2] = normalizedName[1].toString();
            results[(method.getPriority()-1)][3] = normalizedName[0].toString();
            results[(method.getPriority()-1)][4] = getStackTrace(result.getThrowable());
        }
        for(ITestResult result : failed.getAllResults()){
            ITestNGMethod method = result.getMethod();

            Object o = TestsLabels.class.newInstance();
            Method m = TestsLabels.class.getDeclaredMethod(method.getMethodName());
            Object[] normalizedName = (Object[]) m.invoke(o);

            results[(method.getPriority()-1)][0] = normalizedName[2].toString();
            results[(method.getPriority()-1)][1] = "FAIL";
            results[(method.getPriority()-1)][2] = normalizedName[1].toString();
            results[(method.getPriority()-1)][3] = normalizedName[0].toString();
            results[(method.getPriority()-1)][4] = getStackTrace(result.getThrowable());
        }

        return results;
    }

    public static String getStackTrace(Throwable thrown) {
        String msg = "";
        if(thrown != null){
            if (thrown.getClass().getName().contains("TEST SKIPPED"))
                msg = thrown.getMessage();
            else
                msg = Utils.stackTrace(thrown, false)[0];
        }

        return msg;
    }
}
