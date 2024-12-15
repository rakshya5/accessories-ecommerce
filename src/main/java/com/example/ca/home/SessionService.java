package com.example.ca.home;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ca.home.user.User;
import com.example.ca.home.user.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class SessionService {

	@Autowired
	private UserRepository userRepository;

	public static String sampleSpace = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	public String generateSession() {

		Random random = new Random();

		char[] chars = new char[20];

		for (int i = 0; i < 20; i++) {

			int index = random.nextInt(sampleSpace.length());

			chars[i] = sampleSpace.charAt(index);

		}

		return new String(chars);

	}

	public User getAuthenticatedUser(HttpServletRequest request) {

		Cookie[] cookies = request.getCookies();
		String session = null;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("session")) {
					session = cookie.getValue();
					break;
				}
			}
		}

		Optional<User> optionalUser = userRepository.findBySession(session);

		if (optionalUser.isPresent()) {
			return optionalUser.get();
		} else {
			return null;
		}
	}

}