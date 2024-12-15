package com.example.ca.home.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	User findByEmail(String email);

	User findByUsername(String username);

	User findByUsernameAndPassword(String username, String password);

	Optional<User> findBySession(String value);
}






