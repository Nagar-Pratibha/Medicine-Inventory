package com.example.Meds.service;

import com.example.Meds.dto.CartItemDTO;
import com.example.Meds.dto.CartResponseDTO;
import com.example.Meds.entity.Cart;

public interface CartService {

    CartResponseDTO getCartByUserId(Integer userId);

    CartResponseDTO addItemToCart(Integer userId, CartItemDTO cartItemDTO);

    CartResponseDTO updateItemQuantity(Integer userId, Long cartItemId, int quantity);

    CartResponseDTO removeItemFromCart(Integer userId, Long cartItemId);

    void clearCart(Integer userId);
}