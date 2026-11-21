package com.cpms.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Test Data Factory for CPMS tests.
 * Provides builder pattern for creating test data objects.
 * Follows the Factory pattern for maintainable, reusable test data.
 */
public class ProjectTestDataFactory {

    private static final Random random = new Random();
    private static int projectCounter = 0;

    // Project data builder
    public static class ProjectBuilder {
        private String code;
        private String name;
        private String client;
        private double contractValue;
        private String latitude;
        private String longitude;
        private String status = "Active";
        private String description;

        public ProjectBuilder() {
            // Generate unique defaults
            this.code = "TEST-" + String.format("%03d", ++projectCounter);
            this.name = "Test Project " + code;
            this.client = "Test Client " + random.nextInt(1000);
            this.contractValue = 1000000 + random.nextInt(5000000);
            this.latitude = "-26.2041"; // Default JHB
            this.longitude = "28.0473";
            this.description = "Auto-generated test project";
        }

        public ProjectBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public ProjectBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ProjectBuilder withClient(String client) {
            this.client = client;
            return this;
        }

        public ProjectBuilder withContractValue(double value) {
            this.contractValue = value;
            return this;
        }

        public ProjectBuilder withLocation(String lat, String lon) {
            this.latitude = lat;
            this.longitude = lon;
            return this;
        }

        public ProjectBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public ProjectBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Project build() {
            return new Project(code, name, client, contractValue, latitude, longitude, status, description);
        }

        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("code", code);
            map.put("name", name);
            map.put("client", client);
            map.put("contractValue", contractValue);
            map.put("latitude", latitude);
            map.put("longitude", longitude);
            map.put("status", status);
            map.put("description", description);
            return map;
        }
    }

    // Project data class
    public static class Project {
        public final String code;
        public final String name;
        public final String client;
        public final double contractValue;
        public final String latitude;
        public final String longitude;
        public final String status;
        public final String description;

        public Project(String code, String name, String client, double contractValue,
                       String latitude, String longitude, String status, String description) {
            this.code = code;
            this.name = name;
            this.client = client;
            this.contractValue = contractValue;
            this.latitude = latitude;
            this.longitude = longitude;
            this.status = status;
            this.description = description;
        }
    }

    // User data builder
    public static class UserBuilder {
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String role = "ProjectManager";
        private boolean active = true;

        public UserBuilder() {
            int id = random.nextInt(10000);
            this.email = "test.user" + id + "@cpms.demo";
            this.password = "TestPass123!";
            this.firstName = "Test";
            this.lastName = "User" + id;
        }

        public UserBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder withName(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
            return this;
        }

        public UserBuilder withRole(String role) {
            this.role = role;
            return this;
        }

        public UserBuilder withActive(boolean active) {
            this.active = active;
            return this;
        }

        public User build() {
            return new User(email, password, firstName, lastName, role, active);
        }
    }

    // User data class
    public static class User {
        public final String email;
        public final String password;
        public final String firstName;
        public final String lastName;
        public final String role;
        public final boolean active;

        public User(String email, String password, String firstName, String lastName, 
                    String role, boolean active) {
            this.email = email;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
            this.active = active;
        }
    }

    // BOQ Item builder
    public static class BoqItemBuilder {
        private String itemCode;
        private String description;
        private String unit;
        private double quantity;
        private double unitRate;
        private String category;

        public BoqItemBuilder() {
            int id = random.nextInt(10000);
            this.itemCode = "BOQ-" + String.format("%04d", id);
            this.description = "Test BOQ Item " + id;
            this.unit = "m3";
            this.quantity = 100 + random.nextInt(900);
            this.unitRate = 50 + random.nextInt(200);
            this.category = "Earthworks";
        }

        public BoqItemBuilder withItemCode(String code) {
            this.itemCode = code;
            return this;
        }

        public BoqItemBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public BoqItemBuilder withUnit(String unit) {
            this.unit = unit;
            return this;
        }

        public BoqItemBuilder withQuantity(double quantity) {
            this.quantity = quantity;
            return this;
        }

        public BoqItemBuilder withUnitRate(double rate) {
            this.unitRate = rate;
            return this;
        }

        public BoqItemBuilder withCategory(String category) {
            this.category = category;
            return this;
        }

        public BoqItem build() {
            return new BoqItem(itemCode, description, unit, quantity, unitRate, category);
        }

        public String toCsvRow() {
            return String.join(",", itemCode, description, unit, 
                String.valueOf(quantity), String.valueOf(unitRate), category);
        }
    }

    // BOQ Item data class
    public static class BoqItem {
        public final String itemCode;
        public final String description;
        public final String unit;
        public final double quantity;
        public final double unitRate;
        public final String category;

        public BoqItem(String itemCode, String description, String unit, 
                       double quantity, double unitRate, String category) {
            this.itemCode = itemCode;
            this.description = description;
            this.unit = unit;
            this.quantity = quantity;
            this.unitRate = unitRate;
            this.category = category;
        }
    }

    // Factory methods for common test scenarios

    /**
     * Create a valid project for testing.
     */
    public static ProjectBuilder validProject() {
        return new ProjectBuilder();
    }

    /**
     * Create a project with minimal required fields.
     */
    public static ProjectBuilder minimalProject() {
        return new ProjectBuilder()
            .withCode("MIN-" + System.currentTimeMillis())
            .withName("Minimal Project")
            .withClient("Minimal Client")
            .withContractValue(100000);
    }

    /**
     * Create a project with Johannesburg location.
     */
    public static ProjectBuilder jhbProject() {
        return new ProjectBuilder()
            .withCode("JHB-" + System.currentTimeMillis())
            .withLocation("-26.2041", "28.0473");
    }

    /**
     * Create a project with Cape Town location.
     */
    public static ProjectBuilder cptProject() {
        return new ProjectBuilder()
            .withCode("CPT-" + System.currentTimeMillis())
            .withLocation("-33.9249", "18.4241");
    }

    /**
     * Create multiple projects for batch testing.
     */
    public static List<Project> createProjectBatch(int count) {
        List<Project> projects = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            projects.add(new ProjectBuilder()
                .withCode("BATCH-" + String.format("%03d", i))
                .withName("Batch Project " + i)
                .withClient("Batch Client " + i)
                .withContractValue(1000000 + i * 100000)
                .build());
        }
        return projects;
    }

    /**
     * Create BOQ items for testing.
     */
    public static List<BoqItem> createBoqItems(int count) {
        List<BoqItem> items = new ArrayList<>();
        String[] units = {"m3", "m2", "t", "kg", "no", "m", "hr"};
        String[] categories = {"Earthworks", "Concrete", "Steel", "Finishes", "MEP", "Preliminaries"};
        
        for (int i = 0; i < count; i++) {
            items.add(new BoqItemBuilder()
                .withItemCode("BOQ-" + String.format("%04d", i))
                .withDescription("Item " + i + " - " + categories[i % categories.length])
                .withUnit(units[i % units.length])
                .withQuantity(10 + i * 5)
                .withUnitRate(20 + i * 10)
                .withCategory(categories[i % categories.length])
                .build());
        }
        return items;
    }

    /**
     * Generate CSV content for BOQ import testing.
     */
    public static String generateBoqCsv(List<BoqItem> items) {
        StringBuilder csv = new StringBuilder();
        csv.append("item code,description,unit,quantity,unit rate,category\n");
        for (BoqItem item : items) {
            csv.append(item.itemCode).append(",")
               .append(item.description).append(",")
               .append(item.unit).append(",")
               .append(item.quantity).append(",")
               .append(item.unitRate).append(",")
               .append(item.category).append("\n");
        }
        return csv.toString();
    }

    /**
     * Get default director credentials.
     */
    public static UserBuilder director() {
        return new UserBuilder()
            .withEmail("director@cpms.demo")
            .withPassword("ChangeMe123!")
            .withName("Director", "User")
            .withRole("Director");
    }

    /**
     * Get default PM credentials.
     */
    public static UserBuilder projectManager() {
        return new UserBuilder()
            .withEmail("pm@cpms.demo")
            .withPassword("ChangeMe123!")
            .withName("Project", "Manager")
            .withRole("ProjectManager");
    }

    /**
     * Get default QS credentials.
     */
    public static UserBuilder quantitySurveyor() {
        return new UserBuilder()
            .withEmail("qs@cpms.demo")
            .withPassword("ChangeMe123!")
            .withName("Quantity", "Surveyor")
            .withRole("QuantitySurveyor");
    }

    /**
     * Get default Client credentials.
     */
    public static UserBuilder client() {
        return new UserBuilder()
            .withEmail("client@cpms.demo")
            .withPassword("ChangeMe123!")
            .withName("Client", "User")
            .withRole("Client");
    }

    /**
     * Get default Foreman credentials.
     */
    public static UserBuilder foreman() {
        return new UserBuilder()
            .withEmail("foreman@cps.demo")
            .withPassword("ChangeMe123!")
            .withName("Foreman", "User")
            .withRole("Foreman");
    }

    /**
     * Create invalid project data for negative testing.
     */
    public static ProjectBuilder invalidProjectMissingCode() {
        return new ProjectBuilder()
            .withCode("")  // Invalid - empty code
            .withName("Invalid Project")
            .withClient("Test Client")
            .withContractValue(100000);
    }

    public static ProjectBuilder invalidProjectMissingName() {
        return new ProjectBuilder()
            .withCode("INV-001")
            .withName("")  // Invalid - empty name
            .withClient("Test Client")
            .withContractValue(100000);
    }

    public static ProjectBuilder invalidProjectNegativeValue() {
        return new ProjectBuilder()
            .withCode("INV-002")
            .withName("Negative Value Project")
            .withClient("Test Client")
            .withContractValue(-1000);  // Invalid - negative value
    }
}
