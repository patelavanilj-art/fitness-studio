package com.health.controller;

import com.health.model.Feedback;
import com.health.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackApiController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @PostMapping
    public ResponseEntity<?> submit(@RequestBody Map<String, String> body) {
        Feedback f = new Feedback();
        f.setUserName(body.get("userName"));
        f.setEmail(body.get("email"));
        f.setMessage(body.get("message"));
        f.setStatus("New");
        feedbackRepository.save(f);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
