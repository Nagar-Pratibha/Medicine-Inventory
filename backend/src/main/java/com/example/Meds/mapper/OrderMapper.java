package com.example.Meds.mapper;

import com.example.Meds.dto.OrderItemResponseDTO;
import com.example.Meds.dto.OrderResponseDTO;
import com.example.Meds.entity.Order;
import com.example.Meds.entity.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    @Autowired
    private AddressMapper addressMapper; // To convert the nested Address entity

    /**
     * Converts a single OrderItem entity to its response DTO.
     */
    public OrderItemResponseDTO toOrderItemResponseDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setOrderItemId(orderItem.getOrderItemId());
        dto.setQuantity(orderItem.getQuantity());
        // In your service, you would have set the priceAtPurchase on the OrderItem
        // dto.setPriceAtPurchase(orderItem.getPriceAtPurchase());
        dto.setPriceAtPurchase(orderItem.getMedicine().getPrice());
        if (orderItem.getMedicine() != null) {
            dto.setPriceAtPurchase(orderItem.getMedicine().getPrice());
            dto.setMedicineName(orderItem.getMedicine().getName());
        }

        return dto;
    }

    /**
     * Converts an Order entity and its list of items into a comprehensive response DTO.
     */
    public OrderResponseDTO toOrderResponseDTO(Order order, List<OrderItem> orderItems) {
        if (order == null) {
            return null;
        }

        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getOrderId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());

        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
        }

        // Use the AddressMapper to convert the nested Address entity
        if (order.getAddress() != null) {
            dto.setAddress(addressMapper.toAddressResponseDTO(order.getAddress()));
        }

        // Convert the list of OrderItem entities to DTOs
        List<OrderItemResponseDTO> itemDTOs = orderItems.stream()
                .map(this::toOrderItemResponseDTO)
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);

        return dto;
    }
}