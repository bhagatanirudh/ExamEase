package com.examease.controller;


import com.examease.entity.Exam;
import com.examease.repository.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private ExamRepository examRepository;

    @GetMapping("/exams")
    public ResponseEntity<List<Exam>> getScheduledExams() {
        List<Exam> scheduledExams = examRepository.findByStatus("scheduled");
        return ResponseEntity.ok(scheduledExams);
    }

}
