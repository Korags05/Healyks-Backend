package com.kunal.healyks_backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String email;
    private int age;
    private String gender;
    private Double weight;
    private Double height;
    private String bloodGroup;
    private List<String> allergies;
    private List<String> chronicDiseases;
    private List<String> medications;
    private LifestyleDTO lifestyle;
}
