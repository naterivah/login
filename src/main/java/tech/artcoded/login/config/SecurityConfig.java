package tech.artcoded.login.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import tech.artcoded.login.repository.UserRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static tech.artcoded.login.entity.Role.ADMIN;
import static tech.artcoded.login.entity.Role.USER;


@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().authorizeRequests()

                .antMatchers(HttpMethod.DELETE, "/**").hasAnyAuthority(ADMIN.getAuthority())

                .antMatchers(HttpMethod.POST, "/admin/**").hasAuthority(ADMIN.getAuthority())
                .antMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority(ADMIN.getAuthority(), USER.getAuthority())

                .antMatchers(HttpMethod.GET, "/user/**").hasAuthority(USER.getAuthority())
                .antMatchers(HttpMethod.POST, "/user/**").hasAuthority(USER.getAuthority())

                .antMatchers(HttpMethod.GET, "/proxy/**").hasAuthority(ADMIN.getAuthority())
                .antMatchers(HttpMethod.POST, "/proxy/**").hasAuthority(ADMIN.getAuthority())
                .antMatchers(HttpMethod.PUT, "/proxy/**").hasAuthority(ADMIN.getAuthority())

                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                .anyRequest().permitAll()
                .and().csrf().disable()
                .requestCache()
                .requestCache(new NullRequestCache()).and()
                .httpBasic().realmName("login").and()
                .exceptionHandling()
                .authenticationEntryPoint((req, resp, e) -> resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                .formLogin().disable()
        ;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, BCryptPasswordEncoder passwordEncoder) throws Exception {
        auth.userDetailsService(userRepository).passwordEncoder(passwordEncoder);
    }
    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return HeaderHttpSessionIdResolver.xAuthToken();
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;


}

