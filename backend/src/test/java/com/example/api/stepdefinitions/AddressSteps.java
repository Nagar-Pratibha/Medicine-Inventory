package com.example.api.stepdefinitions;

import com.example.Meds.dto.AddressDTO;
import com.example.api.context.APITestContext;
import com.example.api.utils.SpecBuilder;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import java.util.List;

import static io.restassured.RestAssured.given;

public class AddressSteps {

    private final APITestContext context;
    private AddressDTO testAddress;

    public AddressSteps(APITestContext context) {
        this.context = context;
    }

    @Given("I prepare a new address request")
    public void iPrepareANewAddressRequest() {
        testAddress = new AddressDTO();
        testAddress.setLine1("123 Test Street");
        testAddress.setCity("Testville");
        testAddress.setState("TS");
        testAddress.setPostalCode("12345");
        testAddress.setCountry("Testland");
        testAddress.setDefault(false);

        String token = context.get(APITestContext.ContextKeys.AUTH_TOKEN);
        RequestSpecification request = given()
                .spec(SpecBuilder.getRequestSpec(token))
                .body(testAddress);

        context.set(APITestContext.ContextKeys.REQUEST, request);
    }

    @And("the response body should contain the new address details")
    public void theResponseBodyShouldContainTheNewAddressDetails() {
        Response response = context.getResponse();
        Assert.assertEquals(response.jsonPath().getString("line1"), testAddress.getLine1());
        Assert.assertEquals(response.jsonPath().getString("city"), testAddress.getCity());
    }

    @And("I save the address ID")
    public void iSaveTheAddressID() {
        Response response = context.getResponse();
        // --- THIS IS THE FIX ---
        // Changed "id" to "addressId" to match your DTO
        Long addressId = response.jsonPath().getLong("addressId");
        // --- END OF FIX ---
        context.set(APITestContext.ContextKeys.ADDRESS_ID, addressId);
    }

    @Given("I have added an address")
    public void iHaveAddedAnAddress() {
        String token = context.get(APITestContext.ContextKeys.AUTH_TOKEN);
        Integer userId = context.get(APITestContext.ContextKeys.USER_ID);

        AddressDTO address = new AddressDTO();
        address.setLine1("456 Pre-made Ave");
        address.setCity("Getcity");
        address.setState("GS");
        address.setPostalCode("54321");
        address.setCountry("Getland");

        Response response = given()
                .spec(SpecBuilder.getRequestSpec(token))
                .body(address)
                .when()
                .post("/api/users/" + userId + "/addresses");

        Assert.assertEquals(response.getStatusCode(), 201);
        // --- THIS IS THE FIX HERE TOO ---
        Long addressId = response.jsonPath().getLong("addressId");
        // --- END OF FIX ---
        context.set(APITestContext.ContextKeys.ADDRESS_ID, addressId);
    }

    @And("the response body should be a list containing at least one address")
    public void theResponseBodyShouldBeAListContainingAtLeastOneAddress() {
        Response response = context.getResponse();
        List<Object> addressList = response.jsonPath().getList("$");
        Assert.assertFalse(addressList.isEmpty(), "Address list should not be empty");
    }

    @And("the response body should show the address is default")
    public void theResponseBodyShouldShowTheAddressIsDefault() {
        Response response = context.getResponse();
        // Note: Your DTO has 'isDefault' boolean, but Jackson often serializes
        // boolean getters (isDefault()) to just the name without 'is' (default).
        // If this fails, try checking "default" instead of "defaultAddress".
        // Based on your DTO, it might actually be serialized as "default".
        // Let's try "default" first as it matches the field name more closely if lombok handles it standardly.
        // Actually, standard Java Bean convention for 'boolean isDefault' is 'default' in JSON.
        // But your DTO has 'private boolean isDefault;', so Lombok might generate 'isDefault()' getter.
        // Jackson standardly serializes this as 'default'.
        // Let's stick with what likely works, or check your actual JSON response if it fails.

        // Given your DTO: private boolean isDefault;
        // It will likely be serialized as "default" in standard Jackson.
        // If you want to be sure, you can check the actual JSON output in the logs.

        // Let's try 'default' first, as 'defaultAddress' was a guess.
//        System.out.println(response.jsonPath());
        boolean isDefault = response.jsonPath().getBoolean("default");
        Assert.assertTrue(isDefault, "Address should be marked as default");
    }
}