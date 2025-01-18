package com.example.ca.Controller;

import com.example.ca.entity.Cart;
import com.example.ca.entity.Order;
import com.example.ca.entity.OrderStatus;
import com.example.ca.entity.PaymentMethod;
import com.example.ca.service.OrderService;
import com.example.ca.home.user.User;
import com.example.ca.home.SessionService;
import com.example.ca.service.CartService;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CheckoutController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private CartService cartService;

    // GetMapping for Checkout Page
    @GetMapping("/checkout")
    public String checkoutPage(HttpServletRequest request, Model model) {
        // Fetch authenticated user from session
        User user = sessionService.getAuthenticatedUser(request);

        // Redirect to login if user is not authenticated
        if (user == null) {
            return "redirect:/login";
        }

        // Fetch updated cart items
        List<Cart> cartItems = cartService.getCarts(user);

        // Calculate the grand total
        double grandTotal = cartItems.stream()
                                     .mapToDouble(cartItem -> cartItem.getQuantity() * cartItem.getProduct().getPrice())
                                     .sum();

        // Add cart items and grand total to the model
        model.addAttribute("cart", cartItems);
        model.addAttribute("grandTotal", grandTotal);

        return "checkout"; // Return the checkout page view
    }

    @PostMapping("/checkout/confirm")
    public String confirmCheckout(@RequestParam("name") String name,
                                  @RequestParam("address") String address,
                                  @RequestParam("contact") String contact,
                                  @RequestParam("paymentMethod") String paymentMethod,
                                  @RequestParam("totalAmount") double totalAmount,
                                  HttpServletRequest request,
                                  Model model) {

        // Fetch authenticated user from session
        User user = sessionService.getAuthenticatedUser(request);

        // Redirect to login if user is not authenticated
        if (user == null) {
            return "redirect:/login";
        }

        // Create a new order and set values
        Order order = new Order();
        order.setShippingAddress(address);
        order.setContactNumber(contact);
        order.setTotalAmount(totalAmount);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING); // Default status for a new order

        // Convert paymentMethod from string to enum with error handling
        try {
            order.setPaymentMethod(PaymentMethod.valueOf(paymentMethod.toUpperCase()));
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Invalid payment method: " + paymentMethod);
            return "checkout";
        }

        // Set the user who placed the order
        order.setUser(user);

        // Save the order
        try {
            orderService.saveOrder(order);
        } catch (Exception e) {
            model.addAttribute("error", "Error saving order: " + e.getMessage());
            return "checkout";
        }

        // Optionally clear the cart after placing the order
        cartService.clearCart((long) user.getId());

        // Add order to model for confirmation page
        model.addAttribute("order", order);

        return "OrderConfirmation"; // Redirect to order confirmation page
    }
}
