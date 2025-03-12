package com.examease.service;

import com.examease.entity.Notification;
import com.examease.entity.User;
import com.examease.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.sql.Timestamp;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;  // For real-time updates

    public Notification createNotification(String title, String message, String from, String to, String type, User teacher, User student) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setFromUser(from);
        notification.setToUser(to);
        notification.setType(type);
        notification.setCreated(new Timestamp(System.currentTimeMillis()));
        notification.setTeacher(teacher);
        notification.setStudent(student);  // Null for all students
        notification.setRead(false);  // Default: Unread

        Notification savedNotification = notificationRepository.save(notification);

        // Send real-time notification (WebSocket)
        messagingTemplate.convertAndSend("/topic/notifications", savedNotification);
        return savedNotification;
    }

    public List<Notification> getStudentNotifications(Long studentId) {
        return notificationRepository.findByStudentId(studentId);
    }

    public boolean markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null) {
            notification.setRead(true);
            notificationRepository.save(notification);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteNotification(Long notificationId) {
        if (notificationRepository.existsById(notificationId) ) {
            notificationRepository.deleteById(notificationId);
            return true;
        }
        return false;
    }
}
