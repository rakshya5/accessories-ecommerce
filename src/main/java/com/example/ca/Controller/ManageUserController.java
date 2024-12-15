package com.example.ca.Controller;

import com.example.ca.home.user.User;
import com.example.ca.home.user.UserRepository;
import com.example.ca.home.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class ManageUserController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserRepository userRepository;

    // GET /admin/users
    @GetMapping("/users")
    public String manageUsers(Model model, HttpServletRequest request) {
        if (sessionService.getAuthenticatedUser(request) == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }
        model.addAttribute("users", userRepository.findAll()); // Fetch and display all users
        return "manage-users"; // Thymeleaf template for managing users
    }

    // GET /admin/edit-user/{id}
    @GetMapping("/edit-user/{id}")
    public String editUser(@PathVariable int id, Model model, HttpServletRequest request) {
        if (sessionService.getAuthenticatedUser(request) == null) {
            return "redirect:/login"; // Redirect if not authenticated
        }

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        model.addAttribute("user", user);
        return "edit-user"; // Thymeleaf view for editing user
    }

    // POST /admin/update-user
    @PostMapping("/update-user")
    public String updateUser(@ModelAttribute User user, HttpServletRequest request) {
        if (sessionService.getAuthenticatedUser(request) == null) {
            return "redirect:/login"; // Redirect if not authenticated
        }
        userRepository.save(user); // Save the updated user
        return "redirect:/admin/users"; // Redirect to manage users page
    }

    // GET /admin/delete-user/{id}
    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable int id, HttpServletRequest request) {
        if (sessionService.getAuthenticatedUser(request) == null) {
            return "redirect:/login"; // Redirect if not authenticated
        }
        userRepository.deleteById(id); // Delete the user by ID
        return "redirect:/admin/users"; // Redirect to manage users page
    }
}
