package io.redistrict.Algorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AlgorithmController {
    @PostMapping(value = "algorithm")
    @ResponseBody
    public String startAlgorithm(@RequestParam(value = "a") String a, @RequestParam(value = "m1") String m1,
                                 @RequestParam(value = "m2") String m2, @RequestParam(value = "m3") String m3,
                                 @RequestParam(value = "m4") String m4) {
        System.out.println(a + " " + m2 + " " + m3 + " " + m4);
        return "Success";
    }
}
