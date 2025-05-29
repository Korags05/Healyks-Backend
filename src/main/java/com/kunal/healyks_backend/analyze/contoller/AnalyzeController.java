package com.kunal.healyks_backend.analyze.contoller;

import com.kunal.healyks_backend.analyze.service.GeminiHelper;
import com.kunal.healyks_backend.common.ApiResponse;
import com.kunal.healyks_backend.user.model.User;
import com.kunal.healyks_backend.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class AnalyzeController {

    @Autowired
    private GeminiHelper geminiHelper;

    @Autowired
    private UserService userService;

    @PostMapping("/symptoms/analyze")
    public ResponseEntity<ApiResponse<String>> analyzeSymptoms(@RequestBody Map<String, String> body) {
        try {
            String symptom = body.get("symptom");

            if (symptom == null || symptom.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Symptom is required", null));
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = extractEmailFromAuthentication(authentication);
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(401).body(new ApiResponse<>(false, "Unauthorized", null));
            }

            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.status(404).body(new ApiResponse<>(false, "User profile not found", null));
            }

            // Build personalized prompt
            String prompt = buildPrompt(symptom, user);

            // Send to Gemini
            String diagnosis = geminiHelper.analyze(prompt);

            return ResponseEntity.ok(new ApiResponse<>(true, "Symptom analysis successful", diagnosis));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Internal server error", null));
        }
    }

    private String extractEmailFromAuthentication(Authentication authentication) {
        if (authentication == null) return null;
        if (authentication.getDetails() instanceof Map<?, ?> details) {
            return (String) details.get("email");
        }
        return null;
    }

    private String buildPrompt(String symptom, User user) {
        return """
                You are a professional medical expert. Analyze the following symptoms for the given patient and suggest possible causes and advice (under 150 words):

                Patient Profile:
                - Age: %d
                - Gender: %s
                - Weight: %.1f kg
                - Height: %.1f cm
                - Blood Group: %s
                - Allergies: %s
                - Chronic Diseases: %s
                - Medications: %s

                Reported Symptoms:
                %s
                """.formatted(
                user.getAge(),
                user.getGender(),
                user.getWeight(),
                user.getHeight(),
                user.getBloodGroup(),
                String.join(", ", user.getAllergies()),
                String.join(", ", user.getChronicDiseases()),
                String.join(", ", user.getMedications()),
                symptom
        );
    }
}
