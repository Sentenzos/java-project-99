package hexlet.code.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to Spring";
    }

//    @GetMapping("/")
//    public String ind() {
//        return null;
//    }

//    @GetMapping("/api/login")
//    public String login() {
//        return null;
//    }
}
