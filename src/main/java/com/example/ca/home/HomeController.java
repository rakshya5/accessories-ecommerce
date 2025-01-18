package com.example.ca.home;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ca.home.user.User;
import com.example.ca.home.user.UserRepository;
import com.example.ca.model.Product;
import com.example.ca.repository.ProductRepository;
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
	@Autowired
	private ProductRepository productRepository;

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
		user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));

		userRepository.save(user);
		return "redirect:/login"; // Redirect to login page after successful registration
	}
	@GetMapping("/")
	public String getHomepage(Model model, HttpServletRequest request) {
	    User user = sessionService.getAuthenticatedUser(request);
	    if (user != null) {
	        model.addAttribute("user", user); // Pass user details
	        // If cart is not implemented yet, pass an empty list
	        model.addAttribute("cart", List.of());
	    } else {
	        model.addAttribute("cart", List.of()); // Default empty cart for non-logged-in users
	    }
		// Fetch the top 4 products
        List<Product> featuredProducts = productRepository.findTop4ByOrderByIdDesc();
		model.addAttribute("featuredProducts", featuredProducts);
//		model.addAttribute("user", user);
		return "index"; // Homepage view
		
	}

	@GetMapping("/about")
	public String getAboutpage() {
		return "about"; 
	}

	@GetMapping("/contact")
	public String getContactpage() {
		return "contact"; 
	}

	@GetMapping("/register")
	public String getRegisterPage() {
		return "register"; // Registration page view
	}

	@GetMapping("/login")
	public String getLoginPage(@RequestParam(name = "redirectTo", defaultValue = "/") String redirectUrl, Model model) {

//		if (redirectUrl == null || redirectUrl.isEmpty()) {
//			redirectUrl = "/";
//		}

		model.addAttribute("loginAction", redirectUrl);

		return "login"; // Login page view
	}

	@PostMapping("/login")
	public String doLogin(@RequestParam("username") String username, @RequestParam("password") String password,
			Model model, HttpServletResponse response, HttpServletRequest request,
			@RequestParam("redirectTo") String redirectUrl) {

		
		
		User user = userRepository.findByUsername(username);
		
		//check if user is not null and password matches as follows:
		// user.password == hash(form.password)

		System.out.println(username);
		System.out.println(password);

		System.out.println(redirectUrl);

		if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
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

		return user.getType() == UserType.ADMIN ? "redirect:" + redirectUrl : "redirect:/"; // Redirect to
																							// product page
																							// after
																							// successful
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
