package com.example.studylearnspringposts.security;

import com.example.studylearnspringposts.util.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //csrf 는 사용하지않게변경
        http.csrf((auth)->  auth.disable());
        //form 로그인방식도 비활성화한다.
        http.formLogin((auth) -> auth.disable());
        //httpbasic 도
        http.httpBasic((auth) -> auth.disable());


        // 경로별 인가작업을 진행하는 메소드 체인
        http.authorizeHttpRequests((auth) ->
            auth.requestMatchers("/api/v1/*").permitAll()
                .requestMatchers("/api/v1/auth/*").permitAll()
                .requestMatchers("/board").permitAll()
                .anyRequest().authenticated() // 다른모든요청에대해서는 로그인한 사용자만 허용
            );

        // jwt 로그인연동위한 : 필터등록 > At은 대체를함.
        // LoginFilter 에 인자로넘기기위해 새로운 Bena을 등록해야함 > AuthenticationManager
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration)), UsernamePasswordAuthenticationFilter.class); // 두번재인자는 위치

        // jwt 에서는 stateless 하게 관리하기위해 세션설정을 추가로해준다.
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));



        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration ) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
