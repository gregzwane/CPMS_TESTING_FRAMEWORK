# CPMS Testing Framework

Comprehensive Selenium TestNG automation framework for Construction Project Management System (CPMS) testing.

## 🏗️ Framework Architecture

- **Base Framework**: Selenium WebDriver 4.15+ with TestNG 7.8+
- **Design Pattern**: Page Object Model (POM) with Page Factory
- **Reporting**: ExtentReports 5.1+ & Allure 2.24+
- **Build Tool**: Maven 3.8+
- **Cross-Browser**: Chrome, Firefox, Edge support
- **CI/CD**: GitHub Actions with parallel execution
- **API Testing**: REST Assured 5.3+ for backend validation

## 📁 Project Structure

```
CPMS_Testing_Framework/
├── src/main/java/com/cpms/
│   ├── base/           # BaseTest, BasePage classes
│   ├── config/         # ConfigManager for properties
│   ├── pages/          # Page Object Model classes
│   └── utils/          # DriverManager, ExtentManager utilities
├── src/test/java/com/cpms/
│   ├── tests/          # Web UI test classes
│   ├── api/            # REST API test classes
│   └── mobile/         # Mobile test classes (future)
├── src/test/resources/
│   ├── data/           # Test data files (CSV, JSON)
│   ├── testng/         # TestNG suite configurations
│   └── config.properties
├── .github/workflows/  # CI/CD pipeline
└── pom.xml
```

## 🚀 Quick Start

### Prerequisites
- Java 11+
- Maven 3.8+
- Chrome/Firefox/Edge browsers

### Setup
```bash
git clone <repository-url>
cd CPMS_Testing_Framework
mvn clean install
```

### Run Tests
```bash
# Smoke tests (quick validation)
mvn test -Dtest=AuthenticationTests

# Full functional suite
mvn test -DsuiteXmlFile=src/test/resources/testng.xml

# Cross-browser execution
mvn test -Dbrowser=firefox -Denv=staging

# API tests only
mvn test -Dtest=CpmsApiTests

# Headless execution
mvn test -Dheadless=true
```

## 🧪 Test Coverage

### 🔐 Authentication Tests (Cycle 2 - All Passing)
- ✅ Director, PM, QS, Client login validation
- ✅ Invalid credential rejection
- ✅ Required field validation
- ✅ Session management & logout
- ✅ Role-based access control (updated for actual UI tabs)

### 📋 Project Management Tests
- ✅ Project pack import (Scenario A1)
- ✅ Manual project creation with geolocation
- ✅ Project filtering and search (Scenario D)
- ✅ Document classification routing (Scenario E)
- ✅ Client access restrictions (RBAC)

### 🔌 API Tests (Updated for Supabase Auth + Render Backend)
- ✅ Supabase JWT authentication & token validation
- ✅ Projects API (Render backend with Bearer token)
- 🔄 BOQ CSV import processing (endpoint needs verification)
- 🔄 Security: SQL injection prevention
- 🔄 Error handling & validation
- 🔄 Concurrent request handling

### 📱 Mobile Tests (Framework Ready)
- 📋 Mobile login for all roles
- 📋 Responsive navigation
- 📋 Geofenced site diary
- 📋 Document upload from mobile

## 📊 Test Scenarios (From Uploaded Files)

### Scenario A - Project Pack Import
```java
// Import with 6 BOQ lines + documents
String[] testFiles = {
    "boq-budget-riverside.csv",        // 6 BOQ rows
    "programme-baseline-wbs.txt",      // Programme module
    "quality-hs-inspection-checklist.txt", // Quality module
    "site-daily-progress-diary.txt",   // Site module
    "report-ipc-payment-certificate.txt", // Reports module
    "drawing-site-layout-readme.txt"   // Documents module
};
projectsPage.importProjectPack("JHB-T1", "Johannesburg Bulk Earthworks", testFiles);
```

### Scenario B - BOQ Spreadsheet Import
```java
// Standalone BOQ import
boqPage.importBoqSpreadsheet("boq-only-commercial-import.csv");
// Should add T99.001 (fence) and T99.002 (topsoil) lines
```

### Scenario C - API Wake-up
```java
// Handle cold start sequence
dashboardPage.waitForApiToBeReady();
assert dashboardPage.isApiLive();
```

## 🔧 Configuration

### Environment Configuration
```properties
# config.properties
local.url=https://leaverageconstructionhub.vercel.app/
staging.url=https://leaverageconstructionhub.vercel.app/
api.base.url=https://cpms-api-l3o7.onrender.com

# Test credentials
director.email=director@cpms.demo
pm.email=pm@cpms.demo
qs.email=qs@cpms.demo
client.email=client@cpms.demo
```

### Browser Configuration
```bash
# Chrome (default)
mvn test -Dbrowser=chrome

# Firefox
mvn test -Dbrowser=firefox

# Edge
mvn test -Dbrowser=edge

# Headless mode
mvn test -Dheadless=true
```

## 📈 Reporting

### ExtentReports
- Real-time HTML reports with screenshots
- Test step logging and status tracking
- Cross-browser execution summary
- Failure analysis with stack traces

### Allure Reports
- Advanced test analytics and trends
- Detailed test execution timeline
- Attachment support (screenshots, logs)
- Integration with CI/CD pipeline

### View Reports
```bash
# Generate and open ExtentReports
# Available at: target/reports/CPMS_Test_Report_[timestamp].html

# Generate Allure report
mvn allure:serve
```

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow
- **Multi-browser**: Chrome, Firefox, Edge
- **Multi-environment**: Local, Staging
- **Parallel execution**: Reduced test time
- **Security scanning**: OWASP dependency check
- **Report deployment**: GitHub Pages hosting
- **Notifications**: Slack & Email alerts

### Pipeline Triggers
- Push to `main`/`develop` branches
- Pull requests
- Daily scheduled runs (2 AM UTC)
- Manual triggers

## 🎯 Key Features

### ✨ Advanced Capabilities
- **Smart Wait Strategies**: Dynamic element waiting
- **Cross-Browser Driver Management**: Automatic driver setup
- **Screenshot on Failure**: Automatic capture and attachment
- **Data-Driven Testing**: CSV/JSON test data support
- **API Integration**: Full REST API validation
- **Geolocation Testing**: Coordinate and mapping validation
- **Role-Based Testing**: RBAC verification across user types

### 🛡️ Security Testing
- SQL injection prevention validation
- Authentication bypass testing  
- Role-based access control verification
- Input sanitization testing

### 🔧 Framework Utilities
- **ConfigManager**: Centralized configuration
- **DriverManager**: Thread-safe WebDriver handling
- **ExtentManager**: Advanced HTML reporting
- **BasePage**: Reusable web element interactions
- **BaseTest**: Test lifecycle management

## 📝 Test Data

### BOQ Test Data (from uploaded files)
```csv
item code,description,unit,quantity,unit rate
E01.001,Site establishment and hoarding,item,1,185000
E02.010,Bulk excavation to spoil,m3,12500,42.50
E02.020,Import G6 fill and compact,m3,8200,78.00
V01.001,Variation — rock excavation allowance,m3,450,125.00
```

### Geolocation Test Data
- **Johannesburg**: -26.2041, 28.0473
- **Cape Town**: -33.9249, 18.4241

## 🚀 Getting Started - Development

### Adding New Tests
1. Extend `BaseTest` for UI tests
2. Create page objects extending `BasePage`
3. Use `@Test` annotations with Allure labels
4. Add test methods to appropriate TestNG suites

### Example Test Structure
```java
@Epic("Feature Area")
@Feature("Specific Feature")
public class NewFeatureTests extends BaseTest {
    
    @Test(description = "Test scenario description")
    @Story("User Story")
    @Severity(SeverityLevel.HIGH)
    public void testNewFeature() {
        logTestStep("Starting test");
        // Test implementation
        Assert.assertTrue(condition, "Assertion message");
    }
}
```

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/new-test`)
3. Follow Page Object Model patterns
4. Add appropriate test documentation
5. Ensure all tests pass locally
6. Submit pull request

## 📞 Support

For framework issues or test failures:
- Check GitHub Actions logs
- Review ExtentReports for detailed failure analysis  
- Verify environment configuration
- Check API connectivity to https://cpms-api-l3o7.onrender.com

---

**Framework Version**: 1.0.0  
**Last Updated**: October 2026  
**Test Coverage**: Authentication, Projects, BOQ, Documents, API, RBAC  
**Supported Environments**: Local, Staging, Production