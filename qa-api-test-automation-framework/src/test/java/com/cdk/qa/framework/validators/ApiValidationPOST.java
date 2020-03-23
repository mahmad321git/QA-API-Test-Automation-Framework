package com.cdk.qa.framework.validators;

import com.cdk.qa.framework.services.SESManager;
import com.cdk.qa.framework.utils.Constants;
import com.cdk.qa.framework.utils.FileUtils;
import com.cdk.qa.framework.utils.RestAssuredUtils;
import com.cdk.qa.framework.config.extent.ExtentTestManager;
import com.google.common.collect.Maps;
import com.relevantcodes.extentreports.LogStatus;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the test cases for the POST endpoints
 *
 * @author Abdur.Rehman
 */
public class ApiValidationPOST extends ApiValidationBaseClass {

    private final String DATA_PROVIDER = "data-provider";
    private final String TEST_SUITE_POST = "post";

    private final SESManager emailService = new SESManager();

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
     * Validation of POST API with the benchmark of GET Endpoint
     *
     * @param serviceName, Name of the service for which test case is to be executed
     * @param getUrl, URL of the GET Endpoint
     * @param postUrl, URL of the POST Endpoint
     * @param payload, Request body of POST Endpoint
     */
    @Test(dataProvider = DATA_PROVIDER, groups = TEST_SUITE_POST, priority = 1)
    public void postEndpointValidation(String serviceName, String getUrl, String postUrl, String payload) {
        ExtentTestManager.startTest(serviceName.concat(": POSTRequestBody"), "postEndpointValidation");

        List<Map> originalResponseList;
        Map originalJson;

        postRequest(payload, postUrl);
        getSourceResponse(getUrl);

        if ((originalResponse.jsonPath().get()) instanceof HashMap) {
            originalJson = originalResponse.jsonPath().getMap("$");
            removeColumnsFromMap(originalJson);
            postRequestJson = FileUtils.stringToMap(payload);

            try {
                if (postRequestJson != null && originalJson != null) {
                    ExtentTestManager.getTest().log(LogStatus.INFO, "MAP Difference: "
                            + Maps.difference(postRequestJson, originalJson));
                    Assert.assertEquals(postRequestJson, originalJson);
                    ExtentTestManager.getTest().log(LogStatus.INFO,
                            FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                                    Constants.EQUAL_RESPONSE_MESSAGE));
                } else {
                    ExtentTestManager.getTest().log(LogStatus.INFO, Constants.TARGET_INVALID_RESPONSE_MESSAGE);
                }
            } catch (AssertionError error) {
                ExtentTestManager.getTest().log(LogStatus.FAIL, error.getMessage());
                Assert.fail();
            }

        } else if ((originalResponse.jsonPath().get()) instanceof ArrayList) {
            originalResponseList = originalResponse.jsonPath().getList("$");
            removeColumnsFromList(originalResponseList);
            postRequestList = FileUtils.stringToList(payload);
            try {

                if (postRequestList != null) {

                    Assert.assertEquals(originalResponseList.toString(), postRequestList.toString());

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

        } else {
            ExtentTestManager.getTest().log(LogStatus.INFO, Constants.SOURCE_INVALID_RESPONSE_MESSAGE);
        }
    }

    /**
     * Executes at the end of the test suite to populate Extent Report and Send it as email
     *
     * @throws MessagingException, Exception thrown in case of message sending failure
     */

    @AfterSuite(groups = {TEST_SUITE_POST})
    public void endTestSuite() throws MessagingException {
        ExtentTestManager.flush();
        RestAssuredUtils.resetBasePath();
        emailService.sendEmail();
    }
}