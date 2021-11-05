package com.study.tutorial.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.study.tutorial.entity.User;
import com.study.tutorial.repository.UserRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component("userDetailService")
public class CustomUserDetailService implements UserDetailsService{
    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username){
        // 로그인시 DB에서 유저정보와 권한정보를 가져와서
        return userRepository.findOneWithAuthoritiesByUsername(username)
                .map(user -> createUser(username, user))
                .orElseThrow(() -> new UsernameNotFoundException(username +" -> 데이터베이스에서 찾을 수 없습니다."));
    }


    private org.springframework.security.core.userdetails.User createUser(String username, User user) {
        if (!user.isActivated()){
            throw new RuntimeException(username + " -> 활성화되어 있지 않습니다. ");
        }
        // 권한이 있다면 
        // userdetails.User 객체를 생성해서 리턴한다.
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                                                    .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                                                    .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }

    
    
    
}
