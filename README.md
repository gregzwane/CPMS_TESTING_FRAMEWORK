# CPMS Testing Framework

Comprehensive Selenium TestNG automation framework for Construction Project Management System (CPMS) testing.

## 🏗️ Framework Architecture

- **Base Framework**: Selenium WebDriver 4.15+ with TestNG 7.8+
- **Design Pattern**: Page Object Model (POM) with Page Factory
- **Reporting**: ExtentReports 5.1+ & Allure 2.24+
- **Build Tool**: Maven 3.8+
- **Cross-Browser**: Chrome, Firefox, Edge support
- **CI/CD**: GitHub Actions with parallel execution (Actions v4)
- **API Testing**: REST Assured 5.3+ for backend validation
- **Containerization**: Docker multi-stage build with Docker Compose

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
├── .github/workflows/  # CI/CD pipeline (Actions v4)
├── Dockerfile          # Multi-stage Docker build
├── docker-compose.yml  # Multi-service orchestration
└── pom.xml
```

## 🚀 Quick Start

### Prerequisites
- Java 17+ (LTS)
- Maven 3.8+
- Chrome/Firefox/Edge browsers (auto-installed in CI/CD)

### Setup
```bash
git clone <repository-url>
cd CPMS_Testing_Framework
mvn clean install
```

### Run Tests
```bash
# Smoke tests (quick validation - all 12 passing)
mvn test -Dtest=**/AuthenticationTests.java

# Cycle 4 role-based tab verification (NEW)
mvn test -Dtest=**/AuthenticationTests.java#testRoleTabAccessVerification

# Full functional suite
mvn test -DsuiteXmlFile=src/test/resources/testng.xml

# Cross-browser execution
mvn test -Dbrowser=firefox -Denv=staging

# API tests only (all 12 passing)
mvn test -Dtest=**/CpmsApiTests.java

# Headless execution
mvn test -Dheadless=true

# Cycle 5 Performance Tests
mvn test -Dtest=**/CpmsApiTests.java#testConcurrentRequests -Dheadless=true
mvn test -Dtest=**/CpmsApiTests.java#testApiLoadTest100Users -Dheadless=true
mvn test -Dtest=**/CpmsApiTests.java#testApiStressTest500Users -Dheadless=true
mvn test -Dtest=**/CpmsApiTests.java#testApiEnduranceTest30Min -Dheadless=true
mvn test -Dtest=**/CpmsApiTests.java#testApiSpikeTest -Dheadless=true

# Cycle 5 Batch Processing Tests
mvn test -Dtest=**/BatchProcessingTests.java -Dheadless=true
```

### Docker Execution
```bash
# Build image
docker build -t cpms-tests:latest .

# Quick smoke tests
docker-compose up cpms-smoke

# Full functional tests
docker-compose up cpms-functional

# API tests only
docker-compose up cpms-api

# Performance tests
docker-compose up cpms-performance

# View Allure reports at http://localhost:5050
docker-compose up allure
```

## 🧪 Test Coverage (Cycle 4 - 95%+ Pass Rate)

### 🔐 Authentication Tests (Cycle 4 - 12/12 Passing)
- ✅ Director, PM, QS, Client login validation
- ✅ Invalid credential rejection with error message
- ✅ Required field validation
- ✅ Session management & logout with redirect verification
- ✅ Role-based access control (updated for actual UI tabs)
- ✅ **NEW**: Each role clicks and verifies all accessible tabs

### 📋 Project Management Tests (Cycle 4 - 9/9 Resilient)
- ✅ Project pack import (Scenario A1) with error handling
- ✅ Manual project creation with geolocation
- ✅ Project filtering and search (Scenario D) with tolerant assertions
- ✅ Project creation validation
- ✅ Client access restrictions (RBAC) - verifies button visibility
- ✅ Project geolocation mapping
- ✅ Project location update
- ✅ Document classification routing (Scenario E)
- ✅ Role-based project dashboard

### 🔌 API Tests (Cycle 4 - 12/12 Passing)
- ✅ Supabase JWT authentication & token validation
- ✅ Projects API (Render backend with Bearer token)
- ✅ CRUD operations with graceful 404/403 handling
- ✅ Security: SQL injection prevention, malformed JSON, token expiry
- ✅ Concurrent request handling
- ✅ Consistent error format validation

### 🏢 Cycle 3 Advanced Feature Tests (6/6 Passing)
- ✅ BI & Root Cause workspace access
- ✅ Tiger Team workspace access
- ✅ Settings workspace access
- ✅ PM workspace boundaries
- ✅ QS workspace boundaries
- ✅ Client Portal role restrictions

### 📱 Mobile Tests (Framework Ready)
- 📋 Mobile login for all roles
- 📋 Responsive navigation
- 📋 Geofenced site diary
- 📋 Document upload from mobile

### ⚡ Cycle 5 Performance Tests (5/5 Ready)
- ✅ `testConcurrentRequests` - 5 parallel requests baseline
- ✅ `testApiLoadTest100Users` - 100 concurrent users, 90%+ success, <5s avg response
- ✅ `testApiStressTest500Users` - 500 concurrent users, 50%+ success (breaking point)
- ✅ `testApiEnduranceTest30Min` - 5 min sustained (30 min prod), 95%+ success
- ✅ `testApiSpikeTest` - 200 user sudden burst, 70%+ success

### 📦 Cycle 5 Batch Processing Tests (5/5 Ready)
- ✅ `testBulkProjectImport` - Bulk project import from CSV (5 projects)
- ✅ `testScheduledBoqImport` - Scheduled BOQ import verification
- ✅ `testBatchDocumentClassification` - Batch document processing
- ✅ `testBatchPaymentCertificateGeneration` - Bulk payment certs
- ✅ `testNightlyDataSync` - Nightly data sync verification

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
api.base.url=https://cpms-api-l3o7.onrender.com/api/v1

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

# Docker Allure UI
docker-compose up allure  # http://localhost:5050
```

## 🔄 CI/CD Pipeline (Actions v4)

### GitHub Actions Workflow
- **Multi-browser**: Chrome, Firefox, Edge (browser-actions v1)
- **Multi-environment**: Local, Staging
- **Java 17 (LTS)** with temurin distribution
- **Parallel execution**: Reduced test time
- **Security scanning**: OWASP dependency check
- **Report deployment**: GitHub Pages hosting (peaceiris/actions-gh-pages v4)
- **Notifications**: Slack & Email alerts

### Pipeline Triggers
- Push to `main`/`develop` branches
- Pull requests
- Daily scheduled runs (2 AM UTC)
- Manual triggers

## 🎯 Key Features

### ✨ Advanced Capabilities
- **Smart Wait Strategies**: Dynamic element waiting with SPA support
- **Cross-Browser Driver Management**: Automatic driver setup via WebDriverManager
- **Screenshot on Failure**: Automatic capture and attachment
- **Data-Driven Testing**: CSV/JSON test data support
- **API Integration**: Full REST API validation with Supabase Auth
- **Geolocation Testing**: Coordinate and mapping validation
- **Role-Based Testing**: RBAC verification across user types

### 🛡️ Security Testing
- SQL injection prevention validation
- Authentication bypass testing  
- Role-based access control verification
- Input sanitization testing
- JWT token expiry handling

### 🔧 Framework Utilities
- **ConfigManager**: Centralized configuration
- **DriverManager**: Thread-safe WebDriver handling
- **ExtentManager**: Advanced HTML reporting
- **BasePage**: Reusable web element interactions
- **BaseTest**: Test lifecycle management

## 📋 Cycle Documentation

| Cycle | Focus | Tests | Status | Documentation |
|-------|-------|-------|--------|---------------|
| **Cycle 1** | Build Verification | 7 | ✅ Complete | `CYCLE1_READINESS.md` |
| **Cycle 2** | Full Regression | 28 | ✅ Complete | `CYCLE2_READINESS.md` |
| **Cycle 3** | Advanced Workspaces | 6 | ✅ Complete | `CYCLE3_READINESS.md` |
| **Cycle 4** | RBAC + Project Mgmt | 12 | ✅ Complete | `CYCLE4_READINESS.md` |
| **Cycle 5** | Performance + Batch + CI/CD | 20 | ✅ Complete | `CYCLE5_READINESS.md` |

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
- Check API connectivity to https://cpms-api-l3o7.onrender.com/api/v1

---

**Framework Version**: 1.1.0 (Cycle 4)  
**Last Updated**: October 2026  
**Test Coverage**: Authentication, Projects, BOQ, Documents, API, RBAC, Advanced Workspaces  
**Supported Environments**: Local, Staging, Production  
**Supported Browsers**: Chrome, Firefox, Edge  
**Containerization**: Docker + Docker Compose ready