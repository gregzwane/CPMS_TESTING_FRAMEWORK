package com.cpms.utils;

import org.testng.ITestListener;
import org.testng.ITestResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TestNG Listener for test execution lifecycle
 * Handles test start, finish, and failure events
 */
public class TestListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Starting test: {}.{}",
                result.getTestClass().getName(), result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Test PASSED: {}.{}",
                result.getTestClass().getName(), result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("Test FAILED: {}.{} - {}",
                result.getTestClass().getName(),
                result.getMethod().getMethodName(),
                result.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("Test SKIPPED: {}.{} - {}",
                result.getTestClass().getName(),
                result.getMethod().getMethodName(),
                result.getThrowable() != null ? result.getThrowable().getMessage() : "No reason provided");
    }
}