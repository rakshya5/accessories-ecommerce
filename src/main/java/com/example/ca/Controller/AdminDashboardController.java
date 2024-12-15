package com.example.ca.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.ca.home.SessionService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AdminDashboardController {

    @Autowired
    private SessionService sessionService;

    @GetMapping("/AdminDashboard")
    public String adminDashboard(HttpServletRequest request) {
        if (sessionService.getAuthenticatedUser(request) == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }
        return "AdminDashboard"; // Admin dashboard page
    }

    @GetMapping("/products")
    public String manageProducts(HttpServletRequest request) {
        if (sessionService.getAuthenticatedUser(request) == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }
        return "manage-products"; // Manage products page
    }

    @GetMapping("/users")
    public String manageUsers(HttpServletRequest request) {
        if (sessionService.getAuthenticatedUser(request) == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }
        return "manage-users"; // Manage users page
    }

    @GetMapping("/orders")
    public String viewOrders(HttpServletRequest request) {
        if (sessionService.getAuthenticatedUser(request) == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }
        return "view-orders"; // View orders page
    }
}
