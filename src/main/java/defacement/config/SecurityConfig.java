package defacement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                            "/js/**",
                            "/images/**",
                            "/favicon.ico"
                        ).permitAll()
                        .requestMatchers("/login", "/error", "/access-denied", "/contacts").permitAll()
                        .requestMatchers("/default").authenticated()
                        .requestMatchers("/change-password").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/targets/**").hasRole("ADMIN")
                        .requestMatchers("/indicators/**").hasRole("ADMIN")
                        .requestMatchers("/dashboard/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/logs/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/user/**").hasRole("USER")
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/default", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)  // invalidate session
                        .deleteCookies("JSESSIONID")  // remove session cookie
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied")
                );

        http.addFilterAfter(new NoCacheFilter(), org.springframework.security.web.authentication.logout.LogoutFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
