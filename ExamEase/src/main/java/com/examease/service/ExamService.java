package com.examease.service;

import com.examease.entity.ExamSubmission;
import com.examease.repository.ExamRepository;
import com.examease.repository.ExamSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamSubmissionRepository examSubmissionRepository;

    public boolean deleteExam(Long id) {
        if (examRepository.existsById(id) ) {
            examRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public void submitExam(ExamSubmission request) {
        request.setSubmittedAt( new Timestamp(System.currentTimeMillis()));
        request.setStatus("completed");
        examSubmissionRepository.save(request);
    }

}
