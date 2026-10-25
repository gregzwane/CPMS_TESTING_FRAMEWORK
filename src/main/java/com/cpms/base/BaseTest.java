package com.cpms.base;

import com.cpms.config.ConfigManager;
import com.cpms.utils.ExtentManager;
import com.cpms.utils.DriverManager;
import com.aventstack.extentreports.ExtentTest;
import io.qameta.allure.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;

/**
 * Base test class that all test classes should extend
 * Provides driver management, reporting, and common test setup/teardown
 */
public class BaseTest {

    private static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected WebDriver driver;
    protected ExtentTest extentTest;
    protected ConfigManager config;

    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        logger.info("Starting CPMS Test Suite");
        config = ConfigManager.getInstance();
        ExtentManager.initializeExtentReports();
    }

    @BeforeClass(alwaysRun = true)
    public void classSetup() {
        logger.info("Setting up test class: {}", this.getClass().getSimpleName());
    }

    @BeforeMethod(alwaysRun = true)
    public void testSetup(Method method) {
        logger.info("Starting test: {}", method.getName());

        // Ensure config is initialized (handles parallel execution)
        if (config == null) {
            config = ConfigManager.getInstance();
        }

        // Initialize driver
        String browserName = System.getProperty("browser", config.getBrowser());
        driver = DriverManager.getDriver(browserName);

        // Create extent test
        extentTest = ExtentManager.createTest(method.getName(), method.getAnnotation(Test.class).description());
        extentTest.assignCategory(this.getClass().getSimpleName());

        // Navigate to application URL
        String environment = System.getProperty("env", "local");
        String url = config.getApplicationUrl(environment);
        driver.get(url);
        logger.info("Navigated to: {}", url);
    }

    @AfterMethod(alwaysRun = true)
    public void testTeardown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            logger.error("Test failed: {}", result.getName());
            extentTest.fail("Test Failed: " + result.getThrowable().getMessage());

            // Capture screenshot on failure
            byte[] screenshot = captureScreenshot();
            if (screenshot != null) {
                attachScreenshotToAllure("Failed Test Screenshot", screenshot);
                ExtentManager.attachScreenshot(extentTest, screenshot);
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            logger.info("Test passed: {}", result.getName());
            extentTest.pass("Test Passed Successfully");
        } else if (result.getStatus() == ITestResult.SKIP) {
            logger.warn("Test skipped: {}", result.getName());
            extentTest.skip("Test Skipped: " + result.getThrowable().getMessage());
        }

        // Quit driver
        if (driver != null) {
            DriverManager.quitDriver();
            driver = null;
        }
    }

    @AfterClass(alwaysRun = true)
    public void classTeardown() {
        logger.info("Tearing down test class: {}", this.getClass().getSimpleName());
    }

    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        logger.info("Finishing CPMS Test Suite");
        ExtentManager.flushExtentReports();
    }

    /**
     * Capture screenshot for reporting
     */
    protected byte[] captureScreenshot() {
        try {
            if (driver instanceof TakesScreenshot) {
                return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            }
        } catch (Exception e) {
            logger.error("Failed to capture screenshot: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Attach screenshot to Allure report
     */
    @Attachment(value = "{name}", type = "image/png")
    private byte[] attachScreenshotToAllure(String name, byte[] screenshot) {
        return screenshot;
    }

    /**
     * Log test step for reporting
     */
    protected void logTestStep(String stepDescription) {
        logger.info("Test Step: {}", stepDescription);
        if (extentTest != null) {
            extentTest.info(stepDescription);
        }
    }

    /**
     * Wait for specified milliseconds
     */
    protected void waitFor(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Wait interrupted: {}", e.getMessage());
        }
    }
}