package io.redistrict.auth.controller;

import io.redistrict.auth.model.CustomUserDetails;
import io.redistrict.auth.model.Role;
import io.redistrict.auth.model.User;
import io.redistrict.auth.repository.RoleDao;
import io.redistrict.auth.repository.UserDao;
import io.redistrict.auth.service.CustomUserDetailsService;
import io.redistrict.auth.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserDao userDao;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private UserValidator userValidator;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping(value = "/register")
    public String registration(@ModelAttribute("userForm") User userForm, @RequestParam(value = "page", required = false) String page, BindingResult bindingResult, Model model) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("registerFail", true);
            model.addAttribute("loggedIn", false);
            for (Object object : bindingResult.getAllErrors()) {
                if (object instanceof FieldError) {
                    FieldError fieldError = (FieldError) object;
                    model.addAttribute(fieldError.getCode(), true);
                }
            }
            if (page.equals("admin")) {
                return "admin";
            }
            return "index";
        }

//        Role adminRole = createIfNotFound("ROLE_ADMIN");
        Role userRole = createIfNotFound("ROLE_USER");

        Set<Role> roleSet = new HashSet<>();

        userForm.setUsername(userForm.getUsername().toLowerCase());
        String rawPassword = userForm.getPassword();
        userForm.setPassword(passwordEncoder.encode(userForm.getPassword()));

//        adminRole.getUsers().add(userForm);
        userRole.getUsers().add(userForm);

//        roleSet.add(adminRole);
        roleSet.add(userRole);

        userForm.setRoles(roleSet);

        userDao.save(userForm);

//        if (userForm.getRoles().contains(roleDao.findByName("ROLE_ADMIN"))) {
//            model.addAttribute("adminUser", true);
//        }


        if (page.equals("user")) {
            autoLogin(userForm.getUsername().toLowerCase(), rawPassword);
        }

        if (page.equals("admin")) {
            return "redirect:/secure/admin";
        }
        return "redirect:/";
    }

    private void autoLogin(String username, String password) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(customUserDetails, password, customUserDetails.getAuthorities());


        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }

    }

    private Role createIfNotFound(String name) {
        Role role = roleDao.findByName(name);
        if (role == null) {
            role = new Role();
            role.setName(name);
            role.setUsers(new HashSet<>());
            roleDao.save(role);
        }
        return role;
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {


        User user = userDao.findByUsername(userForm.getUsername().toLowerCase());

        if (user == null) {
            model.addAttribute("loginFail", true);
            model.addAttribute("loggedIn", false);
            return "index";
        }

        if (!passwordEncoder.matches(userForm.getPassword(), user.getPassword())) {
            model.addAttribute("loginFail", true);
            model.addAttribute("loggedIn", false);
            return "index";
        }

//        System.out.println(user.getRoles());
        if (user.getRoles().contains(roleDao.findByName("ROLE_ADMIN"))) {
            model.addAttribute("adminUser", true);
        }

        model.addAttribute("loggedIn", true);
        model.addAttribute("username", user.getUsername());

        autoLogin(user.getUsername().toLowerCase(), userForm.getPassword());

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("loggedIn", false);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/";
    }




}
