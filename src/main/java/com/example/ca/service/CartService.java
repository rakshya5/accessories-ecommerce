package com.example.ca.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Add an item to the cart. If the product already exists in the user's cart,
     * update the quantity.
     */
    public void addItemToCart(User user, AddToCartDTO dto) {
        Optional<Product> optionalProduct = productRepository.findById(dto.getProductID());

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();

            Optional<Cart> existingCart = cartRepository.findByUserAndProduct(user, product);
            Cart cart;
            if (existingCart.isPresent()) {
                cart = existingCart.get();
                cart.setQuantity(cart.getQuantity() + dto.getQuantity()); // Update quantity
            } else {
                cart = new Cart();
                cart.setUser(user);
                cart.setProduct(product);
                cart.setQuantity(dto.getQuantity()); // New cart entry
            }
            cartRepository.save(cart);
        }
    }

    /**
     * Update the quantity of an item in the cart.
     */
    public void updateCartItem(long cartId, int quantity) {
        if (quantity < 1) {
            quantity = 1;
        } else if (quantity > 5) {
            quantity = 5;
        }

        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            cart.setQuantity(quantity);
            cartRepository.save(cart);
        }
    }

    /**
     * Get all items in the cart for a specific user.
     */
    public List<Cart> getUserCart(Long userId) {
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        return (cartItems != null && !cartItems.isEmpty()) ? cartItems : new ArrayList<>();
    }

    /**
     * Remove an item from the cart.
     */
    public void removeItemFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    /**
     * Clear the cart for a specific user.
     */
    public String clearCart(Long userId) {
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            return "Cart is already empty!";
        }
        cartRepository.deleteAll(cartItems);
        return "Cart cleared successfully!";
    }

    /**
     * Get the cart items for a specific user.
     */
    public List<Cart> getCarts(User user) {
        List<Cart> carts = cartRepository.findByUser(user);
        return carts != null ? carts : new ArrayList<>();
    }

    /**
     * Check if a product is already in the cart.
     */
    public boolean isProductInCart(Long productId) {
        List<Cart> cartItems = cartRepository.findByProductId(productId);
        return !cartItems.isEmpty();
    }

    /**
     * Place an order for the user by moving cart items to the order table.
     */
    @Transactional
    public void placeOrder(User user) {
        // Step 1: Create the Order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());

        // Calculate the total amount from cart items
        double totalAmount = 0;
        List<Cart> cartItems = cartRepository.findByUser(user);
        for (Cart cart : cartItems) {
            totalAmount += cart.getQuantity() * cart.getProduct().getPrice();
        }
        order.setTotalAmount(totalAmount);

        // Save the order
        order = orderRepository.save(order);

        // Save the cart items as order items
        for (Cart cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());

            orderItemRepository.save(orderItem);
        }

        // Clear the cart after placing the order
        cartRepository.deleteAll(cartItems);
    }
}
