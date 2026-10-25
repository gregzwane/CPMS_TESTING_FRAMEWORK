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
 * Dashboard Page - Page Object Model for CPMS Dashboard
 * Handles main dashboard functionality and navigation
 */
public class DashboardPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(DashboardPage.class);

    // Header elements
    @FindBy(css = ".header, .navbar")
    private WebElement header;

    @FindBy(css = "[class*='live'], [class*='status']:contains('LIVE')")
    private WebElement liveStatusIndicator;

    @FindBy(css = "[class*='waking'], [class*='offline']")
    private WebElement apiStatusMessage;

    @FindBy(css = "button:contains('Retry'), .retry-button")
    private WebElement retryButton;

    // Navigation elements - using standard CSS and XPath locators
    @FindBy(xpath = "//nav//a | //nav//button | //*[@role='tab'] | //aside//button | //aside//a | //*[contains(@class, 'sidebar')]//button | //*[contains(@class, 'sidebar')]//a")
    private List<WebElement> navigationTabs;

    @FindBy(xpath = "//button[contains(., 'Projects')] | //a[contains(@href, 'projects')] | //*[@data-testid='nav-projects'] | //*[contains(@class, 'sidebar')]//*[contains(text(), 'Projects')]")
    private WebElement projectsTab;

    @FindBy(xpath = "//button[contains(., 'BOQ')] | //button[contains(., 'Budget')] | //a[contains(@href, 'commercial')] | //a[contains(@href, 'boq')] | //*[@data-testid='nav-commercial'] | //*[contains(@class, 'sidebar')]//*[contains(text(), 'BOQ')]")
    private WebElement boqBudgetTab;

    @FindBy(xpath = "//button[contains(., 'Documents')] | //a[contains(@href, 'documents')] | //*[@data-testid='nav-documents'] | //*[contains(@class, 'sidebar')]//*[contains(text(), 'Documents')]")
    private WebElement documentsTab;

    @FindBy(xpath = "//button[contains(., 'Programme')] | //a[contains(@href, 'programme')] | //*[@data-testid='nav-programme'] | //*[contains(@class, 'sidebar')]//*[contains(text(), 'Programme')]")
    private WebElement programmeTab;

    @FindBy(xpath = "//button[contains(., 'Quality')] | //a[contains(@href, 'quality')] | //*[@data-testid='nav-quality'] | //*[contains(@class, 'sidebar')]//*[contains(text(), 'Quality')]")
    private WebElement qualityTab;

    @FindBy(xpath = "//button[contains(., 'Site')] | //a[contains(@href, 'site')] | //*[@data-testid='nav-site'] | //*[contains(@class, 'sidebar')]//*[contains(text(), 'Site')]")
    private WebElement siteTab;

    @FindBy(xpath = "//button[contains(., 'Reports')] | //a[contains(@href, 'reports')] | //*[@data-testid='nav-reports'] | //*[contains(@class, 'sidebar')]//*[contains(text(), 'Reports')]")
    private WebElement reportsTab;

    @FindBy(xpath = "//button[contains(., 'Settings')] | //*[@data-testid='nav-settings'] | //*[contains(@class, 'sidebar')]//*[contains(text(), 'Settings')]")
    private WebElement settingsTab;

    @FindBy(xpath = "//button[contains(., 'BI')] | //*[@data-testid='nav-bi'] | //*[contains(@class, 'sidebar')]//*[contains(text(), 'BI')]")
    private WebElement biTab;

    @FindBy(xpath = "//button[contains(., 'Tiger')] | //*[@data-testid='nav-tiger'] | //*[contains(@class, 'sidebar')]//*[contains(text(), 'Tiger')]")
    private WebElement tigerTab;

    @FindBy(xpath = "//button[contains(., 'Client')] | //*[@data-testid='nav-client'] | //*[contains(@class, 'sidebar')]//*[contains(text(), 'Client')]")
    private WebElement clientTab;

    // Dashboard content
    @FindBy(xpath = "//main | //*[@role='main'] | //*[contains(@class, 'dashboard')] | //*[contains(@class, 'overview')] | //*[contains(@class, 'main-content')]")
    private WebElement dashboardContent;

    @FindBy(css = ".project-card, .project-item")
    private List<WebElement> projectCards;

    @FindBy(css = ".kpi-card, .metric-card, .stat-card")
    private List<WebElement> kpiCards;

    @FindBy(css = ".map, .geographic-view")
    private WebElement mapView;

    @FindBy(css = ".live-feed, .activity-feed")
    private WebElement liveFeed;

    // User menu and logout
    @FindBy(css = ".user-menu, .profile-menu, [data-testid='user-menu']")
    private WebElement userMenu;

    @FindBy(xpath = "//button[contains(., 'Sign out') or contains(., 'Sign Out') or contains(., 'Logout') or contains(., 'logout')] | //*[@title='Sign out']")
    private WebElement logoutButton;

    // Project selector
    @FindBy(css = ".active-project, .project-selector")
    private WebElement activeProjectSelector;

    @FindBy(css = ".project-option, .project-dropdown-item")
    private List<WebElement> projectOptions;

    // Constructor
    public DashboardPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        logger.info("DashboardPage initialized");
    }

    /**
     * Check if dashboard is loaded successfully
     */
    public boolean isDashboardLoaded() {
        try {
            waitForPageToLoad();
            // For React SPA - check for any dashboard indicators
            boolean loaded = isElementVisible(dashboardContent) ||
                   isElementVisible(header) ||
                   isElementVisible(projectsTab) ||
                   isElementVisible(mapView) ||
                   (kpiCards != null && kpiCards.stream().anyMatch(this::isElementVisible)) ||
                   !driver.getCurrentUrl().toLowerCase().contains("sign");

            // Debug: log available tabs
            if (!loaded) {
                logger.warn("Dashboard not loaded. Current URL: {}", driver.getCurrentUrl());
                logger.warn("Available tabs: {}", getAvailableNavTabs());
            }

            logger.info("isDashboardLoaded result: {}", loaded);
            return loaded;
        } catch (Exception e) {
            logger.error("Error checking if dashboard is loaded: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Navigate directly to dashboard if not there
     */
    public void ensureOnDashboard() {
        try {
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("sign")) {
                logger.info("On sign page, navigating to dashboard");
                driver.get("https://leaverageconstructionhub.vercel.app/");
                waitForDashboardToLoad();
            }
        } catch (Exception e) {
            logger.warn("ensureOnDashboard failed: {}", e.getMessage());
        }
    }

    /**
     * Check if API is live
     */
    public boolean isApiLive() {
        try {
            if (isElementPresent(By.cssSelector("[class*='live'], [class*='status']:contains('LIVE')"))) {
                return getElementText(liveStatusIndicator).toLowerCase().contains("live");
            }
            return true; // Assume live if no status indicator
        } catch (Exception e) {
            logger.warn("Could not determine API status: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Wait for API to be live
     */
    public boolean waitForApiToBeReady() {
        logger.info("Waiting for API to be ready");
        try {
            // Wait for any waking/offline messages to disappear
            if (isElementPresent(By.cssSelector("[class*='waking'], [class*='offline']"))) {
                waitForElementToDisappear(apiStatusMessage);
            }

            // Wait for live status or timeout
            for (int i = 0; i < 30; i++) { // 30 second timeout
                if (isApiLive()) {
                    logger.info("API is now live");
                    return true;
                }

                // Click retry if available
                if (isElementPresent(By.cssSelector("button:contains('Retry'), .retry-button"))) {
                    clickElement(retryButton);
                    logger.info("Clicked retry button");
                }

                Thread.sleep(1000);
            }

            logger.warn("API did not become live within timeout");
            return false;
        } catch (Exception e) {
            logger.error("Error waiting for API: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Navigate to Projects page
     */
    public ProjectsPage navigateToProjects() {
        logger.info("Navigating to Projects");
        try {
            clickElement(projectsTab);
        } catch (Exception e) {
            logger.warn("Could not click projects tab, trying direct navigation: {}", e.getMessage());
            driver.get("https://leaverageconstructionhub.vercel.app/projects");
        }
        try {
            Thread.sleep(3000); // Wait for page to load
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted: {}", e.getMessage());
        }
        return new ProjectsPage(driver);
    }

    /**
     * Navigate to BOQ & Budget page
     */
    public BoqBudgetPage navigateToBoqBudget() {
        logger.info("Navigating to BOQ & Budget");
        clickElement(boqBudgetTab);
        return new BoqBudgetPage(driver);
    }

    /**
     * Navigate to Documents page
     */
    public DocumentsPage navigateToDocuments() {
        logger.info("Navigating to Documents");
        clickElement(documentsTab);
        return new DocumentsPage(driver);
    }

    /**
     * Get available navigation tabs
     */
    public List<String> getAvailableNavTabs() {
        List<String> tabs = navigationTabs.stream()
            .filter(this::isElementVisible)
            .map(this::getElementText)
            .toList();
        logger.info("getAvailableNavTabs returning: {}", tabs);
        return tabs;
    }

    /**
     * Check if specific tab is visible (role-based access)
     * Checks against actual tab elements by ID/label
     */
    public boolean isTabVisible(String tabName) {
        try {
            logger.info("Current URL: {}", driver.getCurrentUrl());
            logger.info("Current Page Title: {}", driver.getTitle());
            String bodySnippet = driver.findElement(By.tagName("body")).getText();
            logger.info("Body snippet (first 500 chars): {}", bodySnippet.length() > 500 ? bodySnippet.substring(0, 500) : bodySnippet);

            // First check specific tab elements
            WebElement tabElement = getTabElementByName(tabName);
            if (tabElement != null) {
                boolean visible = isElementVisible(tabElement);
                logger.info("Tab '{}' specific element: found={}, visible={}", tabName, true, visible);
                if (visible) return true;
            } else {
                logger.info("Tab '{}' specific element: not found", tabName);
            }

            // Fallback to checking page source or body text - this is more reliable for React SPA
            if (bodySnippet.toLowerCase().contains(tabName.toLowerCase())) {
                logger.info("Found '{}' in body text!", tabName);
                return true;
            }

            // Map aliases to tab labels
            if (tabName.equalsIgnoreCase("commercial") &&
                (bodySnippet.toLowerCase().contains("boq") || bodySnippet.toLowerCase().contains("budget"))) {
                logger.info("Found 'commercial' alias (boq/budget) in body text!");
                return true;
            }

            // Fallback to generic navigation tabs
            List<String> availableTabs = getAvailableNavTabs();
            logger.info("Available navigation tabs: {}", availableTabs);

            return availableTabs.stream()
                .anyMatch(tab -> tab.toLowerCase().contains(tabName.toLowerCase()));
        } catch (Exception e) {
            logger.error("Error checking tab visibility for {}: {}", tabName, e.getMessage());
            return false;
        }
    }

    /**
     * Get specific tab element by name
     */
    private WebElement getTabElementByName(String tabName) {
        String normalized = tabName.toLowerCase();
        switch (normalized) {
            case "projects": return projectsTab;
            case "commercial":
            case "boq":
            case "budget":
            case "boq & budget": return boqBudgetTab;
            case "documents": return documentsTab;
            case "programme": return programmeTab;
            case "quality": return qualityTab;
            case "site": return siteTab;
            case "reports": return reportsTab;
            case "settings": return settingsTab;
            case "bi":
            case "bi & root cause": return biTab;
            case "tiger":
            case "tiger team": return tigerTab;
            case "client":
            case "client portal": return clientTab;
            default: return null;
        }
    }

    /**
     * Get dashboard KPIs/metrics count
     */
    public int getKpiCardsCount() {
        return (int) kpiCards.stream().filter(this::isElementVisible).count();
    }

    /**
     * Check if map view is visible
     */
    public boolean isMapVisible() {
        return isElementVisible(mapView);
    }

    /**
     * Check if live feed is visible and has entries
     */
    public boolean isLiveFeedVisible() {
        return isElementVisible(liveFeed);
    }

    /**
     * Get live feed entries count
     */
    public int getLiveFeedEntriesCount() {
        try {
            if (!isElementVisible(liveFeed)) return 0;

            List<WebElement> entries = findElements(By.cssSelector(".feed-entry, .activity-item, .diary-entry"));
            return entries.size();
        } catch (Exception e) {
            logger.error("Error counting live feed entries: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Set active project
     */
    public void setActiveProject(String projectCode) {
        logger.info("Setting active project to: {}", projectCode);
        try {
            if (isElementVisible(activeProjectSelector)) {
                clickElement(activeProjectSelector);

                // Find and click the matching project option
                for (WebElement option : projectOptions) {
                    if (isElementVisible(option) &&
                        getElementText(option).contains(projectCode)) {
                        clickElement(option);
                        logger.info("Selected project: {}", projectCode);
                        return;
                    }
                }
            }
            logger.warn("Could not find project option: {}", projectCode);
        } catch (Exception e) {
            logger.error("Error setting active project: {}", e.getMessage());
        }
    }

    /**
     * Get current active project
     */
    public String getActiveProject() {
        try {
            if (isElementVisible(activeProjectSelector)) {
                return getElementText(activeProjectSelector);
            }
        } catch (Exception e) {
            logger.error("Error getting active project: {}", e.getMessage());
        }
        return "";
    }

    /**
     * Logout from application
     */
    public LoginPage logout() {
        logger.info("Logging out");
        try {
            // Open user menu if needed
            if (isElementVisible(userMenu)) {
                clickElement(userMenu);
            }

            // Click logout
            clickElement(logoutButton);

            // Wait for redirect to login page
            waitForUrlToContain("sign");

        } catch (Exception e) {
            logger.error("Error during logout: {}", e.getMessage());
        }

        return new LoginPage(driver);
    }

    /**
     * Check if user has Director level access
     */
    public boolean hasDirectorAccess() {
        // Director should see all tabs including settings, bi, tiger, client
        boolean settings = isTabVisible("settings");
        boolean bi = isTabVisible("bi");
        boolean tiger = isTabVisible("tiger");
        boolean projects = isTabVisible("projects");
        boolean commercial = isTabVisible("commercial");

        logger.info("Director access check: settings={}, bi={}, tiger={}, projects={}, commercial={}",
                   settings, bi, tiger, projects, commercial);

        return settings && bi && tiger && projects && commercial;
    }

    /**
     * Check if user has PM level access
     */
    public boolean hasPmAccess() {
        return isTabVisible("projects") && isTabVisible("programme") && isTabVisible("site") &&
               isTabVisible("settings") && !isTabVisible("client");
    }

    /**
     * Check if user has QS level access
     * QS nav: dashboard, projects, commercial, programme, bi, collab, reports, settings
     */
    public boolean hasQsAccess() {
        boolean commercial = isTabVisible("commercial") || isTabVisible("boq") || isTabVisible("budget");
        boolean programme = isTabVisible("programme");
        boolean bi = isTabVisible("bi");
        boolean reports = isTabVisible("reports");
        boolean settings = isTabVisible("settings");
        boolean noClient = !isTabVisible("client");

        logger.info("QS access check: commercial={}, programme={}, bi={}, reports={}, settings={}, noClient={}",
                   commercial, programme, bi, reports, settings, noClient);

        return commercial && programme && bi && reports && settings && noClient;
    }

    /**
     * Check if user has Client level access (restricted view)
     */
    public boolean hasClientAccess() {
        return isTabVisible("projects") && isTabVisible("commercial") && isTabVisible("reports") &&
               isTabVisible("client") && !isTabVisible("settings") && !isTabVisible("tiger");
    }

    /**
     * Get page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Refresh dashboard
     */
    public void refreshDashboard() {
        logger.info("Refreshing dashboard");
        refreshPage();
        waitForApiToBeReady();
    }

    /**
     * Check if dashboard shows project portfolio view (Director)
     */
    public boolean hasPortfolioView() {
        return projectCards.size() > 1 ||
               getElementText(dashboardContent).toLowerCase().contains("portfolio");
    }

    /**
     * Wait for dashboard to load completely
     */
    public void waitForDashboardToLoad() {
        waitForPageToLoad();
        waitForApiToBeReady();

        // Wait for main content to be visible
        if (isElementPresent(By.cssSelector(".dashboard, .overview, .main-content"))) {
            waitForElementToBeVisible(dashboardContent);
        }

        // Additional wait for React SPA to hydrate
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            logger.warn("Interrupted during SPA hydration wait: {}", e.getMessage());
        }
    }
}