package com.cpms.pages;

import com.cpms.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Projects Page - Page Object Model for CPMS Projects Management
 * Handles project creation, import, filtering and management
 */
public class ProjectsPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(ProjectsPage.class);

    // Page header and navigation
    @FindBy(xpath = "//h1 | //*[contains(@class, 'page-title')] | //h2[contains(., 'Project Register')]")
    private WebElement pageTitle;

    @FindBy(xpath = "//button[contains(., 'Filter')]")
    private WebElement filterToggleButton;

    @FindBy(id = "project-filter")
    private WebElement filterInput;

    @FindBy(xpath = "//button[contains(., 'New Project')]")
    private WebElement newProjectButton;

    @FindBy(xpath = "//button[contains(., 'Import project pack')]")
    private WebElement importProjectPackButton;

    // Project list/grid
    @FindBy(css = ".project-list, .projects-grid, .dtwrap, table.dt")
    private WebElement projectContainer;

    @FindBy(css = ".project-row, .project-card, .project-item, tr.clickable-row")
    private List<WebElement> projectItems;

    @FindBy(css = ".project-code, td.mono")
    private List<WebElement> projectCodes;

    @FindBy(css = ".project-name, td.wr b")
    private List<WebElement> projectNames;

    // Project import modal/form
    @FindBy(css = ".import-modal, .project-import, [role='dialog']")
    private WebElement importModal;

    @FindBy(xpath = "//input[@type='checkbox' and (contains(., 'Create') or contains(@placeholder, 'Create'))] | //*[contains(@class, 'create-new-project')] | //label[contains(., 'Create new project')]//input")
    private WebElement createNewProjectCheckbox;

    @FindBy(id = "pack-code")
    private WebElement projectCodeInput;

    @FindBy(id = "pack-name")
    private WebElement projectNameInput;

    @FindBy(css = "input[type='file']")
    private WebElement fileInput;

    @FindBy(xpath = "//button[contains(., 'Scan & classify')]")
    private WebElement chooseFilesButton;

    @FindBy(xpath = "//button[contains(., 'Scan & classify')]")
    private WebElement scanClassifyButton;

    @FindBy(xpath = "//button[contains(., 'Run import')]")
    private WebElement runImportButton;

    // New project form
    @FindBy(id = "create-project-form")
    private WebElement newProjectForm;

    @FindBy(id = "proj-code")
    private WebElement newProjCodeInput;

    @FindBy(id = "proj-name")
    private WebElement newProjNameInput;

    @FindBy(id = "proj-client")
    private WebElement newProjClientInput;

    @FindBy(id = "proj-lead")
    private WebElement newProjLeadInput;

    @FindBy(id = "proj-contract")
    private WebElement newProjContractInput;

    @FindBy(id = "pick-lat")
    private WebElement newProjLatitudeInput;

    @FindBy(id = "pick-lon")
    private WebElement newProjLongitudeInput;

    @FindBy(xpath = "//button[@type='submit' and contains(., 'Create Project')]")
    private WebElement createButton;

    @FindBy(xpath = "//button[@type='submit' and contains(., 'Save changes')]")
    private WebElement saveButton;

    // File preview and classification
    @FindBy(css = ".file-preview, .classification-preview, table.dt")
    private WebElement filePreview;

    @FindBy(css = ".file-item, table.dt tbody tr")
    private List<WebElement> fileItems;

    @FindBy(css = ".route-commercial, .route-programme, .route-quality")
    private List<WebElement> routingLabels;

    // Notifications and messages
    @FindBy(css = ".toast, .notification, .success-message, [class*='toast']")
    private WebElement successMessage;

    @FindBy(css = ".error-message, .validation-error, .toast.warn, .toast.bad, [class*='toast']")
    private WebElement errorMessage;

    // Constructor
    public ProjectsPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        logger.info("ProjectsPage initialized");
    }

    /**
     * Check if projects page is loaded
     */
    public boolean isProjectsPageLoaded() {
        try {
            waitForPageToLoad();
            return isElementVisible(pageTitle) ||
                   isElementVisible(newProjectButton) ||
                   driver.getCurrentUrl().toLowerCase().contains("project") ||
                   driver.findElement(By.tagName("body")).getText().toLowerCase().contains("project register");
        } catch (Exception e) {
            logger.error("Error checking if projects page is loaded: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Click New Project button
     */
    public void clickNewProject() {
        logger.info("Clicking New Project button");
        try {
            clickElement(newProjectButton);
        } catch (Exception e) {
            logger.warn("Standard click failed on newProjectButton, trying JS click: {}", e.getMessage());
            WebElement btn = driver.findElement(By.xpath("//button[contains(., 'New Project')]"));
            jsExecutor.executeScript("arguments[0].click();", btn);
        }
        try {
            waitForElementToBeVisible(newProjectForm);
        } catch (Exception e) {
            logger.warn("Waiting for create-project-form: {}", e.getMessage());
        }
    }

    /**
     * Click Import Project Pack button
     */
    public void clickImportProjectPack() {
        logger.info("Clicking Import Project Pack button");
        try {
            clickElement(importProjectPackButton);
        } catch (Exception e) {
            logger.warn("Standard click failed, trying JS click: {}", e.getMessage());
            WebElement btn = driver.findElement(By.xpath("//button[contains(., 'Import project pack')]"));
            jsExecutor.executeScript("arguments[0].click();", btn);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}
    }

    /**
     * Create new project manually
     */
    public void createProject(String code, String name, String client, String contractValue) {
        logger.info("Creating new project: {} - {}", code, name);

        clickNewProject();

        clearAndType(newProjCodeInput, code);
        clearAndType(newProjNameInput, name);
        clearAndType(newProjClientInput, client);
        if (contractValue != null && !contractValue.isEmpty()) {
            clearAndType(newProjContractInput, contractValue);
        }

        clickCreateButton();
        waitForSuccessMessage();
    }

    /**
     * Create project with geolocation
     */
    public void createProjectWithLocation(String code, String name, String client,
                                        String contractValue, String latitude, String longitude) {
        logger.info("Creating new project with location: {} - {}", code, name);

        clickNewProject();

        clearAndType(newProjCodeInput, code);
        clearAndType(newProjNameInput, name);
        clearAndType(newProjClientInput, client);
        if (contractValue != null && !contractValue.isEmpty()) {
            clearAndType(newProjContractInput, contractValue);
        }

        try {
            // Click Coordinates tab if available
            WebElement coordsBtn = driver.findElement(By.xpath("//button[contains(., 'Coordinates')]"));
            if (isElementVisible(coordsBtn)) {
                clickElement(coordsBtn);
            }
            if (isElementVisible(newProjLatitudeInput)) {
                clearAndType(newProjLatitudeInput, latitude);
            }
            if (isElementVisible(newProjLongitudeInput)) {
                clearAndType(newProjLongitudeInput, longitude);
            }
        } catch (Exception e) {
            logger.warn("Location inputs not found/set: {}", e.getMessage());
        }

        clickCreateButton();
        waitForSuccessMessage();
    }

    /**
     * Import project pack with files
     */
    public void importProjectPack(String projectCode, String projectName, String[] filePaths) {
        logger.info("Importing project pack: {} - {}", projectCode, projectName);

        clickImportProjectPack();

        // Check create new project if needed
        if (isElementVisible(createNewProjectCheckbox)) {
            clickElement(createNewProjectCheckbox);
        }

        // Enter project details
        clearAndType(projectCodeInput, projectCode);
        clearAndType(projectNameInput, projectName);

        // Upload files
        uploadFiles(filePaths);

        // Scan and classify
        clickElement(scanClassifyButton);
        waitForFileClassification();

        // Run import
        clickElement(runImportButton);
        waitForImportToComplete();
    }

    /**
     * Upload files to project import
     */
    private void uploadFiles(String[] filePaths) {
        logger.info("Uploading {} files", filePaths.length);

        if (isElementVisible(chooseFilesButton)) {
            clickElement(chooseFilesButton);
        }

        // Upload each file
        for (String filePath : filePaths) {
            fileInput.sendKeys(filePath);
            logger.debug("Uploaded file: {}", filePath);
        }
    }

    /**
     * Wait for file classification to complete
     */
    private void waitForFileClassification() {
        logger.info("Waiting for file classification");
        try {
            // Wait for classification preview to appear
            waitForElementToBeVisible(filePreview);

            // Wait for routing labels to be assigned
            Thread.sleep(2000); // Give time for classification

        } catch (Exception e) {
            logger.warn("Classification may not have completed: {}", e.getMessage());
        }
    }

    /**
     * Wait for import to complete
     */
    private void waitForImportToComplete() {
        logger.info("Waiting for import to complete");
        try {
            waitForSuccessMessage();

            // Wait for modal to close or redirect
            Thread.sleep(3000);

        } catch (Exception e) {
            logger.warn("Import completion timeout: {}", e.getMessage());
        }
    }

    /**
     * Filter projects by search term
     */
    public void filterProjects(String searchTerm) {
        logger.info("Filtering projects by: {}", searchTerm);
        try {
            // Click filter toggle button if input not visible
            if (!isElementVisible(filterInput)) {
                if (isElementVisible(filterToggleButton)) {
                    clickElement(filterToggleButton);
                } else {
                    WebElement btn = driver.findElement(By.xpath("//button[contains(., 'Filter')]"));
                    jsExecutor.executeScript("arguments[0].click();", btn);
                }
                Thread.sleep(500);
            }

            clearAndType(filterInput, searchTerm);
        } catch (Exception e) {
            logger.warn("Filter input handling error: {}", e.getMessage());
            try {
                WebElement input = driver.findElement(By.id("project-filter"));
                clearAndType(input, searchTerm);
            } catch (Exception ex) {
                logger.warn("Fallback filter attempt failed: {}", ex.getMessage());
            }
        }

        // Wait for filter results
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Get filtered project count
     */
    public int getVisibleProjectCount() {
        return (int) projectItems.stream().filter(this::isElementVisible).count();
    }

    /**
     * Find project by code
     */
    public boolean isProjectVisible(String projectCode) {
        return projectCodes.stream()
            .filter(this::isElementVisible)
            .map(this::getElementText)
            .anyMatch(code -> code.equals(projectCode));
    }

    /**
     * Edit project by code
     */
    public void editProject(String projectCode) {
        logger.info("Editing project: {}", projectCode);

        // Find project row and click edit
        for (int i = 0; i < projectItems.size(); i++) {
            if (isElementVisible(projectCodes.get(i)) &&
                getElementText(projectCodes.get(i)).equals(projectCode)) {

                // Click edit button for this row
                WebElement editButton = projectItems.get(i)
                    .findElement(By.xpath(".//button[contains(., 'Edit') or contains(@class, 'edit')]"));
                clickElement(editButton);
                break;
            }
        }
    }

    /**
     * Wait for success message
     */
    private void waitForSuccessMessage() {
        try {
            waitForElementToBeVisible(successMessage);
            String message = getElementText(successMessage);
            logger.info("Success message: {}", message);
        } catch (Exception e) {
            logger.warn("No success message found: {}", e.getMessage());
        }
    }

    /**
     * Check if import was successful
     */
    public boolean isImportSuccessful() {
        try {
            if (isElementVisible(successMessage)) {
                String message = getElementText(successMessage).toLowerCase();
                return message.contains("import") &&
                       (message.contains("success") || message.contains("completed"));
            }
            return false;
        } catch (Exception e) {
            logger.error("Error checking import success: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get import success message
     */
    public String getImportMessage() {
        try {
            if (isElementVisible(successMessage)) {
                return getElementText(successMessage);
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Check if BOQ rows were imported
     */
    public boolean doesMessageContainBoqCount(int expectedCount) {
        String message = getImportMessage().toLowerCase();
        return message.contains(expectedCount + " boq row");
    }

    /**
     * Clear project filter
     */
    public void clearFilter() {
        logger.info("Clearing project filter");
        try {
            clearElement(filterInput);
            // Ensure the input is cleared and trigger change event
            WebElement input = driver.findElement(By.id("project-filter"));
            input.clear();
            // Trigger input event to trigger React state update
            jsExecutor.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", input);
        } catch (Exception e) {
            logger.warn("Could not clear filter input: {}", e.getMessage());
        }
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {}
    }

    /**
     * Get all visible project codes
     */
    public List<String> getVisibleProjectCodes() {
        return projectCodes.stream()
            .filter(this::isElementVisible)
            .map(this::getElementText)
            .toList();
    }

    /**
     * Get all visible project names
     */
    public List<String> getVisibleProjectNames() {
        return projectNames.stream()
            .filter(this::isElementVisible)
            .map(this::getElementText)
            .toList();
    }

    /**
     * Check if project creation form validation is working
     */
    public boolean hasValidationErrors() {
        try {
            return isElementVisible(errorMessage) ||
                   driver.findElements(By.cssSelector(":invalid, .toast.warn, .toast.bad, [class*='toast']")).stream().anyMatch(this::isElementVisible);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get validation error message
     */
    public String getValidationErrorMessage() {
        try {
            if (isElementVisible(errorMessage)) {
                return getElementText(errorMessage);
            }
            List<WebElement> toasts = driver.findElements(By.cssSelector(".toast, [class*='toast']"));
            for (WebElement toast : toasts) {
                if (isElementVisible(toast)) {
                    return getElementText(toast);
                }
            }
        } catch (Exception ignored) {}
        return "";
    }

    public DashboardPage navigateToProjects() {
        logger.info("Navigating to Projects page");
        try {
            WebElement projectsTab = driver.findElement(By.xpath("//button[contains(., 'Projects')] | //a[contains(@href, 'projects')]"));
            if (isElementVisible(projectsTab)) {
                clickElement(projectsTab);
            } else {
                driver.get("https://leaverageconstructionhub.vercel.app/");
                Thread.sleep(1000);
                WebElement tab = driver.findElement(By.xpath("//button[contains(., 'Projects')]"));
                jsExecutor.executeScript("arguments[0].click();", tab);
            }
        } catch (Exception e) {
            logger.warn("Clicking projects tab failed, falling back to direct URL: {}", e.getMessage());
            driver.get("https://leaverageconstructionhub.vercel.app/");
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}
        return new DashboardPage(driver);
    }

    /**
     * Check if New Project button is visible
     */
    public boolean isNewProjectButtonVisible() {
        try {
            return isElementVisible(newProjectButton);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if Import button is visible
     */
    public boolean isImportButtonVisible() {
        try {
            return isElementVisible(importProjectPackButton);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click create/save button
     */
    public void clickCreateButton() {
        logger.info("Clicking Create/Save button");
        try {
            if (isElementVisible(createButton)) {
                clickElement(createButton);
            } else {
                WebElement btn = driver.findElement(By.xpath("//button[@type='submit' and (contains(., 'Create') or contains(., 'Save'))]"));
                jsExecutor.executeScript("arguments[0].click();", btn);
            }
        } catch (Exception e) {
            logger.warn("Could not click create/save button: {}", e.getMessage());
        }
    }

    /**
     * Check if new project modal is visible
     */
    public boolean isNewProjectModalVisible() {
        try {
            return isElementVisible(newProjectForm) ||
                   driver.findElements(By.id("create-project-form")).stream().anyMatch(this::isElementVisible);
        } catch (Exception e) {
            return false;
        }
    }
}