package com.cpms.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration Manager for handling application properties
 * Singleton pattern implementation
 */
public class ConfigManager {

    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private Properties properties;

    // Configuration keys
    private static final String DEFAULT_BROWSER = "chrome";
    private static final String DEFAULT_TIMEOUT = "30";
    private static final String DEFAULT_ENVIRONMENT = "local";

    private ConfigManager() {
        loadProperties();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
                logger.info("Configuration properties loaded successfully");
            } else {
                logger.warn("config.properties file not found, using default values");
                setDefaultProperties();
            }
        } catch (IOException e) {
            logger.error("Error loading configuration properties: {}", e.getMessage());
            setDefaultProperties();
        }
    }

    private void setDefaultProperties() {
        properties.setProperty("browser", DEFAULT_BROWSER);
        properties.setProperty("timeout", DEFAULT_TIMEOUT);
        properties.setProperty("environment", DEFAULT_ENVIRONMENT);
        properties.setProperty("local.url", "https://leaverageconstructionhub.vercel.app/");
        properties.setProperty("staging.url", "https://leaverageconstructionhub-staging.vercel.app/");
        properties.setProperty("api.base.url", "https://cpms-api-l3o7.onrender.com");
        properties.setProperty("headless", "false");
        properties.setProperty("screenshots.enabled", "true");
    }

    // Browser configuration
    public String getBrowser() {
        return System.getProperty("browser", properties.getProperty("browser", DEFAULT_BROWSER));
    }

    // Timeout configuration
    public int getTimeout() {
        String timeout = System.getProperty("timeout", properties.getProperty("timeout", DEFAULT_TIMEOUT));
        return Integer.parseInt(timeout);
    }

    // Application URLs by environment
    public String getApplicationUrl(String environment) {
        String key = environment.toLowerCase() + ".url";
        return properties.getProperty(key, properties.getProperty("local.url"));
    }

    // API configuration
    public String getApiBaseUrl() {
        return properties.getProperty("api.base.url");
    }

    // Test user credentials
    public String getDirectorEmail() {
        return properties.getProperty("director.email", "director@cpms.demo");
    }

    public String getDirectorPassword() {
        return properties.getProperty("director.password", "ChangeMe123!");
    }

    public String getPmEmail() {
        return properties.getProperty("pm.email", "pm@cpms.demo");
    }

    public String getPmPassword() {
        return properties.getProperty("pm.password", "ChangeMe123!");
    }

    public String getQsEmail() {
        return properties.getProperty("qs.email", "qs@cpms.demo");
    }

    public String getQsPassword() {
        return properties.getProperty("qs.password", "ChangeMe123!");
    }

    public String getClientEmail() {
        return properties.getProperty("client.email", "client@cpms.demo");
    }

    public String getClientPassword() {
        return properties.getProperty("client.password", "ChangeMe123!");
    }

    // Browser options
    public boolean isHeadless() {
        String headless = System.getProperty("headless", properties.getProperty("headless", "false"));
        return Boolean.parseBoolean(headless);
    }

    public boolean areScreenshotsEnabled() {
        String screenshots = properties.getProperty("screenshots.enabled", "true");
        return Boolean.parseBoolean(screenshots);
    }

    // Test data file paths
    public String getTestDataPath() {
        return properties.getProperty("test.data.path", "src/test/resources/data/");
    }

    public String getBoqDataFile() {
        return properties.getProperty("boq.data.file", "boq-budget-riverside.csv");
    }

    public String getReportsPath() {
        return properties.getProperty("reports.path", "target/reports/");
    }

    // Geolocation test data
    public String getJhbLatitude() {
        return properties.getProperty("jhb.latitude", "-26.2041");
    }

    public String getJhbLongitude() {
        return properties.getProperty("jhb.longitude", "28.0473");
    }

    public String getCptLatitude() {
        return properties.getProperty("cpt.latitude", "-33.9249");
    }

    public String getCptLongitude() {
        return properties.getProperty("cpt.longitude", "18.4241");
    }

    // Project test data
    public String getTestProjectCode() {
        return properties.getProperty("test.project.code", "JHB-T1");
    }

    public String getTestProjectName() {
        return properties.getProperty("test.project.name", "Johannesburg Bulk Earthworks — Tester");
    }

    // Mobile configuration
    public String getMobileAppPackage() {
        return properties.getProperty("mobile.app.package", "com.cpms.mobile");
    }

    public String getMobileAppActivity() {
        return properties.getProperty("mobile.app.activity", "com.cpms.mobile.MainActivity");
    }

    public String getAppiumServerUrl() {
        return properties.getProperty("appium.server.url", "http://localhost:4723");
    }

    // Generic property getter
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}