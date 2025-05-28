# Nikolay Ivanov  Collaboration App With Spring
A Spring Boot application that analyzes employee project assignments to determine which pair of employees has collaborated the most (in days).

## Table of Contents
1. [Features](#features)
2. [Getting Started](#getting-started)
3. [Configuration](#configuration)
4. [API Endpoint](#api-endpoint)
5. [CSV Format](#csv-format)
6. [Service Details](#service-details)
7. [Logging & Monitoring](#logging--monitoring)
8. [License](#license)

## Features
- Parse CSV files of employee assignments (supports `NULL` as 'today').
- Compute overlapping days per project and aggregate across projects.
- Return the employee pair with the highest total collaboration time.
- Configurable logging and startup banner.
- CORS-enabled for integration with front-end clients.

## Getting Started


### Build & Run
```bash
mvn clean install
mvn spring-boot:run
```
The service will be available at `http://localhost:8080`.

## Configuration
All runtime settings are in `src/main/resources/application.properties`:
```properties
spring.main.log-startup-info=false
logging.level.org.springframework.boot.SpringApplication=OFF
logging.level.org.springframework.boot.StartupInfoLogger=INFO
logging.level.org.apache.catalina=ERROR
logging.level.org.springframework.boot.web.embedded.tomcat=ERROR
logging.level.root=INFO
```

## API Endpoint
`POST /api/process`

- **Content-Type**: multipart/form-data
- **Form Field**: `file` (CSV file)

**Response** (JSON):
```json
{
  "summary": { "emp1": 143, "emp2": 218, "days": 473 },
  "details": [
    { "emp1": 143, "emp2": 218, "projectId": 12, "days": 51 },
    { "emp1": 101, "emp2": 102, "projectId": 12, "days": 17 }
  ]
}
```
Or plain text: `No overlaps found` if no overlapping assignments.

### Example cURL
```bash
curl -X POST -F file=@test-data.csv \
  http://localhost:8080/api/process
```

## CSV Format
| Column     | Type   | Description                       |
|------------|--------|-----------------------------------|
| EmpID      | int    | Employee identifier               |
| ProjectID  | int    | Project identifier                |
| DateFrom   | date   | Start date (`YYYY-MM-DD`)         |
| DateTo     | date   | End date (`YYYY-MM-DD` or `NULL` for today) |

Sample:
```csv
EmpID,ProjectID,DateFrom,DateTo
143,12,2013-11-01,2014-01-05
218,12,2013-12-15,2014-02-20
```

## Service Details
The `CollaborationService` logic:
1. **Parse CSV**: Apache Commons CSV reads rows into `Assignment` objects.
2. **Compute Overlaps**:
   - Group by `projectId`.
   - Generate unique employee pairs within each project.
   - Calculate overlapping days.
3. **Aggregate Results**:
   - Sum days across all projects per pair.
   - Identify the pair with maximum total.

Returns a `PairResult(emp1, emp2, days)`.

## Logging & Monitoring
- **SLF4J/Logback** via Lombok `@Slf4j`.
- Custom `ServerPortLogger` prints service URL at WARN level after startup.
- Startup banner disabled but logging of key events remains.

## License
© Nikolay Ivanov
