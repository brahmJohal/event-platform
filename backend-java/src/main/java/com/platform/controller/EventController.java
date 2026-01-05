package com.platform.controller;

import com.platform.dto.EventRequestDTO;
import com.platform.model.EventDocument;
import com.platform.service.QueueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
// @RequiredArgsConstructor <-- REMOVED
public class EventController {

    private final QueueService queueService;

    // --- MANUAL CONSTRUCTOR (Fixes the error) ---
    public EventController(QueueService queueService) {
        this.queueService = queueService;
    }
    // --------------------------------------------

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