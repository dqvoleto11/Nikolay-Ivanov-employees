# Nikolay-Ivanov-employees

**Employees Project with Java (Spring Boot) and Angular Front-End**

This repository contains a full-stack application that processes CSV files of employee project assignments and determines which pair of employees has worked together the longest, as well as showing detailed overlaps per project.

---

## Table of Contents
1. [Project Overview](#project-overview)  
2. [Backend (Spring Boot)](#backend-spring-boot)  
   - [Structure](#structure)  
   - [Configuration](#configuration)  
   - [API Endpoint](#api-endpoint)  
   - [Running the Backend](#running-the-backend)  
3. [Front-End (Angular)](#front-end-angular)  
   - [Structure](#structure-1)  
   - [Progress Simulation](#progress-simulation)  
   - [Template & Styles](#template--styles)  
   - [Running the Front-End](#running-the-front-end)  
4. [Sample CSV Files](#sample-csv-files)  
5. [License](#license)

---

## Project Overview

Users upload a CSV containing employee assignments (`EmpID`, `ProjectID`, `DateFrom`, `DateTo`). The backend reads the file, supports multiple date formats, calculates overlapping days per project for each employee pair, and returns:

- **Summary**: the two employees who share the most days together.  
- **Details**: a list of all common projects with the days worked per pair.

The Angular front-end allows file selection, simulates upload/processing progress in 20% steps, and then displays the summary and a data grid of detailed results.

---

## Backend (Spring Boot)

### Structure
```
src/main/java/com/nikolayivanov/employee/
├─ EmployeeApplication.java      # Main Spring Boot app
├─ ServerPortLogger.java         # Listens for the ApplicationReadyEvent and logs the server’s full base URL and port
├─ model/
│  ├─ Assignment.java                 # EmpID, ProjectID, LocalDate from/to
│  ├─ PairResult.java                 # emp1, emp2, total days
│  ├─ DetailedResult.java             # emp1, emp2, projectId, days
│  └─ CollaborationResponse.java      # summary + List<DetailedResult>
├─ service/CollaborationService.java  # CSV parsing, overlap calculations
└─ controller/CollaborationController.java  # POST /api/process
```

### Configuration

- **CSV Parsing** uses Apache Commons CSV with header validation.  
- **Multi-format Date Parsing**: supports `yyyy-MM-dd`, `dd/MM/yyyy`, `MM-dd-yyyy`, `yyyy.MM.dd`, and `NULL` → today.  
- **Logging & Banner** customization in `src/main/resources/application.properties`:
  ```properties
  spring.main.log-startup-info=false
  logging.level.org.springframework.boot.SpringApplication=OFF
  logging.level.org.springframework.boot.StartupInfoLogger=INFO
  logging.level.org.apache.catalina=ERROR
  logging.level.root=INFO
  ```
- **Custom Banner** in ASCII art at `src/main/resources/banner.txt`.


### API Endpoint
```http
POST /api/process
Content-Type: multipart/form-data
Form Field: file (CSV file)
```
**Response** (200 OK):
```json
{
  "summary": { "emp1": 143, "emp2": 218, "days": 473 },
  "details": [
    { "emp1": 143, "emp2": 218, "projectId": 12, "days": 51 },
    { "emp1": 101, "emp2": 102, "projectId": 12, "days": 17 }
  ]
}
```
Errors:  
- `400 Bad Request`: CSV header invalid or date parse errors.  
- `500 Internal Server Error`: unexpected IO failures.

### Running the Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
Server listens on **http://localhost:8080**.

---

## Front-End (Angular)

### Structure
```
src/app/
├─ api.service.ts      # HTTP client, processCsvWithProgress()
├─ app.component.ts    # file selection, progress simulation, API call
├─ app.component.html  # template: input, button, progress bar, spinner, results grid
```

### Progress Simulation

- **Upload Phase**: simulates progress in 20% steps every 500ms.  
- **Processing Phase**: upon reaching 100% upload, resets to 0% and repeats simulation until 100%.  
- **Phase Tracking**: `phase` property (`idle`, `upload`, `processing`, `done`) controls button text and progress bar visibility.

### Template & Styles

- Uses **Bootstrap 5** for layout, buttons, progress bars, spinner, and tables.  
- **app.component.html** handles all UI states with `*ngIf` on `phase` and `progress`.

### Running the Front-End
```bash
cd frontend
npm install
ng serve
```
App available at **http://localhost:4200**; ensure backend is running.

---

## Sample CSV Files

- `test-data.csv`: basic overlap scenarios.  
- `various_date_formats.csv`: exercises date parsing logic.  
- `test-overlap-formats.csv`: ensures calculation of overlaps across multiple formats.

---

## License

© Nikolay Ivanov
