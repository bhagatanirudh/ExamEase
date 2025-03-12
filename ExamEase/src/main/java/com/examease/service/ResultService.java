package com.examease.service;

import com.examease.entity.Result;
import com.examease.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ResultService {

    @Autowired
    private ResultRepository resultRepository;

    public Result saveResult(String examName, String examType, String examDate, String teacherId,MultipartFile file) throws IOException
    {
        Result result = new Result();
        result.setExamName(examName);
        result.setExamType(examType);
        result.setTeacherId( !teacherId.isEmpty() ? Long.valueOf(teacherId) : null);
        result.setExamDate( Timestamp.valueOf( LocalDate.parse(examDate).atStartOfDay() ));
        result.setActive( false );

        // Handle file upload
        if (file != null && !file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get("uploads/results/" + fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());
            result.setResultFile(fileName);
        }
        return resultRepository.save(result);
    }

    public boolean updateResult(Long resultId, String examName, String examType, String examDate, String teacherId, MultipartFile file) throws IOException
    {
        Optional<Result> res = resultRepository.findById( resultId );
        if (res.isPresent()) {
            Result result = res.get();
            result.setExamName(examName);
            result.setExamType(examType);
            result.setTeacherId( !teacherId.isEmpty() ? Long.valueOf(teacherId) : null);
            result.setExamDate( Timestamp.valueOf( LocalDate.parse(examDate).atStartOfDay() ));
            // Handle file upload
            if (file != null && !file.isEmpty()) {
                String fileName = file.getOriginalFilename();
                Path filePath = Paths.get("uploads/results/" + fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, file.getBytes());
                result.setResultFile(fileName);
            }
            resultRepository.save(result);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteResult(Long id) {
        if (resultRepository.existsById(id) ) {
            resultRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean uploadResult(Long id) {
        Optional<Result> optionalResult = resultRepository.findById(id);
        if (optionalResult.isPresent()) {
            Result result = optionalResult.get();
            result.setActive(true);
            resultRepository.save(result);
            return true;
        }
        return false;
    }
}
