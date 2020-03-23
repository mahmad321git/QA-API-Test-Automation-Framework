package com.cdk.qa.framework.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import com.cdk.qa.framework.utils.Constants;
import com.cdk.qa.framework.utils.FileUtils;

/**
 * Standard TestNG Retry Class
 */
public class Retry implements IRetryAnalyzer {

    private int count = 0;

    //To add the retry mechanism for failed test cases
    public boolean retry(ITestResult iTestResult) {
        String maxRetryCount = FileUtils.getPropertyValue
                (Constants.MESSAGES_PROPERTIES_PATH, Constants.MAX_RETRY_COUNT);
        if (!iTestResult.isSuccess()) { // Check if test not succeed
            if (count < Integer.parseInt(maxRetryCount)) { // Check if maxTry count is reached
                count++; // Increase the maxTry count by 1
                iTestResult.setStatus(ITestResult.FAILURE); // Mark test as failed
                return true; // Tells TestNG to re-run the test
            }
        } else {
            iTestResult.setStatus(ITestResult.SUCCESS); // If test passes, TestNG marks it as passed
        }
        return false;
    }
}
