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
 * BOQ Budget Page - Page Object Model for CPMS BOQ & Budget Management
 * Handles BOQ import, budget management and payment certificates
 */
public class BoqBudgetPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(BoqBudgetPage.class);

    // Page header
    @FindBy(css = "h1:contains('BOQ'), h1:contains('Budget'), .page-title")
    private WebElement pageTitle;

    // Import functionality
    @FindBy(css = "button:contains('Import spreadsheet'), .import-boq-btn")
    private WebElement importSpreadsheetButton;

    @FindBy(css = "input[type='file'], .csv-file-input")
    private WebElement csvFileInput;

    @FindBy(css = "button:contains('Preview'), .preview-btn")
    private WebElement previewButton;

    @FindBy(css = "button:contains('Import'), .import-btn")
    private WebElement importButton;

    // BOQ table
    @FindBy(css = ".boq-table, .budget-table, table")
    private WebElement boqTable;

    @FindBy(css = ".boq-row, .budget-row, tbody tr")
    private List<WebElement> boqRows;

    @FindBy(css = ".item-code")
    private List<WebElement> itemCodes;

    @FindBy(css = ".description")
    private List<WebElement> descriptions;

    @FindBy(css = ".quantity")
    private List<WebElement> quantities;

    @FindBy(css = ".unit-rate")
    private List<WebElement> unitRates;

    @FindBy(css = ".total, .amount")
    private List<WebElement> totals;

    // Budget summary
    @FindBy(css = ".budget-total, .contract-value")
    private WebElement budgetTotal;

    @FindBy(css = ".certified-total")
    private WebElement certifiedTotal;

    @FindBy(css = ".variations-total")
    private WebElement variationsTotal;

    // Payment Certificate
    @FindBy(css = "button:contains('Generate Certificate'), .generate-cert-btn")
    private WebElement generateCertificateButton;

    @FindBy(css = ".certificate-modal, .ipc-modal")
    private WebElement certificateModal;

    @FindBy(css = "select[name*='period'], .period-selector")
    private WebElement periodSelector;

    @FindBy(css = ".boq-line-checkbox")
    private List<WebElement> boqLineCheckboxes;

    @FindBy(css = "button:contains('Create Certificate'), .create-cert-btn")
    private WebElement createCertificateButton;

    // Import preview
    @FindBy(css = ".import-preview, .csv-preview")
    private WebElement importPreview;

    @FindBy(css = ".preview-row")
    private List<WebElement> previewRows;

    @FindBy(css = ".import-summary")
    private WebElement importSummary;

    // Edit functionality
    @FindBy(css = ".edit-boq-btn, button:contains('Edit')")
    private List<WebElement> editButtons;

    @FindBy(css = ".quantity-input")
    private WebElement quantityInput;

    @FindBy(css = ".unit-rate-input")
    private WebElement unitRateInput;

    @FindBy(css = "button:contains('Save'), .save-btn")
    private WebElement saveButton;

    // Messages
    @FindBy(css = ".success-message, .toast")
    private WebElement successMessage;

    @FindBy(css = ".error-message, .validation-error")
    private WebElement errorMessage;

    // Constructor
    public BoqBudgetPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        logger.info("BoqBudgetPage initialized");
    }

    /**
     * Check if BOQ & Budget page is loaded
     */
    public boolean isBudgetPageLoaded() {
        try {
            return isElementVisible(pageTitle) &&
                   (getElementText(pageTitle).toLowerCase().contains("boq") ||
                    getElementText(pageTitle).toLowerCase().contains("budget"));
        } catch (Exception e) {
            logger.error("Error checking if budget page is loaded: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Import BOQ spreadsheet
     */
    public void importBoqSpreadsheet(String csvFilePath) {
        logger.info("Importing BOQ spreadsheet: {}", csvFilePath);

        clickElement(importSpreadsheetButton);

        // Upload CSV file
        csvFileInput.sendKeys(csvFilePath);

        // Preview import
        clickElement(previewButton);
        waitForImportPreview();

        // Confirm import
        clickElement(importButton);
        waitForImportToComplete();
    }

    /**
     * Wait for import preview to load
     */
    private void waitForImportPreview() {
        logger.info("Waiting for import preview");
        try {
            waitForElementToBeVisible(importPreview);
            Thread.sleep(1000); // Allow preview to populate
        } catch (Exception e) {
            logger.warn("Preview may not have loaded: {}", e.getMessage());
        }
    }

    /**
     * Wait for import to complete
     */
    private void waitForImportToComplete() {
        logger.info("Waiting for BOQ import to complete");
        try {
            waitForElementToBeVisible(successMessage);
            Thread.sleep(2000); // Allow page to refresh
        } catch (Exception e) {
            logger.warn("Import completion timeout: {}", e.getMessage());
        }
    }

    /**
     * Get number of BOQ rows
     */
    public int getBoqRowCount() {
        try {
            return (int) boqRows.stream().filter(this::isElementVisible).count();
        } catch (Exception e) {
            logger.error("Error counting BOQ rows: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Check if specific BOQ item exists
     */
    public boolean isBoqItemPresent(String itemCode) {
        return itemCodes.stream()
            .filter(this::isElementVisible)
            .map(this::getElementText)
            .anyMatch(code -> code.equals(itemCode));
    }

    /**
     * Get BOQ item details
     */
    public String getBoqItemDescription(String itemCode) {
        for (int i = 0; i < itemCodes.size(); i++) {
            if (isElementVisible(itemCodes.get(i)) &&
                getElementText(itemCodes.get(i)).equals(itemCode)) {
                return getElementText(descriptions.get(i));
            }
        }
        return "";
    }

    /**
     * Get BOQ item quantity
     */
    public String getBoqItemQuantity(String itemCode) {
        for (int i = 0; i < itemCodes.size(); i++) {
            if (isElementVisible(itemCodes.get(i)) &&
                getElementText(itemCodes.get(i)).equals(itemCode)) {
                return getElementText(quantities.get(i));
            }
        }
        return "";
    }

    /**
     * Get BOQ item unit rate
     */
    public String getBoqItemUnitRate(String itemCode) {
        for (int i = 0; i < itemCodes.size(); i++) {
            if (isElementVisible(itemCodes.get(i)) &&
                getElementText(itemCodes.get(i)).equals(itemCode)) {
                return getElementText(unitRates.get(i));
            }
        }
        return "";
    }

    /**
     * Edit BOQ item quantity
     */
    public void editBoqItemQuantity(String itemCode, String newQuantity) {
        logger.info("Editing quantity for item {}: {}", itemCode, newQuantity);

        // Find and click edit button for the item
        for (int i = 0; i < itemCodes.size(); i++) {
            if (isElementVisible(itemCodes.get(i)) &&
                getElementText(itemCodes.get(i)).equals(itemCode)) {

                clickElement(editButtons.get(i));
                clearAndType(quantityInput, newQuantity);
                clickElement(saveButton);
                break;
            }
        }
    }

    /**
     * Edit BOQ item unit rate
     */
    public void editBoqItemUnitRate(String itemCode, String newUnitRate) {
        logger.info("Editing unit rate for item {}: {}", itemCode, newUnitRate);

        for (int i = 0; i < itemCodes.size(); i++) {
            if (isElementVisible(itemCodes.get(i)) &&
                getElementText(itemCodes.get(i)).equals(itemCode)) {

                clickElement(editButtons.get(i));
                clearAndType(unitRateInput, newUnitRate);
                clickElement(saveButton);
                break;
            }
        }
    }

    /**
     * Get budget total value
     */
    public String getBudgetTotal() {
        try {
            if (isElementVisible(budgetTotal)) {
                return getElementText(budgetTotal);
            }
            return "0";
        } catch (Exception e) {
            logger.error("Error getting budget total: {}", e.getMessage());
            return "0";
        }
    }

    /**
     * Generate payment certificate
     */
    public void generatePaymentCertificate(String period, String[] selectedItems) {
        logger.info("Generating payment certificate for period: {}", period);

        clickElement(generateCertificateButton);
        waitForElementToBeVisible(certificateModal);

        // Select period
        selectByVisibleText(periodSelector, period);

        // Select BOQ line items
        selectBoqLinesForCertificate(selectedItems);

        // Create certificate
        clickElement(createCertificateButton);
        waitForCertificateGeneration();
    }

    /**
     * Select BOQ lines for certificate
     */
    private void selectBoqLinesForCertificate(String[] itemCodes) {
        logger.info("Selecting {} BOQ lines for certificate", itemCodes.length);

        for (String itemCode : itemCodes) {
            // Find checkbox for this item and click it
            for (int i = 0; i < this.itemCodes.size(); i++) {
                if (isElementVisible(this.itemCodes.get(i)) &&
                    getElementText(this.itemCodes.get(i)).equals(itemCode)) {
                    clickElement(boqLineCheckboxes.get(i));
                    break;
                }
            }
        }
    }

    /**
     * Wait for certificate generation
     */
    private void waitForCertificateGeneration() {
        logger.info("Waiting for certificate generation");
        try {
            waitForElementToBeVisible(successMessage);
            Thread.sleep(2000);
        } catch (Exception e) {
            logger.warn("Certificate generation timeout: {}", e.getMessage());
        }
    }

    /**
     * Check if import was successful
     */
    public boolean isImportSuccessful() {
        try {
            if (isElementVisible(successMessage)) {
                String message = getElementText(successMessage).toLowerCase();
                return message.contains("import") && message.contains("success");
            }
            return false;
        } catch (Exception e) {
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
     * Validate numeric input in quantity field
     */
    public void enterInvalidQuantity(String invalidValue) {
        logger.info("Entering invalid quantity: {}", invalidValue);
        clearAndType(quantityInput, invalidValue);
    }

    /**
     * Validate numeric input in unit rate field
     */
    public void enterInvalidUnitRate(String invalidValue) {
        logger.info("Entering invalid unit rate: {}", invalidValue);
        clearAndType(unitRateInput, invalidValue);
    }

    /**
     * Get all BOQ item codes
     */
    public List<String> getAllBoqItemCodes() {
        return itemCodes.stream()
            .filter(this::isElementVisible)
            .map(this::getElementText)
            .toList();
    }

    /**
     * Check if certificate generation button is available
     */
    public boolean isCertificateGenerationAvailable() {
        return isElementVisible(generateCertificateButton);
    }

    /**
     * Check if BOQ table is responsive on mobile
     */
    public boolean isBoqTableVisible() {
        return isElementVisible(boqTable);
    }
}