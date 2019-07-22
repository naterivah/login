package tech.artcoded.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import tech.artcoded.login.entity.Role;
import tech.artcoded.login.entity.User;
import tech.artcoded.login.repository.UserRepository;

 import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static tech.artcoded.login.entity.Role.ADMIN;
import static tech.artcoded.login.entity.Role.USER;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Before
    @SneakyThrows
    public void setup() {
        userRepository.deleteAll();
        userRepository.save(User.builder().username("admin").password(passwordEncoder.encode("test")).roles(Arrays.asList(ADMIN, USER)).build());
        userRepository.save(User.builder().username("user").password(passwordEncoder.encode("user")).roles(Arrays.asList(ADMIN, USER)).build());
    }

    @Test
    @SneakyThrows
    public void contextLoads() {
        ResponseEntity<User> body = this.restTemplate.withBasicAuth("admin", "test")
                .postForEntity("/user/info",null,User.class);
        Assertions.assertThat(body).isNotNull();
        Assertions.assertThat(body.getBody()).isNotNull();
        Assertions.assertThat(body.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> xAuthTokens = body.getHeaders().get("x-auth-token");
        Assertions.assertThat(xAuthTokens).isNotNull().isNotEmpty();
        String token = xAuthTokens.get(0);
        Assertions.assertThat(token).isNotEmpty();
        Assertions.assertThat(body.getBody().getUsername()).isEqualTo("admin");
        Assertions.assertThat(body.getBody().getPassword()).isNull();
        Assertions.assertThat(body.getBody().getRoles()).containsExactlyInAnyOrder(ADMIN, USER);

        // from token
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-auth-token", token);

        HttpEntity<Void> request = new HttpEntity<>(null, headers);
        ResponseEntity<User> user = this.restTemplate
                .postForEntity("/user/info",request,User.class);
        Assertions.assertThat(user).isNotNull();
        Assertions.assertThat(user.getBody()).isNotNull();
        Assertions.assertThat(user.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(user.getBody().getUsername()).isEqualTo("admin");
        Assertions.assertThat(user.getBody().getPassword()).isNull();
        Assertions.assertThat(user.getBody().getRoles()).containsExactlyInAnyOrder(ADMIN, USER);
    }

}
