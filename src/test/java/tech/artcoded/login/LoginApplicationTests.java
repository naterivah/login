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
        // given
        String username = "admin";
        String passsword = "test";
        String urlUserInfo = "/user/info";
        String headerToken = "x-auth-token";
        // when
        ResponseEntity<User> body = this.restTemplate.withBasicAuth(username, passsword)
                .postForEntity(urlUserInfo,null,User.class);
        // then
        Assertions.assertThat(body).isNotNull();
        Assertions.assertThat(body.getBody()).isNotNull();
        Assertions.assertThat(body.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(body.getBody().getUsername()).isEqualTo(username);
        Assertions.assertThat(body.getBody().getPassword()).isNull();
        Assertions.assertThat(body.getBody().getRoles()).containsExactlyInAnyOrder(ADMIN, USER);

        List<String> xAuthTokens = body.getHeaders().get(headerToken);
        Assertions.assertThat(xAuthTokens).isNotNull().isNotEmpty();

        // given
        String token = xAuthTokens.get(0);
        HttpHeaders headers = new HttpHeaders();
        headers.set(headerToken, token);
        HttpEntity<Void> request = new HttpEntity<>(null, headers);
        // when
        ResponseEntity<User> user = this.restTemplate
                .postForEntity(urlUserInfo,request,User.class);
        // then
        Assertions.assertThat(user).isNotNull();
        Assertions.assertThat(user.getBody()).isNotNull();
        Assertions.assertThat(user.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(user.getBody().getUsername()).isEqualTo(username);
        Assertions.assertThat(user.getBody().getPassword()).isNull();
        Assertions.assertThat(user.getBody().getRoles()).containsExactlyInAnyOrder(ADMIN, USER);
    }

}
