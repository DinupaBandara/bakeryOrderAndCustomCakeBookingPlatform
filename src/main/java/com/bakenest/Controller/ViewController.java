package com.bakenest.Controller;

import com.bakenest.Model.*;
import com.bakenest.Repository.*;
import com.bakenest.Service.CartService;
import com.bakenest.Service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Controller
public class ViewController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @PostMapping("/auth/logout")
    public String logout(HttpSession session) {
        // 1. Completely clear all session data (loggedUser, role, etc.)
        session.invalidate();

        // 2. Redirect the user to the login page with a logout message
        return "redirect:/home?logout";
    }

    @GetMapping("/home")
    public String showHomePage(Model model, HttpSession session) {
        Object sessionUser = session.getAttribute("loggedUser");

        if (sessionUser instanceof Customer) {
            model.addAttribute("user", (Customer) sessionUser);
        } else {
            model.addAttribute("user", null);
        }

        // NEW: Fetch the 5 most recent feedbacks
        List<Feedback> recentFeedbacks = feedbackRepository.findTop5ByOrderByCreatedAtDesc();
        model.addAttribute("feedbacks", recentFeedbacks);

        model.addAttribute("backendMessage", "Hello! This data came from the Spring Backend.");
        return "index";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        // THIS IS THE FIX: Pass an empty customer object for the register form
        model.addAttribute("customer", new Customer());
        return "login";
    }

    // --- Main Product Catalog ---
    @GetMapping("/customer/product")
    public String showProductPage(Model model, HttpSession session) {
        Object sessionUser = session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        // Strictly check for Customer type and role
        if (sessionUser instanceof Customer && "CUSTOMER".equals(role)) {
            model.addAttribute("user", (Customer) sessionUser);
        } else {
            model.addAttribute("user", null); // Handled as Guest in navbar
        }
        return "/customer/product";
    }

    // --- Custom Cake Page ---
    @GetMapping("/customer/product/customcake")
    public String showCustomCakePage(Model model, HttpSession session) {
        // 1. Retrieve the session user and their role safely
        Object sessionUser = session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        // 2. Validate that the logged-in user is specifically a Customer
        if (sessionUser instanceof Customer && "CUSTOMER".equals(role)) {
            model.addAttribute("user", (Customer) sessionUser);
        } else {
            // 3. Set user to null for Guest view if no customer session is active
            model.addAttribute("user", null);
        }

        // 4. Return the path to your custom cake template
        return "/customer/customeCake";
    }

    // --- Bakery Items with Category Filtering ---
    @GetMapping("/customer/product/bakeryitems")
    public String showBakeryItemPage(@RequestParam(required = false) String category,
                                     Model model,
                                     HttpSession session) {
        Object sessionUser = session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        if (sessionUser instanceof Customer && "CUSTOMER".equals(role)) {
            model.addAttribute("user", (Customer) sessionUser);
        } else {
            model.addAttribute("user", null);
        }

        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);

        Set<String> activeCategories = products.stream()
                .map(Product::getCategory)
                .collect(Collectors.toSet());
        model.addAttribute("activeCategories", activeCategories);

        return "/customer/bakeryItem";
    }

    // --- Customer Profile Page ---
    @GetMapping("/customer/profile")
    public String showCustomerProfile(Model model, HttpSession session) {
        Object sessionUser = session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        // These pages MUST have a valid Customer session
        if (sessionUser instanceof Customer && "CUSTOMER".equals(role)) {
            model.addAttribute("user", (Customer) sessionUser);
            return "/customer/profile";
        }

        return "redirect:/login";
    }

    // --- Customer Cart Page ---
    @GetMapping("/customer/cart")
    public String showCustomerCart(Model model, HttpSession session) {
        // 1. Check authentication
        Object sessionUser = session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        if (sessionUser instanceof Customer && "CUSTOMER".equals(role)) {
            Customer customer = (Customer) sessionUser;

            // 2. Fetch the dynamic list of cart items
            List<CartItem> cartItems = cartService.getCartItems(customer);

            // 3. Get REAL LIVE fee values from the OrderService (No longer guessing from the model!)
            double deliveryFee = orderService.getDeliveryFee();
            double packagingFee = orderService.getPackagingFee();
            double subtotal = cartService.calculateTotal(customer);
            double total = cartItems.isEmpty() ? 0.00 : (subtotal + deliveryFee + packagingFee);
            total = subtotal + deliveryFee + packagingFee;


            // 5. Add data to the model
            model.addAttribute("user", customer);
            model.addAttribute("cartItems", cartItems);

            // Pass the fees separately so JS and HTML can use them
            model.addAttribute("deliveryFee", deliveryFee);
            model.addAttribute("packagingFee", packagingFee);

            // Format numbers for the UI
            model.addAttribute("subtotal", String.format("%.2f", subtotal));
            model.addAttribute("total", String.format("%.2f", total));

            return "customer/cart";
        }

        return "redirect:/login";
    }

    @GetMapping("/customer/order/checkout")
    public String showCheckoutPage(Model model, HttpSession session) {
        // 1. Get as Object first to avoid immediate ClassCastException
        Object sessionUser = session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        // 2. Comprehensive check: Is it a Customer object AND does it have the CUSTOMER role?
        if (!(sessionUser instanceof Customer) || !"CUSTOMER".equals(role)) {
            return "redirect:/login";
        }

        // 3. Safe cast now that we've verified the type
        Customer customer = (Customer) sessionUser;

        // 4. Fetch items and validate cart is not empty
        List<CartItem> cartItems = cartService.getCartItems(customer);
        if (cartItems.isEmpty()) {
            return "redirect:/customer/cart";
        }

        // 5. Logic and Fee Calculations
        double subtotal = cartService.calculateTotal(customer);
        double deliveryFee = orderService.getDeliveryFee();
        double packagingFee = orderService.getPackagingFee();
        double total = subtotal + deliveryFee + packagingFee;

        // 6. Model Mapping
        model.addAttribute("user", customer);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("deliveryFee", deliveryFee);
        model.addAttribute("packagingFee", packagingFee);
        model.addAttribute("subtotal", String.format("%.2f", subtotal));
        model.addAttribute("total", String.format("%.2f", total));

        return "customer/checkout";
    }

    @GetMapping("/customer/orders")
    public String showCustomerOrdersPage(Model model, HttpSession session) {
        Object sessionUser = session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        if (sessionUser instanceof Customer && "CUSTOMER".equals(role)) {
            Customer customer = (Customer) sessionUser;

            // Fetch all orders for this customer (ideally ordered by date descending)
            List<Order> allOrders = orderRepository.findByCustomerOrderByOrderDateDesc(customer);

            List<Order> ongoingOrders = new ArrayList<>();
            List<Order> completedOrders = new ArrayList<>();

            // Split orders based on status
            for (Order order : allOrders) {
                String status = order.getStatus().toUpperCase();
                if (status.equals("DELIVERED") || status.equals("CANCELLED")) {
                    completedOrders.add(order);
                } else {
                    // PENDING, CONFIRMED, PREPARING, SHIPPED, etc.
                    ongoingOrders.add(order);
                }
            }

            model.addAttribute("user", customer);
            model.addAttribute("ongoingOrders", ongoingOrders);
            model.addAttribute("completedOrders", completedOrders);

            return "customer/orders"; // Fixed the leading slash
        }

        return "redirect:/login";
    }


    // --- Admin Dashboard ---
    @GetMapping("/admin/dashboard")
    public String showAdminDashboardPage(HttpSession session, Model model) {
        // 1. Security Check
        Object sessionUser = session.getAttribute("loggedUser");
        if (sessionUser == null || !"ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        // 2. Calculate Total Orders
        long totalOrders = orderRepository.count();

        // 3. Calculate Total Revenue (Only counting DELIVERED orders)
        List<Order> allOrders = orderRepository.findAll();
        double totalRevenue = allOrders.stream()
                .filter(order -> "DELIVERED".equals(order.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();

        // 4. Calculate Total Customers
        long totalCustomers = customerRepository.count();

        // 5. Fetch 5 Most Recent Orders for the preview table
        List<Order> recentOrders = orderRepository.findAllByOrderByOrderDateDesc().stream()
                .limit(5)
                .collect(Collectors.toList());

        // 6. Add everything to the model
        model.addAttribute("admin", sessionUser);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("activePage", "dashboard");

        return "admin/dashboard";
    }

    // --- Product Management ---
    @GetMapping("/admin/product")
    public String showAdminProductPage(Model model, HttpSession session) {
        Object loggedUser = session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        if (!(loggedUser instanceof Admin) || !"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("admin", loggedUser);
        model.addAttribute("activePage", "products");
        return "admin/product";
    }

    // --- Admin Management (Super Admin Only) ---
    @GetMapping("/admin/adminManagement")
    public String showManagementPage(Model model, HttpSession session) {
        Object loggedUser = session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        if (loggedUser instanceof Admin && "ADMIN".equals(role)) {
            Admin loggedIn = (Admin) loggedUser;
            // Check for Super Admin privileges
            if (!loggedIn.isSuperAdmin()) {
                return "redirect:/admin/dashboard";
            }
            model.addAttribute("admin", loggedIn);
            model.addAttribute("admins", adminRepository.findAll());
            model.addAttribute("activePage", "management");
            return "Admin/adminManagement";
        }

        return "redirect:/login";
    }

    // --- Admin Profile ---
    @GetMapping("/admin/profile")
    public String showProfilePage(Model model, HttpSession session) {
        Object loggedUser = session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        if (!(loggedUser instanceof Admin) || !"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        model.addAttribute("admin", loggedUser);
        model.addAttribute("activePage", "profile");
        return "Admin/profile";
    }

    // --- Customer Management ---
    @GetMapping("/admin/customers")
    public String showCustomerPage(Model model, HttpSession session) {
        Object loggedUser = session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        if (!(loggedUser instanceof Admin) || !"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        model.addAttribute("admin", loggedUser);
        model.addAttribute("activePage", "customers");
        model.addAttribute("customers", customerRepository.findAll());
        return "admin/customers";
    }

    // --- Feedback Management ---
    @GetMapping("/admin/feedback")
    public String showAdminFeedbackPage(Model model, HttpSession session) {
        // 1. Security Check (ensure only admins can access)
        Object sessionUser = session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");

        if (sessionUser == null || !"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        // 2. Fetch all feedback
        List<Feedback> allFeedback = feedbackRepository.findAllByOrderByCreatedAtDesc();

        // 3. Calculate statistics
        double averageRating = 0.0;
        int count5Star = 0;

        if (!allFeedback.isEmpty()) {
            double totalStars = 0;
            for (Feedback f : allFeedback) {
                totalStars += f.getRating();
                if (f.getRating() == 5) count5Star++;
            }
            averageRating = totalStars / allFeedback.size();
        }

        // 4. Pass data to the view
        model.addAttribute("admin", sessionUser);
        model.addAttribute("feedbacks", allFeedback);
        model.addAttribute("totalReviews", allFeedback.size());
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("count5Star", count5Star);

        // Tells the sidebar to highlight the "Feedback" link
        model.addAttribute("activePage", "feedback");

        return "admin/feedback";
    }

    // --- Orders Management ---
    @GetMapping("/admin/orders")
    public String showOrdersPage(@RequestParam(name = "filter", required = false, defaultValue = "ALL") String
                                         filter, Model model, HttpSession session) {
        // Security Check: Only Admins allowed
        Object sessionUser = session.getAttribute("loggedUser");
        String role = (String) session.getAttribute("role");
        if (sessionUser == null || !"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        List<Order> orders;

        // Check what filter the admin clicked
        if ("ALL".equalsIgnoreCase(filter)) {
            orders = orderRepository.findAllByOrderByOrderDateDesc();
        } else {
            // Fetch only the specific status
            orders = orderRepository.findByStatusOrderByOrderDateDesc(filter.toUpperCase());
        }

        // Add data to the model for Thymeleaf
        model.addAttribute("orders", orders);

        model.addAttribute("currentFilter", filter.toUpperCase()); // To highlight the active tab in HTML

        // Fetch the LIVE fees from the Service
        model.addAttribute("deliveryFee", orderService.getDeliveryFee());
        model.addAttribute("packagingFee", orderService.getPackagingFee());

        model.addAttribute("admin", sessionUser);
        model.addAttribute("activePage", "orders"); // Highlights the sidebar

        return "admin/orders";
    }

}