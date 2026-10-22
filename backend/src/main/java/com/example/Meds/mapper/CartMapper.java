package com.example.Meds.mapper;

import com.example.Meds.dto.CartItemResponseDTO;
import com.example.Meds.dto.CartResponseDTO;
import com.example.Meds.entity.Cart;
import com.example.Meds.entity.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    /**
     * Converts a single CartItem entity to its corresponding response DTO.
     * It extracts information from the nested Medicine entity.
     */
    public CartItemResponseDTO toCartItemResponseDTO(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }

        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setCartItemId(cartItem.getCartItemId());
        dto.setQuantity(cartItem.getQuantity());

        if (cartItem.getMedicine() != null) {
            dto.setMedicineId(cartItem.getMedicine().getId());
            dto.setMedicineName(cartItem.getMedicine().getName());
            dto.setPrice(cartItem.getMedicine().getPrice());
            // Calculate subtotal
            BigDecimal subtotal = cartItem.getMedicine().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            dto.setSubtotal(subtotal);
        }

        return dto;
    }

    /**
     * Converts a Cart entity and its list of items into a comprehensive response DTO.
     */
    public CartResponseDTO toCartResponseDTO(Cart cart, List<CartItem> cartItems) {
        if (cart == null) {
            return null;
        }

        CartResponseDTO dto = new CartResponseDTO();
        dto.setCartId(cart.getCartId());

        if (cart.getUser() != null) {
            dto.setUserId(cart.getUser().getId());
        }

        // Convert the list of CartItem entities to DTOs
        List<CartItemResponseDTO> itemDTOs = cartItems.stream()
                .map(this::toCartItemResponseDTO)
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);

        // Calculate the total amount for the entire cart
        BigDecimal totalAmount = itemDTOs.stream()
                .map(CartItemResponseDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalAmount(totalAmount);

        return dto;
    }
}