package com.cdk.qa.framework.validators;

import com.cdk.qa.framework.utils.Constants;
import com.google.common.collect.Maps;
import com.cdk.qa.framework.utils.FileUtils;
import com.cdk.qa.framework.config.extent.ExtentTestManager;
import com.relevantcodes.extentreports.LogStatus;
import com.cdk.qa.framework.utils.RestAssuredUtils;
import com.cdk.qa.framework.services.SESManager;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the test cases for the migrated endpoints
 *
 * @author Ahmad.Idrees
 */
public class ApiValidationGET extends ApiValidationBaseClass {

    private final String DATA_PROVIDER = "data-provider";
    private final String TEST_SUITE_SMOKE = "smoke";
    private final String TEST_SUITE_ALL = "all";

    private SESManager emailService = new SESManager();

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
     * @param sourceUrl,   URL of the Source API Server
     * @param targetUrl,   URL of the Migrated API Server
     */

    @Test(dataProvider = DATA_PROVIDER, groups = {TEST_SUITE_SMOKE, TEST_SUITE_ALL}, priority = 3)
    public void statusCodeComparison(String serviceName, String sourceUrl, String targetUrl) {
        ExtentTestManager.startTest(serviceName.concat(": StatusCode"), "StatusCodeTest");

        getSourceResponse(sourceUrl);
        ExtentTestManager.getTest().log(LogStatus.INFO,
                FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                        Constants.SOURCE_RESPONSE_MESSAGE) + originalResponse.getStatusCode());

        getTargetResponse(targetUrl);
        ExtentTestManager.getTest().log(LogStatus.INFO,
                FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                        Constants.TARGET_RESPONSE_MESSAGE) + migratedResponse.getStatusCode());

        if (migratedResponse.asString() != null && originalResponse.asString() != null) {
            Assert.assertEquals(migratedResponse.getStatusCode(), originalResponse.getStatusCode());
        }
    }

    /**
     * Validation of Source VS Target Attribute Counts for single JSON object, or Record Count for list of objects
     *
     * @param serviceName, Name of the service for which test case is to be executed
     * @param sourceUrl,   URL of the Source API Server
     * @param targetUrl,   URL of the Migrated API Server
     */

    @Test(dataProvider = DATA_PROVIDER, groups = {TEST_SUITE_SMOKE, TEST_SUITE_ALL}, priority = 2)
    public void attributesCountComparison(String serviceName, String sourceUrl, String targetUrl) {
        ExtentTestManager.startTest(serviceName.concat(": AttributesCount"), "attributesCountComparison");

        getSourceResponse(sourceUrl);
        getSourceCount();

        getTargetResponse(targetUrl);
        getTargetCount();

        try {
            if (migratedResponseJson != null && originalResponseJson != null) {
                Assert.assertEquals(migratedResponseJson.size(), originalResponseJson.size());
            } else if (migratedResponseList != null && originalResponseList != null) {
                Assert.assertEquals(migratedResponseList.size(), originalResponseList.size());
            }
        } catch (AssertionError error) {
            ExtentTestManager.getTest().log(LogStatus.FAIL, error.getMessage());
            Assert.fail();
        }
    }

    /**
     * Validation of Source VS Target Response Body Comparison
     *
     * @param serviceName, Name of the service for which test case is to be executed
     * @param sourceUrl,   URL of the Source API Server
     * @param targetUrl,   URL of the Migrated API Server
     */
    @Test(dataProvider = DATA_PROVIDER, groups = TEST_SUITE_ALL, priority = 3)
    public void responseBodyComparison(String serviceName, String sourceUrl, String targetUrl) {
        ExtentTestManager.startTest(serviceName.concat(": ResponseBody"), "responseBodyComparison");

        //Java Api Attributes Count
        List<Map> migratedResponseList = null;
        Map migratedJson = null;

        //Flex Api Attributes Count
        List<Map> originalResponseList = null;
        Map originalJson = null;

        getSourceResponse(sourceUrl);

        if ((originalResponse.jsonPath().get()) instanceof HashMap) {
            originalJson = originalResponse.jsonPath().getMap("$");
            removeColumnsFromMap(originalJson);

        } else if ((originalResponse.jsonPath().get()) instanceof ArrayList) {
            originalResponseList = originalResponse.jsonPath().getList("$");
            removeColumnsFromList(originalResponseList);
        } else {
            ExtentTestManager.getTest().log(LogStatus.INFO, Constants.SOURCE_INVALID_RESPONSE_MESSAGE);
        }

        getTargetResponse(targetUrl);

        if ((migratedResponse.jsonPath().get()) instanceof HashMap) {
            migratedJson = migratedResponse.jsonPath().getMap("$");
            removeColumnsFromMap(migratedJson);

        } else if ((migratedResponse.jsonPath().get()) instanceof ArrayList) {
            migratedResponseList = migratedResponse.jsonPath().getList("$");
            removeColumnsFromList(migratedResponseList);
        } else {
            ExtentTestManager.getTest().log(LogStatus.INFO, Constants.TARGET_INVALID_RESPONSE_MESSAGE);
        }

        try {
            if (migratedJson != null && originalJson != null) {
                ExtentTestManager.getTest().log(LogStatus.INFO, "MAP Difference: "
                        + Maps.difference(migratedJson, originalJson));
                Assert.assertEquals(migratedJson, originalJson);
                ExtentTestManager.getTest().log(LogStatus.INFO,
                        FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                                Constants.EQUAL_RESPONSE_MESSAGE));
            } else if (migratedResponseList != null && originalResponseList != null) {
                Assert.assertEquals(migratedResponseList.toString(), originalResponseList.toString());
                ExtentTestManager.getTest().log(LogStatus.INFO,
                        FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                                Constants.EQUAL_RESPONSE_MESSAGE));
                ExtentTestManager.getTest().log(LogStatus.INFO,
                        FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                                Constants.TARGET_RESPONSE_MESSAGE) + originalResponseList);
            }
        } catch (AssertionError error) {
            ExtentTestManager.getTest().log(LogStatus.FAIL, error.getMessage());
            Assert.fail();
        }
    }

    /**
     * Executes at the end of the test suite to populate Extent Report and Send it as email
     *
     * @throws MessagingException, Exception thrown in case of message sending failure
     */

    @AfterSuite(groups = {TEST_SUITE_SMOKE, TEST_SUITE_ALL})
    public void endTestSuite() throws MessagingException {
        ExtentTestManager.flush();
        RestAssuredUtils.resetBasePath();
        emailService.sendEmail();
    }
}