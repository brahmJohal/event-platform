package com.platform.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.platform.model.EventDocument;
import com.platform.repository.EventRepository;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final EventRepository repository;

   
    public AnalyticsController(EventRepository repository) {
        this.repository = repository;
    }
    

    @GetMapping("/count")
    public Map<String, Long> getTotalCount() {
        return Map.of("total_events", repository.count());
    }

    @GetMapping("/by-type/{type}")
    public Map<String, Long> getCountByType(@PathVariable String type) {
        return Map.of("count", repository.countByType(type));
    }

    @GetMapping("/time-range")
    public List<EventDocument> getByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return repository.findByTimestampBetween(from, to);
    }
}