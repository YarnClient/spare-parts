package com.example.spareparts.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wechat")
public class WechatController {

    @GetMapping("/auth-url")
    public ResponseEntity<?> getAuthUrl() {
        return ResponseEntity.ok(Map.of(
                "enabled", false,
                "message", "微信登录暂未配置，请使用密码登录。需在 application.yml 中配置 app.wx.app-id 和 app.wx.app-secret"
        ));
    }

    @PostMapping("/callback")
    public ResponseEntity<?> callback(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(Map.of(
                "enabled", false,
                "message", "微信登录暂未配置"
        ));
    }
}
