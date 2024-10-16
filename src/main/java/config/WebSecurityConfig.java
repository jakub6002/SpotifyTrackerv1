package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/login", "/error", "/webjars/**").permitAll()  // Allow login and error pages
                                .anyRequest().authenticated()  // Protect all other endpoints
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .loginPage("/login")  // Custom login page
                                .defaultSuccessUrl("/home", true)  // Redirect to /home after successful login
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login").permitAll());  // Redirect to login page after logout

        return http.build();
    }
}
