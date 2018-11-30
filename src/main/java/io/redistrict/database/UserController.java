package io.redistrict.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
public class UserController {

    @Autowired
    private UserDao userDao;

    @PostMapping(value = "/register")
    @ResponseBody
    public String addNewUser(@RequestParam(value="username") String username, @RequestParam(value="password") String password,
                             @RequestParam(value="verifypassword") String verifyPassword, @RequestParam(value="email") String email, @RequestParam(value="admin") String admin) {

        if (userDao.findByUsername(username.toLowerCase()) != null) {
            return "This username is taken";
        }

        if (!verifyPassword.equals(password)) {
            return "Passwords do not match";
        }

        Password passwordObj = new Password();
        String salt = passwordObj.getNextSalt();
        String sha256Hex = passwordObj.hashPassword(salt, password);

        User user = new User(username.toLowerCase(), sha256Hex, email, salt, admin.equals("true"));
        userDao.save(user);


        return "Success";

    }

    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestParam(value="username") String username, @RequestParam(value="password") String password) {
        User user = userDao.findByUsername(username.toLowerCase());
        if (user == null) {
            return "Incorrect username/password";
        }

        Password passwordObj = new Password();
        String sha256Hex = passwordObj.hashPassword(user.getSalt(), password);
        if (!user.getPassword().equals(sha256Hex)) {
            return "Incorrect username/password";
        }

        return "Success";
    }


}
