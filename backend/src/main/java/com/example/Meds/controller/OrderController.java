package com.example.Meds.controller;

import com.example.Meds.dto.OrderRequestDTO;
import com.example.Meds.dto.OrderResponseDTO;
import com.example.Meds.entity.Medicine;
import com.example.Meds.entity.Order;
import com.example.Meds.entity.OrderItem;
import com.example.Meds.entity.OrderStatus;
import com.example.Meds.mapper.OrderMapper;
import com.example.Meds.exception.ForbiddenException;
import com.example.Meds.repository.OrderItemRepository;
import com.example.Meds.security.OwnershipValidator;
import com.example.Meds.service.MedicineService;
import com.example.Meds.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
// The URL is now more RESTful, nesting orders under a specific user
@RequestMapping("/api/users/{userId}/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final OrderItemRepository orderItemRepository;
    private final MedicineService medicineService;
    private final OwnershipValidator ownershipValidator;

    @Autowired
    public OrderController(OrderService orderService, OrderMapper orderMapper, OrderItemRepository orderItemRepository,
                           MedicineService medicineService, OwnershipValidator ownershipValidator) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.orderItemRepository = orderItemRepository;
        this.medicineService = medicineService;
        this.ownershipValidator = ownershipValidator;
    }

    /**
     * Places a new order for the specified user.
     * The addressId is sent in the request body for security.
     */
    @PostMapping
    public ResponseEntity<OrderResponseDTO> placeOrder(
            @PathVariable Integer userId,
            @Valid @RequestBody OrderRequestDTO orderRequest) {

        ownershipValidator.assertCanAccessUser(userId);

        Order placedOrder = orderService.placeOrder(userId, orderRequest.getAddressId());
        // We need to fetch the newly created order items to build the response DTO
        List<OrderItem> orderItems = orderItemRepository.findByOrder_OrderId(placedOrder.getOrderId());

        OrderResponseDTO response = orderMapper.toOrderResponseDTO(placedOrder, orderItems);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves all orders for the specified user.
     */
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getUserOrders(@PathVariable Integer userId) {
        ownershipValidator.assertCanAccessUser(userId);
        List<Order> orders = orderService.getOrdersByUserId(userId);

        // Map each order and its items to a response DTO
        List<OrderResponseDTO> response = orders.stream().map(order -> {
            List<OrderItem> orderItems = orderItemRepository.findByOrder_OrderId(order.getOrderId());
            return orderMapper.toOrderResponseDTO(order, orderItems);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    /**
     * Retrieves a single, specific order.
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable Integer userId, @PathVariable Long orderId) {
        ownershipValidator.assertCanAccessUser(userId);

        Order order = orderService.getOrderById(orderId);

        // Orders are addressed by their own id, so confirm this one is the caller's.
        if (order.getUser() == null || !order.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Order " + orderId + " does not belong to user " + userId);
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrder_OrderId(order.getOrderId());
        return ResponseEntity.ok(orderMapper.toOrderResponseDTO(order, orderItems));
    }

    /**
     * Updates the status of an order (Admin action).
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        Order updatedOrder = orderService.updateOrderStatus(orderId, status);
        List<OrderItem> orderItems = orderItemRepository.findByOrder_OrderId(updatedOrder.getOrderId());
        return ResponseEntity.ok(orderMapper.toOrderResponseDTO(updatedOrder, orderItems));
    }
}