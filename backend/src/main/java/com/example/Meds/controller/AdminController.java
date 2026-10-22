package com.example.Meds.controller;

import com.example.Meds.dto.OrderResponseDTO;
import com.example.Meds.entity.Order;
import com.example.Meds.entity.OrderItem;
import com.example.Meds.mapper.OrderMapper;
import com.example.Meds.repository.OrderItemRepository;
import com.example.Meds.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final OrderService orderService;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;

    public AdminController(OrderService orderService, OrderItemRepository orderItemRepository, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderItemRepository = orderItemRepository;
        this.orderMapper = orderMapper;
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrdersForAdmin() {
        List<Order> orders = orderService.getAllOrders();

        List<OrderResponseDTO> response = orders.stream().map(order -> {
            List<OrderItem> orderItems = orderItemRepository.findByOrder_OrderId(order.getOrderId());
            return orderMapper.toOrderResponseDTO(order, orderItems);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
