package com.atalibdev.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class ApplicationSecurity {

    private final AppUserDetailsService appUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authentication = new DaoAuthenticationProvider();
        authentication.setUserDetailsService(appUserDetailsService);
        authentication.setPasswordEncoder(passwordEncoder);
        return authentication;
    }

    @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
       httpSecurity.csrf(AbstractHttpConfigurer::disable);
       httpSecurity.authorizeHttpRequests(auth -> auth
               .requestMatchers("/registration/**","/home", "/login", "/logout").permitAll()
               .anyRequest().authenticated()
               ).formLogin(form -> form.loginPage("/login").permitAll()
                       .usernameParameter("email")
                       .defaultSuccessUrl("/home"))
               .logout((logout) -> logout.logoutSuccessUrl("/login")
                       .permitAll().invalidateHttpSession(true)
                       .clearAuthentication(true).logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                       .logoutSuccessUrl("/login"));
       return httpSecurity.build();
   }
}
