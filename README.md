# 🚀 Production-Ready Microservices Booking Platform

A full-featured microservices platform for booking and scheduling services, built on the Spring Boot 4 / Spring Cloud ecosystem. The platform utilizes an Event-Driven Architecture (EDA) powered by Apache Kafka, centralized configuration management, dynamic service discovery, and a complete observability stack (Prometheus, Grafana, Loki, Promtail).

---

## 🏗️ System Architecture

The architecture implements core enterprise patterns: API Gateway, Externalized Configuration, Service Discovery, CQRS / Pub-Sub Messaging, and Centralized Observability.

<img width="1591" height="973" alt="Architecture_of_the_online_booking_service drawio" src="https://github.com/user-attachments/assets/c3be1441-215e-4530-a49e-f57714028395" />
---

## 🧩 Microservices & Component Overview

* API Gateway (api-gateway): Single entry point for all client requests. Routes traffic to internal microservices, proxies static/media requests to MinIO, and serves frontend UI requests.
* Discovery Server (discovery-server): Service registry powered by Netflix Eureka for dynamic IP and port resolution across containers.
* Config Server (config-server): Centralized file-backed configuration repository managing environment settings for all Spring Boot services.
* Auth Service (auth-service): Handles authentication, authorization, and JWT lifecycle. Utilizes Redis for OTPs/password reset tokens and uploads media/avatars directly to MinIO. Emits user domain events to Apache Kafka.
* Provider Service (provider-service): Manages service provider profiles, schedules, and bookings. Consumes user events from Kafka to sync profiles and emits booking-related events.
* Email Service (email-service): Asynchronous notification system. Consumes Kafka events from both auth-service and provider-service to deliver transactional emails via Brevo SMTP.
* Frontend App (frontend): Client-side Angular web interface integrated directly into the Docker ecosystem.

---

## 📊 Monitoring & Observability Stack

The platform is fully instrumented for production-grade system visibility:

* Prometheus: Dynamically discovers microservices via Eureka Service Discovery (eureka_sd_configs) and scrapes JVM, HTTP latency, and system health metrics from /actuator/prometheus.
* Grafana: Provides visual dashboards for application performance monitoring (APM) and interactive log querying.
* Grafana Loki & Promtail: Collects structured JSON logs directly from the Docker Socket, allowing distributed tracing via traceId, log-level filtering (ERROR, WARN, INFO), and service-specific log streams.

---

## 🛠️ Tech Stack

* Backend: Java 17, Spring Boot 4.x, Spring Cloud (Gateway, Config, Eureka), Spring Data JPA, Hibernate Validator.
* Messaging & Async: Apache Kafka.
* Caching & Storage: Redis, MinIO (S3-compatible Object Storage), PostgreSQL.
* Integrations: Brevo (Email Service SMTP).
* Monitoring & Logging: Spring Boot Actuator, Micrometer, Prometheus, Grafana, Loki, Promtail, Logstash JSON Encoder.
* Orchestration: Docker, Docker Compose.

---

## 🚀 Getting Started

### Prerequisites
* Docker Desktop and Docker Compose installed.

### 1. Environment Configuration
Create a .env file in the root directory based on your environment needs:
```
POSTGRES_DB=booking_db
POSTGRES_USER=your_user
POSTGRES_PASSWORD=your_password
POSTGRES_PORT=5432

REDIS_HOST=redis
REDIS_PORT=6379
KAFKA_PORT=9092
KAFKA_ENDPOINT=kafka:29092
KAFKA_CLUSTER_ID=MkU3OEVBNTcwNTJENDM2Qk

MINIO_ROOT_USER=your_user
MINIO_ROOT_PASSWORD=your_password
MINIO_BUCKET_NAME=images
MINIO_API_PORT=9000
MINIO_CONSOLE_PORT=9001
MINIO_URL=http://minio:9000

CONFIG_SERVER_PORT=8888
DISCOVERY_SERVER_PORT=8761
AUTH_SERVICE_PORT=8082
PROVIDERS_SERVICE_PORT=8083
EMAIL_SERVICE_PORT=8084
API_GATEWAY_PORT=8080
FRONTEND_PORT=8081

SPRING_CLOUD_CONFIG_URI=http://config-server:8888
SPRING_PROFILES_ACTIVE=prod

GATEWAY_URL=http://api-gateway:8080
PROVIDERS_URL=lb://PROVIDER-SERVICE
AUTH_URL=lb://AUTH-SERVICE
FRONTEND_URL=http://frontend:80

JWT_SECRET=your_jwt_secret

MAIL_LOGIN=your_user
MAIL_PASSWORD=your_login
MAIL_FROM=your_data
MAIL_HOST=smtp-relay.brevo.com
MAIL_PORT=587

# Monitoring Ports & Settings
PROMETHEUS_PORT=9090
GRAFANA_PORT=3000
GRAFANA_ADMIN_PASSWORD=your_password
LOKI_PORT=3100
```

### 2. Build & Run the Application

### Option 1: Full Repository Clone (Development)

Use this method if you plan to inspect, modify, or develop the source code for the microservices.

1. Clone the complete repository:
```
git clone https://github.com/D1fferr/OnlineBookingAndRecordingPlatform.git platform-deployment
```
2. Navigate into the project directory:
```
cd platform-deployment
```
3. Create and configure your environment variables:
```
cp .env.example .env
```
```
(Get-Content init-minio.sh -Raw).Replace("`r`n", "`n") | Set-Content -NoNewline init-minio.sh
```
4. Start the entire platform using Docker Hub images:
```
docker compose up -d
```
5. Verify that all containers are up and running:
```
docker compose ps
```

### Option 2: Lightweight Setup (Deployment Only)

Since all services are pre-built and pulled directly from Docker Hub, you do not need the full microservices source code to run the system. You only need the runtime configuration files (`docker-compose.yml`, `.env`, `init-minio.sh`, and configuration folders).

#### Using Sparse Checkout (Automated Git Download)

1. Clone only the required configuration files without the microservices source code:
```
git clone --depth 1 --filter=blob:none --sparse https://github.com/D1fferr/OnlineBookingAndRecordingPlatform.git platform-deployment
```
2. Navigate to the deployment folder:
```   
cd platform-deployment
```
3. Set sparse-checkout to retrieve only necessary runtime directories and files:
```
git sparse-checkout set prometheus promtail images
```
4. Create your environment variables file:
```
cp .env.example .env
```
```
(Get-Content init-minio.sh -Raw).Replace("`r`n", "`n") | Set-Content -NoNewline init-minio.sh
```
5. Pull images and launch the environment:
```
docker compose pull
```
```
docker compose up -d
```
6. Check container status:
```
docker compose ps
```
---
Docker will automatically download the necessary database images, message broker, mount the initialization scripts for MinIO.

All requests to the system go exclusively through the API Gateway (port 8080).
So, you can type http://localhost:8080 in your browser. A frontend with all the functionality will open in front of you.
You can enter the following credentials to see the system from the inside:

admin: login: admin@service.com password: password

provider: login: trump@example.com password: password


## 🌐 Management Dashboards & Web Interfaces

Once running, access the following management panels:

| Service | Endpoint | Description |
| :--- | :--- | :--- |
| API Gateway | http://localhost:8080 | Main entry point for API and Web UI |
| Eureka Dashboard | http://localhost:8761 | Service discovery and registry status |
| Prometheus Targets | http://localhost:9090/targets | Scraped metrics targets status |
| Grafana UI | http://localhost:3000 | System metrics dashboards and log explorer |
| MinIO Console | http://localhost:9001 | S3-compatible object storage web UI |

---

## 🔍 Logs & Metrics Inspection in Grafana

1. Open Grafana (http://localhost:3000) and log in with your credentials (admin / ${GRAFANA_ADMIN_PASSWORD}).
2. Metrics: Import dashboard ID 19004 or 12900 to view pre-configured Spring Boot & JVM telemetry.
3. Logs: Navigate to Explore -> select Loki as the Data Source -> filter by container = api-gateway (or any other service) to stream JSON logs with distributed traceId context.
