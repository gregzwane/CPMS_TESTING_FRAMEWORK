package com.cpms.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all API tests.
 * Centralizes authentication, request specifications, and common test utilities.
 */
public class BaseApiTest {

    protected static final Logger logger = LogManager.getLogger(BaseApiTest.class);
    protected static final String SUPABASE_URL = "https://zpilbrxryhixrqllqndh.supabase.co";
    protected static final String BACKEND_URL = "https://cpms-api-l3o7.onrender.com/api/v1";
    protected static final String API_KEY = "sb_publishable_0zGYs8iNRYzb-j2cq7q_eQ_9UXW-m_p";

    protected String authToken;

    @BeforeClass
    public void setupApiBase() {
        logger.info("BaseApiTest setup completed");
        RestAssured.baseURI = BACKEND_URL;
        authenticate();
    }

    @BeforeMethod
    public void refreshAuthIfNeeded() {
        if (authToken == null || authToken.isEmpty()) {
            authenticate();
        }
    }

    /**
     * Authenticate with Supabase and obtain JWT token.
     */
    protected void authenticate() {
        logger.info("Authenticating with Supabase...");

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "director@cpms.demo");
        loginRequest.put("password", "ChangeMe123!");

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("apikey", API_KEY)
                .body(loginRequest)
                .when()
                .post(SUPABASE_URL + "/auth/v1/token?grant_type=password")
                .then()
                .extract().response();

        int status = response.getStatusCode();
        logger.info("Authentication response status: {}", status);

        if (status == 200) {
            authToken = response.jsonPath().getString("access_token");
            if (authToken != null && !authToken.isEmpty()) {
                logger.info("Authentication successful, token acquired");
            } else {
                throw new RuntimeException("Failed to obtain access token from response");
            }
        } else {
            String errorCode = response.jsonPath().getString("error_code");
            String msg = response.jsonPath().getString("msg");
            throw new RuntimeException("Authentication failed: " + status + " - " + errorCode + " - " + msg);
        }
    }

    /**
     * Get authenticated request specification.
     */
    protected RequestSpecification getAuthenticatedRequest() {
        if (authToken == null || authToken.isEmpty()) {
            authenticate();
        }
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .baseUri(BACKEND_URL);
    }

    /**
     * Get unauthenticated request specification.
     */
    protected RequestSpecification getUnauthenticatedRequest() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .baseUri(BACKEND_URL);
    }

    /**
     * Validate successful response (2xx).
     */
    protected void assertSuccess(Response response) {
        assertSuccess(response, "Request should succeed");
    }

    /**
     * Validate successful response with custom message.
     */
    protected void assertSuccess(Response response, String message) {
        int status = response.getStatusCode();
        if (status < 200 || status >= 300) {
            String body = response.getBody().asString();
            logger.error("Response body: {}", body);
        }
        org.testng.Assert.assertTrue(status >= 200 && status < 300, 
            message + " - Expected 2xx, got: " + status);
    }

    /**
     * Validate error response (4xx/5xx).
     */
    protected void assertError(Response response, int expectedStatus) {
        int status = response.getStatusCode();
        org.testng.Assert.assertEquals(status, expectedStatus, 
            "Expected status " + expectedStatus + " but got " + status);
    }

    /**
     * Validate response is JSON.
     */
    protected void assertJsonResponse(Response response) {
        String contentType = response.getContentType();
        org.testng.Assert.assertNotNull(contentType, "Content-Type should not be null");
        org.testng.Assert.assertTrue(contentType.contains("json"), 
            "Response should be JSON, got: " + contentType);
    }

    /**
     * Get auth token for external use.
     */
    public String getAuthToken() {
        return authToken;
    }
}