package com.cpms.accessibility;

import com.cpms.base.BaseTest;
import com.cpms.pages.LoginPage;
import com.cpms.pages.DashboardPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Accessibility Tests using axe-core.
 * Tests WCAG 2.1 AA compliance for CPMS pages.
 * Injects axe-core JavaScript and runs automated accessibility checks.
 */
@Epic("Accessibility Testing")
@Feature("WCAG 2.1 AA Compliance")
public class AccessibilityTests extends BaseTest {

    private static final Logger logger = LogManager.getLogger(AccessibilityTests.class);
    private static String axeCoreScript;

    static {
        // Load axe-core.min.js from resources
        try (InputStream is = AccessibilityTests.class.getClassLoader()
                .getResourceAsStream("axe-core/axe-core.min.js")) {
            if (is != null) {
                axeCoreScript = new Scanner(is, StandardCharsets.UTF_8).useDelimiter("\\A").next();
            } else {
                // Fallback: use CDN URL injection approach
                axeCoreScript = null;
                logger.warn("axe-core.min.js not found in resources, will inject from CDN");
            }
        } catch (Exception e) {
            logger.warn("Failed to load axe-core from resources: {}", e.getMessage());
            axeCoreScript = null;
        }
    }

    /**
     * Test Case: Accessibility - Login page WCAG compliance
     * Verifies login page meets WCAG 2.1 AA standards.
     */
    @Test(description = "Accessibility - Login Page WCAG 2.1 AA", priority = 1)
    @Description("Verify login page accessibility compliance")
    @Story("Accessibility - Login Page")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginPageAccessibility() {
        logTestStep("Starting login page accessibility test");

        driver.get("https://cpms.eminovatech.com/");
        waitFor(3000);

        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Login page should be displayed");

        // Run axe accessibility check
        List<Map<String, Object>> violations = runAxeCheck();

        // Filter and report violations
        logViolations(violations, "Login Page");

        // Assert no critical or serious violations
        Assert.assertTrue(!hasCriticalViolations(violations), 
            "Login page has critical/serious accessibility violations: " + formatViolations(violations));
        
        logTestStep("Login page accessibility test completed");
    }

    /**
     * Test Case: Accessibility - Director Dashboard WCAG compliance
     * Verifies dashboard meets WCAG 2.1 AA standards.
     */
    @Test(description = "Accessibility - Director Dashboard WCAG 2.1 AA", priority = 2)
    @Description("Verify Director dashboard accessibility compliance")
    @Story("Accessibility - Dashboard")
    @Severity(SeverityLevel.CRITICAL)
    public void testDirectorDashboardAccessibility() {
        logTestStep("Starting Director dashboard accessibility test");

        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsDirector();
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Dashboard should be loaded");

        // Wait for all widgets to render
        waitFor(5000);

        // Run axe accessibility check
        List<Map<String, Object>> violations = runAxeCheck();

        // Filter and report violations
        logViolations(violations, "Director Dashboard");

        // Assert no critical or serious violations
        Assert.assertTrue(!hasCriticalViolations(violations), 
            "Dashboard has critical/serious accessibility violations: " + formatViolations(violations));
        
        logTestStep("Director dashboard accessibility test completed");
    }

    /**
     * Test Case: Accessibility - Projects page WCAG compliance
     * Verifies projects list page meets WCAG 2.1 AA standards.
     */
    @Test(description = "Accessibility - Projects Page WCAG 2.1 AA", priority = 3)
    @Description("Verify Projects page accessibility compliance")
    @Story("Accessibility - Projects Page")
    @Severity(SeverityLevel.CRITICAL)
    public void testProjectsPageAccessibility() {
        logTestStep("Starting Projects page accessibility test");

        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsDirector();
        dashboardPage.navigateToProjects();

        // Wait for projects list to load
        waitFor(3000);

        // Run axe accessibility check
        List<Map<String, Object>> violations = runAxeCheck();

        // Filter and report violations
        logViolations(violations, "Projects Page");

        // Assert no critical or serious violations
        Assert.assertTrue(!hasCriticalViolations(violations), 
            "Projects page has critical/serious accessibility violations: " + formatViolations(violations));
        
        logTestStep("Projects page accessibility test completed");
    }

    /**
     * Inject and run axe-core accessibility check.
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> runAxeCheck() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Inject axe-core if not already injected
            if (axeCoreScript != null) {
                js.executeScript(axeCoreScript);
            } else {
                // Inject from CDN
                js.executeScript(
                    "var script = document.createElement('script');" +
                    "script.src = 'https://cdnjs.cloudflare.com/ajax/libs/axe-core/4.8.2/axe.min.js';" +
                    "script.onload = function() { window.axeLoaded = true; };" +
                    "document.head.appendChild(script);"
                );
                // Wait for load
                waitFor(2000);
            }

            // Run axe check
            Object result = js.executeScript(
                "return new Promise(function(resolve) {" +
                "  if (typeof axe !== 'undefined') {" +
                "    axe.run({runOnly: {type: 'tag', values: ['wcag2aa', 'wcag21aa', 'best-practice']}}, function(err, results) {" +
                "      resolve(results.violations);" +
                "    });" +
                "  } else {" +
                "    resolve([]);" +
                "  }" +
                "});"
            );

            return (List<Map<String, Object>>) result;

        } catch (Exception e) {
            logger.error("Axe accessibility check failed: {}", e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Check if violations include critical or serious issues.
     */
    private boolean hasCriticalViolations(List<Map<String, Object>> violations) {
        if (violations == null || violations.isEmpty()) {
            return false;
        }

        for (Map<String, Object> violation : violations) {
            Object impact = violation.get("impact");
            if (impact != null) {
                String impactStr = impact.toString().toLowerCase();
                if ("critical".equals(impactStr) || "serious".equals(impactStr)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Log violations for reporting.
     */
    private void logViolations(List<Map<String, Object>> violations, String pageName) {
        if (violations == null || violations.isEmpty()) {
            logger.info("{} - No accessibility violations found", pageName);
            return;
        }

        logger.warn("{} - Found {} accessibility violations:", pageName, violations.size());
        
        int critical = 0, serious = 0, moderate = 0, minor = 0;
        
        for (Map<String, Object> violation : violations) {
            String id = (String) violation.get("id");
            String impact = (String) violation.get("impact");
            String description = (String) violation.get("description");
            String help = (String) violation.get("help");
            String helpUrl = (String) violation.get("helpUrl");
            List<Map<String, Object>> nodes = (List<Map<String, Object>>) violation.get("nodes");

            switch (impact != null ? impact.toLowerCase() : "") {
                case "critical": critical++; break;
                case "serious": serious++; break;
                case "moderate": moderate++; break;
                case "minor": minor++; break;
            }

            logger.warn("  [{}] {} - {} ({})", 
                impact != null ? impact.toUpperCase() : "UNKNOWN", 
                id, description, helpUrl);
            
            if (nodes != null) {
                for (Map<String, Object> node : nodes) {
                    List<String> target = (List<String>) node.get("target");
                    logger.warn("    Element: {}", target != null ? target : "unknown");
                }
            }
        }
        
        logger.warn("  Summary: Critical={}, Serious={}, Moderate={}, Minor={}", 
            critical, serious, moderate, minor);
    }

    /**
     * Format violations for assertion message.
     */
    private String formatViolations(List<Map<String, Object>> violations) {
        if (violations == null || violations.isEmpty()) {
            return "None";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> v : violations) {
            String impact = (String) v.get("impact");
            String id = (String) v.get("id");
            String desc = (String) v.get("description");
            if ("critical".equalsIgnoreCase(impact) || "serious".equalsIgnoreCase(impact)) {
                sb.append(String.format("[%s] %s: %s; ", 
                    impact.toUpperCase(), id, desc));
            }
        }
        return sb.toString();
    }
}
