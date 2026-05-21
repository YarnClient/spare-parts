package com.example.spareparts.filter;

import com.example.spareparts.config.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class JwtAuthFilter implements Filter {

    @Autowired
    private JwtUtil jwtUtil;

    private static final String[] PROTECTED_PATHS = {
            "/api/parts",
            "/api/history",
            "/api/summary",
            "/api/users/me"
    };

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI();

        // Check if this path needs auth
        boolean needsAuth = false;
        for (String prefix : PROTECTED_PATHS) {
            if (path.startsWith(prefix)) {
                needsAuth = true;
                break;
            }
        }

        if (!needsAuth) {
            chain.doFilter(req, res);
            return;
        }

        // Extract and validate token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"未登录\"}");
            return;
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"登录已过期，请重新登录\"}");
            return;
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        chain.doFilter(req, res);
    }
}
