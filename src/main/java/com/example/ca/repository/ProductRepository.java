package com.example.ca.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ca.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // For search functionality
    List<Product> findByNameContaining(String query);

    // For fetching top 4 latest products
    List<Product> findTop4ByOrderByIdDesc();
}
