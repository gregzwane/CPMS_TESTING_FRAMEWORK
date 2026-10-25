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
 * Documents Page - Page Object Model for CPMS Document Management
 * Handles document upload, classification, and access control
 */
public class DocumentsPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(DocumentsPage.class);

    // Page header
    @FindBy(css = "h1:contains('Documents'), .page-title")
    private WebElement pageTitle;

    // Upload functionality
    @FindBy(css = "button:contains('Upload'), .upload-btn")
    private WebElement uploadButton;

    @FindBy(css = "input[type='file'], .file-input")
    private WebElement fileInput;

    @FindBy(css = "select[name*='category'], .category-selector")
    private WebElement categorySelector;

    @FindBy(css = "input[name*='version'], .version-input")
    private WebElement versionInput;

    @FindBy(css = "button:contains('Upload Document'), .upload-doc-btn")
    private WebElement uploadDocumentButton;

    // Document list/grid
    @FindBy(css = ".document-list, .documents-grid")
    private WebElement documentContainer;

    @FindBy(css = ".document-item, .doc-row")
    private List<WebElement> documentItems;

    @FindBy(css = ".document-name, .doc-name")
    private List<WebElement> documentNames;

    @FindBy(css = ".document-category, .doc-category")
    private List<WebElement> documentCategories;

    @FindBy(css = ".document-date, .upload-date")
    private List<WebElement> documentDates;

    @FindBy(css = ".document-uploader, .uploaded-by")
    private List<WebElement> documentUploaders;

    // Document actions
    @FindBy(css = ".download-btn, button:contains('Download')")
    private List<WebElement> downloadButtons;

    @FindBy(css = ".view-btn, button:contains('View'), button:contains('Open')")
    private List<WebElement> viewButtons;

    @FindBy(css = ".delete-btn, button:contains('Delete')")
    private List<WebElement> deleteButtons;

    // Module/Category filters
    @FindBy(css = ".filter-general, .category-general")
    private WebElement generalFilter;

    @FindBy(css = ".filter-commercial, .category-commercial")
    private WebElement commercialFilter;

    @FindBy(css = ".filter-programme, .category-programme")
    private WebElement programmeFilter;

    @FindBy(css = ".filter-quality, .category-quality")
    private WebElement qualityFilter;

    @FindBy(css = ".filter-site, .category-site")
    private WebElement siteFilter;

    @FindBy(css = ".filter-reports, .category-reports")
    private WebElement reportsFilter;

    // Search and filter
    @FindBy(css = ".search-input, .document-search")
    private WebElement searchInput;

    @FindBy(css = ".filter-btn, .apply-filter")
    private WebElement filterButton;

    // Upload modal
    @FindBy(css = ".upload-modal, .document-upload")
    private WebElement uploadModal;

    @FindBy(css = ".file-preview")
    private WebElement filePreview;

    @FindBy(css = ".upload-progress")
    private WebElement uploadProgress;

    // Messages
    @FindBy(css = ".success-message, .toast")
    private WebElement successMessage;

    @FindBy(css = ".error-message, .validation-error")
    private WebElement errorMessage;

    // Document viewer
    @FindBy(css = ".document-viewer, .doc-preview")
    private WebElement documentViewer;

    // Constructor
    public DocumentsPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        logger.info("DocumentsPage initialized");
    }

    /**
     * Check if Documents page is loaded
     */
    public boolean isDocumentsPageLoaded() {
        try {
            return isElementVisible(pageTitle) &&
                   getElementText(pageTitle).toLowerCase().contains("documents");
        } catch (Exception e) {
            logger.error("Error checking if documents page is loaded: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Upload document with category
     */
    public void uploadDocument(String filePath, String category, String version) {
        logger.info("Uploading document: {} to category: {}", filePath, category);

        clickElement(uploadButton);
        waitForElementToBeVisible(uploadModal);

        // Upload file
        fileInput.sendKeys(filePath);

        // Set category if selector is available
        if (isElementVisible(categorySelector)) {
            selectByVisibleText(categorySelector, category);
        }

        // Set version if input is available
        if (isElementVisible(versionInput)) {
            clearAndType(versionInput, version);
        }

        // Submit upload
        clickElement(uploadDocumentButton);
        waitForUploadToComplete();
    }

    /**
     * Upload document without specifying category (auto-classification)
     */
    public void uploadDocument(String filePath) {
        logger.info("Uploading document for auto-classification: {}", filePath);

        clickElement(uploadButton);
        waitForElementToBeVisible(uploadModal);

        fileInput.sendKeys(filePath);
        clickElement(uploadDocumentButton);
        waitForUploadToComplete();
    }

    /**
     * Wait for upload to complete
     */
    private void waitForUploadToComplete() {
        logger.info("Waiting for document upload to complete");
        try {
            // Wait for progress if visible
            if (isElementVisible(uploadProgress)) {
                waitForElementToDisappear(uploadProgress);
            }

            // Wait for success message
            waitForElementToBeVisible(successMessage);
            Thread.sleep(1000);
        } catch (Exception e) {
            logger.warn("Upload completion timeout: {}", e.getMessage());
        }
    }

    /**
     * Get number of documents in list
     */
    public int getDocumentCount() {
        return (int) documentItems.stream().filter(this::isElementVisible).count();
    }

    /**
     * Check if document exists by name
     */
    public boolean isDocumentPresent(String documentName) {
        return documentNames.stream()
            .filter(this::isElementVisible)
            .map(this::getElementText)
            .anyMatch(name -> name.contains(documentName));
    }

    /**
     * Get document category
     */
    public String getDocumentCategory(String documentName) {
        for (int i = 0; i < documentNames.size(); i++) {
            if (isElementVisible(documentNames.get(i)) &&
                getElementText(documentNames.get(i)).contains(documentName)) {
                return getElementText(documentCategories.get(i));
            }
        }
        return "";
    }

    /**
     * Filter documents by category
     */
    public void filterByCategory(String category) {
        logger.info("Filtering documents by category: {}", category);

        switch (category.toLowerCase()) {
            case "general":
                if (isElementVisible(generalFilter)) clickElement(generalFilter);
                break;
            case "commercial":
                if (isElementVisible(commercialFilter)) clickElement(commercialFilter);
                break;
            case "programme":
                if (isElementVisible(programmeFilter)) clickElement(programmeFilter);
                break;
            case "quality":
                if (isElementVisible(qualityFilter)) clickElement(qualityFilter);
                break;
            case "site":
                if (isElementVisible(siteFilter)) clickElement(siteFilter);
                break;
            case "reports":
                if (isElementVisible(reportsFilter)) clickElement(reportsFilter);
                break;
            default:
                logger.warn("Unknown category filter: {}", category);
        }

      //  Thread.sleep(1000); // Wait for filter to apply
    }

    /**
     * Search documents
     */
    public void searchDocuments(String searchTerm) {
        logger.info("Searching documents: {}", searchTerm);
        clearAndType(searchInput, searchTerm);

        if (isElementVisible(filterButton)) {
            clickElement(filterButton);
        }

      //  Thread.sleep(1000);
    }

    /**
     * Download document by name
     */
    public void downloadDocument(String documentName) {
        logger.info("Downloading document: {}", documentName);

        for (int i = 0; i < documentNames.size(); i++) {
            if (isElementVisible(documentNames.get(i)) &&
                getElementText(documentNames.get(i)).contains(documentName)) {
                clickElement(downloadButtons.get(i));
                break;
            }
        }
    }

    /**
     * View/Open document by name
     */
    public void viewDocument(String documentName) {
        logger.info("Viewing document: {}", documentName);

        for (int i = 0; i < documentNames.size(); i++) {
            if (isElementVisible(documentNames.get(i)) &&
                getElementText(documentNames.get(i)).contains(documentName)) {
                clickElement(viewButtons.get(i));
                break;
            }
        }
    }

    /**
     * Delete document by name
     */
    public void deleteDocument(String documentName) {
        logger.info("Deleting document: {}", documentName);

        for (int i = 0; i < documentNames.size(); i++) {
            if (isElementVisible(documentNames.get(i)) &&
                getElementText(documentNames.get(i)).contains(documentName)) {
                clickElement(deleteButtons.get(i));

                // Accept confirmation if present
                try {
                    Thread.sleep(500);
                    acceptAlert();
                } catch (Exception e) {
                    // No confirmation dialog
                }
                break;
            }
        }
    }

    /**
     * Check if upload was successful
     */
    public boolean isUploadSuccessful() {
        try {
            if (isElementVisible(successMessage)) {
                String message = getElementText(successMessage).toLowerCase();
                return message.contains("upload") &&
                       (message.contains("success") || message.contains("completed"));
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get upload success message
     */
    public String getUploadMessage() {
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
     * Check if validation errors are present
     */
    public boolean hasValidationErrors() {
        return isElementVisible(errorMessage);
    }

    /**
     * Get validation error message
     */
    public String getValidationError() {
        if (hasValidationErrors()) {
            return getElementText(errorMessage);
        }
        return "";
    }

    /**
     * Upload unsupported file type
     */
    public void uploadUnsupportedFile(String filePath) {
        logger.info("Attempting to upload unsupported file: {}", filePath);
        uploadDocument(filePath);
    }

    /**
     * Upload oversized file
     */
    public void uploadOversizedFile(String filePath) {
        logger.info("Attempting to upload oversized file: {}", filePath);
        uploadDocument(filePath);
    }

    /**
     * Get all visible document names
     */
    public List<String> getVisibleDocumentNames() {
        return documentNames.stream()
            .filter(this::isElementVisible)
            .map(this::getElementText)
            .toList();
    }

    /**
     * Get all visible document categories
     */
    public List<String> getVisibleDocumentCategories() {
        return documentCategories.stream()
            .filter(this::isElementVisible)
            .map(this::getElementText)
            .toList();
    }

    /**
     * Check if document viewer is opened
     */
    public boolean isDocumentViewerOpen() {
        return isElementVisible(documentViewer);
    }

    /**
     * Close document viewer
     */
    public void closeDocumentViewer() {
        if (isDocumentViewerOpen()) {
            // Try to find and click close button
            try {
                WebElement closeButton = documentViewer.findElement(
                    By.cssSelector(".close-btn, button:contains('Close'), .modal-close"));
                clickElement(closeButton);
            } catch (Exception e) {
                // Try ESC key or click outside
                logger.info("Attempting to close viewer with ESC key");
                documentViewer.sendKeys(org.openqa.selenium.Keys.ESCAPE);
            }
        }
    }

    /**
     * Check if document routing worked correctly for import
     */
    public boolean isDocumentRoutedCorrectly(String fileName, String expectedCategory) {
        String actualCategory = getDocumentCategory(fileName);
        return actualCategory.toLowerCase().contains(expectedCategory.toLowerCase());
    }

    /**
     * Verify client access restrictions
     */
    public boolean hasDeleteAccess() {
        return deleteButtons.stream().anyMatch(this::isElementVisible);
    }

    /**
     * Verify client access restrictions
     */
    public boolean hasUploadAccess() {
        return isElementVisible(uploadButton);
    }
}