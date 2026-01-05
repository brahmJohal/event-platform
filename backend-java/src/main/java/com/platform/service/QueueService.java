package com.platform.service;

import com.platform.model.EventDocument;
import org.springframework.stereotype.Service;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class QueueService {
    private final BlockingQueue<EventDocument> queue = new LinkedBlockingQueue<>();

    public void push(EventDocument event) {
        queue.offer(event);
    }

    public EventDocument take() throws InterruptedException {
        return queue.take(); // Blocks until element is available
    }
}