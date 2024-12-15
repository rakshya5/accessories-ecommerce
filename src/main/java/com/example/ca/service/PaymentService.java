package com.example.ca.service;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public boolean processPayment(Long userId, Double amount) {
        // Simulate a payment gateway integration
        System.out.println("Processing payment for User ID: " + userId + " of amount: $" + amount);
        return true; // Always return true for this mock
    }
}
