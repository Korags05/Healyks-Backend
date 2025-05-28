package com.kunal.healyks_backend.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseAuthenticationFilter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Skip authentication for auth endpoints and health checks
        String requestPath = request.getRequestURI();
        if (requestPath.startsWith("/auth/") ||
                requestPath.equals("/") ||
                requestPath.startsWith("/actuator/") ||
                requestPath.startsWith("/error")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header for path: {}", requestPath);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "error", "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        try {
            FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token);
            logger.debug("Firebase token verified successfully for user: {}", firebaseToken.getUid());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            firebaseToken.getUid(),
                            null,
                            new ArrayList<>()
                    );

            // Add additional user info to authentication details
            Map<String, Object> details = new HashMap<>();
            details.put("email", firebaseToken.getEmail());
            details.put("name", firebaseToken.getName());
            details.put("uid", firebaseToken.getUid());
            authentication.setDetails(details);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch (FirebaseAuthException e) {
            logger.warn("Firebase token validation failed: {}", e.getAuthErrorCode());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "error", "Invalid or expired Firebase token");
        } catch (Exception e) {
            logger.error("Unexpected error during authentication", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "error", "Authentication service error");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status,
                                   String statusValue, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("status", statusValue);
        errorResponse.put("message", message);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}