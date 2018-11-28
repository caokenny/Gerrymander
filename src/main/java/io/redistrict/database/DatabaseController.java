package io.redistrict.database;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
//@RequestMapping("/db")
public class DatabaseController {

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/register")
    @ResponseBody
    public String addNewUser(@RequestParam(value="username") String username, @RequestParam(value="password") String password,
                      @RequestParam(value="verifypassword") String verifyPassword, @RequestParam(value="email") String email, @RequestParam(value="admin") String admin,
                      HttpServletRequest request) {

        if (!verifyPassword.equals(password)) {
            return "Passwords do not match";
        }

        Iterable<User> iterable = userRepository.findAll();

        for (User user : iterable) {
            if (user.getUsername().toLowerCase().equals(username.toLowerCase())) {
                return "Username is already in use";
            }
            if (user.getEmail().toLowerCase().equals(email.toLowerCase())) {
                return "Email is already in use";
            }
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        if (admin.equals("true")) {
            user.setAdmin(true);
        } else {
            user.setAdmin(false);
        }
        userRepository.save(user);

        request.getSession().setAttribute("username", username);
        request.getSession().setAttribute("loggedInUser", true);
        return "Success";
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestParam(value="username") String username, @RequestParam(value="password") String password, HttpServletRequest request) {
        Iterable<User> iterable = userRepository.findAll();

        for (User user : iterable) {
            System.out.println(user.getUsername());
            System.out.println(username);
            if (user.getUsername().toLowerCase().equals(username.toLowerCase())) {
                if (user.getPassword().equals(password)) {
                    request.getSession().setAttribute("username", username);
                    request.getSession().setAttribute("loggedInUser", true);
                    return "Success";
                }
            }
        }

        return "Incorrect username/password";
    }

    @PostMapping("/logout")
    @ResponseBody
    public String logout(HttpServletRequest request) {
        request.getSession().setAttribute("username", null);
        request.getSession().setAttribute("loggedInUser", null);
        return "Success";
    }

}
