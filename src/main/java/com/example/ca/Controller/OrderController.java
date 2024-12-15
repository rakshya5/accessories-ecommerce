package com.example.ca.Controller;

import com.example.ca.entity.Order;
import com.example.ca.home.SessionService;
import com.example.ca.home.user.User;
import com.example.ca.service.OrderService;
import com.example.ca.service.ProductService; // Ensure this import if you have a ProductService
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ProductService productService; // ProductService to fetch product details

    // Place the order after cart review (Buy Now feature)
    @PostMapping("/place-order/{productID}")
    public String placeOrder(@PathVariable Long productID, HttpServletRequest request) {
        // Fetch authenticated user from session
        User user = sessionService.getAuthenticatedUser(request);

        // Redirect to login if user is not authenticated
        if (user == null) {
            return "redirect:/login";
        }

        // Place the order for the specific product
        Order order = orderService.placeOrder(user, productID);

        // Redirect to the success page
        return "redirect:/order-success";
    }

    // Display order success page
    @GetMapping("/order-success")
    public String orderSuccess() {
        return "order-success";
    }
}
