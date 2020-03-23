package com.cdk.qa.framework.services;

import com.cdk.qa.framework.utils.FileUtils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.cdk.qa.framework.utils.Constants;

import java.io.File;
import java.io.IOException;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class S3Manager {

    /**
     * Download test data file form s3
     */
    public static void downloadExcelFilesFromS3() {

        String s3filePath = SSMManager.getSSMParameter(FileUtils.getPropertyValue
                (Constants.HEADERS_PROPERTIES_PATH, Constants.FILE_PATH));
        String s3bucketName = SSMManager.getSSMParameter
                (FileUtils.getPropertyValue(Constants.HEADERS_PROPERTIES_PATH, Constants.BUCKET_NAME));

        if (s3filePath != null && s3bucketName != null) {
            try {
                final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
                S3Object s3Object = s3.getObject(s3bucketName, s3filePath);
                S3ObjectInputStream s3is = s3Object.getObjectContent();
                File des = new File(Constants.API_CONFIG_FILE_DESTINATION_PATH + s3filePath);
                //use  org.apache.commons.io utility to write data on local
                org.apache.commons.io.FileUtils.copyInputStreamToFile(s3is.getDelegateStream(), des);
            } catch (AmazonServiceException | IOException e) {
                log.error("Error Message", e);
                System.exit(1);
            }
        }
    }
}