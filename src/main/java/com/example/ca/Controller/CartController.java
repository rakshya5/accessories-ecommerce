package com.example.ca.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
		return "cart"; // Return the correct view name for your cart page
	}

	@PostMapping("/remove")
	public String removeCart(@RequestParam("cartId") long id) {
		cartService.removeItemFromCart(id);

		return "redirect:/cart";
	}

	@PostMapping("/add")
	public String addToCart(@RequestParam("productID") Long productId,
			@RequestParam(value = "quantity", defaultValue = "1") int quantity, HttpServletRequest request) {
		User user = sessionService.getAuthenticatedUser(request); // Fetch authenticated user
		AddToCartDTO cartDTO = new AddToCartDTO(productId, quantity); // Create DTO
		cartService.addItemToCart(user, cartDTO); // Add to cart
		return "redirect:/product"; // Redirect back to product page
	}

	// Handle placing the order from the cart
    @PostMapping("/place-order")
    public String placeTheOrder(HttpServletRequest request) {
    	System.out.println("inside placeTheOrder()");

		User user = sessionService.getAuthenticatedUser(request); // Fetch authenticated user
        if (user != null) {
        	
        	System.out.println("before cartService.placeOrder()");
        	
            cartService.placeOrder(user);
            return "redirect:/order-success"; // Or redirect to any other success page
        }
       
        // Step 2: Redirect to the success page
        
        return "redirect:/cart";
    }
}