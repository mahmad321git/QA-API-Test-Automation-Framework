package com.cdk.qa.framework.validators;

import com.cdk.qa.framework.utils.Constants;
import com.cdk.qa.framework.utils.FileUtils;
import com.cdk.qa.framework.config.extent.ExtentTestManager;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import com.relevantcodes.extentreports.LogStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Implementation of the test cases for the POST endpoints
 *
 * @author Abdur.Rehman
 */
public class ApiValidationPOST extends ApiValidationBaseClass {

    private final String DATA_PROVIDER = "data-provider";
    private final String TEST_SUITE_POST = "post";


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
     * @param getUrl,      URL of the GET Endpoint
     * @param postUrl,     URL of the POST Endpoint
     * @param payload,     Request body of POST Endpoint
     */
    @Test(dataProvider = DATA_PROVIDER, groups = TEST_SUITE_POST, priority = 1)
    public void postEndpointValidation(String serviceName, String getUrl, String postUrl, String payload) {
        ExtentTestManager.startTest(serviceName.concat(": POSTRequestBody"), "postEndpointValidation");

        List<Map> originalResponseList;
        Map originalJson;

        postRequest(payload, postUrl);
        getSourceResponse(getUrl);


        if ((originalResponse.jsonPath().get()) instanceof HashMap) {
            originalJson = new TreeMap<>(originalResponse.jsonPath().getMap("$"));
            removeColumnsFromMap(originalJson);
            postRequestJson = FileUtils.stringToMap(payload);

            try {
                if (postRequestJson != null && originalJson != null) {
                    Assert.assertEquals(postRequestJson, originalJson);
                    ExtentTestManager.getTest().log(LogStatus.INFO,
                            FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                                    Constants.EQUAL_RESPONSE_MESSAGE));
                } else {

                    ExtentTestManager.getTest().log(LogStatus.INFO, Constants.TARGET_INVALID_RESPONSE_MESSAGE);
                }
            } catch (AssertionError error) {
                ExtentTestManager.getTest().log(LogStatus.FAIL, "<b>Mismatched Attributes: <span style='font-weight:bold;color:red'>" + Maps.difference(postRequestJson, originalJson).entriesDiffering().keySet() + "</span>");
                ExtentTestManager.getTest().log(LogStatus.INFO, "<span class='label pass'>EXPECTED: " + Maps.filterKeys(originalJson, Predicates.in(Maps.difference(postRequestJson, originalJson).entriesDiffering().keySet())));
                ExtentTestManager.getTest().log(LogStatus.FAIL, "<span class='label failure'>ACTUAL: " + Maps.filterKeys(postRequestJson, Predicates.in(Maps.difference(postRequestJson, originalJson).entriesDiffering().keySet())));
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

}