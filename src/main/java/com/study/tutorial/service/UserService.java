package com.study.tutorial.service;

import java.util.Collections;
import java.util.Optional;

import com.study.tutorial.dto.UserDto;
import com.study.tutorial.entity.Authority;
import com.study.tutorial.entity.User;
import com.study.tutorial.repository.UserRepository;
import com.study.tutorial.util.SecurityUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User signup(UserDto userDto){
        // 회원가입시 username 을 기준으로 데이터베이스에 있는지 확인하고
        if(userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null){
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        // 권한정보 추가하고
        Authority authority = Authority.builder()
                                .authorityName("ROLE_USER")
                                .build();

        // user정보를 추가해서 저장한다.
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();
        
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(String username) {
        // username을 기준으로 가져오고
        return userRepository.findOneWithAuthoritiesByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> getMyUserWithAuthorities() {
        // 현재 securityContext에 저장된 username의 정보만 가져온다.
        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
    }

    
}
