package com.platform.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.dto.EventRequestDTO;
import com.platform.model.EventDocument;
import com.platform.service.QueueService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")

public class EventController {

    private final QueueService queueService;

   
    public EventController(QueueService queueService) {
        this.queueService = queueService;
    }
    

    @PostMapping
    public ResponseEntity<String> ingestEvent(@Valid @RequestBody EventRequestDTO dto) {
        EventDocument event = new EventDocument();
        event.setId(UUID.randomUUID().toString());
        event.setType(dto.getType());
        event.setPayload(dto.getPayload());
        event.setTimestamp(LocalDateTime.now());

        queueService.push(event);

        return ResponseEntity.accepted().body("Event queued with ID: " + event.getId());
    }
}