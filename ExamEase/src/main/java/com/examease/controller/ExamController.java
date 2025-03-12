package com.examease.controller;

import com.examease.entity.Exam;
import com.examease.entity.ExamSubmission;
import com.examease.entity.Question;
import com.examease.repository.ExamRepository;
import com.examease.repository.ExamSubmissionRepository;
import com.examease.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/exams")
public class ExamController {

    @Autowired
    private ExamService examService;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamSubmissionRepository examSubmissionRepository;

    @GetMapping("/getByTeacherId/{teacherId}")
    public ResponseEntity<List<Exam>> getAllExamsById(@PathVariable Long teacherId) {
        List<Exam> list = examRepository.findByTeacherId(teacherId);
        if ( list.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(list);
        }
    }

    @GetMapping("/getByStatus/{status}")
    public ResponseEntity<List<Exam>> getAllExamsById(@PathVariable String status) {
        List<Exam> list = examRepository.findByStatus(status);
        if ( list.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(list);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createExam(@RequestBody Exam exam) {

        // Ensuring bidirectional relationship
        for (Question question : exam.getQuestions())
            question.setExam(exam);

        Exam savedExam = examRepository.save(exam);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Exam created successfully!");
        response.put("examId", savedExam.getId().toString());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteExam(@PathVariable Long id) {
        boolean deleted = examService.deleteExam(id);
        Map<String, String> response = new HashMap<>();
        if (deleted) {
            response.put("message", "Exam deleted successfully!");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Exam not found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/schedule/{id}")
    public ResponseEntity<Map<String, String>> scheduleExam(@PathVariable Long id) {

        Map<String, String> response = new HashMap<>();
        Optional<Exam> optionalExam = examRepository.findById(id);
        if ( optionalExam.isPresent() ) {
            Exam exam = optionalExam.get();
            if ( exam.getStatus().equals("scheduled") ) {
                response.put("message", "Exam is already scheduled!");
                return ResponseEntity.ok(response);
            }
            else {
                exam.setStatus("scheduled");
                examRepository.save(exam);
                response.put("message", "Exam scheduled successfully!");
                return ResponseEntity.ok(response);
            }
        } else {
            response.put("error", "Exam not found or already scheduled!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Fetch exam details
    @GetMapping("/getById/{examId}")
    public ResponseEntity<Exam> getExamDetails(@PathVariable Long examId) {
        Optional<Exam> exam = examRepository.findById(examId);
        if (exam.isPresent()) {
            return ResponseEntity.ok(exam.get());
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Submit exam responses
    @PostMapping("/submit")
    public ResponseEntity<Map<String, String>> submitExam(@RequestBody ExamSubmission request) {
        examService.submitExam(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Exam submitted successfully!");
        return ResponseEntity.ok(response);
    }

    // Fetch exam submission for a student
    @GetMapping("/getExamSubmitted/{studentId}")
    public ResponseEntity<List<ExamSubmission>> getExamSubmissionDetails(@PathVariable Long studentId) {
        List<ExamSubmission> examSubmissionList = examSubmissionRepository.findByStudentId(studentId);
        if ( examSubmissionList != null && !examSubmissionList.isEmpty() ) {
            return ResponseEntity.ok(examSubmissionList);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Fetch exam submission by exam
    @GetMapping("/getExamSubmittedByExam/{examId}")
    public ResponseEntity<List<ExamSubmission>> getExamSubmissionByExam(@PathVariable Long examId) {
        List<ExamSubmission> examSubmissionList = examSubmissionRepository.findByExamId(examId);
        if ( examSubmissionList != null && !examSubmissionList.isEmpty() ) {
            return ResponseEntity.ok(examSubmissionList);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
