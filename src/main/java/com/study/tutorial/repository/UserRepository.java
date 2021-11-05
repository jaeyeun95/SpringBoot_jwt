package com.study.tutorial.repository;

import java.util.Optional;

import com.study.tutorial.entity.User;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long>{
    // EntityGraph -> 쿼리문이 수행될 떄 Lazy조회가 아니고 Eager 조회로 authorities 정보를 같이 가져온다.
    @EntityGraph(attributePaths = "authorities")
    // username을 기준으로 User의 정보를 가져올 때 권한 정보도 같이 가져온다.
    Optional<User> findOneWithAuthoritiesByUsername(String username);
    
}
