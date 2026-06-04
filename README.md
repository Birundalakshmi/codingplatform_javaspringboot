
# Live Coding Arena

A real-time collaborative coding platform built with Spring Boot and MySQL.

## Features

- **User Authentication**: Secure login/register for students, mentors, and administrators
- **Real-time Collaboration**: Multiple users can edit code simultaneously using WebSocket
- **Problem Bank**: Coding problems categorized by difficulty and topic
- **Code Evaluation**: Automatic code compilation and testing
- **Leaderboard**: Performance tracking and rankings
- **Responsive Design**: Works on desktop and mobile devices

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.2, Spring Security, Spring Data JPA
- **Database**: MySQL 8.0
- **Frontend**: HTML5, CSS3, JavaScript, Thymeleaf
- **Real-time**: WebSocket with STOMP protocol
- **Code Editor**: CodeMirror integration

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Git

## Setup Instructions

### 1. Clone the Repository
```bash
git clone <repository-url>
cd java_project3
```

### 2. Database Setup
1. Install MySQL and start the service
2. Create a database (optional - application will create it automatically):
```sql
CREATE DATABASE livecoding_arena;
```
3. Update database credentials in `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/livecoding_arena?createDatabaseIfNotExist=true
    username: root
    password: your_password
```

### 3. Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### 4. Access the Application
- Open your browser and go to: `http://localhost:8080`
- Register a new account or use the sample data

## Default Users

The application initializes with sample problems. You can register new users with different roles:
- **Student**: Can solve problems and view leaderboard
- **Mentor**: Can solve problems and access mentor features
- **Admin**: Full access to all features

## API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### Problems
- `GET /api/problems` - Get all problems
- `GET /api/problems/{id}` - Get specific problem
- `GET /api/problems/category/{category}` - Filter by category
- `GET /api/problems/difficulty/{difficulty}` - Filter by difficulty

### Submissions
- `POST /api/submissions/submit` - Submit code for evaluation

### Leaderboard
- `GET /api/leaderboard` - Get user rankings

## WebSocket Endpoints

- `/ws` - WebSocket connection endpoint
- `/app/code.update` - Send code updates
- `/topic/code` - Subscribe to code changes
- `/app/cursor.update` - Send cursor position
- `/topic/cursor` - Subscribe to cursor updates

## Project Structure

```
src/
├── main/
│   ├── java/com/livecoding/arena/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA entities
│   │   ├── repository/     # Data repositories
│   │   ├── service/        # Business logic
│   │   ├── websocket/      # WebSocket handlers
│   │   └── LiveCodingArenaApplication.java
│   └── resources/
│       ├── static/         # CSS, JS, images
│       ├── templates/      # Thymeleaf templates
│       └── application.yml # Configuration
└── test/                   # Test classes
```

## Usage

1. **Register/Login**: Create an account or login with existing credentials
2. **Browse Problems**: View available coding problems with filters
3. **Code Editor**: Select a problem and start coding
4. **Real-time Collaboration**: Share session with others for collaborative coding
5. **Submit Solutions**: Test and submit your code for evaluation
6. **View Leaderboard**: Check your ranking and progress

## Development

### Adding New Problems
Problems are initialized in `ProblemService.initializeSampleProblems()`. To add more:

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

### Extending Code Evaluation
The `CodeEvaluationService` currently uses simplified evaluation. For production:
- Implement Docker containers for secure code execution
- Add support for multiple programming languages
- Integrate with online judges or testing frameworks

## Security Notes

- Passwords are encrypted using BCrypt
- JWT tokens can be implemented for stateless authentication
- Input validation is applied to prevent SQL injection
- For production, implement proper code sandboxing

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.

# Java_LiveCodingArena

