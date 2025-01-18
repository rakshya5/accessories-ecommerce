package com.example.ca.service;

import com.example.ca.entity.Cart;
import com.example.ca.entity.Order;
import com.example.ca.entity.OrderItem;
import com.example.ca.entity.OrderStatus; // Ensure this import is included
import com.example.ca.entity.PaymentMethod;
import com.example.ca.model.Product;
import com.example.ca.home.user.User;
import com.example.ca.repository.CartRepository;
import com.example.ca.repository.OrderItemRepository;
import com.example.ca.repository.OrderRepository;
import com.example.ca.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    // Updated placeOrder method to minimize duplicate entries
    public Order placeOrder(User user, Long productID, String shippingAddress, String contactNumber, String paymentMethod) {
        // Retrieve the product by ID
        Product product = productRepository.findById(productID)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Create a new Order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(product.getPrice());
        order.setShippingAddress(shippingAddress);
        order.setContactNumber(contactNumber);

        // Convert payment method to enum
        try {
            PaymentMethod paymentEnum = PaymentMethod.valueOf(paymentMethod.toUpperCase());
            order.setPaymentMethod(paymentEnum);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid payment method: " + paymentMethod);
        }

        // Save the order first
        order = orderRepository.save(order);

        // Create and save OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(1); // For "Buy Now" behavior, quantity is always 1
        orderItem.setPrice(product.getPrice());
        
        // Save OrderItem
        orderItemRepository.save(orderItem);

        return order;
    }

    // Checkout all items in the cart for the user
    public Order checkoutCart(User user, String shippingAddress, String contactNumber, String paymentMethod) {
        // Get all cart items for this user
        List<Cart> cartItems = cartRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty. Cannot place order.");
        }

        // Create and save a new Order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(shippingAddress);  // Set shipping address
        order.setContactNumber(contactNumber);      // Set contact number

        // Convert payment method to enum
        try {
            PaymentMethod paymentEnum = PaymentMethod.valueOf(paymentMethod.toUpperCase());
            order.setPaymentMethod(paymentEnum);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid payment method: " + paymentMethod);
        }

        double totalAmount = cartItems.stream()
                .mapToDouble(cart -> cart.getQuantity() * cart.getProduct().getPrice())
                .sum();
        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);

        // Create and save OrderItem for each cart item
        for (Cart cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItemRepository.save(orderItem);
        }

        // Delete all cart items of the user
        cartRepository.deleteAll(cartItems);

        return order;
    }

    // Method to get all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Fetch orders for a specific user
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    // Fetch order by ID (Admin)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    }
    
    // New method to update order status
    public void updateOrderStatus(Long orderId, String status) {
        // Fetch the order by ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Convert the status string to OrderStatus enum
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(orderStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + status);
        }

        // Save the updated order back to the repository
        orderRepository.save(order);
    }
    
    public Order saveOrder(Order order) {
        return orderRepository.save(order); // Delegate to OrderRepository
    }
}