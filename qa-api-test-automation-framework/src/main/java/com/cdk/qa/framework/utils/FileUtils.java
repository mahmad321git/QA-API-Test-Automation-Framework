package com.cdk.qa.framework.utils;

import java.net.URL;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import com.cdk.qa.framework.services.S3Manager;

import java.lang.reflect.Type;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

import java.util.Properties;
import java.util.stream.IntStream;
import java.util.List;
import java.util.Map;


/**
 * This file utility class contains the methods to extract information from different files
 *
 * @author Maria.Saif
 */
@UtilityClass
@Slf4j
public class FileUtils {

    /**
     * To read the config file in excel format, to retrieve the values of the endpoints against
     * mentioned service
     *
     * @param sheetName, For which the configurations are to be extracted
     * @return data
     * @throws IOException, Exception thrown in case of file read/access failure
     */
    //Read the configuration file in excel to retrieve the endpoint information
    public synchronized Object[][] getEndpointConfig(String sheetName) throws IOException {
        //download file from s3
        S3Manager.downloadExcelFilesFromS3();
        String testConfigFilePath = Constants.API_CONFIG_FILE_PATH;
        Object[][] data;

        try (FileInputStream file = new FileInputStream(testConfigFilePath)) {
            try (Workbook book = WorkbookFactory.create(file)) {
                Sheet sheet = book.getSheet(sheetName);
                //If the required sheet is not in the file,
                //it will throw an exception in the test logger and quit the execution.
                data = new Object[sheet.getLastRowNum()][sheet.getRow(0).getLastCellNum()];
                IntStream.range(0, sheet.getLastRowNum()).forEach(i ->
                        IntStream.range(0, sheet.getRow(0).getLastCellNum()).forEach(k ->
                                data[i][k] = sheet.getRow(i + 1).getCell(k).toString()
                        )
                );
            }
        }
        //If the required path is invalid, or the file is not placed
        //it will throw an exception in the test logger and quit the execution.
        catch (IOException e) {
            throw new IOException(e.getMessage(), e);
        }
        return data;
    }

    /**
     * Get the values of the properties from the properties
     *
     * @param key, Key of the property to get value for
     * @return propertyValue
     */
    public synchronized String getPropertyValue(String filePath, String key) {

        String propertyValue = "message not found - ";
        try (InputStream input = new FileInputStream(filePath)) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            propertyValue = prop.getProperty(key);
        }
        //In case the message isn't retrieved, the test case flow can continue,
        //as this is just for console logging, and the exceptions will be logged in the console.
        catch (IOException ex) {
            log.error(ex.getMessage());
        }
        return propertyValue;
    }

    /**
     * get file from classpath, resources folder
     * @param fileName Name of the file
     */
    public synchronized File getFileFromResources(String fileName) {

        ClassLoader classLoader = FileUtils.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }
    }

    /**
     * Return contents of json file
     *
     * @param file The file to be read
     * @return String
     */
    public synchronized String returnFile(File file) {

        StringBuilder contentBuilder = new StringBuilder();
        String line;
        if (file == null) return "File not found";

        try (FileReader reader = new FileReader(file);
             BufferedReader br = new BufferedReader(reader)) {
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return contentBuilder.toString();
    }
    /**
     * To convert JSON string into Map
     *
     * @param payload, URL of the Source Server
     */
    @SuppressWarnings("UnstableApiUsage")
    public List<Map<String, Object>> stringToList(String payload) {
        Gson gson = new Gson();
        Type resultType = new TypeToken<List<Map<String, Object>>>() {
        }.getType();
        return gson.fromJson(payload, resultType);
    }

    /**
     * To convert JSON string into List
     *
     * @param payload, URL of the Source Server
     */
    public Map stringToMap(String payload) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(payload, Map.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
