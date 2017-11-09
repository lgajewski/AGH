package pl.edu.agh.iosr.raft.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    private String message = "Hello World";

    @RequestMapping("/home")
    public String home(Map<String, Object> model) {
        model.put("message", this.message);
        return "home";
    }

}
