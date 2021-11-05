package com.study.tutorial.config;


import com.study.tutorial.jwt.JwtAuthenticationEntryPoint;
import com.study.tutorial.jwt.JwtSecurityConfig;
import com.study.tutorial.jwt.TokenProvider;
import com.study.tutorial.jwt.JwtAccessDeniedHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  // @PreAuthorize 어노테이션을 메소드 단위로 추가하기 위해서 적용
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(
        TokenProvider tokenProvider,
        JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
        JwtAccessDeniedHandler jwtAccessDeniedHandler){
            this.tokenProvider = tokenProvider;
            this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
            this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
            .antMatchers("/h2-console/**","/favicon.ico");
            // -> /h2-console/ 하위 모든 요청과 파비콘은 모두 무시하는 설정
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 토큰을 사용하기 때문에 csrf 설정은 disabled
            .csrf().disable() // HttpSecurity

            .exceptionHandling()
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler)

            // h2 콘솔을 위한 설정
            .and()
            .headers()
            .frameOptions()
            .sameOrigin()

            // 세션을 사용하지 않기 때문에 STATELESS 로 설정
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()
            .authorizeRequests()    // HttpServletRequest 를 사용하는 모든 요청들에 대한 접근제한을 설정한다.
            .antMatchers("/api/hello").permitAll()  // "/api/hello" 에는 인증 없이 접근할수 있다.
            .antMatchers("/api/authenticate").permitAll()  // 회원가입 API
            .antMatchers("/api/signup").permitAll()  // 로그인 API
            .anyRequest().authenticated()  // 나머지 요청들은 모두 인증받아야 한다.

            .and()
            .apply(new JwtSecurityConfig(tokenProvider));
    }

    
    
}
