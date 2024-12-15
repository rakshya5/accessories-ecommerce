package com.example.ca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ca.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByNameContaining(String query);
}
