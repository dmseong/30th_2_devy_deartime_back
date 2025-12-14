package com.project.deartime.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("!test")
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 비활성화 (API 테스트를 위해)
                .csrf(csrf -> csrf.disable())

                // 2. HTTP Basic 인증 및 Form Login 비활성화
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())

                // 3. 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        // 🚨 개발/테스트 중: 모든 /api/v1/letters/** 경로 접근을 허용합니다. 🚨
                        .requestMatchers("/api/**").permitAll()

                        // 나머지 요청은 인증 필요 (나중에 JWT 구현 시 변경)
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
