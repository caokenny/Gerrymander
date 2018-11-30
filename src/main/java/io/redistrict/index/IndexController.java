package io.redistrict.index;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
//@RequestMapping("/index")
public class IndexController {

    @RequestMapping("/")
    public String home() {
        return "index";
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
