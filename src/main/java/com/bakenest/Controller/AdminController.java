package com.bakenest.Controller;

import com.bakenest.Model.Admin;
import com.bakenest.Service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/management/add")
    public String addAdmin(@ModelAttribute Admin admin, RedirectAttributes ra) {
        try {
            adminService.registerAdmin(admin);
            ra.addFlashAttribute("success", "New administrator added!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/adminManagement";
    }

    @PostMapping("/management/update")
    public String updateAdmin(@ModelAttribute Admin admin, RedirectAttributes ra, HttpSession session) {
        try {
            adminService.updateAdmin(admin);

            // REFRESH SESSION LOGIC:
            Admin loggedIn = (Admin) session.getAttribute("loggedUser");
            if (loggedIn != null && loggedIn.getId().equals(admin.getId())) {
                // Update the session object so the UI reflects changes immediately
                loggedIn.setSuperAdmin(admin.isSuperAdmin());
                loggedIn.setName(admin.getName());
                loggedIn.setEmail(admin.getEmail());
                session.setAttribute("loggedUser", loggedIn);
            }

            ra.addFlashAttribute("success", "Admin powers updated!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/adminManagement";
    }

    @PostMapping("/management/delete/{id}")
    public String deleteAdmin(@PathVariable Long id, RedirectAttributes ra) {
        try {
            adminService.deleteAdmin(id);
            ra.addFlashAttribute("success", "Admin removed successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/adminManagement";
    }

    @PostMapping("/profile/update")
    public String updateAdminProfile(@ModelAttribute Admin admin, RedirectAttributes ra, HttpSession session) {
        try {
            adminService.updateAdmin(admin);

            // REFRESH SESSION LOGIC:
            Admin loggedIn = (Admin) session.getAttribute("loggedUser");
            if (loggedIn != null && loggedIn.getId().equals(admin.getId())) {
                // Update the session object so the UI reflects changes immediately
                loggedIn.setSuperAdmin(admin.isSuperAdmin());
                loggedIn.setName(admin.getName());
                loggedIn.setEmail(admin.getEmail());
                session.setAttribute("loggedUser", loggedIn);
            }

            ra.addFlashAttribute("success", "Admin details updated!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/profile";
    }
}