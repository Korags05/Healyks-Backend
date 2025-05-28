package com.kunal.healyks_backend.user.model;

import com.kunal.healyks_backend.user.enums.PhysicalActivity;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Lifestyle {
    private Boolean smoking = false;
    private Boolean alcohol = false;

    @Enumerated(EnumType.STRING)
    private PhysicalActivity physicalActivity;
}
