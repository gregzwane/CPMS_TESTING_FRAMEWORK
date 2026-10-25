package com.cpms.pages;

import com.cpms.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Login Page - Page Object Model for CPMS Sign In
 * Handles authentication for different user roles
 */
public class LoginPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(LoginPage.class);

    // Page elements using Page Factory
    @FindBy(css = "input[type='email'], input[name='email']")
    private WebElement emailInput;

    @FindBy(css = "input[type='password'], input[name='password']")
    private WebElement passwordInput;

    @FindBy(css = "button[type='submit'], button.sign-in, button[class*='sign'], button[id*='sign']")
    private WebElement signInButton;

    @FindBy(xpath = "//*[contains(@class, 'error') or contains(@class, 'alert') or contains(@class, 'text-red') or @role='alert'] | //div[contains(text(), 'Invalid') or contains(text(), 'Error') or contains(text(), 'incorrect')]")
    private WebElement errorMessage;

    @FindBy(css = "[class*='loading'], [class*='spinner']")
    private WebElement loadingIndicator;

    // Constructor
    public LoginPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        logger.info("LoginPage initialized");
    }

    /**
     * Check if login page is displayed
     */
    public boolean isLoginPageDisplayed() {
        try {
            // Navigate to login page if not already there
            String currentUrl = driver.getCurrentUrl();
            if (!currentUrl.contains("sign")) {
                logger.info("Not on login page, navigating to sign in page");
                driver.get("https://leaverageconstructionhub.vercel.app/");
                Thread.sleep(3000); // Wait for SPA to load
            }

            return isElementVisible(emailInput) &&
                   isElementVisible(passwordInput) &&
                   isElementVisible(signInButton);
        } catch (Exception e) {
            logger.error("Error checking if login page is displayed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Enter email address
     */
    public LoginPage enterEmail(String email) {
        logger.info("Entering email: {}", email);
        clearAndType(emailInput, email);
        return this;
    }

    /**
     * Enter password
     */
    public LoginPage enterPassword(String password) {
        logger.info("Entering password");
        clearAndType(passwordInput, password);
        return this;
    }

    /**
     * Click sign in button
     */
    public void clickSignIn() {
        logger.info("Clicking sign in button");
        clickElement(signInButton);
    }

    /**
     * Perform complete login
     */
    public DashboardPage login(String email, String password) {
        logger.info("Performing login for user: {}", email);
        enterEmail(email);
        enterPassword(password);
        clickSignIn();

        // Wait for login to complete - either success (dashboard) or error
        waitForLoginToComplete();

        // Navigate to dashboard if not redirected automatically
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.ensureOnDashboard();

        return dashboardPage;
    }

    /**
     * Login as Director
     */
    public DashboardPage loginAsDirector() {
        String email = config.getDirectorEmail();
        String password = config.getDirectorPassword();
        logger.info("Logging in as Director: {}", email);
        return login(email, password);
    }

    /**
     * Login as Project Manager
     */
    public DashboardPage loginAsProjectManager() {
        String email = config.getPmEmail();
        String password = config.getPmPassword();
        logger.info("Logging in as Project Manager: {}", email);
        return login(email, password);
    }

    /**
     * Login as Quantity Surveyor
     */
    public DashboardPage loginAsQuantitySurveyor() {
        String email = config.getQsEmail();
        String password = config.getQsPassword();
        logger.info("Logging in as Quantity Surveyor: {}", email);
        return login(email, password);
    }

    /**
     * Login as Client
     */
    public DashboardPage loginAsClient() {
        String email = config.getClientEmail();
        String password = config.getClientPassword();
        logger.info("Logging in as Client: {}", email);
        return login(email, password);
    }

    /**
     * Check if login error is displayed
     */
    public boolean isLoginErrorDisplayed() {
        try {
            return isElementVisible(errorMessage);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get login error message
     */
    public String getLoginErrorMessage() {
        if (isLoginErrorDisplayed()) {
            return getElementText(errorMessage);
        }
        return "";
    }

    /**
     * Wait for login process to complete
     */
    private void waitForLoginToComplete() {
        // Wait for loading to disappear or page to change
        try {
            // Wait briefly for any loading indicator to appear first
            Thread.sleep(2000);

            // Wait for loading to disappear
            if (isElementPresent(By.cssSelector("[class*='loading'], [class*='spinner']"))) {
                waitForElementToDisappear(loadingIndicator);
            }

            // Wait for either success (URL change) or error message - longer timeout for SPA
            waitForAnyCondition(
                () -> !driver.getCurrentUrl().contains("sign"),
                () -> isLoginErrorDisplayed()
            );

            // Additional wait for SPA to fully load
            Thread.sleep(3000);

        } catch (Exception e) {
            logger.warn("Timeout waiting for login completion: {}", e.getMessage());
        }
    }

    /**
     * Check if login was successful
     * Returns true if login form is no longer displayed
     */
    public boolean isLoginSuccessful() {
        try {
            boolean formStillVisible = isLoginPageDisplayed();
            boolean success = !formStillVisible;
            logger.info("isLoginSuccessful check: formVisible={}, result={}", formStillVisible, success);
            return success;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Clear email and password fields
     */
    public LoginPage clearFields() {
        logger.info("Clearing login fields");
        clearElement(emailInput);
        clearElement(passwordInput);
        return this;
    }

    /**
     * Check if sign in button is enabled
     */
    public boolean isSignInButtonEnabled() {
        return signInButton.isEnabled();
    }

    /**
     * Get page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Check if specific validation message is shown for email field
     */
    public boolean isEmailValidationShown() {
        try {
            // Check for HTML5 validation or custom validation messages
            String validationMessage = emailInput.getAttribute("validationMessage");
            return validationMessage != null && !validationMessage.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if specific validation message is shown for password field
     */
    public boolean isPasswordValidationShown() {
        try {
            String validationMessage = passwordInput.getAttribute("validationMessage");
            return validationMessage != null && !validationMessage.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}