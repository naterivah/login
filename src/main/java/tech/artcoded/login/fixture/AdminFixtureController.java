package tech.artcoded.login.fixture;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@CrossOrigin(value = "*", allowedHeaders = "*", exposedHeaders = "x-auth-token")
@Profile("dev")
@RequestMapping("/admin")
public class AdminFixtureController {
    @GetMapping("/welcome")
    public Map<String,String> adminPage(){
        return Collections.singletonMap("message","hello from admin controller!");
    }
}
