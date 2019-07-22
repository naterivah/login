package tech.artcoded.login.profile.dev;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tech.artcoded.login.entity.User;
import tech.artcoded.login.repository.UserRepository;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@Profile("dev")
@Slf4j
public class UserFixture implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserFixture(UserRepository userRepository, ObjectMapper objectMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        userRepository.deleteAll();
        User[] users = objectMapper.readValue(new ClassPathResource("user-fixture.json").getInputStream(), User[].class);

        userRepository.saveAll(Stream.of(users).map(user -> user.toBuilder().password(passwordEncoder.encode(user.getPassword())).build()).collect(Collectors.toList()));


    }
}
