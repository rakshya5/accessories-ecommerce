package com.example.ca.home;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ca.home.user.User;
import com.example.ca.home.user.UserRepository;
import com.example.ca.validation.ValidationError;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SessionService sessionService;

	@PostMapping("/register")
	public String doRegistration(@RequestParam("fullname") String fullname, @RequestParam("username") String username,
			@RequestParam("email") String email, @RequestParam("password") String password, Model model) {

		ValidationError validationError = new ValidationError();

		// Check if email/username already exists
		User existingUserByEmail = userRepository.findByEmail(email);
		if (existingUserByEmail != null) {
			validationError.setEmail("Sorry, email is already taken");
		}

		User existingUserByUsername = userRepository.findByUsername(username);
		if (existingUserByUsername != null) {
			validationError.setUsername("Sorry, username is already taken");
		}

		if (validationError.hasErrors()) {
			model.addAttribute("error", validationError);
			return "register";
		}

		// Insert new user into the database
		User user = new User();
		user.setFullname(fullname);
		user.setEmail(email);
		user.setUsername(username);
		user.setType(UserType.USER); // Default to 'USER'
		user.setPassword(password);

		userRepository.save(user);
		return "redirect:/"; // Redirect to homepage after successful registration
	}

	@GetMapping("/")
	public String getHomepage() {
		return "index"; // Homepage view
	}

	@GetMapping("/about")
	public String getAboutpage() {
		return "about"; // Homepage view
	}
	
	@GetMapping("/contact")
	public String getContactpage() {
		return "contact"; // Homepage view
	}
     
	
	
	@GetMapping("/register")
	public String getRegisterPage() {
		return "register"; // Registration page view
	}

	@GetMapping("/login")
	public String getLoginPage() {
		return "login"; // Login page view
	}

	@PostMapping("/login")
	public String doLogin(@RequestParam("username") String username, @RequestParam("password") String password,
			Model model, HttpServletResponse response, HttpServletRequest request) {

		User user = userRepository.findByUsernameAndPassword(username, password);

		System.out.println(username);
		System.out.println(password);

		if (user == null) {
			ValidationError error = new ValidationError();
			error.setLogin("Invalid credentials");
			model.addAttribute("error", error);
			return "login";
		}

		// Set the user session and role in session
		String session = sessionService.generateSession();
		user.setSession(session);
		userRepository.save(user);

		// Save session in a cookie
		Cookie cookie = new Cookie("session", session);
		cookie.setPath("/");
		cookie.setMaxAge(86400);
		response.addCookie(cookie);

//        // Store user role in session
//        request.getSession().setAttribute("userType", user.getType().name());

		return user.getType() == UserType.ADMIN ? "redirect:/AdminDashboard" : "redirect:/product"; // Redirect to
																									// product page
																									// after successful
																									// login
	}

	@GetMapping("/logout")
	public String doLogout(HttpServletRequest request, HttpServletResponse response) {
	    User user = sessionService.getAuthenticatedUser(request);
	    if (user != null) {
	        user.setSession(null); // Clear session from the database
	        userRepository.save(user);
	    }

	    // Clear the session cookie
	    Cookie cookie = new Cookie("session", null);
	    cookie.setMaxAge(0);
	    cookie.setPath("/");
	    response.addCookie(cookie);

	    return "redirect:/"; // Redirect to login page after logout
	}


}
