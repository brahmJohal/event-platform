package com.platform.repository;

import com.platform.model.EventDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<EventDocument, String> {
    long countByType(String type);
    List<EventDocument> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}