# Coding Arena

A full-stack online coding practice platform built with Java Spring Boot and MySQL. Students can solve problems in an in-browser code editor, mentors can manage problems, and admins can monitor overall progress — all through role-based dashboards.

## Features

- **Role-based Authentication** — Secure login/register for Students, Mentors, and Admins with BCrypt password encryption
- **Problem Bank** — Coding problems categorized by difficulty (Easy, Medium, Hard) and topic
- **In-browser Code Editor** — Supports Java, Python, C, and C++
- **Automatic Code Evaluation** — Compiles and runs code against test cases with detailed error feedback
- **Score-based Leaderboard** — Every accepted submission earns points; rankings updated on each submission
- **Mentor Dashboard** — Add, edit, and delete problems; view top performers and struggling students
- **Admin Dashboard** — Monitor all users, problems, and submissions with student progress tracking
- **Light Theme UI** — Clean, responsive design that works on desktop and mobile

## Technology Stack

- **Backend** — Java 17, Spring Boot 3.2, Spring Security, Spring Data JPA
- **Database** — MySQL 8.0
- **Frontend** — HTML5, CSS3, JavaScript, Thymeleaf
- **Build Tool** — Maven

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

## Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/Birundalakshmi/Codingplatform_springboot.git
cd Codingplatform_springboot
```

### 2. Database Setup
Update database credentials in `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/livecoding_arena?createDatabaseIfNotExist=true
    username: root
    password: your_password
```

### 3. Build and Run
```bash
mvn clean install
mvn spring-boot:run
```

### 4. Access the Application
Open your browser and go to `http://localhost:8088`

## User Roles

| Role    | Access |
|---------|--------|
| Student | Solve problems, submit code, view leaderboard |
| Mentor  | All student access + add/manage problems, view student progress |
| Admin   | Full access — manage users, problems, and view all submissions |

## API Endpoints

### Authentication
- `POST /api/auth/register` — Register a new user
- `POST /api/auth/login` — Login

### Problems
- `GET /api/problems` — Get all problems
- `GET /api/problems/{id}` — Get a specific problem

### Submissions
- `POST /api/submissions/submit` — Submit code for evaluation

### Leaderboard
- `GET /api/leaderboard` — Get user rankings

## Project Structure

```
src/
├── main/
│   ├── java/com/livecoding/arena/
│   │   ├── config/         # Security and WebSocket configuration
│   │   ├── controller/     # REST and Web controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA entities (User, Problem, Submission)
│   │   ├── repository/     # Spring Data repositories
│   │   ├── service/        # Business logic
│   │   └── LiveCodingArenaApplication.java
│   └── resources/
│       ├── static/         # CSS, JS
│       ├── templates/      # Thymeleaf HTML templates
│       └── application.yml
└── test/                   # Unit tests
```

## Adding New Problems

Mentors and Admins can add problems directly from their dashboards. To add programmatically via `ProblemService`:

```java
createProblem(
    "Problem Title",
    "Problem description...",
    Problem.Difficulty.MEDIUM,
    "Category",
    "Test input",
    "Expected output"
);
```

## License

This project is licensed under the MIT License.
