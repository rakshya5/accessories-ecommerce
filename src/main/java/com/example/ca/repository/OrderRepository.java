package com.example.ca.repository;

import com.example.ca.entity.Order;
import com.example.ca.home.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
