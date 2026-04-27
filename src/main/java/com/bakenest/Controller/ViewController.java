package com.bakenest.Controller;

import com.bakenest.Model.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ViewController {

    @GetMapping("/home")
    public String showHomePage(Model model) {
        model.addAttribute("backendMessage", "Hello! This data came from the Spring Backend.");
        return "index";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        // THIS IS THE FIX: Pass an empty customer object for the register form
        model.addAttribute("customer", new Customer());
        return "login";
    }

    @GetMapping("/product")
    public String showProductPage(Model model) {
        // THIS IS THE FIX: Pass an empty customer object for the register form
        model.addAttribute("customer", new Customer());
        return "product";
    }
}