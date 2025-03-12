package com.examease.service;

import com.examease.entity.User;
import com.examease.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    // Register new user
    public boolean registerUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return false; // User already exists
        }
        user.setCreated(new Timestamp(System.currentTimeMillis()));
        user.setActive(true);
        userRepository.save(user);
        return true;
    }

    // Authenticate user login
    public Long authenticate(String email, String password, String role) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            if (user.get().getPassword().equals(password) && user.get().getRole().equals(role)) {
                return user.get().getId();
            } else {
                return null;
            }
        }
        return null;
    }
}
