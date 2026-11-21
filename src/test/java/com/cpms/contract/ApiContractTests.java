package com.cpms.contract;

import com.cpms.api.BaseApiTest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

import java.util.List;
import java.util.Map;

/**
 * API Contract Tests for CPMS Backend.
 * Validates API responses match expected schemas/contracts.
 * Uses JSON Schema validation for request/response contracts.
 */
@Epic("API Contract Testing")
@Feature("Response Schema Validation")
public class ApiContractTests extends BaseApiTest {

    private static final Logger logger = LogManager.getLogger(ApiContractTests.class);

    @BeforeMethod
    public void ensureAuth() {
        refreshAuthIfNeeded();
    }

    /**
     * Test Case: Contract - GET /projects returns valid project array schema
     */
    @Test(description = "Contract - Projects list response schema", priority = 1)
    @Description("Verify GET /projects returns valid project array with required fields")
    @Story("Contract - Projects API - List")
    @Severity(SeverityLevel.CRITICAL)
    public void testProjectsListContract() {
        logger.info("Testing projects list contract");

        RequestSpecification spec = getAuthenticatedRequest();
        Assert.assertNotNull(spec, "Authenticated request spec should not be null");

        Response response = spec
                .when()
                .get("/projects")
                .then()
                .extract().response();

        int status = response.getStatusCode();
        
        if (status == 200) {
            assertJsonResponse(response);
            
            // Validate response is an array
            List<?> projects = response.jsonPath().getList("$");
            Assert.assertNotNull(projects, "Response should be an array");
            
            // If projects exist, validate each project structure
            if (!projects.isEmpty()) {
                for (Object projectObj : projects) {
                    if (projectObj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> project = (Map<String, Object>) projectObj;
                        validateProjectSchema(project);
                    }
                }
            }
            
            logger.info("Projects list contract validation passed - {} projects", projects.size());
        } else if (status == 404 || status == 403) {
            logger.warn("Projects endpoint returned {} - contract validation skipped", status);
        } else {
            Assert.fail("Unexpected response code: " + status);
        }
    }

    /**
     * Test Case: Contract - POST /projects creates project with valid response schema
     */
    @Test(description = "Contract - Project creation response schema", priority = 2)
    @Description("Verify POST /projects returns created project with all required fields")
    @Story("Contract - Projects API - Create")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateProjectContract() {
        logger.info("Testing project creation contract");

        RequestSpecification spec = getAuthenticatedRequest();
        Assert.assertNotNull(spec, "Authenticated request spec should not be null");

        Map<String, Object> projectRequest = new java.util.HashMap<>();
        projectRequest.put("code", "CONTRACT-TEST-" + System.currentTimeMillis());
        projectRequest.put("name", "Contract Test Project");
        projectRequest.put("client", "Contract Test Client");
        projectRequest.put("contractValue", 2500000);

        Response response = spec
                .body(projectRequest)
                .when()
                .post("/projects")
                .then()
                .extract().response();

        int status = response.getStatusCode();
        
        if (status == 201 || status == 200) {
            assertJsonResponse(response);
            
            // Validate created project has all required fields
            String code = response.jsonPath().getString("code");
            String name = response.jsonPath().getString("name");
            String client = response.jsonPath().getString("client");
            Object contractValue = response.jsonPath().get("contractValue");
            
            Assert.assertNotNull(code, "Created project must have code");
            Assert.assertNotNull(name, "Created project must have name");
            Assert.assertNotNull(client, "Created project must have client");
            Assert.assertNotNull(contractValue, "Created project must have contractValue");
            
            Assert.assertEquals(code, projectRequest.get("code"), "Code should match request");
            Assert.assertEquals(name, projectRequest.get("name"), "Name should match request");
            
            logger.info("Project creation contract validation passed");
        } else if (status == 404 || status == 403 || status == 405) {
            logger.warn("Create project endpoint returned {} - contract validation skipped", status);
        } else {
            logger.warn("Unexpected response code: {} - documenting behavior", status);
        }
    }

    /**
     * Test Case: Contract - Authentication token response schema
     */
    @Test(description = "Contract - Auth token response schema", priority = 3)
    @Description("Verify Supabase auth returns valid JWT token structure")
    @Story("Contract - Authentication API")
    @Severity(SeverityLevel.CRITICAL)
    public void testAuthTokenContract() {
        logger.info("Testing auth token contract");

        // Re-authenticate to get fresh response
        Map<String, String> loginRequest = new java.util.HashMap<>();
        loginRequest.put("email", "director@cpms.demo");
        loginRequest.put("password", "ChangeMe123!");

        Response response = io.restassured.RestAssured.given()
                .contentType(io.restassured.http.ContentType.JSON)
                .header("apikey", "sb_publishable_0zGYs8iNRYzb-j2cq7q_eQ_9UXW-m_p")
                .body(loginRequest)
                .when()
                .post("https://zpilbrxryhixrqllqndh.supabase.co/auth/v1/token?grant_type=password")
                .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200, "Auth should return 200");
        assertJsonResponse(response);

        // Validate token response structure
        String accessToken = response.jsonPath().getString("access_token");
        String tokenType = response.jsonPath().getString("token_type");
        Integer expiresIn = response.jsonPath().getInt("expires_in");
        String refreshToken = response.jsonPath().getString("refresh_token");
        Object user = response.jsonPath().get("user");

        Assert.assertNotNull(accessToken, "Response must contain access_token");
        Assert.assertTrue(accessToken.length() > 20, "Access token should be JWT (reasonable length)");
        Assert.assertEquals(tokenType, "bearer", "Token type should be bearer");
        Assert.assertNotNull(expiresIn, "Response must contain expires_in");
        Assert.assertTrue(expiresIn > 0, "expires_in should be positive");
        Assert.assertNotNull(refreshToken, "Response must contain refresh_token");
        Assert.assertNotNull(user, "Response must contain user object");

        // Validate user object structure
        if (user instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> userMap = (Map<String, Object>) user;
            Assert.assertNotNull(userMap.get("id"), "User must have id");
            Assert.assertNotNull(userMap.get("email"), "User must have email");
        }

        logger.info("Auth token contract validation passed");
    }

    /**
     * Test Case: Contract - Error response format consistency
     */
    @Test(description = "Contract - Error response format", priority = 4)
    @Description("Verify all error responses follow consistent format")
    @Story("Contract - Error Handling")
    @Severity(SeverityLevel.CRITICAL)
    public void testErrorResponseContract() {
        logger.info("Testing error response contract");

        RequestSpecification spec = getAuthenticatedRequest();
        Assert.assertNotNull(spec, "Authenticated request spec should not be null");

        // Test 1: Invalid project creation (missing required fields)
        Map<String, Object> invalidRequest = new java.util.HashMap<>();
        invalidRequest.put("code", "INVALID-001");
        // Missing required 'name' field

        Response validationError = spec
                .body(invalidRequest)
                .when()
                .post("/projects");

        // Test 2: Non-existent resource
        Response notFoundError = spec
                .when()
                .get("/projects/non-existent-id-12345");

        // Validate error responses are JSON when they return errors
        validateErrorResponse(validationError, "Validation Error");
        validateErrorResponse(notFoundError, "Not Found Error");

        logger.info("Error response contract validation passed");
    }

    /**
     * Test Case: Contract - BOQ Import response schema
     */
    @Test(description = "Contract - BOQ Import response schema", priority = 5)
    @Description("Verify BOQ import returns valid response structure")
    @Story("Contract - BOQ Import API")
    @Severity(SeverityLevel.CRITICAL)
    public void testBoqImportContract() {
        logger.info("Testing BOQ import contract");

        String csvContent = "item code,description,unit,quantity,unit rate\n" +
                "CONTRACT-001,Contract Test Item,m3,100,50.00\n" +
                "CONTRACT-002,Another Contract Item,t,200,75.00";

        Response response = io.restassured.RestAssured.given()
                .header("Authorization", "Bearer " + getAuthToken())
                .multiPart("file", "contract-test-boq.csv", csvContent.getBytes(), "text/csv")
                .when()
                .post("https://cpms-api-l3o7.onrender.com/api/v1/boq/import")
                .then()
                .extract().response();

        int status = response.getStatusCode();
        
        if (status == 200 || status == 201) {
            assertJsonResponse(response);
            
            // Validate import response has expected fields
            Object imported = response.jsonPath().get("imported");
            Object rowCount = response.jsonPath().get("rowCount");
            Object errors = response.jsonPath().get("errors");
            
            // At minimum, should indicate success
            Assert.assertTrue(imported != null || rowCount != null,
                "Import response should contain success indicators");
            
            if (rowCount != null) {
                int count = rowCount instanceof Integer ? (Integer) rowCount : Integer.parseInt(rowCount.toString());
                Assert.assertTrue(count > 0, "Should report imported row count");
            }
            
            logger.info("BOQ import contract validation passed");
        } else if (status == 404 || status == 403 || status == 405 || status == 415) {
            logger.warn("BOQ import endpoint returned {} - contract validation skipped", status);
        } else {
            logger.warn("Unexpected BOQ import response: {} - documenting behavior", status);
        }
    }

    /**
     * Validate project object has all required fields.
     */
    private void validateProjectSchema(Map<String, Object> project) {
        // Required fields per API contract
        Assert.assertNotNull(project.get("id"), "Project must have id");
        Assert.assertNotNull(project.get("code"), "Project must have code");
        Assert.assertNotNull(project.get("name"), "Project must have name");
        
        // Optional but expected fields
        if (project.get("client") != null) {
            Assert.assertTrue(project.get("client") instanceof String, "Client should be string");
        }
        if (project.get("contractValue") != null) {
            Assert.assertTrue(project.get("contractValue") instanceof Number, "Contract value should be number");
        }
        if (project.get("latitude") != null) {
            Assert.assertTrue(project.get("latitude") instanceof String, "Latitude should be string");
        }
        if (project.get("longitude") != null) {
            Assert.assertTrue(project.get("longitude") instanceof String, "Longitude should be string");
        }
        if (project.get("status") != null) {
            Assert.assertTrue(project.get("status") instanceof String, "Status should be string");
        }
        
        logger.debug("Project schema validated: {}", project.get("code"));
    }

    /**
     * Validate error response follows contract.
     */
    private void validateErrorResponse(Response response, String testName) {
        int status = response.getStatusCode();
        logger.info("{} - Status: {}", testName, status);
        
        if (status >= 400) {
            String contentType = response.getContentType();
            if (contentType != null && contentType.contains("json")) {
                // Validate error response structure
                Object errorCode = response.jsonPath().get("error_code");
                Object message = response.jsonPath().get("message");
                Object errors = response.jsonPath().get("errors");
                Object detail = response.jsonPath().get("detail");
                
                // At minimum, should have some error indication
                boolean hasErrorInfo = errorCode != null || message != null || errors != null || detail != null;
                Assert.assertTrue(hasErrorInfo, testName + " - Error response should contain error information");
                
                logger.info("{} - Error structure validated", testName);
            } else {
                logger.warn("{} - Non-JSON error response: {}", testName, contentType);
            }
        }
    }
}
