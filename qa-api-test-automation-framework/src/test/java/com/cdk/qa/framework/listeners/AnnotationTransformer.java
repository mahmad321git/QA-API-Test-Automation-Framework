package com.cdk.qa.framework.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

/**
 * Standard class for TestNG Annotation Transformer
 */
public class AnnotationTransformer implements IAnnotationTransformer {

    public void transform(ITestAnnotation annotation, @SuppressWarnings("rawtypes") Class testClass,
                          @SuppressWarnings("rawtypes") Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(Retry.class);
    }
}
