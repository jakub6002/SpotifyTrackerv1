package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {


    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(authorizeRequests ->
                            authorizeRequests
                                    .requestMatchers("/login", "/error", "/webjars/**", "/css/**", "/images/**").permitAll() // Allow static resources
                                    .anyRequest().authenticated()
                    )
                    .oauth2Login(oauth2Login ->
                            oauth2Login
                                    .loginPage("/login")
                                    .defaultSuccessUrl("/home", true) // Redirect to /home after successful login
                                    .failureUrl("/login?error=true")
                    )
                    .logout(logout ->
                            logout.logoutSuccessUrl("/login").permitAll()
                    )
                    .sessionManagement(sessionManagement ->
                            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    );

            return http.build();
        }
    }
}