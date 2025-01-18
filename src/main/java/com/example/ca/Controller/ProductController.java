package com.example.ca.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ca.entity.Cart;
import com.example.ca.home.SessionService;
import com.example.ca.home.user.User;
import com.example.ca.model.Product;
import com.example.ca.repository.ProductRepository;
import com.example.ca.service.CartService;
import com.example.ca.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private ProductService productService;

    // Method to show the product page
    @GetMapping("/product")
    public String productPage(Model model, HttpServletRequest request) {
    	
//    	System.out.println("In /product page");
    	
        // Get the authenticated user from session
        User user = sessionService.getAuthenticatedUser(request);
        
//        System.out.println("after getAuthUser()");
        List<Cart> carts = new ArrayList<>();

        // If the user is authenticated, get their cart
        if (user != null) {
            carts = cartService.getCarts(user);
        }
        
//        System.out.println("after user not null");

        // Add products and cart information to the model
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("cart", carts);
        model.addAttribute("cartSize", carts.size()); // Add cart size
        model.addAttribute("user", user);

        // Check if the user is an admin
//        String userType = (String) request.getSession().getAttribute("userType");
//        model.addAttribute("isAdmin", "ADMIN".equals(userType));

        return "product"; // Return the product view
    }
    
    @PostMapping("/product/buyNow")
    public String buyNow(@RequestParam("productID") Long productID, 
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest request) {
        
        // Retrieve authenticated user from session
        User user = sessionService.getAuthenticatedUser(request);

        if (user == null) {
            return "redirect:/login"; // Redirect to login if user is not authenticated
        }

        // Fetch the product by ID
        Product product = productService.findById(productID);

        if (product == null) {
            // Handle product not found case
            redirectAttributes.addFlashAttribute("error", "Product not found.");
            return "redirect:/product";
        }

        // Add product to cart
        AddToCartDTO cartDTO = new AddToCartDTO(productID, 1); // Create DTO
        cartService.addItemToCart(user, cartDTO);

        // Redirect to order success page
        return "redirect:/cart";
    }

    	

    // Method to handle product search
    @GetMapping("/product/search")
    public String searchProduct(@RequestParam("query") String query, Model model, HttpServletRequest request) {
        // Search products by name containing the query string
        List<Product> matchingProducts = productRepository.findByNameContaining(query);

        // Get the authenticated user and their cart
        User user = sessionService.getAuthenticatedUser(request);
        List<Cart> carts = new ArrayList<>();

        if (user != null) {
            carts = cartService.getCarts(user);
        }

        // Add matching products and cart information to the model
        model.addAttribute("products", matchingProducts);
        model.addAttribute("cart", carts);
        model.addAttribute("cartSize", carts.size());

        // Check if the user is an admin
        String userType = (String) request.getSession().getAttribute("userType");
        model.addAttribute("isAdmin", "ADMIN".equals(userType));

        return "product";
    }

    // Error handler for product not found
    @GetMapping("/error/product-not-found")
    public String productNotFound(Model model) {
        model.addAttribute("errorMessage", "The requested product was not found.");
        return "error"; // Point to an error view like error.html
    }
}


