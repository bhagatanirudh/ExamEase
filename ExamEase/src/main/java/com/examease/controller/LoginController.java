package com.examease.controller;

import com.examease.entity.User;
import com.examease.repository.UserRepository;
import com.examease.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        boolean isRegistered = userService.registerUser(user);

        if (isRegistered) {
            response.put("message", "Registration successful!");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "User already exists!");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if ( existingUser.isPresent() ) {
            if ( existingUser.get().getPassword().equals(user.getPassword()) ) {
                if ( existingUser.get().getRole().equals( user.getRole() ) ) {
                    if ( existingUser.get().isActive() ) {
                        response.put("message", "Login successful!");
                        response.put("userId", existingUser.get().getId().toString());
                        return ResponseEntity.ok(response);
                    } else {
                        response.put("message", "Account is not active!");
                        return ResponseEntity.status(401).body(response);
                    }
                } else {
                    response.put("message", "Login not allowed for this role!");
                    return ResponseEntity.status(401).body(response);
                }
            } else {
                response.put("message", "Invalid credentials!");
                return ResponseEntity.status(401).body(response);
            }
        } else {
            response.put("message", "User not found!");
            return ResponseEntity.status(401).body(response);
        }
    }

}
