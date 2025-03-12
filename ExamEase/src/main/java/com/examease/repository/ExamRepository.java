package com.examease.repository;

import com.examease.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findByStatus(String status);
    List<Exam> findByTeacherId(Long teacherId);
}
