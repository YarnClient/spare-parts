package com.example.spareparts.service;

import com.example.spareparts.config.JwtUtil;
import com.example.spareparts.model.User;
import com.example.spareparts.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> register(String nickname, String password) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new RuntimeException("昵称不能为空");
        }
        if (password == null || password.length() < 6) {
            throw new RuntimeException("密码至少6位");
        }
        if (userRepo.existsByNickname(nickname.trim())) {
            throw new RuntimeException("该昵称已被注册");
        }

        User user = new User();
        user.setNickname(nickname.trim());
        user.setPasswordHash(passwordEncoder.encode(password));
        userRepo.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getNickname());
        return buildUserResponse(user, token);
    }

    public Map<String, Object> login(String nickname, String password) {
        User user = userRepo.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getNickname());
        return buildUserResponse(user, token);
    }

    public User getUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    public Map<String, Object> updateProfile(Long userId, String nickname, String avatarUrl) {
        User user = getUserById(userId);
        if (nickname != null && !nickname.trim().isEmpty() && !nickname.equals(user.getNickname())) {
            if (userRepo.existsByNickname(nickname.trim())) {
                throw new RuntimeException("该昵称已被使用");
            }
            user.setNickname(nickname.trim());
        }
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl.trim());
        }
        userRepo.save(user);
        return buildUserResponse(user, null);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("原密码错误");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("新密码至少6位");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    private Map<String, Object> buildUserResponse(User user, String token) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (token != null) {
            result.put("token", token);
        }
        Map<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("id", user.getId());
        userMap.put("nickname", user.getNickname());
        userMap.put("avatarUrl", user.getAvatarUrl());
        userMap.put("createdAt", user.getCreatedAt());
        result.put("user", userMap);
        return result;
    }
}
