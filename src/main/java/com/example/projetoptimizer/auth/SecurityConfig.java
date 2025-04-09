package com.example.projetoptimizer.auth;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final GithubTokenAuthenticationFilter githubTokenAuthenticationFilter;
    private static final String[] SWAGGER_PATHS = {"/context-path/v3/api-docs", "/context-path/swagger-ui.html", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**", "/webjars/swagger-ui/**"};


    public SecurityConfig(GithubTokenAuthenticationFilter githubTokenAuthenticationFilter) {
        this.githubTokenAuthenticationFilter = githubTokenAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> {
                            authz
                                    .requestMatchers(SWAGGER_PATHS).permitAll()
                                    .anyRequest().authenticated();
                        }
                )
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterAt(githubTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(withDefaults());

        return http.build();
    }
}