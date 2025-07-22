package org.example.eventmanager.security;


import org.example.eventmanager.security.jwt.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;
    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers(HttpMethod.POST, "/users")
                                .permitAll()

                                .requestMatchers(HttpMethod.GET, "/users/{userId}")
                                .hasAnyAuthority("ADMIN")

                                .requestMatchers(HttpMethod.POST, "/users/auth")
                                .permitAll()


                                .requestMatchers(HttpMethod.POST, "/events")
                                .hasAnyAuthority("USER")

                                .requestMatchers(HttpMethod.DELETE, "events/{eventId}")
                                .hasAnyAuthority("ADMIN", "USER")

                                .requestMatchers(HttpMethod.GET, "events/{eventId}")
                                .hasAnyAuthority("ADMIN", "USER")

                                .requestMatchers(HttpMethod.PUT, "events/{eventId}")
                                .hasAnyAuthority("ADMIN", "USER")

                                .requestMatchers(HttpMethod.POST, "/events/search")
                                .hasAnyAuthority("ADMIN", "USER")

                                .requestMatchers(HttpMethod.GET, "/events/my")
                                .hasAuthority("USER")


                                .requestMatchers(HttpMethod.POST, "/events/registrations/")
                                .hasAuthority("USER")

                                .requestMatchers(HttpMethod.DELETE, "/events/registrations/cancel/")
                                .hasAuthority("USER")

                                .requestMatchers(HttpMethod.GET, "/events/registrations/my")
                                .hasAuthority("USER")


                                .requestMatchers(HttpMethod.POST, "/locations")
                                .hasAnyAuthority("ADMIN")

                                .requestMatchers(HttpMethod.DELETE, "/locations/{locationId}")
                                .hasAnyAuthority("ADMIN")

                                .requestMatchers(HttpMethod.PUT, "/locations/{locationId}")
                                .hasAnyAuthority("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/locations/**")
                                .hasAnyAuthority("ADMIN", "USER")

                                .requestMatchers(HttpMethod.GET, "/**")
                                .permitAll()

                                .anyRequest().authenticated()
                )
                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(customAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler)
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
