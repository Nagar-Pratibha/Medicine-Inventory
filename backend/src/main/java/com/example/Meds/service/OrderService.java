package com.example.Meds.service;

import com.example.Meds.entity.Order;
import com.example.Meds.entity.OrderStatus;

import java.util.List;

public interface OrderService {
    Order placeOrder(Integer userId, Long addressId);
    List<Order> getOrdersByUserId(Integer userId);
    Order updateOrderStatus(Long orderId, OrderStatus status);
    Order getOrderById(Long orderId);

    List<Order> getAllOrders();
}