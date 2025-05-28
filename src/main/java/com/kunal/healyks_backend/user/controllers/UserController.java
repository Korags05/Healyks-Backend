package com.kunal.healyks_backend.user.controllers;

import com.kunal.healyks_backend.common.ApiResponse;
import com.kunal.healyks_backend.user.dto.UserBody;
import com.kunal.healyks_backend.user.dto.UserResponse;
import com.kunal.healyks_backend.user.mapper.UserMapper;
import com.kunal.healyks_backend.user.model.User;
import com.kunal.healyks_backend.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/postUserBody")
    public ResponseEntity<ApiResponse<String>> postUserBody(@RequestBody UserBody userDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Extract email from authentication details (set by FirebaseAuthenticationFilter)
            String email = extractEmailFromAuthentication(authentication);

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Unable to extract email from authentication", null));
            }

            User user = UserMapper.toUser(userDTO);
            user.setEmail(email); // Set the actual email address from Firebase token

            User saved = userService.addOrUpdateUser(user);
            if (saved != null) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ApiResponse<>(true, "User added successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "Failed to add user", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Server error: " + e.getMessage(), null));
        }
    }

    @GetMapping("/details")
    public ResponseEntity<ApiResponse<UserResponse>> getUserDetails() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Extract email from authentication details (set by FirebaseAuthenticationFilter)
            String email = extractEmailFromAuthentication(authentication);

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Unable to extract email from authentication", null));
            }

            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "User not found", null));
            }

            UserResponse dto = UserMapper.toResponseDTO(user);
            return ResponseEntity.ok(new ApiResponse<>(true, "User details fetched successfully", dto));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error fetching user details: " + e.getMessage(), null));
        }
    }

    /**
     * Extract email from authentication details set by FirebaseAuthenticationFilter
     */
    private String extractEmailFromAuthentication(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        // Extract email from authentication details (stored by your FirebaseAuthenticationFilter)
        if (authentication.getDetails() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
            return (String) details.get("email");
        }

        return null;
    }
}