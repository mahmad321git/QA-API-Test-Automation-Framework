package com.cdk.qa.framework.validators;

import com.cdk.qa.framework.utils.Constants;
import com.cdk.qa.framework.utils.FileUtils;
import com.cdk.qa.framework.config.extent.ExtentTestManager;
import com.relevantcodes.extentreports.LogStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;


public class PostMigrationTests extends ApiValidationBaseClass {

    private final String DATA_PROVIDER = "data-provider";
    private final String TEST_SUITE_PM = "PM";


    /**
     * Retrieve endpoint configuration from the excel file
     *
     * @return configurations
     */
    @DataProvider(name = DATA_PROVIDER)
    public Object[][] dataProviderMethod() throws IOException {
        String serviceSheetName = System.getProperty("serviceName");
        return FileUtils.getEndpointConfig(serviceSheetName);
    }

    /**
     * Validation of Source vs Target Status Code Comparison
     *
     * @param serviceName, Name of the service for which test case is to be executed
     * @param sourceUrl, URL of the Source API Server
     */
    @Test(dataProvider = DATA_PROVIDER, groups = {TEST_SUITE_PM}, priority = 1)
    public void postMigrationCodeComparison(String serviceName, String sourceUrl) {
        ExtentTestManager.startTest(serviceName.concat(": StatusCode"), "StatusCodeTest");

        getSourceResponse(sourceUrl);
        ExtentTestManager.getTest().log(LogStatus.INFO,
                FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                        Constants.SOURCE_RESPONSE_MESSAGE) + originalResponse.getStatusCode());
        if (originalResponse.asString() != null) {
            Assert.assertEquals(originalResponse.getStatusCode(), 200);
        }
    }
}
