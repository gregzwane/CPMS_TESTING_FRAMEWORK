package com.cpms.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

/**
 * CycleFive - Performance & Load Testing for CPMS API
 * Tests API performance under various load conditions
 * Test Cycle: CycleFive (Performance & Load Testing)
 */
@Epic("CycleFive - Performance & Load Testing")
@Feature("API Performance & Load Testing")
public class Cycle5PerformanceTests {

    private static final Logger logger = LogManager.getLogger(Cycle5PerformanceTests.class);
    private static final String SUPABASE_URL = "https://zpilbrxryhixrqllqndh.supabase.co";
    private static final String BACKEND_URL = "https://cpms-api-l3o7.onrender.com/api/v1";
    private static final String API_KEY = "sb_publishable_0zGYs8iNRYzb-j2cq7q_eQ_9UXW-m_p";
    private String authToken;

    @BeforeClass
    public void setupApi() {
        logger.info("CycleFive Performance API tests setup completed");
    }

    private RequestSpecification getAuthenticatedRequest() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .baseUri(BACKEND_URL);
    }

    @Test(description = "CycleFive - Baseline concurrent requests test", priority = 1)
    @Description("Verify API handles baseline concurrent requests")
    @Story("CycleFive - Performance - Concurrent Requests")
    @Severity(SeverityLevel.CRITICAL)
    public void testConcurrentRequests() {
        logTestStep("Starting concurrent requests test");

        if (authToken == null || authToken.isEmpty()) {
            testAuthenticationSuccess();
        }

        long startTime = System.currentTimeMillis();

        Thread[] threads = new Thread[5];
        boolean[] results = new boolean[5];

        for (int i = 0; i < 5; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                Response response = getAuthenticatedRequest()
                        .when()
                        .get("/projects");
                int status = response.getStatusCode();
                results[index] = (status == 200 || status == 403 || status == 404);
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.warn("Thread interrupted: {}", e.getMessage());
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Concurrent requests completed in {} ms", duration);

        int successCount = 0;
        for (boolean result : results) {
            if (result) successCount++;
        }
        logger.info("Successful requests: {}/5", successCount);
        Assert.assertTrue(successCount > 0, "At least some concurrent requests should be handled");

        logTestStep("Concurrent requests test completed");
    }

    private void logTestStep(String step) {
        System.out.println("[TEST STEP] " + step);
    }

    @Test(description = "CycleFive - API Load Test with 100 concurrent users", priority = 2)
    @Description("Verify API handles 100 concurrent user load")
    @Story("CycleFive - Performance - Load Test 100 Users")
    @Severity(SeverityLevel.CRITICAL)
    public void testApiLoadTest100Users() {
        if (authToken == null || authToken.isEmpty()) {
            testAuthenticationSuccess();
        }

        logger.info("Starting API Load Test - 100 concurrent users");

        long startTime = System.currentTimeMillis();

        Thread[] threads = new Thread[100];
        boolean[] results = new boolean[100];
        int[] responseTimes = new int[100];

        for (int i = 0; i < 100; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                long threadStart = System.currentTimeMillis();
                Response response = getAuthenticatedRequest()
                        .when()
                        .get("/projects");
                long threadDuration = System.currentTimeMillis() - threadStart;
                int status = response.getStatusCode();
                results[index] = (status == 200 || status == 403 || status == 404);
                responseTimes[index] = (int) threadDuration;
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.warn("Thread interrupted: {}", e.getMessage());
            }
        }

        long totalDuration = System.currentTimeMillis() - startTime;
        int successCount = 0;
        long totalResponseTime = 0;

        for (int i = 0; i < 100; i++) {
            if (results[i]) {
                successCount++;
                totalResponseTime += responseTimes[i];
            }
        }

        double avgResponseTime = successCount > 0 ? (double) totalResponseTime / successCount : 0;
        double successRate = (double) successCount / 100 * 100;

        logger.info("Load Test 100 Users: Success Rate: {}%, Avg Response Time: {}ms, Total Duration: {}ms",
                String.format("%.2f", successRate), String.format("%.2f", avgResponseTime), totalDuration);

        Assert.assertTrue(successRate >= 80.0, "Success rate should be >= 80%");

        logTestStep("Load test 100 users completed");
    }

    @Test(description = "CycleFive - API Stress Test with 500 concurrent users", priority = 3)
    @Description("Verify API handles 500 concurrent user stress load")
    @Story("CycleFive - Performance - Stress Test 500 Users")
    @Severity(SeverityLevel.CRITICAL)
    public void testApiStressTest500Users() {
        if (authToken == null || authToken.isEmpty()) {
            testAuthenticationSuccess();
        }

        logger.info("Starting API Stress Test - 500 concurrent users");

        long startTime = System.currentTimeMillis();

        Thread[] threads = new Thread[500];
        boolean[] results = new boolean[500];
        int[] responseTimes = new int[500];

        for (int i = 0; i < 500; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                long threadStart = System.currentTimeMillis();
                Response response = getAuthenticatedRequest()
                        .when()
                        .get("/projects");
                long threadDuration = System.currentTimeMillis() - threadStart;
                int status = response.getStatusCode();
                results[index] = (status == 200 || status == 403 || status == 404);
                responseTimes[index] = (int) threadDuration;
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.warn("Thread interrupted: {}", e.getMessage());
            }
        }

        long totalDuration = System.currentTimeMillis() - startTime;
        int successCount = 0;
        long totalResponseTime = 0;

        for (int i = 0; i < 500; i++) {
            if (results[i]) {
                successCount++;
                totalResponseTime += responseTimes[i];
            }
        }

        double avgResponseTime = successCount > 0 ? (double) totalResponseTime / successCount : 0;
        double successRate = (double) successCount / 500 * 100;

        logger.info("Stress Test 500 Users: Success Rate: {}%, Avg Response Time: {}ms, Total Duration: {}ms",
                String.format("%.2f", successRate), String.format("%.2f", avgResponseTime), totalDuration);

        Assert.assertTrue(successRate >= 50.0, "Stress test success rate should be >= 50%");

        logTestStep("Stress test 500 users completed");
    }

    @Test(description = "CycleFive - API Endurance Test - 30 minutes sustained", priority = 4)
    @Description("Verify API stability under sustained load for 30 minutes")
    @Story("CycleFive - Performance - Endurance Test 30 Min")
    @Severity(SeverityLevel.CRITICAL)
    public void testApiEnduranceTest30Min() {
        if (authToken == null || authToken.isEmpty()) {
            testAuthenticationSuccess();
        }

        logger.info("Starting API Endurance Test - 30 minutes sustained");

        long startTime = System.currentTimeMillis();
        long endTime = startTime + (30 * 60 * 1000); // 30 minutes
        int totalRequests = 0;
        int successCount = 0;
        int errorCount = 0;

        // Run requests at ~10 requests/second for 30 minutes
        while (System.currentTimeMillis() < endTime) {
            long requestStart = System.currentTimeMillis();
            Response response = getAuthenticatedRequest()
                    .when()
                    .get("/projects");
            long requestDuration = System.currentTimeMillis() - requestStart;

            int status = response.getStatusCode();
            totalRequests++;

            if (status == 200 || status == 403 || status == 404) {
                successCount++;
            } else {
                errorCount++;
            }

            // Throttle to ~10 requests/second
            long sleepTime = 100 - requestDuration;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    logger.warn("Endurance test interrupted");
                    break;
                }
            }
        }

        long totalDuration = System.currentTimeMillis() - startTime;
        double successRate = totalRequests > 0 ? (double) successCount / totalRequests * 100 : 0;

        logger.info("Endurance Test 30 Min: Total Requests: {}, Success Rate: {}%, Errors: {}",
                totalRequests, String.format("%.2f", successRate), errorCount);

        Assert.assertTrue(successRate >= 90.0, "Endurance test success rate should be >= 90%");

        logTestStep("Endurance test 30 minutes completed");
    }

    @Test(description = "CycleFive - API Spike Test - sudden burst", priority = 5)
    @Description("Verify API handles sudden burst of traffic")
    @Story("CycleFive - Performance - Spike Test")
    @Severity(SeverityLevel.CRITICAL)
    public void testApiSpikeTest() {
        if (authToken == null || authToken.isEmpty()) {
            testAuthenticationSuccess();
        }

        logger.info("Starting API Spike Test - sudden burst");

        // Simulate spike: 0 to 200 concurrent in 1 second
        Thread[] threads = new Thread[200];
        boolean[] results = new boolean[200];

        long spikeStart = System.currentTimeMillis();

        for (int i = 0; i < 200; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                Response response = getAuthenticatedRequest()
                        .when()
                        .get("/projects");
                int status = response.getStatusCode();
                results[index] = (status == 200 || status == 403 || status == 404);
            });
            threads[i].start();
        }

        // Release all at once (simulate spike)
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.warn("Thread interrupted: {}", e.getMessage());
            }
        }

        long spikeDuration = System.currentTimeMillis() - spikeStart;
        int successCount = 0;
        for (boolean result : results) {
            if (result) successCount++;
        }

        double successRate = (double) successCount / 200 * 100;

        logger.info("Spike Test 200 Users: Success Rate: {}%, Duration: {}ms",
                String.format("%.2f", successRate), spikeDuration);

        Assert.assertTrue(successRate >= 70.0, "Spike test success rate should be >= 70%");

        logTestStep("Spike test completed");
    }

    // Authentication helper methods
    private void testAuthenticationSuccess() {
        java.util.Map<String, String> loginRequest = new java.util.HashMap<>();
        loginRequest.put("email", "director@cpms.demo");
        loginRequest.put("password", "ChangeMe123!");

        Response response = RestAssured.given()
                .contentType(io.restassured.http.ContentType.JSON)
                .header("apikey", "sb_publishable_0zGYs8iNRYzb-j2cq7q_eQ_9UXW-m_p")
                .body(loginRequest)
                .when()
                .post("https://zpilbrxryhixrqllqndh.supabase.co/auth/v1/token?grant_type=password")
                .then()
                .extract().response();

        authToken = response.jsonPath().getString("access_token");
        if (authToken == null) {
            throw new RuntimeException("Failed to obtain auth token");
        }
    }
}