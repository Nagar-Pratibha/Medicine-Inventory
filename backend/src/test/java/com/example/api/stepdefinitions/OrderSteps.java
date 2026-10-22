package com.example.api.stepdefinitions;

import com.example.Meds.dto.OrderRequestDTO;
import com.example.api.context.APITestContext;
import com.example.api.utils.SpecBuilder;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderSteps {

    private final APITestContext context;
    private final CartSteps cartSteps;

    public OrderSteps(APITestContext context) {
        this.context = context;
        this.cartSteps = new CartSteps(context);
    }

    @When("I place an order with my address")
    public void iPlaceAnOrderWithMyAddress() {
        Long addressId = context.get(APITestContext.ContextKeys.ADDRESS_ID);
        Integer userId = context.get(APITestContext.ContextKeys.USER_ID);
        String token = context.get(APITestContext.ContextKeys.AUTH_TOKEN);

        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setUserId(userId);
        orderRequest.setAddressId(addressId);

        Response response = given()
                .spec(SpecBuilder.getRequestSpec(token))
                .body(orderRequest)
                .when()
                .post("/api/users/" + userId + "/orders");

        context.set(APITestContext.ContextKeys.RESPONSE, response);
    }

    @And("the response body should contain the order details with status {string}")
    public void theResponseBodyShouldContainTheOrderDetailsWithStatus(String expectedStatus) {
        Response response = context.getResponse();
        // --- THIS IS THE FIX ---
        // Changed "orderStatus" to "status" to match OrderResponseDTO
        Assert.assertEquals(response.jsonPath().getString("status"), expectedStatus);
        // --- END OF FIX ---
        Assert.assertNotNull(response.jsonPath().get("orderId"), "Order ID should not be null");
        List<Object> items = response.jsonPath().getList("items"); // Also ensure this matches DTO (it was 'items' in your DTO, 'orderItems' in my previous guess)
        Assert.assertFalse(items == null || items.isEmpty(), "Order should have items");
    }

    @And("I save the order ID")
    public void iSaveTheOrderID() {
        Response response = context.getResponse();
        Long orderId = response.jsonPath().getLong("orderId");
        context.set(APITestContext.ContextKeys.ORDER_ID, orderId);
    }

    @Given("I have placed an order")
    public void iHavePlacedAnOrder() {
        cartSteps.iHaveAddedTheMedicineToMyCart();
        iPlaceAnOrderWithMyAddress();

        Response response = context.getResponse();
        Assert.assertEquals(response.getStatusCode(), 201, "Failed to place background order");
        iSaveTheOrderID();
    }

    @And("the response body should be a list containing the placed order")
    public void theResponseBodyShouldBeAListContainingThePlacedOrder() {
        Response response = context.getResponse();
        Long expectedOrderId = context.get(APITestContext.ContextKeys.ORDER_ID);

        List<Map<String, Object>> orders = response.jsonPath().getList("$");
        boolean found = orders.stream()
                .anyMatch(order -> ((Number) order.get("orderId")).longValue() == expectedOrderId);

        Assert.assertTrue(found, "Placed order not found in user's order list");
    }

    @And("the response body should contain the user's order")
    public void theResponseBodyShouldContainTheUserSOrder() {
        theResponseBodyShouldBeAListContainingThePlacedOrder();
    }

    @When("I update the order status to {string}")
    public void iUpdateTheOrderStatusTo(String newStatus) {
        Long orderId = context.get(APITestContext.ContextKeys.ORDER_ID);
        Integer userId = context.get(APITestContext.ContextKeys.USER_ID);
        String token = context.get(APITestContext.ContextKeys.AUTH_TOKEN);

        Response response = given()
                .spec(SpecBuilder.getRequestSpec(token))
                .queryParam("status", newStatus)
                .when()
                .put("/api/users/" + userId + "/orders/" + orderId + "/status");

        context.set(APITestContext.ContextKeys.RESPONSE, response);
    }

    @And("the order status should be {string}")
    public void theOrderStatusShouldBe(String expectedStatus) {
        Response response = context.getResponse();
        // --- THIS IS THE FIX HERE TOO ---
        Assert.assertEquals(response.jsonPath().getString("status"), expectedStatus);
        // --- END OF FIX ---
    }
}