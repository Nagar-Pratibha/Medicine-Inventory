package com.example.api.stepdefinitions;

import com.example.Meds.dto.LoginRequestDTO;
import com.example.Meds.dto.UserDTO;
import com.example.api.context.APITestContext;
import com.example.api.utils.SpecBuilder;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import static io.restassured.RestAssured.given;

public class AuthSteps {

    private final APITestContext context;

    // 1. REMOVE the instance variables
    // private String userEmail;
    // private String userPassword;

    public AuthSteps(APITestContext context) {
        this.context = context;
    }

    // --- Steps for Registration ---

    @Given("I prepare a new user registration request")
    public void iPrepareANewUserRegistrationRequest() {
        // 2. Use nanoTime for better uniqueness and save to context
        String email = "testuser" + System.nanoTime() + "@example.com";
        String password = "Password123";

        context.set(APITestContext.ContextKeys.USER_EMAIL, email);
        context.set(APITestContext.ContextKeys.USER_PASSWORD, password);

        UserDTO userDTO = new UserDTO();
        userDTO.setName("Test User");
        userDTO.setEmail(email);
        userDTO.setPassword(password);

        RequestSpecification request = given()
                .spec(SpecBuilder.getRequestSpec())
                .body(userDTO);

        context.set(APITestContext.ContextKeys.REQUEST, request);
    }


    @And("the response body should contain the user's details")
    public void theResponseBodyShouldContainTheUserSDetails() {
        Response response = context.getResponse();
        // 3. Get the email from the context
        String expectedEmail = context.get(APITestContext.ContextKeys.USER_EMAIL);
        String actualEmail = response.jsonPath().getString("email");

        Assert.assertEquals(actualEmail, expectedEmail);
        Assert.assertNotNull(response.jsonPath().getString("id"));
    }

    @And("I save the user's ID")
    public void iSaveTheUserSID() {
        Response response = context.getResponse();
        Integer userId = response.jsonPath().getInt("id");
        context.set(APITestContext.ContextKeys.USER_ID, userId);
    }

    // --- Steps for Login ---

    @Given("I am a registered user")
    public void iAmARegisteredUser() {
        // 4. Use nanoTime and save to context
        String email = "loginuser" + System.nanoTime() + "@example.com";
        String password = "Password123";

        context.set(APITestContext.ContextKeys.USER_EMAIL, email);
        context.set(APITestContext.ContextKeys.USER_PASSWORD, password);

        UserDTO userDTO = new UserDTO("Login User", email, password);

        // Perform the registration call
        given().spec(SpecBuilder.getRequestSpec())
                .body(userDTO)
                .when()
                .post("/api/users/register")
                .then()
                .statusCode(201);
    }

    @When("I prepare a login request with my credentials")
    public void iPrepareALoginRequestWithMyCredentials() {
        // 5. Get credentials from the context
        String email = context.get(APITestContext.ContextKeys.USER_EMAIL);
        String password = context.get(APITestContext.ContextKeys.USER_PASSWORD);

        LoginRequestDTO loginDTO = new LoginRequestDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword(password);

        RequestSpecification request = given()
                .spec(SpecBuilder.getRequestSpec())
                .body(loginDTO);

        context.set(APITestContext.ContextKeys.REQUEST, request);
    }

    @When("I prepare a login request with an incorrect password")
    public void iPrepareALoginRequestWithAnIncorrectPassword() {
        // 6. Get email from the context
        String email = context.get(APITestContext.ContextKeys.USER_EMAIL);

        LoginRequestDTO loginDTO = new LoginRequestDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword("wrongPassword!");

        RequestSpecification request = given()
                .spec(SpecBuilder.getRequestSpec())
                .body(loginDTO);

        context.set(APITestContext.ContextKeys.REQUEST, request);
    }

    @And("the response body should contain an auth token")
    public void theResponseBodyShouldContainAnAuthToken() {
        Response response = context.getResponse();
        String token = response.jsonPath().getString("token");
        Assert.assertNotNull(token);
        Assert.assertFalse(token.isEmpty(), "Token should not be empty");
    }

    @And("I save the auth token and user ID for subsequent requests")
    public void iSaveTheAuthTokenAndUserIDForSubsequentRequests() {
        Response response = context.getResponse();
        String token = response.jsonPath().getString("token");
        Integer userId = response.jsonPath().getInt("user.id");

        context.set(APITestContext.ContextKeys.AUTH_TOKEN, token);
        context.set(APITestContext.ContextKeys.USER_ID, userId);
    }
}