package com.cpms.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * ExtentReports Manager for HTML reporting
 * Singleton pattern implementation for thread-safe reporting
 */
public class ExtentManager {

    private static final Logger logger = LogManager.getLogger(ExtentManager.class);
    private static ExtentReports extentReports;
    private static ExtentSparkReporter sparkReporter;
    private static final String REPORTS_DIR = "target/reports/";
    private static final String SCREENSHOTS_DIR = "target/screenshots/";

    /**
     * Initialize ExtentReports with configuration
     */
    public static synchronized void initializeExtentReports() {
        if (extentReports == null) {
            createReportsDirectory();
            createScreenshotsDirectory();

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String reportPath = REPORTS_DIR + "CPMS_Test_Report_" + timestamp + ".html";

            sparkReporter = new ExtentSparkReporter(reportPath);
            configureSparkReporter();

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            setSystemInfo();

            logger.info("ExtentReports initialized. Report will be saved to: {}", reportPath);
        }
    }

    /**
     * Configure Spark Reporter settings
     */
    private static void configureSparkReporter() {
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("CPMS Test Automation Report");
        sparkReporter.config().setReportName("Construction Project Management System - Test Results");
        sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

        // Custom CSS for better styling
        String customCSS = ".brand-logo {\n" +
                           "    background-color: #1f4e78 !important;\n" +
                           "}\n" +
                           ".nav-wrapper {\n" +
                           "    background-color: #1f4e78 !important;\n" +
                           "}\n" +
                           ".test-count-item {\n" +
                           "    background-color: #2d6aa5 !important;\n" +
                           "}\n";

        try {
            sparkReporter.config().setCss(customCSS);
        } catch (Exception e) {
            logger.warn("Could not apply custom CSS: {}", e.getMessage());
        }
    }

    /**
     * Set system information in the report
     */
    private static void setSystemInfo() {
        extentReports.setSystemInfo("Application", "CPMS - Construction Project Management System");
        extentReports.setSystemInfo("Environment", System.getProperty("env", "local"));
        extentReports.setSystemInfo("Browser", System.getProperty("browser", "chrome"));
        extentReports.setSystemInfo("OS", System.getProperty("os.name"));
        extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        extentReports.setSystemInfo("User", System.getProperty("user.name"));
        extentReports.setSystemInfo("Test Framework", "Selenium WebDriver + TestNG");
        extentReports.setSystemInfo("Reporting", "ExtentReports v5.1.1");
        extentReports.setSystemInfo("Execution Time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    /**
     * Create a new test in the report
     */
    public static synchronized ExtentTest createTest(String testName, String testDescription) {
        if (extentReports == null) {
            initializeExtentReports();
        }

        ExtentTest test = extentReports.createTest(testName, testDescription);
        logger.debug("Created ExtentTest: {}", testName);
        return test;
    }

    /**
     * Attach screenshot to test
     */
    public static void attachScreenshot(ExtentTest test, byte[] screenshot) {
        if (test == null || screenshot == null) {
            logger.warn("Cannot attach screenshot - test or screenshot is null");
            return;
        }

        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS"));
            String screenshotName = "screenshot_" + timestamp + ".png";
            String screenshotPath = SCREENSHOTS_DIR + screenshotName;

            // Save screenshot to file
            Path path = Paths.get(screenshotPath);
            Files.write(path, screenshot);

            // Attach to ExtentReports
            test.info("Screenshot captured", MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            logger.debug("Screenshot attached to test: {}", screenshotPath);

        } catch (IOException e) {
            logger.error("Failed to save screenshot: {}", e.getMessage());

            // Fallback: attach as base64 embedded image
            try {
                String base64Screenshot = Base64.getEncoder().encodeToString(screenshot);
                test.info("Screenshot captured", MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build());
            } catch (Exception ex) {
                logger.error("Failed to attach screenshot as base64: {}", ex.getMessage());
            }
        }
    }

    /**
     * Log test step with info level
     */
    public static void logInfo(ExtentTest test, String message) {
        if (test != null) {
            test.log(Status.INFO, message);
        }
    }

    /**
     * Log test step with pass level
     */
    public static void logPass(ExtentTest test, String message) {
        if (test != null) {
            test.log(Status.PASS, message);
        }
    }

    /**
     * Log test step with fail level
     */
    public static void logFail(ExtentTest test, String message) {
        if (test != null) {
            test.log(Status.FAIL, message);
        }
    }

    /**
     * Log test step with warning level
     */
    public static void logWarning(ExtentTest test, String message) {
        if (test != null) {
            test.log(Status.WARNING, message);
        }
    }

    /**
     * Log test step with skip level
     */
    public static void logSkip(ExtentTest test, String message) {
        if (test != null) {
            test.log(Status.SKIP, message);
        }
    }

    /**
     * Flush the ExtentReports (write to file)
     */
    public static synchronized void flushExtentReports() {
        if (extentReports != null) {
            extentReports.flush();
            logger.info("ExtentReports flushed successfully");
        }
    }

    /**
     * Get the ExtentReports instance
     */
    public static ExtentReports getExtentReports() {
        if (extentReports == null) {
            initializeExtentReports();
        }
        return extentReports;
    }

    /**
     * Create reports directory if it doesn't exist
     */
    private static void createReportsDirectory() {
        try {
            Path reportsPath = Paths.get(REPORTS_DIR);
            if (!Files.exists(reportsPath)) {
                Files.createDirectories(reportsPath);
                logger.info("Created reports directory: {}", REPORTS_DIR);
            }
        } catch (IOException e) {
            logger.error("Failed to create reports directory: {}", e.getMessage());
        }
    }

    /**
     * Create screenshots directory if it doesn't exist
     */
    private static void createScreenshotsDirectory() {
        try {
            Path screenshotsPath = Paths.get(SCREENSHOTS_DIR);
            if (!Files.exists(screenshotsPath)) {
                Files.createDirectories(screenshotsPath);
                logger.info("Created screenshots directory: {}", SCREENSHOTS_DIR);
            }
        } catch (IOException e) {
            logger.error("Failed to create screenshots directory: {}", e.getMessage());
        }
    }

    /**
     * Cleanup resources
     */
    public static synchronized void cleanup() {
        if (extentReports != null) {
            extentReports.flush();
            extentReports = null;
        }

        if (sparkReporter != null) {
            sparkReporter = null;
        }

        logger.info("ExtentManager cleanup completed");
    }
}