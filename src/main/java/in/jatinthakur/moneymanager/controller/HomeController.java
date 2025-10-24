package in.jatinthakur.moneymanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/status", "/health" ,"/api/v1/home"})
public class HomeController {
    @GetMapping
    public String healthCheck() {
        return "Application is running smoothly.";
    }
}
