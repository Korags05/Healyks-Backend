package com.kunal.healyks_backend.user.mapper;

import com.kunal.healyks_backend.user.dto.LifestyleDTO;
import com.kunal.healyks_backend.user.dto.UserBody;
import com.kunal.healyks_backend.user.dto.UserResponse;
import com.kunal.healyks_backend.user.enums.Gender;
import com.kunal.healyks_backend.user.enums.PhysicalActivity;
import com.kunal.healyks_backend.user.model.Lifestyle;
import com.kunal.healyks_backend.user.model.User;

public class UserMapper {

    public static User toUser(UserBody dto) {
        User user = new User();
        // Email will be set by the controller from Firebase authentication
        user.setAge(dto.getAge());
        user.setGender(Gender.valueOf(dto.getGender().toUpperCase()));
        user.setWeight(dto.getWeight());
        user.setHeight(dto.getHeight());
        user.setBloodGroup(dto.getBloodGroup());
        user.setAllergies(dto.getAllergies());
        user.setChronicDiseases(dto.getChronicDiseases());
        user.setMedications(dto.getMedications());

        Lifestyle lifestyle = new Lifestyle();
        if (dto.getLifestyle() != null) {
            lifestyle.setSmoking(dto.getLifestyle().getSmoking());
            lifestyle.setAlcohol(dto.getLifestyle().getAlcohol());
            lifestyle.setPhysicalActivity(
                    dto.getLifestyle().getPhysicalActivity() != null
                            ? PhysicalActivity.valueOf(dto.getLifestyle().getPhysicalActivity().toUpperCase())
                            : null
            );
        }
        user.setLifestyle(lifestyle);

        return user;
    }

    public static UserResponse toResponseDTO(User user) {
        Lifestyle lifestyle = user.getLifestyle();

        // Handle null lifestyle object
        if (lifestyle == null) {
            lifestyle = new Lifestyle(); // Create empty lifestyle if null
        }

        // Safe handling of physicalActivity enum
        String physicalActivityStr = null;
        if (lifestyle.getPhysicalActivity() != null) {
            physicalActivityStr = lifestyle.getPhysicalActivity().toString().toLowerCase();
        }

        LifestyleDTO lifestyleDTO = new LifestyleDTO(
                lifestyle.getSmoking(),
                lifestyle.getAlcohol(),
                physicalActivityStr
        );

        return new UserResponse(
                user.getEmail(),
                user.getAge(),
                user.getGender() != null ? user.getGender().toString().toLowerCase() : null,
                user.getWeight(),
                user.getHeight(),
                user.getBloodGroup(),
                user.getAllergies(),
                user.getChronicDiseases(),
                user.getMedications(),
                lifestyleDTO
        );
    }
}