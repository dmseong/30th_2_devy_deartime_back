package com.project.deartime.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // ⬅️ 이 줄을 추가합니다.
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@Profile("test")
public class MockSecurityConfig {

    private static final Long TEMP_USER_ID = 1L;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // 1. 모든 API 요청을 허용 (인증 필터 무시)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().permitAll()
                )

                // 2. 가짜 인증 필터를 필터 체인에 추가합니다.
                .addFilterBefore(new MockAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 모든 요청에 대해 SecurityContext에 지정된 사용자 ID (100L)를 가진 인증 객체를 강제로 설정하는 필터
     */
    private static class MockAuthenticationFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {

            // 1. 임시 Principal (사용자 ID) 생성
            Object principal = TEMP_USER_ID;

            // 2. 권한은 부여하지 않은 단순 인증 토큰 생성
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    principal,
                    null, // Credential은 null
                    null  // 권한(Authorities)은 null
            );

            // 3. SecurityContext에 강제 주입
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);
        }
    }
}
