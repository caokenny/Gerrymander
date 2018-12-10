package io.redistrict.auth.controller;

import io.redistrict.auth.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/secure")
public class AdminController {

    @Autowired
    private UserDao userDao;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminSettings(Model model) {
        model.addAttribute("users", userDao.findAll());
        return "admin";
    }

}
