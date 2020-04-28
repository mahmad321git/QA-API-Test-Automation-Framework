package com.cdk.qa.framework.config.extent;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

/**
 * Standard class for Extent Reports Manager
 */
@UtilityClass
public class ExtentTestManager {

    private Map<Integer, ExtentTest> extentTestMap = new HashMap<>();
    private ExtentReports extent = ExtentManager.getReporter();

    public synchronized ExtentTest getTest() {
        return extentTestMap.get((int) (Thread.currentThread().getId()));
    }

    public synchronized void endTest() {
        extent.endTest( extentTestMap.get((int) (Thread.currentThread().getId())));
    }

    @SuppressWarnings("UnusedReturnValue")
    public synchronized ExtentTest startTest(String testName, String desc) {
        ExtentTest test = extent.startTest(testName, desc);
        extentTestMap.put((int) (Thread.currentThread().getId()), test);
        return test;
    }

    public synchronized void flush() {
        extent.flush();
    }
}
