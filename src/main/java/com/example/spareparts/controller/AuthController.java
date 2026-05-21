package com.example.spareparts.controller;

import com.example.spareparts.model.User;
import com.example.spareparts.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            String nickname = body.get("nickname");
            String password = body.get("password");
            return ResponseEntity.ok(authService.register(nickname, password));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            String nickname = body.get("nickname");
            String password = body.get("password");
            return ResponseEntity.ok(authService.login(nickname, password));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/users/me")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "未登录"));
        }
        User user = authService.getUserById(userId);
        Map<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("id", user.getId());
        userMap.put("nickname", user.getNickname());
        userMap.put("avatarUrl", user.getAvatarUrl());
        userMap.put("createdAt", user.getCreatedAt());
        return ResponseEntity.ok(userMap);
    }

    @PutMapping("/users/me")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> body,
                                            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "未登录"));
        }
        try {
            return ResponseEntity.ok(authService.updateProfile(
                    userId,
                    body.get("nickname"),
                    body.get("avatarUrl")
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/users/me/password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body,
                                             HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "未登录"));
        }
        try {
            authService.changePassword(
                    userId,
                    body.get("oldPassword"),
                    body.get("newPassword")
            );
            return ResponseEntity.ok(Map.of("success", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
