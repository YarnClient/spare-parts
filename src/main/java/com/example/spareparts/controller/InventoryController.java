package com.example.spareparts.controller;

import com.example.spareparts.model.HistoryRecord;
import com.example.spareparts.model.Part;
import com.example.spareparts.service.InventoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InventoryController {

    @Autowired
    private InventoryService service;

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @GetMapping("/parts")
    public ResponseEntity<?> getParts(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) return authError();
        return ResponseEntity.ok(service.getAllParts(userId));
    }

    @PostMapping("/parts")
    public ResponseEntity<?> addPart(@RequestBody Map<String, Object> body,
                                     HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) return authError();
        try {
            String name = (String) body.get("name");
            int quantity = ((Number) body.get("quantity")).intValue();
            String unit = (String) body.get("unit");
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "备件名称不能为空"));
            }
            if (quantity <= 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "数量必须大于0"));
            }
            Part part = service.addOrRestock(userId, name.trim(), quantity, unit);
            return ResponseEntity.ok(part);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/parts/consume")
    public ResponseEntity<?> consume(@RequestBody Map<String, Object> body,
                                     HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) return authError();
        try {
            String name = (String) body.get("name");
            int quantity = ((Number) body.get("quantity")).intValue();
            String dateStr = (String) body.get("date");
            LocalDate date = (dateStr != null && !dateStr.isEmpty()) ? LocalDate.parse(dateStr) : LocalDate.now();

            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "备件名称不能为空"));
            }
            if (quantity <= 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "数量必须大于0"));
            }

            service.consume(userId, name.trim(), quantity, date);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/parts/{id}")
    public ResponseEntity<?> deletePart(@PathVariable Long id,
                                        HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) return authError();
        try {
            service.deletePart(userId, id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) return authError();
        return ResponseEntity.ok(service.getAllHistory(userId));
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<?> deleteHistory(@PathVariable Long id,
                                           HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) return authError();
        service.deleteHistory(userId, id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) return authError();
        return ResponseEntity.ok(service.getSummary(userId));
    }

    private ResponseEntity<?> authError() {
        return ResponseEntity.status(401).body(Map.of("message", "未登录"));
    }
}
