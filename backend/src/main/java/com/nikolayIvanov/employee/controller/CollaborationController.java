package com.nikolayIvanov.employee.controller;

import com.nikolayIvanov.employee.model.CollaborationResponse;
import com.nikolayIvanov.employee.model.DetailedResult;
import com.nikolayIvanov.employee.model.PairResult;
import com.nikolayIvanov.employee.service.CollaborationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Slf4j
public class CollaborationController {
    @Autowired
    private CollaborationService service;
    @PostMapping("/process")
    public ResponseEntity<?> processCsv(@RequestParam("file") MultipartFile file) {
        try {
            PairResult summary;
            try (InputStreamReader reader1 = new InputStreamReader(file.getInputStream())) {
                summary = service.findMaxPair(reader1);
            }
            List<DetailedResult> details;
            try (InputStreamReader reader2 = new InputStreamReader(file.getInputStream())) {
                details = service.findAllDetails(reader2);
            }
            CollaborationResponse response = new CollaborationResponse(summary, details);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid CSV format", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            log.error("Error processing CSV", e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
}
