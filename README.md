# Distributed Event Data Processing Platform

A polyglot backend system that ingests events via Java, processes them concurrently, enriches data using a Python service, and stores results in MongoDB.

## Tech Stack
* **Core Backend:** Java 21, Spring Boot 3
* **Dynamic Worker:** Python 3.9, Flask
* **Database:** MongoDB
* **Infrastructure:** Docker, Docker Compose

## Architecture
1.  **Ingestion:** `POST /api/events` pushes data to a `BlockingQueue`.
2.  **Concurrency:** A Java Worker Pool (ExecutorService) pulls events.
3.  **Enrichment:** Worker calls the Python container via internal REST API to calculate metrics.
4.  **Persistence:** Enriched events are saved to MongoDB.
5.  **Analytics:** Data exposed via `/api/analytics` endpoints.

## How to Run
1.  Ensure you have Docker and Docker Compose installed.
2.  Clone this repository.
3.  Run the stack:
    ```bash
    docker-compose up --build
    ```
4.  Wait for services to start. Backend runs on port `8080`.

## API Examples (CURL)

### 1. Ingest an Event
```bash
curl -X POST http://localhost:8080/api/events \
     -H "Content-Type: application/json" \
     -d '{"type": "login", "payload": "User 123 login attempt"}'