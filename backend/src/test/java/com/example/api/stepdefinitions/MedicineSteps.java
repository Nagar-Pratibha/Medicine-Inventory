package com.example.api.stepdefinitions;

import com.example.Meds.dto.MedicineDTO;
import com.example.api.context.APITestContext;
import com.example.api.utils.SpecBuilder;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;

public class MedicineSteps {

    private final APITestContext context;

    public MedicineSteps(APITestContext context) {
        this.context = context;
    }

    /**
     * NOTE: For this step to pass, your application database MUST
     * have an admin user with these credentials upon startup.
     * e.g., in a data.sql file
     */
    @Given("I am logged in as an admin")
    public void iAmLoggedInAsAnAdmin() {
        // --- ADMIN LOGIN ---
        // These credentials must exist in your database and have Role.ADMIN
        String adminEmail = "admin@meds.com";
        String adminPassword = "AdminPass123!";

        String loginBody = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", adminEmail, adminPassword);

        Response loginResponse = given()
                .spec(SpecBuilder.getRequestSpec())
                .body(loginBody)
                .when()
                .post("/api/auth/login");

        // Fail the test if admin login doesn't work
        if (loginResponse.getStatusCode() != 200) {
            Assert.fail("Could not log in as admin. Ensure user 'admin@example.com' exists with Role.ADMIN.");
        }

        // --- Save to Context ---
        String token = loginResponse.jsonPath().getString("token");
        Integer userId = loginResponse.jsonPath().getInt("user.id");

        context.set(APITestContext.ContextKeys.AUTH_TOKEN, token);
        context.set(APITestContext.ContextKeys.USER_ID, userId);
    }

    @Given("I prepare a new medicine request")
    public void iPrepareANewMedicineRequest() {
        MedicineDTO medicineDTO = new MedicineDTO(
                "TestMed" + System.nanoTime(),
                "Test Description",
                new BigDecimal("19.99"),
                100,
                "Test Pharma",
                "10 pack",
                "Test Composition"
        );

        String token = context.get(APITestContext.ContextKeys.AUTH_TOKEN);
        RequestSpecification request = given()
                .spec(SpecBuilder.getRequestSpec(token))
                .body(medicineDTO);

        context.set(APITestContext.ContextKeys.REQUEST, request);
    }

    @And("the response body should contain a forbidden error message")
    public void theResponseBodyShouldContainAForbiddenErrorMessage() {
        Response response = context.getResponse();
        String error = response.jsonPath().getString("error");
        Assert.assertEquals(error, "Forbidden");
    }

    @And("I save the medicine's ID")
    public void iSaveTheMedicineSID() {
        Response response = context.getResponse();
        Long medicineId = response.jsonPath().getLong("id");
        context.set(APITestContext.ContextKeys.MEDICINE_ID, medicineId);
    }

//    @Given("I have created a medicine")
//    public void iHaveCreatedAMedicine() {
//        // This helper step creates a medicine using the admin token
//        MedicineDTO medicineDTO = new MedicineDTO(
//                "Pre-made Med" + System.nanoTime(),
//                "Pre-made Desc",
//                new BigDecimal("10.00"),
//                50,
//                "Test Pharma",
//                "5 pack",
//                "Test Comp"
//        );
//        String token = context.get(APITestContext.ContextKeys.AUTH_TOKEN);
//
//        Response response = given()
//                .spec(SpecBuilder.getRequestSpec(token))
//                .body(medicineDTO)
//                .when()
//                .post("/api/medicines");
//
//        Assert.assertEquals(response.getStatusCode(), 201);
//        Long medicineId = response.jsonPath().getLong("id");
//        context.set(APITestContext.ContextKeys.MEDICINE_ID, medicineId);
//    }

    @Given("I have created a medicine")
    public void iHaveCreatedAMedicine() {
        // 1. Save the current USER token so we can restore it later
        String userToken = context.get(APITestContext.ContextKeys.AUTH_TOKEN);

        // 2. Log in as ADMIN to get an admin token
        // (Make sure these credentials match your data.sql)
        String adminEmail = "admin@meds.com";
        String adminPassword = "AdminPass123!";

        String adminLoginBody = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", adminEmail, adminPassword);

        Response adminLoginResponse = given()
                .spec(SpecBuilder.getRequestSpec())
                .body(adminLoginBody)
                .when()
                .post("/api/auth/login");

        Assert.assertEquals(adminLoginResponse.getStatusCode(), 200, "Admin login failed during setup");
        String adminToken = adminLoginResponse.jsonPath().getString("token");

        // 3. Create the medicine using the ADMIN token
        MedicineDTO medicineDTO = new MedicineDTO(
                "Cart Test Med " + System.nanoTime(),
                "Desc",
                new BigDecimal("10.00"),
                100,
                "Pharma",
                "Pack",
                "Comp"
        );

        Response createResponse = given()
                .spec(SpecBuilder.getRequestSpec(adminToken)) // Use admin token here
                .body(medicineDTO)
                .when()
                .post("/api/medicines");

        Assert.assertEquals(createResponse.getStatusCode(), 201, "Admin failed to create medicine");
        Long medicineId = createResponse.jsonPath().getLong("id");
        context.set(APITestContext.ContextKeys.MEDICINE_ID, medicineId);

        // 4. Restore the USER token to the context so subsequent steps run as the user
        context.set(APITestContext.ContextKeys.AUTH_TOKEN, userToken);
    }

    @And("I prepare an update for that medicine")
    public void iPrepareAnUpdateForThatMedicine() {
        MedicineDTO updateDTO = new MedicineDTO(
                "Updated Med" + System.nanoTime(),
                "Updated Desc",
                new BigDecimal("99.99"),
                1,
                "Updated Pharma",
                "1 pack",
                "Updated Comp"
        );

        String token = context.get(APITestContext.ContextKeys.AUTH_TOKEN);
        RequestSpecification request = given()
                .spec(SpecBuilder.getRequestSpec(token))
                .body(updateDTO);

        context.set(APITestContext.ContextKeys.REQUEST, request);
    }

}