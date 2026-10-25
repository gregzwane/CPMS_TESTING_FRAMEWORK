package com.cpms.tests;

import com.cpms.base.BaseTest;
import com.cpms.pages.LoginPage;
import com.cpms.pages.DashboardPage;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

/**
 * Cycle 3 Tests - New Feature Workspaces
 * Tests new views identified from scan:
 * - BI & Root Cause Analysis
 * - Tiger Team view
 * - Settings view
 * - Comms Hub view
 * - Site Map view
 */
@Epic("Cycle 3 - Advanced Features")
@Feature("New Workspace Views")
public class Cycle3FeatureTests extends BaseTest {

    @Test(description = "Director can access BI & Root Cause workspace", priority = 1)
    @Description("Verify that Director role can access and view the BI & Root Cause analysis workspace")
    @Story("BI & Root Cause Workspace Access")
    @Severity(SeverityLevel.CRITICAL)
    public void testBiWorkspaceAccess() {
        logTestStep("Starting BI workspace access test");

        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsDirector();

        logTestStep("Verifying BI tab is accessible for Director");
        Assert.assertTrue(dashboardPage.isTabVisible("bi"),
                "BI & Root Cause tab should be visible for Director");

        logTestStep("BI workspace access test completed");
    }

    @Test(description = "Director can access Tiger Team workspace", priority = 2)
    @Description("Verify that Director role can access and view the Tiger Team incident workspace")
    @Story("Tiger Team Workspace Access")
    @Severity(SeverityLevel.CRITICAL)
    public void testTigerTeamWorkspaceAccess() {
        logTestStep("Starting Tiger Team workspace access test");

        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsDirector();

        logTestStep("Verifying Tiger Team tab is accessible for Director");
        Assert.assertTrue(dashboardPage.isTabVisible("tiger"),
                "Tiger Team tab should be visible for Director");

        logTestStep("Tiger Team workspace access test completed");
    }

    @Test(description = "Director can access Settings workspace", priority = 3)
    @Description("Verify that Director role can access the application Settings workspace")
    @Story("Settings Workspace Access")
    @Severity(SeverityLevel.CRITICAL)
    public void testSettingsWorkspaceAccess() {
        logTestStep("Starting Settings workspace access test");

        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsDirector();

        logTestStep("Verifying Settings tab is accessible for Director");
        Assert.assertTrue(dashboardPage.isTabVisible("settings"),
                "Settings tab should be visible for Director");

        logTestStep("Settings workspace access test completed");
    }

    @Test(description = "PM has access to operational workspaces but not Client Portal", priority = 4)
    @Description("Verify that PM has appropriate workspace boundaries enforced")
    @Story("PM Workspace Access Control")
    @Severity(SeverityLevel.CRITICAL)
    public void testPmWorkspaceBoundaries() {
        logTestStep("Starting PM workspace boundaries test");

        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsProjectManager();

        logTestStep("Verifying PM has operational tabs");
        Assert.assertTrue(dashboardPage.isTabVisible("projects"), "PM should have Projects tab");
        Assert.assertTrue(dashboardPage.isTabVisible("programme"), "PM should have Programme tab");

        logTestStep("Verifying PM does not have Client Portal access");
        Assert.assertFalse(dashboardPage.isTabVisible("client"), "PM should NOT have Client Portal tab");

        logTestStep("PM workspace boundaries test completed");
    }

    @Test(description = "QS has access to commercial workspaces but not Site tab", priority = 5)
    @Description("Verify that QS commercial focus is properly enforced via tab visibility")
    @Story("QS Workspace Access Control")
    @Severity(SeverityLevel.CRITICAL)
    public void testQsWorkspaceBoundaries() {
        logTestStep("Starting QS workspace boundaries test");

        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsQuantitySurveyor();

        logTestStep("Verifying QS has commercial tabs");
        Assert.assertTrue(dashboardPage.isTabVisible("commercial"), "QS should have BOQ & Budget tab");
        Assert.assertTrue(dashboardPage.isTabVisible("programme"), "QS should have Programme tab");
        Assert.assertTrue(dashboardPage.isTabVisible("reports"), "QS should have Reports tab");

        logTestStep("Verifying QS does not have Client Portal access");
        Assert.assertFalse(dashboardPage.isTabVisible("client"), "QS should NOT have Client Portal tab");

        logTestStep("QS workspace boundaries test completed");
    }

    @Test(description = "Client Portal shows restricted view with Client role", priority = 6)
    @Description("Verify that Client role is restricted to Client Portal, Projects and Reports")
    @Story("Client Portal Restriction")
    @Severity(SeverityLevel.BLOCKER)
    public void testClientPortalRoleRestrictions() {
        logTestStep("Starting Client Portal role restrictions test");

        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAsClient();

        logTestStep("Verifying Client has restricted workspace access");
        Assert.assertTrue(dashboardPage.isTabVisible("client"), "Client should have Client Portal tab");
        Assert.assertTrue(dashboardPage.isTabVisible("reports"), "Client should have Reports tab");

        logTestStep("Verifying Client does not have internal management tabs");
        Assert.assertFalse(dashboardPage.isTabVisible("settings"), "Client should NOT have Settings tab");
        Assert.assertFalse(dashboardPage.isTabVisible("tiger"), "Client should NOT have Tiger Team tab");

        logTestStep("Client Portal role restrictions test completed");
    }
}