package com.example.ca.repository;

import com.example.ca.entity.Cart;
import com.example.ca.home.user.User;
import com.example.ca.model.Product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);
    List<Cart> findByUser(User user);
    List<Cart> findByProductId(Long productId);  // Add this method to check for product in cart

 // Find cart item by user and product
    Optional<Cart> findByUserAndProduct(User user, Product product);
}

