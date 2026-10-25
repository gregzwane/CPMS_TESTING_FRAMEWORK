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
    @FindBy(xpath = "//h1 | //*[contains(@class, 'page-title')]")
    private WebElement pageTitle;

    @FindBy(xpath = "//input[contains(@placeholder, 'Filter') or contains(@placeholder, 'Search') or contains(@class, 'filter') or contains(@class, 'search')] | //input[@type='text'] | //input[not(@type='hidden')]")
    private WebElement filterInput;

    @FindBy(xpath = "//button[contains(., 'New Project') or contains(., '+ New')] | //*[contains(@class, 'new-project')]")
    private WebElement newProjectButton;

    @FindBy(xpath = "//button[contains(., 'Import') and contains(., 'Project')] | //*[contains(@class, 'import') and contains(@class, 'project')]")
    private WebElement importProjectPackButton;

    // Project list/grid
    @FindBy(css = ".project-list, .projects-grid")
    private WebElement projectContainer;

    @FindBy(css = ".project-row, .project-card, .project-item")
    private List<WebElement> projectItems;

    @FindBy(css = ".project-code")
    private List<WebElement> projectCodes;

    @FindBy(css = ".project-name")
    private List<WebElement> projectNames;

    // Project import modal/form
    @FindBy(css = ".import-modal, .project-import")
    private WebElement importModal;

    @FindBy(xpath = "//input[@type='checkbox' and (contains(., 'Create') or contains(@placeholder, 'Create'))] | //*[contains(@class, 'create-new-project')]")
    private WebElement createNewProjectCheckbox;

    @FindBy(css = "input[name*='code'], .project-code-input")
    private WebElement projectCodeInput;

    @FindBy(css = "input[name*='name'], .project-name-input")
    private WebElement projectNameInput;

    @FindBy(css = "input[type='file'], .file-input")
    private WebElement fileInput;

    @FindBy(xpath = "//button[contains(., 'Choose') and contains(., 'file')] | //*[contains(@class, 'choose-file')]")
    private WebElement chooseFilesButton;

    @FindBy(xpath = "//button[contains(., 'Scan') and contains(., 'Classif')] | //*[contains(@class, 'scan-classif')]")
    private WebElement scanClassifyButton;

    @FindBy(xpath = "//button[contains(., 'Run') and contains(., 'Import')] | //*[contains(@class, 'run-import')]")
    private WebElement runImportButton;

    // New project form
    @FindBy(css = ".new-project-modal, .project-form")
    private WebElement newProjectModal;

    @FindBy(xpath = "//input[contains(@name, 'client') or contains(@placeholder, 'Client')] | //*[contains(@class, 'client-input')]")
    private WebElement clientInput;

    @FindBy(xpath = "//input[contains(@name, 'value') or contains(@name, 'contract') or contains(@placeholder, 'Contract')] | //*[contains(@class, 'contract-value')]")
    private WebElement contractValueInput;

    @FindBy(xpath = "//input[contains(@name, 'lat') or contains(@placeholder, 'Latitude')] | //*[contains(@class, 'latitude-input')]")
    private WebElement latitudeInput;

    @FindBy(xpath = "//input[contains(@name, 'lon') or contains(@placeholder, 'Longitude')] | //*[contains(@class, 'longitude-input')]")
    private WebElement longitudeInput;

    @FindBy(xpath = "//button[contains(., 'Create')] | //*[contains(@class, 'create-btn')]")
    private WebElement createButton;

    @FindBy(xpath = "//button[contains(., 'Save')] | //*[contains(@class, 'save-btn')]")
    private WebElement saveButton;

    // File preview and classification
    @FindBy(css = ".file-preview, .classification-preview")
    private WebElement filePreview;

    @FindBy(css = ".file-item")
    private List<WebElement> fileItems;

    @FindBy(css = ".route-commercial, .route-programme, .route-quality")
    private List<WebElement> routingLabels;

    // Notifications and messages
    @FindBy(xpath = "//*[contains(@class, 'toast') or contains(@class, 'notification') or contains(@class, 'success')]")
    private WebElement successMessage;

    @FindBy(xpath = "//*[contains(@class, 'error') or contains(@class, 'validation-error')]")
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
            return isElementVisible(pageTitle) &&
                   getElementText(pageTitle).toLowerCase().contains("project");
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
        clickElement(newProjectButton);
        waitForElementToBeVisible(newProjectModal);
    }

    /**
     * Click Import Project Pack button
     */
    public void clickImportProjectPack() {
        logger.info("Clicking Import Project Pack button");
        clickElement(importProjectPackButton);
        waitForElementToBeVisible(importModal);
    }

    /**
     * Create new project manually
     */
    public void createProject(String code, String name, String client, String contractValue) {
        logger.info("Creating new project: {} - {}", code, name);

        clickNewProject();

        clearAndType(projectCodeInput, code);
        clearAndType(projectNameInput, name);
        clearAndType(clientInput, client);
        clearAndType(contractValueInput, contractValue);

        clickElement(createButton);
        waitForSuccessMessage();
    }

    /**
     * Create project with geolocation
     */
    public void createProjectWithLocation(String code, String name, String client,
                                        String contractValue, String latitude, String longitude) {
        logger.info("Creating new project with location: {} - {}", code, name);

        clickNewProject();

        clearAndType(projectCodeInput, code);
        clearAndType(projectNameInput, name);
        clearAndType(clientInput, client);
        clearAndType(contractValueInput, contractValue);

        // Navigate to map tab or location section
        clearAndType(latitudeInput, latitude);
        clearAndType(longitudeInput, longitude);

        clickElement(createButton);
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
            clearAndType(filterInput, searchTerm);
        } catch (Exception e) {
            logger.warn("Filter input not found, skipping filter: {}", e.getMessage());
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
        clearElement(filterInput);
       // Thread.sleep(1000);
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
        return isElementVisible(errorMessage);
    }

    /**
     * Get validation error message
     */
    public String getValidationErrorMessage() {
        if (hasValidationErrors()) {
            return getElementText(errorMessage);
        }
        return "";
    }

    public DashboardPage navigateToProjects() {
        logger.info("Navigating to Projects page");
        // Try clicking a projects tab or navigating to projects URL
        try {
            driver.get("https://leaverageconstructionhub.vercel.app/projects");
        } catch (Exception e) {
            logger.warn("Could not navigate to projects page directly");
        }
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
            } else if (isElementVisible(saveButton)) {
                clickElement(saveButton);
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
            return isElementVisible(newProjectModal);
        } catch (Exception e) {
            return false;
        }
    }
}