package com.example.ca.home;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ca.home.user.User;
import com.example.ca.home.user.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class AdminService {

	@Autowired
	private UserRepository userRepository;

	@PostConstruct
	public void setup() {
		if (userRepository.count() == 0) {
			User user = new User();
			user.setFullname("Admin");
			user.setEmail("admin@gmail.com");
			user.setUsername("admin");
			user.setType(UserType.ADMIN);
			user.setPassword("admin123");

			userRepository.save(user);
		}
	}

}
