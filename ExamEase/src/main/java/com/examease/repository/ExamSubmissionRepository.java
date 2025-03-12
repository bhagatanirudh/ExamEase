package com.examease.repository;

import com.examease.entity.Exam;
import com.examease.entity.ExamSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamSubmissionRepository extends JpaRepository<ExamSubmission, Long> {
    List<ExamSubmission> findByStudentId(Long studentId);
    List<ExamSubmission> findByExamId(Long examId);
}
