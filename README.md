# Yushan Config Server

> ⚙️ **Config Server for Yushan Platform (Phase 2 - Microservices)** - Centralized configuration management for all microservices.

## 📋 Overview

Yushan Config Server is a Spring Cloud Config Server that provides centralized configuration for all microservices in the Yushan system. This server uses **Git Backend** to read configuration from the GitHub repository [yushan-microservices-config-data](https://github.com/phutruonnttn/yushan-microservices-config-data).

## 🚀 Tech Stack

- **Framework**: Spring Boot 3.4.10
- **Language**: Java 21
- **Spring Cloud**: 2024.0.2
- **Build Tool**: Maven
- **Service Discovery**: Eureka Client

## ✨ Features

- Centralized configuration management
- **Git Backend** - Read configs from GitHub repository
- Service discovery via Eureka
- Environment-specific configurations
- Automatic config refresh (fetches latest from Git every 5 minutes)
- Version control for all configurations

## 🏗️ Architecture

```
Business Services → Config Server (API) → Config Repo (GitHub)
```

**Config Repo**: [yushan-microservices-config-data](https://github.com/phutruonnttn/yushan-microservices-config-data)
- Stores all configuration files
- Version controlled with Git
- Can be edited directly on GitHub

**Config Server**: This repository
- Reads configs from Git repo
- Serves configs via REST API
- Automatically fetches latest configs from Git

## 🏗️ Project Structure

```
yushan-config-server/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/yushan/config/
│       │       └── YushanConfigServerApplication.java
│       └── resources/
│           ├── application.yml
│           └── application-docker.yml
├── Dockerfile
├── docker-compose.yml
└── pom.xml

Note: Config files are stored in separate Git repo: yushan-microservices-config-data
```

## 🚦 Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- Eureka Service Registry running on port 8761

### Run Locally

```bash
# Clone repository
git clone https://github.com/phutruonnttn/yushan-microservices-config-server.git
cd yushan-microservices-config-server

# Run Config Server
./mvnw spring-boot:run
```

Config server will run on port **8888** and automatically fetch configs from Git repo.

### Run with Docker

```bash
# Build image
docker build -t yushan-config-server:latest .

# Run container
docker run -d \
  --name yushan-config-server \
  -p 8888:8888 \
  -e EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://host.docker.internal:8761/eureka/ \
  yushan-config-server:latest
```

### Docker Compose

```bash
docker-compose up -d
```

## ⚙️ Configuration

### application.yml

```yaml
server:
  port: 8888

spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        # Git Backend - Read configs from GitHub repository
        git:
          uri: ${CONFIG_REPO_URI:https://github.com/phutruonnttn/yushan-microservices-config-data.git}
          search-paths: configs
          default-label: main
          refresh-rate: 300  # Fetch new configs every 5 minutes
          clone-on-start: true
          force-pull: true

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL:http://localhost:8761/eureka/}
```

### Configuration File Structure

Config files are stored in the separate Git repository: [yushan-microservices-config-data](https://github.com/phutruonnttn/yushan-microservices-config-data)

Each service has its own configuration file in the `configs/` directory:

- `user-service.yml` - Configuration for User Service
- `content-service.yml` - Configuration for Content Service
- `engagement-service.yml` - Configuration for Engagement Service
- `gamification-service.yml` - Configuration for Gamification Service
- `analytics-service.yml` - Configuration for Analytics Service

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `CONFIG_REPO_URI` | Git repository URL for configs | `https://github.com/phutruonnttn/yushan-microservices-config-data.git` |
| `EUREKA_URL` | Eureka server URL | `http://localhost:8761/eureka/` |

### Example: user-service.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:yushan_user}
    username: ${DB_USER:yushan_user}
    password: ${DB_PASSWORD:password}
  
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}

jwt:
  secret: ${JWT_SECRET:your-secret-key}
  expiration: 86400000

kafka:
  bootstrap-servers: ${KAFKA_SERVERS:localhost:9092}
```

## 🔗 Connecting from Microservices

To connect a microservice to Config Server, add to `application.yml`:

```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8888
  application:
    name: user-service  # Service name (must match config file name)

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

## 📡 API Endpoints

### Health Check
- `GET /actuator/health` - Health status

### Config Endpoints
- `GET /{application}/{profile}` - Get configuration for application and profile
- `GET /{application}/{profile}/{label}` - Get configuration with specific label

Example:
```bash
# Get configuration for user-service
curl http://localhost:8888/user-service/default

# Get configuration with production profile
curl http://localhost:8888/user-service/production
```

## 🔍 Verification

### Check Config Server is Running

```bash
curl http://localhost:8888/actuator/health
```

### Check Configuration for Service

```bash
# Get configuration for user-service
curl http://localhost:8888/user-service/default
```

### Check Eureka Registration

Open http://localhost:8761 and look for `CONFIG-SERVER` in the list of registered services.

## 🐛 Troubleshooting

### Issue: Config Server Won't Start

**Solution:**
1. Check if port 8888 is already in use
2. Check network connectivity to GitHub (Config Server needs to clone Git repo)
3. Verify `CONFIG_REPO_URI` environment variable is correct
4. View logs for specific errors (check Git clone messages)

### Issue: Microservices Can't Fetch Configuration

**Solution:**
1. Ensure Config Server is running
2. Check `spring.application.name` in microservice matches config file name
3. Check Config Server URL in `spring.config.import`
4. Check Eureka is running (Config Server needs to register with Eureka)

### Issue: Can't Connect to Eureka

**Solution:**
1. Ensure Eureka Server is running on port 8761
2. Check `EUREKA_URL` environment variable
3. Check network connectivity

## 🔐 Security (Production)

In production, you should protect Config Server:

1. Add Spring Security
2. Use authentication for endpoints
3. Encrypt sensitive values in config files
4. Use HTTPS

## 📊 Monitoring

Config Server exposes metrics through:
- Spring Boot Actuator endpoints (`/actuator/metrics`)
- Health check endpoint (`/actuator/health`)

## 🛠️ Built With

- [Spring Boot](https://spring.io/projects/spring-boot) - Application framework
- [Spring Cloud Config](https://spring.io/projects/spring-cloud-config) - Configuration management
- [Spring Cloud Netflix Eureka](https://spring.io/projects/spring-cloud-netflix) - Service discovery

## 🤝 Contributing

1. Fork repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

This project is part of the Yushan Platform ecosystem.

## Testing

### Unit Tests

The Config Server includes unit tests to verify the Spring context loads successfully:

```bash
# Run tests
./mvnw test
```

## Security

### Vulnerability Fixes

- ✅ **CVE-2025-48924 Fixed**: Excluded vulnerable `commons-lang:commons-lang@2.6` from `spring-cloud-starter-netflix-eureka-client` dependency
- ✅ **Security Scanning**: CI/CD pipeline includes OWASP Dependency Check and Snyk vulnerability scanning

## CI/CD Pipeline

The Config Server includes a comprehensive CI/CD pipeline with:

- ✅ **Unit Tests**: JUnit 5 tests for application context loading
- ✅ **Code Quality**: SpotBugs static analysis, Checkstyle code style checks
- ✅ **Code Coverage**: JaCoCo coverage reports
- ✅ **Security Scanning**: OWASP Dependency Check and Snyk vulnerability scanning
- ✅ **Container Scanning**: Trivy container vulnerability scanning
- ✅ **Quality Gates**: SonarCloud analysis and quality gates
- ✅ **Docker Build**: Automated Docker image building and pushing to GitHub Container Registry

## 🔗 Links

- **Config Repository**: [yushan-microservices-config-data](https://github.com/phutruonnttn/yushan-microservices-config-data) - Git repository storing all configuration files
- **Service Registry**: [yushan-microservices-service-registry](https://github.com/phutruonnttn/yushan-microservices-service-registry)
- **API Gateway**: [yushan-microservices-api-gateway](https://github.com/phutruonnttn/yushan-microservices-api-gateway)
- **Platform Documentation**: [yushan-platform-docs](https://github.com/phutruonnttn/yushan-platform-docs) - Complete documentation for all phases
- **Phase 2 Architecture**: See [Phase 2 Microservices Architecture](https://github.com/phutruonnttn/yushan-platform-docs/blob/main/docs/phase2-microservices/PHASE2_MICROSERVICES_ARCHITECTURE.md)
- **Phase 3 Architecture**: See [Phase 3 Kubernetes README](https://github.com/phutruonnttn/yushan-platform-docs/blob/main/docs/phase3-kubernetes/README.md)

---

**Yushan Config Server** - Centralized configuration management for microservices ⚙️

*Last Updated: November 2025*
