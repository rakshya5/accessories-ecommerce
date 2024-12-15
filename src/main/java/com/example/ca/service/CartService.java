package com.example.ca.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ca.Controller.AddToCartDTO;
import com.example.ca.entity.Cart;
import com.example.ca.entity.Order;
import com.example.ca.entity.OrderItem;
import com.example.ca.home.user.User;
import com.example.ca.model.Product;
import com.example.ca.repository.CartRepository;
import com.example.ca.repository.OrderItemRepository;
import com.example.ca.repository.OrderRepository;
import com.example.ca.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private ProductRepository productRepository;  // Correctly injected here


    // Add item to the cart (now including productId)
    public void addItemToCart(User user, AddToCartDTO dto) {
        Optional<Product> optionalProduct = productRepository.findById(dto.getProductID());

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();

            Cart cart = new Cart();
            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(dto.getQuantity());

            cartRepository.save(cart);
        }
    }

    // Get all items in the cart for a user
    public List<Cart> getUserCart(Long userId) {
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        return (cartItems != null && !cartItems.isEmpty()) ? cartItems : new ArrayList<>();
    }

    // Remove an item from the cart by ID
    public void removeItemFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    // Clear all items from the cart for a user
    public String clearCart(Long userId) {
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            return "Cart is already empty!";
        }
        cartRepository.deleteAll(cartItems);
        return "Cart cleared successfully!";
    }

    // Get all carts for a specific user
    public List<Cart> getCarts(User user) {
        List<Cart> carts = cartRepository.findByUser(user);
        return carts != null ? carts : new ArrayList<>();
    }

    // Check if the product exists in the cart
    public boolean isProductInCart(Long productId) {
        List<Cart> cartItems = cartRepository.findByProductId(productId);
        return !cartItems.isEmpty();
    }

    // Method to place the order
    @Transactional
    public void placeOrder(User user) {
        // Step 1: Create the Order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now()); // Use LocalDateTime.now() for the current date and time

        // Calculate the total amount from the cart items
        double totalAmount = 0;
        List<Cart> cartItems = cartRepository.findByUser(user);
        for (Cart cart : cartItems) {
            totalAmount += cart.getQuantity() * cart.getProduct().getPrice();
        }
        order.setTotalAmount(totalAmount);

        // Save the order
        System.out.println("Before saving order");
        order = orderRepository.save(order);

        // Store the Cart items as OrderItems
        for (Cart cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());

            orderItemRepository.save(orderItem);
        }

        // Delete the Cart items
        cartRepository.deleteAll(cartItems);
    }
}
