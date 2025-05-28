package com.kunal.healyks_backend.user.model;

import com.kunal.healyks_backend.user.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String email;

    private int age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Double weight;
    private Double height;

    private String bloodGroup;

    @ElementCollection
    private List<String> allergies;

    @ElementCollection
    private List<String> chronicDiseases;

    @ElementCollection
    private List<String> medications;

    @Embedded
    private Lifestyle lifestyle;
}
