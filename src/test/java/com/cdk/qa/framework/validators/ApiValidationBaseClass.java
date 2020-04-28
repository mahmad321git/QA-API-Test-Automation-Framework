package com.cdk.qa.framework.validators;

import com.cdk.qa.framework.services.SESManager;
import com.cdk.qa.framework.services.SSMManager;
import com.cdk.qa.framework.utils.FileUtils;
import com.cdk.qa.framework.utils.Constants;
import com.relevantcodes.extentreports.LogStatus;
import com.cdk.qa.framework.utils.RestAssuredUtils;
import com.cdk.qa.framework.config.extent.ExtentTestManager;

import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.util.Strings;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the Base Class for the generic methods used for test cases
 *
 * @author Adil.Qayyum
 */
public class ApiValidationBaseClass {

    //Variables for RestAssured Responses
    Response migratedResponse = null;
    Response originalResponse = null;
    private Response postResponse = null;


    //Variables for List type response body
    List<Map> migratedResponseList = null;
    List<Map> originalResponseList = null;
    List<Map<String, Object>> postRequestList = null;


    //Variables for the Map type response body
    Map migratedResponseJson = null;
    Map originalResponseJson = null;
    Map postRequestJson = null;

    //Variables for headers
    private String authorizationVal = null;
    private String xEnterpriseIdVal = null;
    private String xStoreIdVal = null;
    private String RemoteUserVal = null;
    private String AcceptVal = null;
    private String ContentTypeVal = null;

    private List<String> removableColumns = new ArrayList<>(
            Arrays.asList("pickId", "errorLevel", "errorMessage"));
    private Headers headers = null;

    //Initialing variable for Email Service
    private SESManager emailService = new SESManager();


    private void getHeadersValue() {
        String parameter = SSMManager.getSSMParameter(FileUtils.getPropertyValue
                (Constants.HEADERS_PROPERTIES_PATH, Constants.SSM_NAME));

        JSONObject jsonObj = new JSONObject(parameter);

        //AUTHORIZATION_VALUE
        authorizationVal = jsonObj.getString("AUTHORIZATION_VALUE");
        //xEnterpriseId
        xEnterpriseIdVal = jsonObj.getString("X_ENTERPRISE_ID_VALUE");
        //xStoreId
        xStoreIdVal = jsonObj.getString("X_STORE_ID_VALUE");
        //REMOTE_USER
        RemoteUserVal = jsonObj.getString("REMOTE_USER_VALUE");
        //ACCEPT_VALUE
        AcceptVal = jsonObj.getString("ACCEPT_VALUE");
        //CONTENT_TYPE_VALUE
        ContentTypeVal = jsonObj.getString("CONTENT_TYPE_VALUE");
    }

    /**
     * Method to be executed before each test case execution
     */
    @BeforeMethod(alwaysRun = true)
    public void setup() {
        getHeadersValue();
        //Setting the headers
        Header authorization = new Header(FileUtils.getPropertyValue
                (Constants.HEADERS_PROPERTIES_PATH, Constants.AUTHORIZATION_NAME), authorizationVal);
        Header xEnterpriseId = new Header(FileUtils.getPropertyValue
                (Constants.HEADERS_PROPERTIES_PATH, Constants.X_ENTERPRISE_NAME), xEnterpriseIdVal);
        Header xStoreId = new Header(FileUtils.getPropertyValue
                (Constants.HEADERS_PROPERTIES_PATH, Constants.X_STORE_NAME), xStoreIdVal);
        Header remoteUser = new Header(FileUtils.getPropertyValue
                (Constants.HEADERS_PROPERTIES_PATH, Constants.REMOTE_USER_NAME), RemoteUserVal);
        Header accept = new Header("Accept", AcceptVal);
        Header content = new Header("Content-Type", ContentTypeVal);
        //Setting the headers
        headers = new Headers(authorization, xEnterpriseId, xStoreId, remoteUser, accept, content);
    }

    /**
     * Method to be executed after each test case execution
     */
    @AfterMethod
    public void afterTest() {
        // Reset Values
        RestAssuredUtils.resetBaseURI();

        //Resetting responses of Flex and Java APIs
        migratedResponse = null;
        originalResponse = null;
        postResponse = null;
    }

    /**
     * To retrieve the response from the SourceUrl
     *
     * @param sourceUrl, URL of the Source Server
     */
    void getSourceResponse(String sourceUrl) {

        if (Strings.isNotNullAndNotEmpty(sourceUrl)) {
            RestAssuredUtils.setBaseURI(sourceUrl);
        }
        try {
            ExtentTestManager.getTest().log(LogStatus.INFO,
                    FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                            Constants.SOURCE_BASE_PATH_MESSAGE) + RestAssured.baseURI);
            originalResponse = RestAssured.given().urlEncodingEnabled(false).headers(headers).get(sourceUrl);
        } catch (Exception e) {
            ExtentTestManager.getTest().log(LogStatus.INFO,
                    FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                            Constants.SOURCE_ENDPOINT_NOT_ACCESSIBLE) + RestAssured.baseURI);
            Assert.fail();
        }
    }

    /**
     * To retrieve response from the TargetUrl
     *
     * @param targetUrl, URL of the Migrated Server
     */
    void getTargetResponse(String targetUrl) {

        if (Strings.isNotNullAndNotEmpty(targetUrl)) {
            RestAssuredUtils.setBaseURI(targetUrl);
        }
        try {
            ExtentTestManager.getTest().log(LogStatus.INFO,
                    FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                            Constants.TARGET_BASE_PATH_MESSAGE) + RestAssured.baseURI);
            migratedResponse = RestAssured.given().urlEncodingEnabled(false).headers(headers).get(targetUrl);
        } catch (Exception e) {
            ExtentTestManager.getTest().log(LogStatus.INFO,
                    FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                            Constants.TARGET_ENDPOINT_NOT_ACCESSIBLE) + RestAssured.baseURI);
            Assert.fail();
        }
    }

    /**
     * To get the counts of attributes/records from the response body for Source
     */
    void getSourceCount() {

        if ((originalResponse.jsonPath().get()) instanceof HashMap) {
            originalResponseJson = originalResponse.jsonPath().getMap("$");
            ExtentTestManager.getTest().log(LogStatus.INFO,
                    FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                            Constants.SOURCE_API_ATTRIBUTE_COUNT) + originalResponseJson.size());
        } else if ((originalResponse.jsonPath().get()) instanceof ArrayList) {
            originalResponseList = originalResponse.jsonPath().getList("$");
            ExtentTestManager.getTest().log(LogStatus.INFO,
                    FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                            Constants.SOURCE_API_ATTRIBUTE_COUNT) + originalResponseList.size());
        } else {
            ExtentTestManager.getTest().log(LogStatus.INFO,
                    Constants.SOURCE_INVALID_RESPONSE_MESSAGE);
        }
    }

    /**
     * To get the counts of attributes/records from the response body for Target
     */
    void getTargetCount() {

        if ((migratedResponse.jsonPath().get()) instanceof HashMap) {
            migratedResponseJson = migratedResponse.jsonPath().getMap("$");
            ExtentTestManager.getTest().log(LogStatus.INFO,
                    FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                            Constants.TARGET_API_ATTRIBUTE_COUNT) + migratedResponseJson.size());
        } else if ((migratedResponse.jsonPath().get()) instanceof ArrayList) {
            migratedResponseList = migratedResponse.jsonPath().getList("$");
            ExtentTestManager.getTest().log(LogStatus.INFO,
                    FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                            Constants.TARGET_API_ATTRIBUTE_COUNT) + migratedResponseList.size());
        } else {
            ExtentTestManager.getTest().log(LogStatus.INFO,
                    Constants.TARGET_INVALID_RESPONSE_MESSAGE);
        }
    }

    /**
     * To send the request on Source Server
     *
     * @param payload, Request body of POST Endpoint
     * @param postUrl, URL of the POST Endpoint
     */
    void postRequest(String payload, String postUrl) {
        if (Strings.isNotNullAndNotEmpty(payload)) {
            RestAssuredUtils.setBaseURI(postUrl);
        }
        try {
            ExtentTestManager.getTest().log(LogStatus.INFO,
                    FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                            Constants.POST_BASE_PATH_MESSAGE) + postUrl);
            postResponse = RestAssured.given().urlEncodingEnabled(false).headers(headers).body(payload).post(postUrl);
            if (postResponse.statusCode() == 200) {

                ExtentTestManager.getTest().log(LogStatus.INFO,
                        FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                                Constants.POST_VALID_RESPONSE_MESSAGE) + postResponse.statusCode());

            } else {
                ExtentTestManager.getTest().log(LogStatus.INFO,
                        FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                                Constants.POST_INVALID_RESPONSE_MESSAGE) + postResponse.statusCode());

                Assert.fail();

            }
        } catch (Exception e) {
            ExtentTestManager.getTest().log(LogStatus.INFO,
                    FileUtils.getPropertyValue(Constants.MESSAGES_PROPERTIES_PATH,
                            Constants.SOURCE_ENDPOINT_NOT_ACCESSIBLE) + RestAssured.baseURI);
            Assert.fail();
        }

    }

    /**
     * To remove the unrequired keys from the endpoint response body
     *
     * @param responseList, Contains the JSON response list returned by the endpoint
     */
    @SuppressWarnings("unchecked")
    void removeColumnsFromList(List<Map> responseList) {
        responseList.forEach(response ->
                removableColumns.forEach(column -> {
                    if ((response).containsKey(column)) {
                        response.remove(column);
                    }
                    if ((response).containsKey("bu")) {
                        response.replace("bu", 1);
                    }
                })
        );
    }

    /**
     * To remove the unrequired keys from the endpoint response body
     *
     * @param response, Contains the JSON response list returned by the endpoint
     */
    @SuppressWarnings("unchecked")
    void removeColumnsFromMap(Map response) {
        removableColumns.forEach(column -> {
            if ((response).containsKey(column)) {
                response.remove(column);
            }
            if ((response).containsKey("bu")) {
                response.replace("bu", 1);
            }
        });
    }


    /**
     *
     * Executes at the end of the test suite to populate Extent Report and Send it as email
     *
     * @throws MessagingException, Exception thrown in case of message sending failure
     */

    @AfterSuite(alwaysRun = true)
    public void endTestSuite() throws MessagingException {
        ExtentTestManager.flush();
        RestAssuredUtils.resetBasePath();
        emailService.sendEmail();
    }
}