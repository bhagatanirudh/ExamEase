package com.examease.controller;

import com.examease.entity.Result;
import com.examease.entity.User;
import com.examease.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/getUserByRole/{role}")
    public ResponseEntity<List<User>> getAllResultById(@PathVariable String role) {
        List<User> list = userRepository.findAllByRole(role);
        if ( list.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(list);
        }
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User profile = user.get();
            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/user/profile/image/")
                    .path(profile.getProfile()) // Assuming profileImage stores the filename
                    .toUriString();

            Map<String, Object> response = new HashMap<>();
            response.put("id", profile.getId());
            response.put("name", profile.getName());
            response.put("email", profile.getEmail());
            response.put("phone", profile.getPhone());
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "User not found"));
        }
    }

    @GetMapping("/profile/image/{filename}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String filename) {
        try {
            Path imagePath = Paths.get("uploads/profile_pics").resolve(filename).normalize();
            Resource resource = new UrlResource(imagePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Could not read the file: " + filename);
            }
            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable Long id, @RequestBody User user) {

        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setName(user.getName());
                    existingUser.setPhone(user.getPhone());
                    existingUser.setAddress(user.getAddress());
                    existingUser.setGender(user.getGender());
                    userRepository.save(existingUser);
                    return ResponseEntity.ok(Map.of("message", "Profile updated successfully!"));
                })
                .orElse(ResponseEntity.status(404).body(Map.of("error", "User not found")));
    }

    @PostMapping("/uploadProfilePic/{id}")
    public ResponseEntity<Map<String, String>> uploadProfilePic(@PathVariable Long id, @RequestParam("file") MultipartFile file) {

        return userRepository.findById(id)
                .map(existingUser -> {
                    try {
                        String uploadDir = "uploads/profile_pics/";
                        Files.createDirectories(Paths.get(uploadDir));

                        String fileName = "profile_" + id + "_" + file.getOriginalFilename();
                        Path filePath = Paths.get(uploadDir, fileName);
                        Files.write(filePath, file.getBytes());

                        existingUser.setProfile(fileName);
                        userRepository.save(existingUser);

                        return ResponseEntity.ok(Map.of("message", "Profile picture uploaded successfully!", "profilePicUrl", existingUser.getProfile()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(500).body(Map.of("error", "File upload failed!"));
                    }
                })
                .orElse(ResponseEntity.status(404).body(Map.of("error", "User not found")));
    }

    @PostMapping("/update-password/{id}")
    public ResponseEntity<String> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");

            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Verify old password
            if ( !oldPassword.equals(existingUser.getPassword()) ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Old password is incorrect!");
            }

            existingUser.setPassword(newPassword);
            userRepository.save(existingUser);
            return ResponseEntity.ok("Password updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating password: " + e.getMessage());
        }
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<String> updatePassword(@PathVariable Long id) {
        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if ( !existingUser.isActive() ) {
                return ResponseEntity.badRequest().body("Account is already deactivated.");
            }

            existingUser.setActive(false);
            userRepository.save(existingUser);
            return ResponseEntity.ok("User deactivated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deactivated account: " + e.getMessage());
        }
    }

}
