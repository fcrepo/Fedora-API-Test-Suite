package com.ibr.fedora;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class TestSuiteGlobals {
    public static String cssReport = "reportStyle.css";
    public static String outputDirectory = "report";
    public static String outputName = "testsuite";
    public static String earlReportSyntax = "TURTLE";
    public static String ldptNamespace = "http://fedora.info/2017/06/30/spec/#";
    public static String earlReportAssertor = "https://wiki.duraspace.org/display/FF";

    public static String today(){
        String date = new SimpleDateFormat("MM-dd-yyyy HH-mm-ss").format(new Date());
        return date;
    }
    public static String today(String format){
        String date = new SimpleDateFormat(format).format(new Date());
        return date;
    }

    public static PrintStream logFile() throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(new File(TestSuiteGlobals.outputDirectory + "/" + TestSuiteGlobals.outputName + "-execution.log"), true);
        PrintStream ps = new PrintStream(fos);
        return ps;
    }
}
