package com.platform;

import com.platform.dto.EventRequestDTO;
import com.platform.controller.EventController;
import com.platform.service.QueueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EventProcessingTest {

    @Mock
    private QueueService queueService;

    @InjectMocks
    private EventController eventController;

    @Test
    public void testIngestEvent_Success() {
        // Arrange
        EventRequestDTO dto = new EventRequestDTO();
        dto.setType("click_event");
        dto.setPayload("User clicked button");

        // Act
        ResponseEntity<String> response = eventController.ingestEvent(dto);

        // Assert
        assertEquals(202, response.getStatusCode().value());
        verify(queueService).push(any());
    }
}
