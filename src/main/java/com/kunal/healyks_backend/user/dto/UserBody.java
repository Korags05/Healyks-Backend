package com.kunal.healyks_backend.user.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserBody {
    private int age;
    private String gender; // lowercase from JSON
    private Double weight;
    private Double height;
    private String bloodGroup;
    private List<String> allergies;
    private List<String> chronicDiseases;
    private List<String> medications;

    private LifestyleDTO lifestyle;
}