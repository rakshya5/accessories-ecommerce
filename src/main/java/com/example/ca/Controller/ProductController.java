package com.example.ca.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        // Get the authenticated user from session
        User user = sessionService.getAuthenticatedUser(request);
        List<Cart> carts = new ArrayList<>();

        // If the user is authenticated, get their cart
        if (user != null) {
            carts = cartService.getCarts(user);
        }

        // Add products and cart information to the model
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("cart", carts);
        model.addAttribute("cartSize", carts.size()); // Add cart size

        // Check if the user is an admin
        String userType = (String) request.getSession().getAttribute("userType");
        model.addAttribute("isAdmin", "ADMIN".equals(userType));

        return "product"; // Return the product view
    }
    
    @PostMapping("/buyNow")
    public String buyNow(@RequestParam Long productID, Model model) {
        // Fetch the product by ID
        Product product = productService.getProductById(productID)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        model.addAttribute("product", product);
        return "checkout"; // Redirect to the checkout page
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


