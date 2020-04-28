package com.cdk.qa.framework.services;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public final class SSMManager {
    /**
     * Get the Parameters  from the SSM
     *
     * @return keyValue
     */
    public static String getSSMParameter(String key) {
        AWSSimpleSystemsManagement ssmClient = AWSSimpleSystemsManagementClientBuilder.defaultClient();
        GetParameterRequest parameterRequest = new GetParameterRequest();
        parameterRequest.withName(key).setWithDecryption(true);
        GetParameterResult parameterResult = ssmClient
                .getParameter(parameterRequest);
        String keyValue = null;
        Parameter paramList = parameterResult.getParameter();
        if (paramList != null) {
            keyValue = paramList.getValue();
        }
        return keyValue;
    }
}
