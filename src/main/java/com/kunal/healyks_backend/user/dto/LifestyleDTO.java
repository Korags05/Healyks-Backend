package com.kunal.healyks_backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LifestyleDTO {
    private Boolean smoking;
    private Boolean alcohol;
    private String physicalActivity;
}
