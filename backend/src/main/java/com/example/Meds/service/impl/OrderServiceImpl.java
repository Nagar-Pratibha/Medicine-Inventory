package com.example.Meds.service.impl;

import com.example.Meds.entity.*;
import com.example.Meds.exception.ResourceNotFoundException;
import com.example.Meds.repository.*;
import com.example.Meds.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    // MedicineRepository is not used and can be removed for cleaner code
    // private final MedicineRepository medicineRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            CartRepository cartRepository,
                            CartItemRepository cartItemRepository,
                            AddressRepository addressRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    @Transactional
    public Order placeOrder(Integer userId, Long addressId) {
        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));
        List<CartItem> cartItems = cartItemRepository.findByCart_CartId(cart.getCartId());
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot place an order with an empty cart.");
        }

        // Calculate total amount from cart items
        BigDecimal subTotal = cartItems.stream()
                .map(item -> item.getMedicine().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tax
        BigDecimal taxRate = BigDecimal.valueOf(0.05);
        BigDecimal totalAmount = subTotal.add(subTotal.multiply(taxRate));

        // Create and save the primary Order object
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setAddress(address);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING); // Correctly set the initial status
        order = orderRepository.save(order);

        // Create OrderItem entries for each item in the cart
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMedicine(cartItem.getMedicine());
            orderItem.setQuantity(cartItem.getQuantity());
            // It's good practice to save the price at the time of purchase
            // orderItem.setPriceAtPurchase(cartItem.getMedicine().getPrice());
            orderItemRepository.save(orderItem);
        }

        // Clear the user's cart after the order is placed
        cartItemRepository.deleteAll(cartItems);

        return order;
    }

    @Override
    public List<Order> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

}