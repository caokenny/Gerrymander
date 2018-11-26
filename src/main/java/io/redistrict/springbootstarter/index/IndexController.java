package io.redistrict.springbootstarter.index;

import io.redistrict.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class IndexController {

//    @RequestMapping("/")
//    public String home() {
//        return "index";
//    }

    @PostMapping("/register")
    public String register(HttpServletRequest request) {
        String username = request.getParameter("username");
        String pass = request.getParameter("password");
        String email = request.getParameter("email");
        User user = new User(username, pass, email);
        return "loggedIn";
    }

//    @PostMapping("/login")
//    public String login(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String username = request.getParameter("username");
//        String pass = request.getParameter("password");
//        request.getSession().setAttribute("currentUser", username);
//        //CHECK WITH DATABASE
//        return "loggedIn";
//    }

    @PostMapping("/login")
    public String login(@RequestParam(value = "username") String username, HttpServletRequest request) {
        request.getSession().setAttribute("username", username);
        request.getSession().setAttribute("loggedInUser", true);
        return "redirect:/index";
    }

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/color")
    public void color() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            if (i % 2 == 0) {
                this.template.convertAndSend("/index/change", "red");
                Thread.sleep(1000);
            } else {
                this.template.convertAndSend("/index/change", "blue");
                Thread.sleep(1000);
            }
        }
    }
}
