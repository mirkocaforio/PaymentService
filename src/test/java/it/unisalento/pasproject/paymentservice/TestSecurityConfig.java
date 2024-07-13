package it.unisalento.pasproject.paymentservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class TestSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(AbstractHttpConfigurer::disable);

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/payment/analytics/admin").hasRole("ADMIN")
                .requestMatchers("/api/payment/analytics/user").hasRole("UTENTE")
                .requestMatchers("/api/invoice/find/all").hasRole("UTENTE")
                .requestMatchers("/api/invoice/find").hasRole("UTENTE")
                .requestMatchers("/api/payment/pay/{id}").hasRole("UTENTE")
                .requestMatchers("/api/settings/payment/get").hasRole("UTENTE")
                .requestMatchers("/api/settings/payment/update").hasRole("ADMIN")
                .anyRequest().authenticated()
        );

        return http.build();
    }
}
