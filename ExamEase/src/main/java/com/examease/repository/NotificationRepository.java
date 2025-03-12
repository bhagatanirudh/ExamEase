package com.examease.repository;

import com.examease.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStudentId(Long studentId); // Get notifications for a student
    List<Notification> findByTeacherId(Long teacherId);
}
