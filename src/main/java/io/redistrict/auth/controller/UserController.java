package io.redistrict.auth.controller;

import io.redistrict.auth.model.User;
import io.redistrict.auth.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value = "/register")
    public String addNewUser(@ModelAttribute("user") User user, Model model) {

        if (userDao.findByUsername(user.getUsername().toLowerCase()) != null) {
            model.addAttribute("registerFail", true);
            model.addAttribute("userTaken", true);
            return "index";
        }

        if (!user.getPasswordConfirm().equals(user.getPassword())) {
            model.addAttribute("pwNoMatch", true);
            return "index";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPasswordConfirm(user.getPassword());
        userDao.save(user);

        model.addAttribute("loggedIn", true);
        model.addAttribute("username", user.getUsername());
        return "index";
    }

    @GetMapping("/logout")
    public String logout(Model model) {
        model.addAttribute("loggedIn", false);
        return "index";
    }


    @PostMapping("/login")
    public String login(@ModelAttribute("user") User user, Model model) {

        User dbUser = userDao.findByUsername(user.getUsername().toLowerCase());

        if (dbUser == null) {
            model.addAttribute("loginFail", true);
            model.addAttribute("loggedIn", false);
            return "index";
        }

        if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            model.addAttribute("loginFail", true);
            model.addAttribute("loggedIn", false);
            return "index";
        }

        model.addAttribute("loggedIn", true);
        model.addAttribute("username", user.getUsername());
        return "index";
    }


}
