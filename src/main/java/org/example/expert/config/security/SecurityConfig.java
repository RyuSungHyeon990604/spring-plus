package org.example.expert.config.security;

import lombok.RequiredArgsConstructor;
import org.example.expert.config.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //jwt 와 같이 사용중이므로 spring security에서는 인가와 관련된 작업만 설정

        http.csrf(AbstractHttpConfigurer::disable) //폼 기반 로그인 방식이 아니라면 보통 CSRF를 비활성화함
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .formLogin(AbstractHttpConfigurer::disable)//로그인은 따로 구현된 api로 처리
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)//명시적으로 순서 지정 UsernamePasswordAuthenticationFilter는 위에서 비활성화 됨
            //인가 설정
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/auth/**").permitAll()//회원가입과 로그인은 모두 허용
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()//나머지는 인증된 사용자만
            )
            //세션은 사용하지않도록 설정
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
