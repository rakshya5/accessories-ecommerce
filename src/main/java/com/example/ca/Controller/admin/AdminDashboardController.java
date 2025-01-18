package com.example.ca.Controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ca.home.SessionService;
import com.example.ca.home.user.User;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private SessionService sessionService;

    @GetMapping("/dashboard")
    public String adminDashboard(HttpServletRequest request, RedirectAttributes rattribs) {

        User user = sessionService.getAuthenticatedUser(request);
        if (user == null || !user.isAdmin()) { 
            rattribs.addAttribute("redirectTo", "/admin/dashboard");
            return "redirect:/login"; // Redirect to login if not authenticated or not an admin
        }

        return "AdminDashboard"; 
    }
}
