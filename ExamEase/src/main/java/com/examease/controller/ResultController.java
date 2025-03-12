package com.examease.controller;

import com.examease.entity.Result;
import com.examease.repository.ResultRepository;
import com.examease.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.MimeTypeUtils;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/results")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @Autowired
    private ResultRepository resultRepository;

    @PostMapping("/save")
    public ResponseEntity<Map<String, String>> save(@RequestParam("examName") String examName, @RequestParam("examType") String examType, @RequestParam("examDate") String examDate, @RequestParam("teacherId") String teacherId, @RequestParam(value = "file", required = false) MultipartFile file)
    {
        Map<String, String> response = new HashMap<>();
        try {
            Result res = resultService.saveResult(examName, examType, examDate, teacherId, file);
            if ( res != null ) {
                response.put("message", "Result save successfully!");
                response.put("resultId", res.getId().toString());
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, String>> update( @RequestParam("examName") String examName, @RequestParam("examType") String examType, @RequestParam("examDate") String examDate, @RequestParam("teacherId") String teacherId, @RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("resultId") Long resultId)
    {
        Map<String, String> response = new HashMap<>();
        try {
            boolean res = resultService.updateResult( resultId, examName, examType, examDate, teacherId, file);
            if ( res ) {
                response.put("message", "Result updated successfully!");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Result not found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/getById/{teacherId}")
    public ResponseEntity<List<Result>> getAllResultById(@PathVariable Long teacherId) {
        List<Result> list = resultRepository.findByTeacherId(teacherId);
        if ( list.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(list);
        }
    }

    @GetMapping("/getActiveResult")
    public ResponseEntity<List<Result>> getAllActiveResult() {
        List<Result> list = resultRepository.findByActiveTrue();
        if ( list.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(list);
        }
    }

    @DeleteMapping("/delete/{resultId}")
    public ResponseEntity<Map<String, String>> delete( @PathVariable Long resultId ){
        boolean deleted = resultService.deleteResult(resultId);
        Map<String, String> response = new HashMap<>();
        if (deleted) {
            response.put("message", "Result deleted successfully!");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Result not found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/upload/{id}")
    public ResponseEntity<Map<String, String>> uplaodResult(@PathVariable Long id) {
        boolean upload = resultService.uploadResult(id);
        Map<String, String> response = new HashMap<>();
        if (upload) {
            response.put("message", "Result uploaded successfully!");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Result not found or already uploaded!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/view/{resultId}")
    public ResponseEntity<Resource> viewResult(@PathVariable Long resultId) {
        Optional<Result> res = resultRepository.findById(resultId);
        if ( res.isPresent() ) {
            Result result = res.get();
            if ( result.getResultFile() == null ) {
                return ResponseEntity.notFound().build();
            }
            try {
                Path path = Paths.get("uploads/results/" + result.getResultFile());
                Resource resource = new UrlResource(path.toUri());

                // Determine MIME type dynamically
                String contentType = Files.probeContentType(path);
                if (contentType == null) {
                    contentType = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE; // Default type if unknown
                }
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName().toString() + "\"")
                        .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                        .body(resource);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/download/{resultId}")
    public ResponseEntity<Resource> downloadResult(@PathVariable Long resultId) {

        Optional<Result> res = resultRepository.findById(resultId);
        if ( res.isPresent() ) {
            Result result = res.get();
            if (result.getResultFile() == null) {
                return ResponseEntity.notFound().build();
            }
            try {
                Path filePath = Paths.get("uploads/results/").resolve(result.getResultFile()).normalize();
                Resource resource = new UrlResource(filePath.toUri());

                if (resource.exists() || resource.isReadable()) {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                            .body(resource);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
