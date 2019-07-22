package tech.artcoded.login.fixture;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.nio.charset.Charset;

@RestController
@Slf4j
@Profile("dev")
public class HomeController {
    private final String homepageCached;

    @SneakyThrows
    public HomeController() {
        try (InputStream is = new ClassPathResource("template/index.html").getInputStream()) {
            this.homepageCached = IOUtils.toString(is, Charset.defaultCharset());
        }
    }

    @GetMapping(value = {"", "/"}, produces = MediaType.TEXT_HTML_VALUE)
    @SneakyThrows
    public String homepage() {
        log.info("new request for the basic homepage, only for dev purpose!");
        return this.homepageCached;
    }
}
