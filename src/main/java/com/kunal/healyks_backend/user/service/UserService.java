package com.kunal.healyks_backend.user.service;

import com.kunal.healyks_backend.user.model.User;
import com.kunal.healyks_backend.user.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;


    public User addOrUpdateUser(User userRequest) {
        // Check if user already exists by email
        Optional<User> optionalUser = userRepo.findByEmail(userRequest.getEmail());

        User userToSave;

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();

            // Update fields
            existingUser.setAge(userRequest.getAge());
            existingUser.setGender(userRequest.getGender());
            existingUser.setWeight(userRequest.getWeight());
            existingUser.setHeight(userRequest.getHeight());
            existingUser.setBloodGroup(userRequest.getBloodGroup());
            existingUser.setAllergies(userRequest.getAllergies());
            existingUser.setChronicDiseases(userRequest.getChronicDiseases());
            existingUser.setMedications(userRequest.getMedications());
            existingUser.setLifestyle(userRequest.getLifestyle());

            userToSave = existingUser;
        } else {
            userToSave = userRequest;
        }

        return userRepo.save(userToSave);
    }


    public User getUserByEmail(String email) {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        return optionalUser.orElse(null);
    }
}
