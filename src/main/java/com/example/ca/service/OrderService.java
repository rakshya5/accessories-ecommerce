package com.example.ca.service;

import com.example.ca.entity.Cart;
import com.example.ca.entity.Order;
import com.example.ca.entity.OrderItem;
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

    // Place an order with productID and quantity
    public Order placeOrder(User user, Long productID) {
        // Retrieve the product by ID
        Product product = productRepository.findById(productID)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Create a new Order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now()); // Use LocalDateTime for current timestamp
        order.setTotalAmount(product.getPrice()); // Total amount from the product price

        // Save the order
        order = orderRepository.save(order);

        // Create order item
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(1); // For "Buy Now" behavior, quantity is always 1
        orderItem.setPrice(product.getPrice());

        orderItemRepository.save(orderItem);

        // Clear the user's cart after placing the order
        List<Cart> cartItems = cartRepository.findByUser(user);
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
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
