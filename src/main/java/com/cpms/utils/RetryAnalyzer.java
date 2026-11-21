package com.cpms.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Intelligent retry analyzer for TestNG tests.
 * Implements exponential backoff with configurable max retries.
 * Only retries on specific failure types (flaky tests, timeouts, network issues).
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);
    
    // Configuration - can be overridden via system properties
    private static final int DEFAULT_MAX_RETRIES = Integer.parseInt(
        System.getProperty("test.retry.count", "2"));
    private static final int DEFAULT_MAX_RETRIES_API = Integer.parseInt(
        System.getProperty("api.retry.count", "3"));
    
    private int retryCount = 0;
    private int maxRetries = DEFAULT_MAX_RETRIES;
    private final Class<?> testClass;

    public RetryAnalyzer() {
        this.testClass = getClass();
    }

    /**
     * Constructor for specific test class configuration.
     */
    public RetryAnalyzer(Class<?> testClass) {
        this.testClass = testClass;
        // Use API retry count for API tests
        if (testClass.getPackageName().contains(".api.")) {
            this.maxRetries = DEFAULT_MAX_RETRIES_API;
        }
    }

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount >= maxRetries) {
            logger.warn("Max retries ({}) exceeded for test: {}", maxRetries, result.getName());
            return false;
        }

        // Check if test failure is retryable
        if (!isRetryableFailure(result)) {
            logger.info("Test failure not retryable: {}", result.getName());
            return false;
        }

        retryCount++;
        logger.info("Retrying test: {} (attempt {}/{})", result.getName(), retryCount, maxRetries);
        
        // Exponential backoff: 1s, 2s, 4s, 8s...
        long delay = (long) Math.pow(2, retryCount - 1) * 1000;
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Retry sleep interrupted: {}", e.getMessage());
        }
        
        return true;
    }

    /**
     * Determine if a test failure is retryable.
     * Retries on: timeouts, network errors, stale element, flaky assertions
     * Does NOT retry on: assertion errors (logic bugs), configuration errors
     */
    private boolean isRetryableFailure(ITestResult result) {
        Throwable throwable = result.getThrowable();
        if (throwable == null) {
            return false;
        }

        String message = throwable.getMessage();
        String className = throwable.getClass().getName();
        
        // Log the failure for analysis
        logger.debug("Analyzing failure: {} - {}", className, message);

        // Retryable exception types
        boolean isTimeout = className.contains("TimeoutException") 
            || className.contains("SocketTimeoutException")
            || className.contains("ConnectTimeoutException");
        
        boolean isNetworkError = className.contains("ConnectException")
            || className.contains("UnknownHostException")
            || className.contains("SSLException")
            || className.contains("HttpHostConnectException");
        
        boolean isStaleElement = className.contains("StaleElementReferenceException");
        
        boolean isWebDriverFlaky = className.contains("WebDriverException")
            && (message != null && (message.contains("timed out") 
                || message.contains("disconnected")
                || message.contains("session deleted")));

        boolean isRestAssuredRetryable = className.contains("RestAssuredException")
            && (message != null && (message.contains("connection") 
                || message.contains("timeout")
                || message.contains("503")
                || message.contains("504")));

        // Non-retryable: assertion errors (actual bugs), illegal arguments (config issues)
        boolean isAssertionError = className.contains("AssertionError") 
            || className.contains("AssertionFailedError");
        
        boolean isConfigError = className.contains("IllegalArgumentException")
            || className.contains("IllegalStateException")
            || className.contains("ConfigurationException");

        boolean retryable = isTimeout || isNetworkError || isStaleElement 
            || isWebDriverFlaky || isRestAssuredRetryable;
        
        boolean nonRetryable = isAssertionError || isConfigError;

        if (retryable && !nonRetryable) {
            logger.info("Failure classified as RETRYABLE: {} - {}", className, message);
            return true;
        } else if (nonRetryable) {
            logger.info("Failure classified as NON-RETRYABLE (assertion/config): {} - {}", className, message);
            return false;
        } else {
            // Unknown exception type - be conservative, don't retry
            logger.info("Failure classified as UNKNOWN - not retrying: {} - {}", className, message);
            return false;
        }
    }

    /**
     * Reset retry count (called before each test method).
     */
    public void reset() {
        this.retryCount = 0;
    }

    /**
     * Get current retry count.
     */
    public int getRetryCount() {
        return retryCount;
    }

    /**
     * Get max retries configured.
     */
    public int getMaxRetries() {
        return maxRetries;
    }
}
