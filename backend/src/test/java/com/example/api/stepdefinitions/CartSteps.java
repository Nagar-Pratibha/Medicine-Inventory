package com.example.api.stepdefinitions;

import com.example.Meds.dto.CartItemDTO;
import com.example.api.context.APITestContext;
import com.example.api.utils.SpecBuilder;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class CartSteps {

    private final APITestContext context;

    public CartSteps(APITestContext context) {
        this.context = context;
    }

    @When("I add the medicine to my cart with quantity {int}")
    public void iAddTheMedicineToMyCartWithQuantity(int quantity) {
        Long medicineId = context.get(APITestContext.ContextKeys.MEDICINE_ID);
        Integer userId = context.get(APITestContext.ContextKeys.USER_ID);
        String token = context.get(APITestContext.ContextKeys.AUTH_TOKEN);

        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setMedicineId(medicineId);
        cartItemDTO.setQuantity(quantity);

        Response response = given()
                .spec(SpecBuilder.getRequestSpec(token))
                .body(cartItemDTO)
                .when()
                .post("/api/users/" + userId + "/cart/items");

        context.set(APITestContext.ContextKeys.RESPONSE, response);
    }

    @And("the cart should contain the medicine with quantity {int}")
    public void theCartShouldContainTheMedicineWithQuantity(int expectedQuantity) {
        Response response = context.getResponse();
        Long expectedMedicineId = context.get(APITestContext.ContextKeys.MEDICINE_ID);

        // Parse the list of items from the cart response
        List<Map<String, Object>> items = response.jsonPath().getList("items");
        Assert.assertNotNull(items, "Cart items list should not be null");

        // Find the specific item in the cart
        Map<String, Object> foundItem = items.stream()
                .filter(item -> ((Number) item.get("medicineId")).longValue() == expectedMedicineId)
                .findFirst()
                .orElse(null);

        Assert.assertNotNull(foundItem, "Medicine not found in cart");
        Assert.assertEquals(((Number) foundItem.get("quantity")).intValue(), expectedQuantity);

        // Save the cart item ID for later use (update/delete)
        Long cartItemId = ((Number) foundItem.get("cartItemId")).longValue();
        context.set(APITestContext.ContextKeys.CART_ITEM_ID, cartItemId);
    }

    @Given("I have added the medicine to my cart")
    public void iHaveAddedTheMedicineToMyCart() {
        // Re-use the logic to add an item with quantity 1
        iAddTheMedicineToMyCartWithQuantity(1);
        Response response = context.getResponse();
        Assert.assertEquals(response.getStatusCode(), 200);

        // Ensure we save the Cart Item ID
        theCartShouldContainTheMedicineWithQuantity(1);
    }

    @When("I update the cart item quantity to {int}")
    public void iUpdateTheCartItemQuantityTo(int newQuantity) {
        Long cartItemId = context.get(APITestContext.ContextKeys.CART_ITEM_ID);
        Integer userId = context.get(APITestContext.ContextKeys.USER_ID);
        String token = context.get(APITestContext.ContextKeys.AUTH_TOKEN);

        Response response = given()
                .spec(SpecBuilder.getRequestSpec(token))
                .queryParam("quantity", newQuantity)
                .when()
                .put("/api/users/" + userId + "/cart/items/" + cartItemId);

        context.set(APITestContext.ContextKeys.RESPONSE, response);
    }

    @When("I remove the item from the cart")
    public void iRemoveTheItemFromTheCart() {
        Long cartItemId = context.get(APITestContext.ContextKeys.CART_ITEM_ID);
        Integer userId = context.get(APITestContext.ContextKeys.USER_ID);
        String token = context.get(APITestContext.ContextKeys.AUTH_TOKEN);

        Response response = given()
                .spec(SpecBuilder.getRequestSpec(token))
                .when()
                .delete("/api/users/" + userId + "/cart/items/" + cartItemId);

        context.set(APITestContext.ContextKeys.RESPONSE, response);
    }

    @And("the cart should not contain the medicine")
    public void theCartShouldNotContainTheMedicine() {
        Response response = context.getResponse();
        Long medicineId = context.get(APITestContext.ContextKeys.MEDICINE_ID);

        List<Map<String, Object>> items = response.jsonPath().getList("items");

        if (items == null || items.isEmpty()) {
            return; // Cart is empty, so it definitely doesn't contain the medicine
        }

        boolean containsMedicine = items.stream()
                .anyMatch(item -> ((Number) item.get("medicineId")).longValue() == medicineId);

        Assert.assertFalse(containsMedicine, "Cart should not contain the removed medicine");
    }

    @And("the cart should be empty")
    public void theCartShouldBeEmpty() {
        // We need to make a fresh GET request to verify it's empty
        // because the DELETE /cart endpoint returns 204 No Content (no body).
        Integer userId = context.get(APITestContext.ContextKeys.USER_ID);
        String token = context.get(APITestContext.ContextKeys.AUTH_TOKEN);

        Response response = given()
                .spec(SpecBuilder.getRequestSpec(token))
                .when()
                .get("/api/users/" + userId + "/cart");

        List<Object> items = response.jsonPath().getList("items");
        Assert.assertTrue(items == null || items.isEmpty(), "Cart should be empty");
    }
}