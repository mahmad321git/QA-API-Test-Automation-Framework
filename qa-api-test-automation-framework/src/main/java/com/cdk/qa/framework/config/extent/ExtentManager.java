package com.cdk.qa.framework.config.extent;

import com.relevantcodes.extentreports.ExtentReports;
import com.cdk.qa.framework.utils.Constants;
import lombok.experimental.UtilityClass;

/**
 * Standard class for Extent Reports Manager
 */
@UtilityClass
public class ExtentManager {
    private ExtentReports extent;

    public synchronized ExtentReports getReporter() {
        if (extent == null) {
            //Set HTML reporting file location
            extent = new ExtentReports(Constants.EXTENT_REPORT_PATH, true);
        }
        return extent;
    }
}
