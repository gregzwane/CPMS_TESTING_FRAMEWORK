package com.cpms.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Driver Manager utility for cross-browser testing
 * Handles driver creation, configuration and cleanup
 */
public class DriverManager {

    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ConcurrentHashMap<Long, WebDriver> drivers = new ConcurrentHashMap<>();

    // Browser types
    public enum BrowserType {
        CHROME, FIREFOX, EDGE
    }

    /**
     * Get WebDriver instance for the specified browser
     */
    public static WebDriver getDriver(String browserName) {
        BrowserType browserType;
        try {
            browserType = BrowserType.valueOf(browserName.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Unknown browser type: {}. Defaulting to Chrome", browserName);
            browserType = BrowserType.CHROME;
        }

        return getDriver(browserType);
    }

    /**
     * Get WebDriver instance for the specified browser type
     */
    public static WebDriver getDriver(BrowserType browserType) {
        WebDriver driver = driverThreadLocal.get();

        if (driver == null) {
            switch (browserType) {
                case CHROME:
                    driver = createChromeDriver();
                    break;
                case FIREFOX:
                    driver = createFirefoxDriver();
                    break;
                case EDGE:
                    driver = createEdgeDriver();
                    break;
                default:
                    throw new IllegalArgumentException("Browser type not supported: " + browserType);
            }

            // Configure driver timeouts
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            driver.manage().window().maximize();

            // Store driver in thread local and concurrent map
            driverThreadLocal.set(driver);
            drivers.put(Thread.currentThread().getId(), driver);

            logger.info("Created {} driver for thread: {}", browserType, Thread.currentThread().getId());
        }

        return driver;
    }

    /**
     * Create Chrome WebDriver with options
     */
    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        // Common Chrome options
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--remote-allow-origins=*");

        // Headless mode if configured
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
            logger.info("Running Chrome in headless mode with 1920x1080 window size");
        }

        // Mobile emulation for responsive testing
        if (Boolean.parseBoolean(System.getProperty("mobile", "false"))) {
            options.setExperimentalOption("mobileEmulation",
                java.util.Map.of("deviceName", "iPhone 12 Pro"));
            logger.info("Running Chrome with mobile emulation");
        }

        return new ChromeDriver(options);
    }

    /**
     * Create Firefox WebDriver with options
     */
    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();

        // Common Firefox options
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        // Headless mode if configured
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        if (headless) {
            options.addArguments("--headless");
            logger.info("Running Firefox in headless mode");
        }

        return new FirefoxDriver(options);
    }

    /**
     * Create Edge WebDriver with options
     */
    private static WebDriver createEdgeDriver() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();

        // Common Edge options
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");

        // Headless mode if configured
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        if (headless) {
            options.addArguments("--headless");
            logger.info("Running Edge in headless mode");
        }

        return new EdgeDriver(options);
    }

    /**
     * Get current driver instance
     */
    public static WebDriver getCurrentDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Quit current driver and clean up
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        Long threadId = Thread.currentThread().getId();

        if (driver != null) {
            try {
                driver.quit();
                logger.info("Driver quit successfully for thread: {}", threadId);
            } catch (Exception e) {
                logger.error("Error quitting driver for thread {}: {}", threadId, e.getMessage());
            } finally {
                driverThreadLocal.remove();
                drivers.remove(threadId);
            }
        }
    }

    /**
     * Quit all drivers (cleanup method for test suite)
     */
    public static void quitAllDrivers() {
        logger.info("Quitting all drivers. Active drivers: {}", drivers.size());

        drivers.forEach((threadId, driver) -> {
            try {
                if (driver != null) {
                    driver.quit();
                    logger.info("Driver quit for thread: {}", threadId);
                }
            } catch (Exception e) {
                logger.error("Error quitting driver for thread {}: {}", threadId, e.getMessage());
            }
        });

        drivers.clear();

        // Clean up thread local as well
        try {
            driverThreadLocal.remove();
        } catch (Exception e) {
            logger.warn("Error cleaning up thread local: {}", e.getMessage());
        }
    }

    /**
     * Get number of active driver instances
     */
    public static int getActiveDriverCount() {
        return drivers.size();
    }

    /**
     * Check if driver is available for current thread
     */
    public static boolean isDriverAvailable() {
        return driverThreadLocal.get() != null;
    }
}