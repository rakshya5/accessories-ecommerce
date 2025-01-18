package com.example.ca.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ca.entity.Cart;
import com.example.ca.home.SessionService;
import com.example.ca.home.user.User;
import com.example.ca.service.CartService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private SessionService sessionService;

    @GetMapping("")
    public String getCartPage(HttpServletRequest request, Model model) {
        User user = sessionService.getAuthenticatedUser(request);

        List<Cart> cartItems = cartService.getCarts(user);
        double grandTotal = 0;

        for (Cart cart : cartItems) {
            grandTotal += cart.getQuantity() * cart.getProduct().getPrice();
        }

        model.addAttribute("cart", cartItems);
        model.addAttribute("grandTotal", grandTotal);
        return "cart"; 
    }

    @PostMapping("/remove")
    public String removeCart(@RequestParam("cartId") long id) {
        cartService.removeItemFromCart(id);
        return "redirect:/cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("productID") Long productId,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity, HttpServletRequest request) {
        User user = sessionService.getAuthenticatedUser(request);

        if (user == null) {
            return "redirect:/login";
        }

        cartService.addItemToCart(user, new AddToCartDTO(productId, quantity));
        return "redirect:/product";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam("cartId") long cartId, 
                              @RequestParam("quantity") int quantity, 
                              RedirectAttributes redirectAttributes) {

        if (quantity < 1) {
            quantity = 1;
        } else if (quantity > 5) {
            quantity = 5;
        }

        try {
            cartService.updateCartItem(cartId, quantity);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to update cart item.");
        }

        return "redirect:/cart";
    }

    @PostMapping("/place-order")
    public String placeTheOrder(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User user = sessionService.getAuthenticatedUser(request);
        if (user != null) {
            List<Cart> cartItems = cartService.getCarts(user);
            double grandTotal = 0;

            for (Cart cart : cartItems) {
                grandTotal += cart.getQuantity() * cart.getProduct().getPrice();
            }

            // Optionally, you can clear the cart after placing an order or keep it as is.
            // cartService.clearCart(user);

            // Redirect to payment page with the total amount
            return "redirect:/payment?amount=" + grandTotal;
        }
        return "redirect:/cart";
    }
}