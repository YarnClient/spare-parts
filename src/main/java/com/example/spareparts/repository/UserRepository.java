package com.example.spareparts.repository;

import com.example.spareparts.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNickname(String nickname);

    Optional<User> findByOpenid(String openid);

    boolean existsByNickname(String nickname);
}
