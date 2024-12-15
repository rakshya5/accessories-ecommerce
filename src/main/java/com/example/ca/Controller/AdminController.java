package com.example.ca.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ca.entity.Order;
import com.example.ca.home.SessionService;
import com.example.ca.home.UserType;
import com.example.ca.home.user.User;
import com.example.ca.model.Product;
import com.example.ca.service.OrderService;
import com.example.ca.service.ProductService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private SessionService sessionService;

	private static final String UPLOAD_DIR = "src/main/resources/static/images/";

	// Dashboard
	@GetMapping("/ViewProducts")
	public String showDashboard(Model model, HttpServletRequest request) {
		if (!isAdminAuthenticated(request)) {
			return "redirect:/login";
		}
		List<Product> products = productService.getAllProducts();
		model.addAttribute("products", products);
		return "ViewProducts";
	}

	// Add product form
	@GetMapping("/add-product")
	public String showAddProductForm(HttpServletRequest request) {
		if (!isAdminAuthenticated(request)) {
			return "redirect:/admin/login";
		}
		return "add-product";
	}

	// Add product handler
	@PostMapping("/add-product")
	public String addProduct(@ModelAttribute Product product, @RequestParam("image") MultipartFile image,
			RedirectAttributes redirectAttributes, HttpServletRequest request) {
		if (!isAdminAuthenticated(request)) {
			return "redirect:/admin/login";
		}

		if (!image.isEmpty()) {
			try {
				String fileName = image.getOriginalFilename();
				Path path = Paths.get(UPLOAD_DIR + fileName);
				Files.write(path, image.getBytes());
				product.setImageUrl(fileName);
			} catch (IOException e) {
				e.printStackTrace();
				redirectAttributes.addFlashAttribute("error", "Failed to upload image");
				return "redirect:/admin/add-product";
			}
		}

		productService.createProduct(product);
		return "redirect:/admin/ViewProducts";
	}

	// Edit product form
	@GetMapping("/edit-product/{id}")
	public String showEditProductForm(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
		if (!isAdminAuthenticated(request)) {
			return "redirect:/admin/login";
		}

		Optional<Product> productOptional = productService.getProductById(id);
		if (productOptional.isPresent()) {
			model.addAttribute("product", productOptional.get());
			return "edit-product";
		} else {
			model.addAttribute("error", "Product not found");
			return "redirect:/admin/ViewProducts";
		}
	}

	// Edit product handler
	@PostMapping("/edit-product/{id}")
	public String editProduct(@PathVariable("id") Long id, @ModelAttribute Product product,
			@RequestParam("image") MultipartFile image, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		if (!isAdminAuthenticated(request)) {
			return "redirect:/admin/login";
		}

		try {
			Optional<Product> existingProductOptional = productService.getProductById(id);
			if (existingProductOptional.isPresent()) {
				Product existingProduct = existingProductOptional.get();
				existingProduct.setName(product.getName());
				existingProduct.setPrice(product.getPrice());

				if (!image.isEmpty()) {
					String fileName = image.getOriginalFilename();
					Path path = Paths.get(UPLOAD_DIR + fileName);
					Files.write(path, image.getBytes());
					existingProduct.setImageUrl(fileName);
				}

				productService.updateProduct(id, existingProduct);
			}
		} catch (IOException e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "Failed to update product");
			return "redirect:/admin/edit-product/" + id;
		}

		return "redirect:/admin/ViewProducts";
	}

	// Delete product
	@GetMapping("/delete-product/{id}")
	public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		System.out.println("hi " + id);
		if (!isAdminAuthenticated(request)) {
			return "redirect:/admin/login";
		}

		try {
			System.out.println("inside delete ");
			productService.deleteProduct(id);
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/admin/ViewProducts";
	}

	// View all orders
	@GetMapping("/orders")
	public String viewOrders(Model model, HttpServletRequest request) {
		if (!isAdminAuthenticated(request)) {
			return "redirect:/admin/login";
		}

		List<Order> orders = orderService.getAllOrders();
		model.addAttribute("orders", orders);
		return "orders";
	}

	// View order details
	@GetMapping("/order/{id}")
	public String viewOrderDetails(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
		if (!isAdminAuthenticated(request)) {
			return "redirect:/admin/login";
		}

		Order order = orderService.getOrderById(id); // Make sure this method exists in OrderService
		model.addAttribute("order", order);
		return "order-details"; // Ensure this HTML template is created
	}

	// Logout handler
	@GetMapping("/logout")
	public String logout(HttpServletResponse response) {
		// Clear the adminToken cookie
		Cookie cookie = new Cookie("adminToken", null);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(0); // Invalidate the cookie
		response.addCookie(cookie);

		// Redirect to login page after logout
		return "redirect:/admin/login";
	}

	// Helper method to check admin authentication
	private boolean isAdminAuthenticated(HttpServletRequest request) {
		User user = sessionService.getAuthenticatedUser(request);
		if (user != null) {
			return user.getType() == UserType.ADMIN;

		}
		return false;

	}
}
