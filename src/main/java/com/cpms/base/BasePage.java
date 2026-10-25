package com.cpms.base;

import com.cpms.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

/**
 * Base Page class containing common methods for all page objects
 * Provides reusable web element interaction methods
 */
public abstract class BasePage {

    protected static final Logger logger = LogManager.getLogger(BasePage.class);
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;
    protected JavascriptExecutor jsExecutor;
    protected ConfigManager config;

    // Constructor
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.config = ConfigManager.getInstance();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(config.getTimeout()));
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    // Element visibility and presence methods
    protected boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isElementVisible(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isElementEnabled(WebElement element) {
        try {
            return element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    // Wait methods
    protected WebElement waitForElementToBeVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitForElementToBeClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected void waitForElementToDisappear(WebElement element) {
        wait.until(ExpectedConditions.invisibilityOf(element));
    }

    protected void waitForTextToBePresentInElement(WebElement element, String text) {
        wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    protected void waitForUrlToContain(String urlFragment) {
        wait.until(ExpectedConditions.urlContains(urlFragment));
    }

    protected void waitForAnyCondition(Supplier<Boolean>... conditions) {
        wait.until(driver -> {
            for (Supplier<Boolean> condition : conditions) {
                if (condition.get()) {
                    return true;
                }
            }
            return false;
        });
    }

    // Click methods
    protected void clickElement(WebElement element) {
        try {
            waitForElementToBeClickable(element);
            element.click();
            logger.debug("Clicked element: {}", element.toString());
        } catch (Exception e) {
            logger.error("Failed to click element: {}", e.getMessage());
            // Try JavaScript click as fallback
            jsExecutor.executeScript("arguments[0].click();", element);
            logger.debug("Clicked element using JavaScript");
        }
    }

    protected void clickElementWithActions(WebElement element) {
        waitForElementToBeVisible(element);
        actions.moveToElement(element).click().perform();
        logger.debug("Clicked element using Actions");
    }

    protected void doubleClickElement(WebElement element) {
        waitForElementToBeVisible(element);
        actions.doubleClick(element).perform();
        logger.debug("Double-clicked element");
    }

    // Text input methods
    protected void clearAndType(WebElement element, String text) {
        try {
            waitForElementToBeVisible(element);
            element.clear();
            element.sendKeys(text);
            logger.debug("Typed text: {} into element", text);
        } catch (Exception e) {
            logger.error("Failed to type text: {}", e.getMessage());
            // Fallback using JavaScript
            jsExecutor.executeScript("arguments[0].value = arguments[1];", element, text);
        }
    }

    protected void typeText(WebElement element, String text) {
        waitForElementToBeVisible(element);
        element.sendKeys(text);
        logger.debug("Typed text: {}", text);
    }

    protected void clearElement(WebElement element) {
        waitForElementToBeVisible(element);
        element.clear();
        logger.debug("Cleared element");
    }

    // Text retrieval methods
    protected String getElementText(WebElement element) {
        waitForElementToBeVisible(element);
        return element.getText().trim();
    }

    protected String getElementAttribute(WebElement element, String attribute) {
        waitForElementToBeVisible(element);
        return element.getAttribute(attribute);
    }

    protected String getElementValue(WebElement element) {
        return getElementAttribute(element, "value");
    }

    // Dropdown/Select methods
    protected void selectByVisibleText(WebElement element, String text) {
        waitForElementToBeVisible(element);
        Select select = new Select(element);
        select.selectByVisibleText(text);
        logger.debug("Selected option by text: {}", text);
    }

    protected void selectByValue(WebElement element, String value) {
        waitForElementToBeVisible(element);
        Select select = new Select(element);
        select.selectByValue(value);
        logger.debug("Selected option by value: {}", value);
    }

    protected void selectByIndex(WebElement element, int index) {
        waitForElementToBeVisible(element);
        Select select = new Select(element);
        select.selectByIndex(index);
        logger.debug("Selected option by index: {}", index);
    }

    protected String getSelectedOptionText(WebElement element) {
        waitForElementToBeVisible(element);
        Select select = new Select(element);
        return select.getFirstSelectedOption().getText();
    }

    // JavaScript methods
    protected void scrollToElement(WebElement element) {
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
        logger.debug("Scrolled to element");
    }

    protected void scrollToTop() {
        jsExecutor.executeScript("window.scrollTo(0, 0);");
        logger.debug("Scrolled to top of page");
    }

    protected void scrollToBottom() {
        jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        logger.debug("Scrolled to bottom of page");
    }

    protected void highlightElement(WebElement element) {
        jsExecutor.executeScript("arguments[0].style.border='3px solid red'", element);
        logger.debug("Highlighted element");
    }

    // Navigation methods
    protected void navigateToUrl(String url) {
        driver.get(url);
        logger.info("Navigated to URL: {}", url);
    }

    protected void refreshPage() {
        driver.navigate().refresh();
        logger.debug("Page refreshed");
    }

    protected void navigateBack() {
        driver.navigate().back();
        logger.debug("Navigated back");
    }

    protected void navigateForward() {
        driver.navigate().forward();
        logger.debug("Navigated forward");
    }

    // Window/Tab methods
    protected void switchToNewWindow() {
        String originalWindow = driver.getWindowHandle();
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
        logger.debug("Switched to new window");
    }

    protected void closeCurrentWindowAndSwitchBack() {
        String currentWindow = driver.getWindowHandle();
        driver.close();

        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(currentWindow)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
        logger.debug("Closed current window and switched back");
    }

    // Utility methods
    protected void waitForPageToLoad() {
        wait.until(driver -> jsExecutor.executeScript("return document.readyState").equals("complete"));
        logger.debug("Page loaded completely");
    }

    protected void implicitWait(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Wait interrupted: {}", e.getMessage());
        }
    }

    protected List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    protected WebElement findElement(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // Page-specific helper methods
    protected boolean isPageTitleContains(String expectedTitle) {
        return driver.getTitle().toLowerCase().contains(expectedTitle.toLowerCase());
    }

    protected boolean isCurrentUrlContains(String expectedUrlFragment) {
        return driver.getCurrentUrl().toLowerCase().contains(expectedUrlFragment.toLowerCase());
    }

    protected void takeScreenshot() {
        // This will be handled by the test framework through BaseTest
        logger.debug("Screenshot capture requested");
    }

    // Alert handling
    protected void acceptAlert() {
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
        logger.debug("Alert accepted");
    }

    protected void dismissAlert() {
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().dismiss();
        logger.debug("Alert dismissed");
    }

    protected String getAlertText() {
        wait.until(ExpectedConditions.alertIsPresent());
        return driver.switchTo().alert().getText();
    }
}