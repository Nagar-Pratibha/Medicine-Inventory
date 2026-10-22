package com.example.api.stepdefinitions;

import com.example.Meds.dto.UserDTO;
import com.example.api.context.APITestContext;
import com.example.api.utils.SpecBuilder;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import static io.restassured.RestAssured.given;

public class UserSteps {

    private final APITestContext context;
    private String newName;

    public UserSteps(APITestContext context) {
        this.context = context;
    }

    /*
     * This is a shared step that can be used by any feature.
     * It performs both registration and login to get a valid token and user ID.
     */
    @Given("I am a logged-in user")
    public void iAmALoggedInUser() {
        // --- Register ---
        String email = "liveuser" + System.nanoTime() + "@example.com";
        String password = "Password123";
        UserDTO userDTO = new UserDTO("Live User", email, password);

        Response regResponse = given()
                .spec(SpecBuilder.getRequestSpec())
                .body(userDTO)
                .when()
                .post("/api/users/register");

        Assert.assertEquals(regResponse.getStatusCode(), 201);
        // We no longer need the ID from here
        // Integer userId = regResponse.jsonPath().getInt("id");

        // --- Login ---
        String loginBody = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);

        Response loginResponse = given()
                .spec(SpecBuilder.getRequestSpec())
                .body(loginBody)
                .when()
                .post("/api/auth/login");

        Assert.assertEquals(loginResponse.getStatusCode(), 200);

        // --- Save to Context ---
        String token = loginResponse.jsonPath().getString("token");

        // --- THIS IS THE FIX ---
        // Get the userId from the login response's nested 'user' object
        Integer userId = loginResponse.jsonPath().getInt("user.id");
        // --- END OF FIX ---

        context.set(APITestContext.ContextKeys.AUTH_TOKEN, token);
        context.set(APITestContext.ContextKeys.USER_ID, userId);
        context.set(APITestContext.ContextKeys.USER_EMAIL, email);
    }

//    @When("I send a GET request to {string}")
//    public void iSendAGETRequestTo(String endpoint) {
//        String token = context.get(APITestContext.ContextKeys.AUTH_TOKEN);
//        Integer userId = context.get(APITestContext.ContextKeys.USER_ID);
//
//        RequestSpecification request = given()
//                .spec(SpecBuilder.getRequestSpec(token)) // Use the spec with the auth token
//                .pathParam("USER_ID", userId); // Set the path parameter
//
//        Response response = request.when().get(endpoint);
//        context.set(APITestContext.ContextKeys.RESPONSE, response);
//    }

    @And("the response body should contain my user details")
    public void theResponseBodyShouldContainMyUserDetails() {
        Response response = context.getResponse();
        String expectedEmail = context.get(APITestContext.ContextKeys.USER_EMAIL);
        String actualEmail = response.jsonPath().getString("email");
        Integer expectedId = context.get(APITestContext.ContextKeys.USER_ID);
        Integer actualId = response.jsonPath().getInt("id");

        Assert.assertEquals(actualEmail, expectedEmail);
        Assert.assertEquals(actualId, expectedId);
    }

    @Given("I prepare an update request with a new name")
    public void iPrepareAnUpdateRequestWithANewName() {
        String token = context.get(APITestContext.ContextKeys.AUTH_TOKEN);
        this.newName = "Updated Name" + System.nanoTime();

        // Re-using UserDTO, but now we make it valid.
        UserDTO partialUpdateDTO = new UserDTO();
        partialUpdateDTO.setName(this.newName);

        // --- THIS IS THE FIX ---
        // Get the email from the context and add it to the DTO
        // to pass the @NotBlank validation.
        String email = context.get(APITestContext.ContextKeys.USER_EMAIL);
        partialUpdateDTO.setEmail(email);

        // We can leave password as null because it is not @NotBlank
        // (validation for @Size is only triggered if not null).
        // --- END OF FIX ---

        RequestSpecification request = given()
                .spec(SpecBuilder.getRequestSpec(token))
                .body(partialUpdateDTO);

        context.set(APITestContext.ContextKeys.REQUEST, request);
    }

//    @When("I send a PUT request to {string}")
//    public void iSendAPUTRequestTo(String endpoint) {
//        RequestSpecification request = context.getRequest();
//        Integer userId = context.get(APITestContext.ContextKeys.USER_ID);
//
//        Response response = request
//                .pathParam("USER_ID", userId)
//                .when()
//                .put(endpoint);
//
//        context.set(APITestContext.ContextKeys.RESPONSE, response);
//    }

    @And("the response body should contain the updated name")
    public void theResponseBodyShouldContainTheUpdatedName() {
        Response response = context.getResponse();
        String actualName = response.jsonPath().getString("name");
        Assert.assertEquals(actualName, this.newName);
    }

    @When("I send a GET request without an auth token to {string}")
    public void iSendAGETRequestWithoutAnAuthTokenTo(String endpoint) {
        Integer userId = context.get(APITestContext.ContextKeys.USER_ID);

        RequestSpecification request = given()
                .spec(SpecBuilder.getRequestSpec()) // Note: Using the spec *without* a token
                .pathParam("USER_ID", userId);

        // We expect a 401, so we tell REST Assured not to throw an exception
        Response response = request.when().get(endpoint);
        context.set(APITestContext.ContextKeys.RESPONSE, response);
    }
}