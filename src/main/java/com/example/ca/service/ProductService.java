package com.example.ca.service;

import com.example.ca.model.Product;
import com.example.ca.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;  // Inject CartService

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get a product by its ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Create a new product
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // Update an existing product by ID
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        product.setImageUrl(productDetails.getImageUrl());

        return productRepository.save(product);
    }

    // Delete a product by ID
    public void deleteProduct(Long id) {
    	System.out.println("delete");
        // Check if the product is in any cart before deleting
        boolean isProductInCart = cartService.isProductInCart(id);
        if (isProductInCart) {
            throw new RuntimeException("Cannot delete product as it is in the cart.");
        }

        // If not in the cart, proceed with deletion
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}
