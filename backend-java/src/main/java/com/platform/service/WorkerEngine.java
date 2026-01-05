package com.platform.service;

import com.platform.model.EventDocument;
import com.platform.repository.EventRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WorkerEngine {

    private static final Logger log = LoggerFactory.getLogger(WorkerEngine.class);

    private final QueueService queueService;
    private final EventRepository repository;
    private final RestTemplate restTemplate;

    public WorkerEngine(QueueService queueService, EventRepository repository, RestTemplate restTemplate) {
        this.queueService = queueService;
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    @Value("${app.worker.thread-count}")
    private int threadCount;

    @Value("${app.python.service.url}")
    private String pythonServiceUrl;

    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(this::processLoop);
        }
        log.info("Worker Engine started with {} threads.", threadCount);
    }

    private void processLoop() {
        while (true) {
            try {
                EventDocument event = queueService.take();
                processEvent(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Error processing event", e);
            }
        }
    }

    private void processEvent(EventDocument event) {
        try {
            // Call Python Service
            Map<String, Object> request = Map.of("payload", event.getPayload());
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(pythonServiceUrl, request, Map.class);

            // Enrich Event
            if (response != null) {
                event.setComplexityScore((Integer) response.get("complexity_score"));
                @SuppressWarnings("unchecked")
                List<String> tags = (List<String>) response.get("tags");
                event.setTags(tags);
                event.setProcessedBy((String) response.get("processed_by"));
            }
            
            event.setProcessedAt(LocalDateTime.now());
            
            // Save to DB
            repository.save(event);
            log.info("Event {} processed and saved.", event.getId());

        } catch (IllegalStateException e) {
            log.error("Failed to communicate with Python worker", e);
        }
    }
}