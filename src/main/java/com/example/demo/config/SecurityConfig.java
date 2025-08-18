package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // API 서버는 일반적으로 세션을 사용하지 않으므로 csrf 보호 비활성화
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                // 예매(/reserve) API는 반드시 인증된(로그인한) 사용자만 호출 가능
                .requestMatchers("/api/performances/*/reserve").authenticated()
                // 그 외 모든 요청은 인증 없이 허용
                .anyRequest().permitAll()
            )
            // OAuth2 리소스 서버 설정을 활성화하고, JWT를 사용하도록 지정
            .oauth2ResourceServer(oauth2 -> oauth2.jwt());

        return http.build();
    }
}