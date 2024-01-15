package com.project.commerce.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers(
                                        "/store/read/**", "/store/search/**",
                                        "/product/read/**/**","/product/search/**/**",
                                        "/cart/read")
                                .hasAnyRole("USER", "ADMIN")
                                .requestMatchers(
                                        "/store/register","/store/update","/store/delete/**",
                                        "/product/register/**", "/product/update/**","/product/delete/**/**")
                                .hasRole("ADMIN")
                                .requestMatchers("/cart/add", "/cart/update", "/cart/clear")
                                .hasRole("USER")
                                .requestMatchers(
                                        "/**/signup", "/**/signin", "/error")
                                .permitAll()
                                .anyRequest().authenticated()
                );
        http.addFilterBefore(this.jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
