package com.example.ca.Controller;

import com.example.ca.entity.Order;
import com.example.ca.model.Product;
import com.example.ca.home.SessionService;
import com.example.ca.home.user.User;
import com.example.ca.service.OrderService;
import com.example.ca.service.ProductService; 
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ProductService productService;

    // Place the order after cart review (Buy Now feature)
    @PostMapping("/place-order/{productID}")
    public String placeOrder(@PathVariable Long productID, HttpServletRequest request, Model model) {
        // Fetch authenticated user from session
        User user = sessionService.getAuthenticatedUser(request);

        // Redirect to login if user is not authenticated
        if (user == null) {
            return "redirect:/login";
        }

        // Fetch the product details
        Product product = productService.getProductById(productID)
                                      .orElse(null); // This will return null if product not found

        if (product == null) {
            model.addAttribute("error", "Product not found.");
            return "redirect:/product-list"; // Redirect to product list if product is not found
        }

        // Add product details to model
        model.addAttribute("product", product);
        model.addAttribute("user", user);

        return "checkout"; // Redirect to checkout page
    }

    // Handle checkout process (confirm order details)
    @PostMapping("/order/checkout/confirm")
    public String confirmCheckout(
            @RequestParam("shippingAddress") String shippingAddress,
            @RequestParam("contactNumber") String contactNumber,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam("productID") Long productID,
            HttpServletRequest request,
            Model model) {

        // Fetch authenticated user from session
        User user = sessionService.getAuthenticatedUser(request);

        // Redirect to login if user is not authenticated
        if (user == null) {
            return "redirect:/login";
        }

        // Place order with the provided details
        Order order = orderService.placeOrder(user, productID, shippingAddress, contactNumber, paymentMethod);

        // Add order to model
        model.addAttribute("order", order);

        return "order-confirmation"; // Redirect to order confirmation page
    }

    // Display order success page
    @GetMapping("/OrderConfirmation")
    public String orderConfirmation() {
        return "order-confirmation"; // Redirect to order confirmation page
    }

    // View order details (For admin or user)
    @GetMapping("/order/view")
    public String viewOrderDetails(@RequestParam("orderId") Long orderId, Model model, HttpServletRequest request) {
        // Fetch the order by ID
        Order order = orderService.getOrderById(orderId);

        if (order == null) {
            model.addAttribute("error", "Order not found.");
            return "redirect:/admin/orders";
        }

        // Add the user details to the model to display the username
        model.addAttribute("order", order);
        model.addAttribute("username", order.getUser().getUsername());

        return "order-details"; // View template name
    }
}
