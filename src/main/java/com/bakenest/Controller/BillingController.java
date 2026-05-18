package com.bakenest.Controller;

import com.bakenest.model.Bill;
import com.bakenest.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer/billing")
    
public class BillingController {

    @Autowired
    private BillingService billingService;

    @PostMapping("/generate/{orderId}")
    public String generateBill(@PathVariable Long orderId,
                               RedirectAttributes redirectAttributes) {

        Bill bill = billingService.generateBill(orderId);

        if (bill != null) {
            redirectAttributes.addFlashAttribute("success",
                    "Bill generated for Order #" + orderId);
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Bill generation failed");
        }

        return "redirect:/customer/orders";
    }

    @PostMapping("/pay")
    public String payBill(@RequestParam Long orderId,
                          @RequestParam String paymentMethod,
                          @RequestParam double discount,
                          RedirectAttributes redirectAttributes) {

        if (discount < 0 || discount > 100) {
            discount = 0;
        }

        Bill bill = billingService.processPayment(orderId, paymentMethod, discount);

        if (bill != null) {
            redirectAttributes.addFlashAttribute("success",
                    "Payment successful. Final Amount: Rs. " + bill.getFinalAmount());
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Payment failed");
        }

        return "redirect:/customer/orders";
    }
}
