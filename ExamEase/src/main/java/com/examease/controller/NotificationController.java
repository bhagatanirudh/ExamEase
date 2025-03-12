package com.examease.controller;

import com.examease.entity.Notification;
import com.examease.entity.Result;
import com.examease.entity.User;
import com.examease.repository.NotificationRepository;
import com.examease.repository.UserRepository;
import com.examease.service.NotificationService;
import com.examease.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createNotification(@RequestBody Map<String, Object> payload) {
        String title = (String) payload.get("title");
        String message = (String) payload.get("message");
        String from = (String) payload.get("fromUser");
        String to = (String) payload.get("toUser");
        String type = (String) payload.get("type");
        Long teacherId = Long.valueOf((String) payload.get("teacherId"));
        Long studentId = payload.containsKey("studentId") ? ((Number) payload.get("studentId")).longValue() : null;

        User teacher = userRepository.findById( teacherId ).orElse(null);
        User student = null;
        if (studentId != null) {
            student = userRepository.findById(studentId).orElse(null);
        }

        Notification notification = notificationService.createNotification(title, message, from, to
                , type, teacher, student);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notification Sent successfully!");
        response.put("notificationId", notification.getId().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<List<Notification>> getStudentNotifications(@PathVariable Long studentId) {
        return ResponseEntity.ok(notificationService.getStudentNotifications(studentId));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Notification>> getAllNotification() {
        List<Notification> list = notificationRepository.findAll();
        if ( list.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(list);
        }
    }

    @GetMapping("/getByTeacherId/{teacherId}")
    public ResponseEntity<List<Notification>> getAllNotificationById(@PathVariable Long teacherId) {
        List<Notification> list = notificationRepository.findByTeacherId(teacherId);
        if ( list.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(list);
        }
    }

    @PutMapping("/markAsRead/{notificationId}")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long notificationId) {
        boolean read = notificationService.markAsRead(notificationId);
        Map<String, String> response = new HashMap<>();
        if (read) {
            response.put("message", "Notification marked as read!");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Notification not found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/markAllAsRead/{teacherId}")
    public ResponseEntity<Map<String, String>> markAllAsRead(@PathVariable Long teacherId) {
        List<Notification> list = notificationRepository.findByTeacherId( teacherId );
        Map<String, String> response = new HashMap<>();
        if ( list.isEmpty() ) {
            response.put("error", "Notification not found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            for (Notification notification : list) {
                notificationService.markAsRead(notification.getId());
            }
            response.put("message", "Notification marked as read!");
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping("/delete/{notificationId}")
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable Long notificationId) {
        boolean deleted = notificationService.deleteNotification(notificationId);
        Map<String, String> response = new HashMap<>();
        if (deleted) {
            response.put("message", "Notification deleted successfully!");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Notification not found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/deleteAll/{teacherId}")
    public ResponseEntity<Map<String, String>> deleteAllNotification(@PathVariable Long teacherId) {
        List<Notification> list = notificationRepository.findByTeacherId( teacherId );
        Map<String, String> response = new HashMap<>();
        if ( list.isEmpty() ) {
            response.put("error", "Notification not found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            for (Notification notification : list) {
                notificationService.deleteNotification(notification.getId());
            }
            response.put("message", "Notification deleted successfully!");
            return ResponseEntity.ok(response);
        }
    }

}
