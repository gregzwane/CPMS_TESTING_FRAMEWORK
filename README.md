# CPMS Testing Framework

Comprehensive Selenium TestNG automation framework for Construction Project Management System (CPMS) testing.

## 🏗️ Framework Architecture

- **Base Framework**: Selenium WebDriver 4.15+ with TestNG 7.8+
- **Design Pattern**: Page Object Model (POM) with Page Factory
- **Reporting**: ExtentReports 5.1+ & **Allure 2.24.0** (Allure 3 compatible)
- **Build Tool**: Maven 3.8+
- **Cross-Browser**: Chrome, Firefox, Edge support
- **CI/CD**: GitHub Actions with parallel execution (Actions v7+)
- **API Testing**: REST Assured 5.3+ for backend validation
- **Containerization**: Docker multi-stage build with Docker Compose
- **Monitoring**: Micrometer + Prometheus metrics, structured JSON logging

## 📁 Project Structure

```
CPMS_Testing_Framework/
├── src/main/java/com/cpms/
│   ├── base/           # BaseTest, BasePage classes
│   ├── config/         # ConfigManager for properties
│   ├── pages/          # Page Object Model classes
│   └── utils/          # DriverManager, ExtentManager utilities
├── src/test/java/com/cpms/
│   ├── tests/          # Web UI test classes (organized by Cycle)
│   ├── api/            # REST API test classes
│   └── mobile/         # Mobile test classes (future)
├── src/test/resources/
│   ├── data/           # Test data files (CSV, JSON)
│   ├── testng/         # TestNG suite configurations
│   └── config.properties
├── .github/workflows/  # CI/CD pipeline (Actions v7+)
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

# API tests only (all 16 passing)
mvn test -Dtest=**/CpmsApiTests.java

# Headless execution
mvn test -Dheadless=true

# Cycle 5 Performance Tests
mvn test -Dtest=**/Cycle5PerformanceTests.java -Dheadless=true

# Cycle 5 Batch Processing Tests
mvn test -Dtest=**/BatchProcessingTests.java -Dheadless=true

# Full Regression Pack (REGRESSION TEST CPMS PACK)
mvn test -DsuiteXmlFile=src/test/resources/testng.xml -Dheadless=true
```

### Docker Execution
```bash
# Build image
docker build -t cpms-tests:latest .

# Quick smoke tests
docker-compose up cpms-smoke

# Cycle 1 & 2 - Authentication & Project Management
docker-compose up cpms-cycle1-2

# Cycle 3 - Advanced Workspace Tests
docker-compose up cpms-cycle3

# Cycle 4 - Role-Based Tab Access Tests
docker-compose up cpms-cycle4

# Cycle 5 - Performance Tests
docker-compose up cpms-cycle5-perf

# Cycle 5 - Batch Processing Tests
docker-compose up cpms-cycle5-batch

# Cycle 5 - Full Regression (All Cycles)
docker-compose up cpms-cycle5-full

# API tests only
docker-compose up cpms-api

# View Allure reports at http://localhost:5050
docker-compose up allure
```

### Allure Report with Environment Information
```bash
# Generate and open Allure report
mvn allure:serve

# Generate static Allure report
mvn allure:report

# Docker Allure UI
docker-compose up allure  # http://localhost:5050
```

## 🧪 Test Coverage (Cycle 5 - 95%+ Pass Rate)

### 🔐 Authentication Tests (CycleOne - 12/12 Passing)
- ✅ Director, PM, QS, Client login validation
- ✅ Invalid credential rejection with error message
- ✅ Required field validation
- ✅ Session management & logout with redirect verification
- ✅ Role-based access control (updated for actual UI tabs)
- ✅ **NEW**: Each role clicks and verifies all accessible tabs

### 📋 Project Management Tests (CycleTwo - 9/9 Resilient)
- ✅ Project pack import (Scenario A1) with error handling
- ✅ Manual project creation with geolocation
- ✅ Project filtering and search (Scenario D) with tolerant assertions
- ✅ Project creation validation
- ✅ Client access restrictions (RBAC) - verifies button visibility
- ✅ Project geolocation mapping
- ✅ Project location update
- ✅ Document classification routing (Scenario E)
- ✅ Role-based project dashboard

### 🔌 API Tests (16/16 Passing)
- ✅ Supabase JWT authentication & token validation
- ✅ Projects API (Render backend with Bearer token)
- ✅ CRUD operations with graceful 404/403 handling
- ✅ Security: SQL injection prevention, malformed JSON, token expiry
- ✅ Concurrent request handling
- ✅ Consistent error format validation
- ✅ Performance tests (load, stress, endurance, spike)

### 🏢 CycleThree Advanced Feature Tests (6/6 Passing)
- ✅ BI & Root Cause workspace access
- ✅ Tiger Team workspace access
- ✅ Settings workspace access
- ✅ PM workspace boundaries
- ✅ QS workspace boundaries
- ✅ Client Portal role restrictions

### 📦 CycleFive Batch Processing Tests (5/5 Ready)
- ✅ `testBulkProjectImport` - Bulk project import from CSV (5 projects)
- ✅ `testScheduledBoqImport` - Scheduled BOQ import verification
- ✅ `testBatchDocumentClassification` - Batch document processing
- ✅ `testBatchPaymentCertificateGeneration` - Bulk payment certs
- ✅ `testNightlyDataSync` - Nightly data sync verification

### ⚡ CycleFive Performance Tests (5/5 Ready)
- ✅ `testConcurrentRequests` - 5 parallel requests baseline
- ✅ `testApiLoadTest100Users` - 100 concurrent users, 90%+ success, <5s avg response
- ✅ `testApiStressTest500Users` - 500 concurrent users, 50%+ success (breaking point)
- ✅ `testApiEnduranceTest30Min` - 5 min sustained (30 min prod), 95%+ success
- ✅ `testApiSpikeTest` - 200 user sudden burst, 70%+ success

### 📦 CycleFive Batch Processing Tests (5/5 Ready)
- ✅ `testBulkProjectImport` - Bulk project import from CSV (5 projects)
- ✅ `testScheduledBoqImport` - Scheduled BOQ import verification
- ✅ `testBatchDocumentClassification` - Batch document processing
- ✅ `testBatchPaymentCertificateGeneration` - Bulk payment certs
- ✅ `testNightlyDataSync` - Nightly data sync verification

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

### Allure Reports (v2.24.0 - Allure 3 Compatible)
- Advanced test analytics and trends
- Detailed test execution timeline
- Attachment support (screenshots, logs)
- Integration with CI/CD pipeline
- **Environment information includes application URL**

### View Reports
```bash
# Generate and open ExtentReports
# Available at: target/reports/CPMS_Test_Report_[timestamp].html

# Generate Allure report
mvn allure:report

# Docker Allure UI
docker-compose up allure  # http://localhost:5050
```

## 🔄 CI/CD Pipeline (Actions v7+ with Allure 3)

### GitHub Actions Workflow - Allure 3 Report Publishing
```yaml
name: CPMS Testing Framework CI/CD

on: [push]

permissions:
  contents: write

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v7

      - name: Set up JDK
        uses: actions/setup-java@v6
        with:
          distribution: zulu
          java-version: 17

      - name: Run tests
        run: SELENIDE_URL="${{ vars.SELENIDE_URL }}" ./gradlew clean test

      - name: Build test report
        if: always()
        run: |
          npm install -g allure
          allure generate build/allure-results -o allure-report

      - name: Publish test report
        uses: peaceiris/actions-gh-pages@v4
        if: always()
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_branch: gh-pages
          publish_dir: allure-report
```

### Current Pipeline Features
- **Multi-browser**: Chrome, Firefox, Edge (browser-actions v1)
- **Multi-environment**: Local, Staging
- **Java 17 (LTS)** with Zulu distribution
- **Parallel execution**: Reduced test time
- **Security scanning**: OWASP dependency check
- **SAST**: SpotBugs, Semgrep, PMD (SARIF upload)
- **Container scanning**: Trivy, Docker Scout
- **SBOM generation**: SPDX JSON
- **Secret scanning**: TruffleHog
- **Allure 3 report deployment** to GitHub Pages (peaceiris/actions-gh-pages v4)
- **Performance test stage** with regression checking
- **Slack & Email notifications** on failure

### Pipeline Triggers
- Push to `main`/`develop` branches
- Pull requests
- **Nightly integration tests** - 2 AM UTC daily
- **Weekly dependency updates** - Sunday 3 AM UTC
- **Weekly security scanning** - Sunday 4 AM UTC
- **Weekly performance/load tests** - Sunday 5 AM UTC
- **Batch data processing** - Daily 1 AM UTC
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

## 📋 REGRESSION TEST CPMS PACK

**Test Pack:** REGRESSION TEST CPMS PACK  
**Total Tests:** 54 | **Pass Rate:** 95%+ | **Core Suites:** 100%

### Test ID Mapping
| Prefix | Type | Range |
|--------|------|-------|
| FNT | Functional Tests | FNT001-FNT028 |
| APT | API Tests | APT001-APT016 |
| PFT | Performance Tests | PFT001-PFT005 |
| BAT | Batch Processing | BAT001-BAT005 |

### Test Execution Commands
```bash
# Full Regression Pack
mvn test -DsuiteXmlFile=src/test/resources/testng.xml -Dheadless=true

# Individual Cycles
mvn test -Dtest=**/AuthenticationTests.java -Dheadless=true          # CycleOne (FNT001-FNT012)
mvn test -Dtest=**/ProjectManagementTests.java -Dheadless=true       # CycleTwo (FNT013-FNT021)
mvn test -Dtest=**/Cycle3FeatureTests.java -Dheadless=true           # CycleThree (FNT022-FNT027)
mvn test -Dtest=**/AuthenticationTests.java#testRoleTabAccessVerification -Dheadless=true  # CycleFour (FNT028)
mvn test -Dtest=**/CpmsApiTests.java -Dheadless=true                 # CycleFive API (APT001-APT016)
mvn test -Dtest=**/Cycle5PerformanceTests.java -Dheadless=true       # CycleFive Perf (PFT001-PFT005)
mvn test -Dtest=**/BatchProcessingTests.java -Dheadless=true         # CycleFive Batch (BAT001-BAT005)

# Docker
docker-compose up cpms-cycle1-2   # Cycle 1 & 2
docker-compose up cpms-cycle3     # Cycle 3
docker-compose up cpms-cycle4     # Cycle 4
docker-compose up cpms-cycle5-perf # Cycle 5 Performance
docker-compose up cpms-cycle5-batch # Cycle 5 Batch
docker-compose up cpms-cycle5-full # Full Regression
docker-compose up cpms-api        # API Only
docker-compose up allure          # Reports at http://localhost:5050
```

## 📋 Application Features & Test Coverage

| Feature Area | Application Feature | Test Coverage | Test IDs |
|--------------|---------------------|---------------|----------|
| **Authentication** | Sign In / Login | ✅ Full | FNT001-FNT007, FNT011 |
| **Authentication** | Sign Out / Logout | ✅ Full | FNT008 |
| **Authentication** | Session Management | ✅ Full | FNT008, FNT009 |
| **Authentication** | RBAC / Role Access | ✅ Full | FNT010, FNT011, FNT028 |
| **Projects** | Project Pack Import | ✅ Full | FNT013 |
| **Projects** | Manual Creation | ✅ Full | FNT014 |
| **Projects** | Filtering/Search | ✅ Full | FNT015 |
| **Projects** | Geolocation/Map | ✅ Full | FNT018, FNT019 |
| **Projects** | Document Classification | ✅ Full | FNT020 |
| **Projects** | RBAC Project Access | ✅ Full | FNT017, FNT021 |
| **Commercial/BOQ** | BOQ Import | ✅ API | APT006 |
| **Commercial/BOQ** | Scheduled BOQ Import | 🟡 Ready | BAT002 |
| **Commercial/BOQ** | Payment Certificates | 🟡 Ready | BAT004 |
| **Programme** | Programme/WBS Management | ⚠️ Partial | - |
| **Quality/H&S** | Inspections, NCRs, Checklists | ⚠️ Partial | - |
| **Site** | Daily Diary, Geofenced Diary | ⚠️ Partial | - |
| **Documents** | Document Management | ⚠️ Partial | FNT020 |
| **Documents** | Batch Classification | 🟡 Ready | BAT003 |
| **Comms Hub** | Communications | ❌ Missing | - |
| **Tiger Team** | Incident Management | ✅ Access | FNT023 |
| **Reports** | Payment Certs/IPC | ✅ API | APT003 |
| **Reports** | Batch Generation | 🟡 Ready | BAT004 |
| **BI & Root Cause** | Analytics | ✅ Access | FNT022 |
| **Settings** | Configuration | ✅ Access | FNT024 |
| **Client Portal** | Client View | ✅ Access | FNT027 |
| **API** | Supabase Auth | ✅ Full | APT001, APT002 |
| **API** | CRUD Operations | ✅ Full | APT003-APT006 |
| **API** | Security (SQLi, XSS) | ✅ Full | APT007-APT010 |
| **API** | Performance/Load | 🟡 Ready | PFT001-PFT005 |
| **Batch** | Nightly Sync | 🟡 Ready | BAT005 |
| **Batch** | Bulk Operations | 🟡 Ready | BAT001, BAT003 |

## ⚠️ Missing Items - Final Cycle Development

| Priority | Feature Area | Missing Items | Suggested Test IDs |
|----------|--------------|---------------|-------------------|
| **HIGH** | Programme | Programme/WBS management tests | FNT029-FNT032 |
| **HIGH** | Quality/H&S | Inspection, NCR, Checklist tests | FNT033-FNT036 |
| **HIGH** | Site/Diary | Daily progress, Geofenced diary tests | FNT037-FNT040 |
| **HIGH** | Comms Hub | RFI, Communication, Notifications tests | FNT041-FNT044 |
| **HIGH** | Documents | Full document lifecycle (upload, version, download) | FNT045-FNT048 |
| **HIGH** | Reports | Custom report builder, Scheduling | FNT049-FNT052 |
| **MEDIUM** | Mobile | Mobile app tests (login, diary, upload) | MBT001-MBT004 |
| **MEDIUM** | BI/Analytics | Custom dashboards, Drill-down, Export | FNT053-FNT056 |
| **MEDIUM** | Settings | User management, Roles, Notifications | FNT057-FNT060 |
| **LOW** | Advanced Search | Global search, Filters, Saved searches | FNT061-FNT064 |
| **LOW** | Notifications | Email, In-app, Webhook notifications | FNT065-FNT068 |

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

**Framework Version**: 1.2.0 (CycleFive)  
**Last Updated**: October 2026  
**Test Coverage**: Authentication, Projects, BOQ, Documents, API, RBAC, Advanced Workspaces, Performance, Batch Processing  
**Supported Environments**: Local, Staging, Production  
**Supported Browsers**: Chrome, Firefox, Edge  
**Containerization**: Docker + Docker Compose ready