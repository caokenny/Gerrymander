package io.redistrict.index;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @RequestMapping("/")
    public String home(HttpServletRequest request, Model model) {
        if (!request.isUserInRole("USER")) {
            model.addAttribute("loggedIn", false);
        }
        if (request.isUserInRole("ADMIN")) {
            model.addAttribute("adminUser", true);
        }
        return "index";
    }

    @RequestMapping("/about")
    public String aboutPage() {
        return "about";
    }
}
