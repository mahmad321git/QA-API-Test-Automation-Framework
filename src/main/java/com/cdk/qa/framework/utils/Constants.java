package com.cdk.qa.framework.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    /**
     * Constants name mapping with message properties
     */
    public final String SOURCE_BASE_PATH_MESSAGE = "message.source.base.path";
    public final String TARGET_BASE_PATH_MESSAGE = "message.target.base.path";
    public final String SOURCE_RESPONSE_MESSAGE = "message.source.response";
    public final String TARGET_RESPONSE_MESSAGE = "message.target.response";
    public final String POST_BASE_PATH_MESSAGE = "message.post.base.path";

    public final String SOURCE_ENDPOINT_NOT_ACCESSIBLE = "message.source.not.accessible";
    public final String TARGET_ENDPOINT_NOT_ACCESSIBLE = "message.target.not.accessible";
    public final String SOURCE_API_ATTRIBUTE_COUNT = "message.source.attribute.count";
    public final String TARGET_API_ATTRIBUTE_COUNT = "message.target.attribute.count";
    public final String EQUAL_RESPONSE_MESSAGE = "message.equal.response";
    public final String SOURCE_INVALID_RESPONSE_MESSAGE = "message.source.response.invalid";
    public final String TARGET_INVALID_RESPONSE_MESSAGE = "message.target.response.invalid";
    public final String POST_VALID_RESPONSE_MESSAGE = "message.post.response.valid";
    public final String POST_INVALID_RESPONSE_MESSAGE = "message.post.response.invalid";


    public final String ON_START_MESSAGE = "message.test.listener.onstart";
    public final String ON_FINISH_MESSAGE = "message.test.listener.onfinish";
    public final String ON_TEST_START_MESSAGE = "message.test.listener.onteststart";
    public final String ON_TEST_SUCCESS_MESSAGE = "message.test.listener.ontestsuccess";
    public final String ON_TEST_FAILURE_MESSAGE = "message.test.listener.ontestfailure";
    public final String ON_TEST_SKIPPED_MESSAGE = "message.test.listener.ontestskipped";
    public final String ON_SUCCESS_RATIO_MESSAGE = "message.test.listener.successratio";

    /**
     * Constants name mapping with email properties
     */
    public final String EMAIL_SENDER = "email.sender";
    public final String EMAIL_RECIPIENT = "email.recipient";
    public final String EMAIL_BODY_TEXT = "email.body.text";

    /**
     * Constants for file paths
     */
    private final String WORKING_DIRECTORY = System.getProperty("user.dir");
    public final String OPERATING_SYSTEM = System.getProperty("os.name");
    public final String BUCKET_NAME = "bucket.name";
    public final String FILE_PATH = "file.path";
    public final String MESSAGES_PROPERTIES_PATH =
            WORKING_DIRECTORY + "\\src\\test\\resources\\messages.properties";

    public final String HEADERS_PROPERTIES_PATH =
            WORKING_DIRECTORY + "\\src\\test\\resources\\headers.properties";

    public final String EMAIL_PROPERTIES_PATH =
            WORKING_DIRECTORY + "\\src\\test\\resources\\email.properties";

    public final String EXTENT_REPORT_PATH =
            WORKING_DIRECTORY + "\\extentreport\\ExtentReportResults.html";

    final String API_CONFIG_FILE_PATH =
            WORKING_DIRECTORY + "\\src\\test\\resources\\" + OPERATING_SYSTEM + "APIData.xlsx";

    public final String API_CONFIG_FILE_DESTINATION_PATH =
            WORKING_DIRECTORY + "\\src\\test\\resources\\" + OPERATING_SYSTEM;
    /**
     * Constants for Email Configuration
     */

    public final String EMAIL_ATTACHMENT = WORKING_DIRECTORY
            + "\\extentreport\\ExtentReportResults.html";

    public final String EMAIL_SUBJECT = " - API Test Automation Results - ";

    /**
     * Constants for services
     */
    public final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
    public final String MAX_RETRY_COUNT = "max.retry.count";
    public final String SSM_NAME = "ssm.credentials.name";
    public final String AUTHORIZATION_NAME = "header.authorization.name";
    public final String X_ENTERPRISE_NAME = "header.x.enterprise.name";
    public final String X_STORE_NAME = "header.x.store.name";
    public final String REMOTE_USER_NAME = "header.remote.user.name";


}
