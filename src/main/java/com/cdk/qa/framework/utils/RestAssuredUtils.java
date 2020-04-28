package com.cdk.qa.framework.utils;

import io.restassured.RestAssured;
import lombok.experimental.UtilityClass;

/**
 * This class is for base methods of RestAssured Framework
 *
 * @author Adil.Qayyum
 */
@UtilityClass
public class RestAssuredUtils {

    /**
     * Sets Base URI
     *
     * @param baseUrl, Base URL
     */
    public synchronized void setBaseURI(String baseUrl) {
        RestAssured.baseURI = baseUrl;
    }

    /**
     * Reset Base URI (after test)
     */
    public synchronized void resetBaseURI() {
        RestAssured.baseURI = null;
    }

    /**
     * Reset base path
     */
    public synchronized void resetBasePath() {
        RestAssured.basePath = null;
    }

}

