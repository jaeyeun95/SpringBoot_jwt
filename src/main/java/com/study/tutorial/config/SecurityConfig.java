package com.study.tutorial.config;

import javax.annotation.security.PermitAll;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    

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
            .authorizeRequests()    // HttpServletRequest 를 사용하는 모든 요청들에 대한 접근제한을 설정한다.
            .antMatchers("/api/hello").permitAll()  // "/api/hello" 에는 인증 없이 접근할수 있다.
            .anyRequest().authenticated();  // 나머지 요청들은 모두 인증받아야 한다.
    }

    
    
}
