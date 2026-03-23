# Conference Management System (CMS)

> A full-stack web application built with **Spring Boot MVC**, **Thymeleaf**, **Spring Security**, **Spring Data JPA**, and **H2 Database**.

---

## Table of Contents
1. [Overview](#overview)
2. [Technology Stack](#technology-stack)
3. [Directory Structure](#directory-structure)
4. [MVC Architecture](#mvc-architecture)
5. [Design Patterns Used](#design-patterns-used)
6. [Features](#features)
7. [User Roles & Credentials](#user-roles--credentials)
8. [Setup & Run](#setup--run)
9. [Database](#database)
10. [UML Notes](#uml-notes)

---

## Overview

The Conference Management System is a unified web platform that automates end-to-end management of professional conferences. It supports 5 user roles:

| Role | Responsibilities |
|------|-----------------|
| **Admin** | Full system control: conferences, sessions, users, papers, certificates, reports |
| **Author** | Submit papers & abstracts, track reviews, submit revisions |
| **Reviewer** | Evaluate assigned papers, submit structured feedback with scores |
| **Speaker** | View assigned sessions, manage profile, view attendee feedback |
| **Attendee** | Register for conferences, browse schedule, give feedback, collect certificates |

---

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.2 |
| Web MVC | Spring MVC + Thymeleaf |
| Security | Spring Security 6 (form login, role-based access) |
| Persistence | Spring Data JPA + Hibernate |
| Database | H2 (in-memory, dev) — swap to MySQL/PostgreSQL for prod |
| Build | Maven |
| Java Version | Java 17 |
| Frontend | Thymeleaf + HTML5 + CSS3 + Vanilla JS |
| Extras | Lombok, iText 7 (PDF certificates) |

---

## Directory Structure

```
cms/
├── pom.xml
└── src/
    └── main/
        ├── java/com/cms/
        │   ├── ConferenceManagementApplication.java   ← Entry point
        │   │
        │   ├── config/
        │   │   ├── SecurityConfig.java                ← Spring Security config
        │   │   └── CustomLoginSuccessHandler.java     ← Role-based redirect after login
        │   │
        │   ├── model/                                 ← JPA Entities (M in MVC)
        │   │   ├── User.java
        │   │   ├── Paper.java
        │   │   ├── Review.java
        │   │   ├── Session.java
        │   │   ├── Conference.java
        │   │   ├── Registration.java
        │   │   ├── Abstract.java
        │   │   ├── Certificate.java
        │   │   ├── Message.java
        │   │   └── SessionFeedback.java
        │   │
        │   ├── repository/                            ← Spring Data JPA Repositories
        │   │   ├── UserRepository.java
        │   │   ├── PaperRepository.java
        │   │   ├── ReviewRepository.java
        │   │   ├── SessionRepository.java
        │   │   ├── ConferenceRepository.java
        │   │   ├── RegistrationRepository.java
        │   │   ├── AbstractRepository.java
        │   │   ├── CertificateRepository.java
        │   │   ├── MessageRepository.java
        │   │   └── SessionFeedbackRepository.java
        │   │
        │   ├── service/                               ← Business Logic (Service Layer)
        │   │   ├── UserService.java                   ← Interface (Strategy Pattern)
        │   │   ├── PaperService.java
        │   │   ├── ReviewService.java
        │   │   ├── SessionService.java
        │   │   ├── ConferenceService.java
        │   │   ├── RegistrationService.java
        │   │   ├── DataInitializerService.java        ← Seeds demo data on startup
        │   │   └── impl/
        │   │       ├── UserServiceImpl.java
        │   │       ├── PaperServiceImpl.java
        │   │       ├── ReviewServiceImpl.java
        │   │       ├── SessionServiceImpl.java
        │   │       ├── ConferenceServiceImpl.java
        │   │       ├── RegistrationServiceImpl.java
        │   │       ├── AbstractServiceImpl.java       ← Minor Feature 1
        │   │       ├── CertificateServiceImpl.java    ← Minor Feature 2
        │   │       ├── MessageServiceImpl.java        ← Minor Feature 3
        │   │       ├── FeedbackServiceImpl.java       ← Minor Feature 4
        │   │       └── CustomUserDetailsService.java  ← Spring Security integration
        │   │
        │   └── controller/                            ← MVC Controllers (C in MVC)
        │       ├── AuthController.java                ← /login, /register
        │       ├── AdminController.java               ← /admin/**
        │       ├── AuthorController.java              ← /author/**
        │       ├── ReviewerController.java            ← /reviewer/**
        │       ├── SpeakerController.java             ← /speaker/**
        │       ├── AttendeeController.java            ← /attendee/**
        │       ├── MessageController.java             ← /messages/**
        │       └── ScheduleController.java            ← /schedule
        │
        └── resources/
            ├── application.properties
            ├── static/
            │   ├── css/style.css                      ← Complete UI stylesheet
            │   └── js/app.js                          ← Client-side interactions
            └── templates/                             ← Thymeleaf Views (V in MVC)
                ├── auth/
                │   ├── login.html
                │   └── register.html
                ├── common/
                │   ├── fragments.html                 ← Navbar + alerts fragments
                │   ├── schedule.html
                │   └── messages.html
                ├── admin/
                │   ├── dashboard.html
                │   ├── papers.html
                │   ├── paper-detail.html
                │   ├── sessions.html
                │   ├── conferences.html
                │   ├── conference-edit.html
                │   ├── users.html
                │   ├── certificates.html
                │   └── reports.html
                ├── author/
                │   ├── dashboard.html
                │   ├── submit-paper.html
                │   ├── my-papers.html
                │   ├── paper-detail.html
                │   ├── revise-paper.html
                │   ├── submit-abstract.html
                │   ├── my-abstracts.html
                │   ├── revise-abstract.html
                │   └── profile.html
                ├── reviewer/
                │   ├── dashboard.html
                │   ├── my-reviews.html
                │   ├── review-form.html
                │   └── profile.html
                ├── speaker/
                │   ├── dashboard.html
                │   ├── my-sessions.html
                │   ├── session-detail.html
                │   └── profile.html
                └── attendee/
                    ├── dashboard.html
                    ├── conferences.html
                    ├── register-conference.html
                    ├── schedule.html
                    ├── feedback.html
                    ├── certificates.html
                    └── profile.html
```

---

## MVC Architecture

This application strictly follows the **Model-View-Controller (MVC)** pattern enforced by Spring MVC:

```
Browser Request
      │
      ▼
  DispatcherServlet  (Spring MVC Front Controller)
      │
      ▼
  Controller  (@Controller classes)
  ├── Validates request parameters
  ├── Calls Service layer
  └── Adds data to Model → returns View name
      │
      ▼
  Service Layer  (Business Logic)
  ├── Applies business rules
  ├── Orchestrates repositories
  └── Handles transactions (@Transactional)
      │
      ▼
  Repository Layer  (JpaRepository)
  └── CRUD + custom JPQL queries → Database
      │
      ▼
  Model  (JPA Entities)
  └── Mapped to database tables via Hibernate
      │
      ▼
  View  (Thymeleaf Templates .html)
  └── Rendered server-side, returned to browser
```

**Key Spring MVC Annotations used:**

| Annotation | Purpose |
|-----------|---------|
| `@Controller` | Marks MVC controller |
| `@RequestMapping` | Maps URL prefix |
| `@GetMapping` / `@PostMapping` | HTTP method mapping |
| `@PathVariable` | Extracts URL path variable |
| `@RequestParam` | Extracts query/form param |
| `@ModelAttribute` | Binds form data to object |
| `@PreAuthorize` | Method-level security |
| `RedirectAttributes` | Flash messages across redirects |

---

## Design Patterns Used

### 1. MVC Pattern (Architectural)
The entire application is structured using Model-View-Controller enforced by Spring MVC's `DispatcherServlet`. Controllers never contain business logic; Services never render views.

### 2. Strategy Pattern
Service interfaces (`UserService`, `PaperService`, `ReviewService`, etc.) define contracts. Implementations (`UserServiceImpl`, etc.) are the concrete strategies injected via Spring's DI. This allows swapping implementations without changing controllers.

```java
// Interface = Strategy contract
public interface ReviewService {
    Review assignReview(Paper paper, User reviewer);
    Review submitReview(Long reviewId, Review reviewData);
    void autoDecidePaperStatus(Paper paper);  // ← Business rule strategy
}

// Concrete strategy
@Service
public class ReviewServiceImpl implements ReviewService { ... }
```

### 3. Repository Pattern (DAO)
Spring Data JPA repositories abstract all data access. Controllers and Services never write SQL — they use repository methods. Custom JPQL queries are encapsulated inside repositories.

```java
@Query("SELECT r FROM Review r WHERE r.reviewer = :reviewer AND r.status != 'SUBMITTED'")
List<Review> findPendingReviewsByReviewer(User reviewer);
```

### 4. Builder Pattern
Lombok's `@Builder` annotation generates fluent builders for all entity classes. Used extensively in `DataInitializerService` for clean, readable object construction.

```java
Paper paper = Paper.builder()
    .title("Scalable Microservices...")
    .author(author1)
    .status(Paper.Status.UNDER_REVIEW)
    .build();
```

### 5. Template Method Pattern (Thymeleaf Fragments)
`common/fragments.html` defines reusable navbar and alerts fragments included in all pages via `th:replace`. This ensures consistent layout without duplication.

```html
<th:block th:replace="~{common/fragments :: navbar}"></th:block>
```

### 6. Observer / Chain of Responsibility (Auto-Decision)
When all reviews for a paper are submitted, `ReviewServiceImpl.autoDecidePaperStatus()` automatically updates the paper's status based on aggregate reviewer recommendations — simulating an observer response to state change.

### 7. Singleton Pattern
All Spring `@Service`, `@Repository`, and `@Controller` beans are singletons managed by the Spring IoC container.

### 8. Factory Pattern (via Spring Security)
`CustomLoginSuccessHandler` acts as a factory that produces different redirect URLs based on the authenticated user's role.

---

## Features

### Major Features (4)

#### 1. Paper Submission & Peer Review Management
- Authors submit papers with title, abstract, keywords, and file upload
- Admin assigns multiple reviewers per paper (conflict-of-interest check: author ≠ reviewer)
- Reviewers submit structured feedback: strengths, weaknesses, score (1–10), recommendation
- Workflow: SUBMITTED → UNDER_REVIEW → ACCEPTED / REJECTED / REVISION_REQUIRED → REVISED
- Auto-decision: paper status updated automatically when all reviews are submitted
- Version tracking on revisions

#### 2. Speaker Management
- Speaker profiles with bio, institution, topics
- Admin assigns accepted papers/sessions to speakers
- Conflict detection: system prevents scheduling the same speaker in overlapping time slots
- Speakers view their sessions, audience feedback stats, and comments

#### 3. Attendee Registration
- Online registration for conferences with registration type (Student / Professional / Academic / Virtual)
- Dietary preferences and special requirements captured
- Automatic waitlisting when conference reaches max capacity
- Attendees can cancel registrations
- Registration status: CONFIRMED / WAITLISTED / CANCELLED

#### 4. Schedule Builder
- Admin creates sessions with date, time, room, track, speaker, and session type
- Supports parallel sessions across multiple tracks and rooms
- **Conflict detection** prevents: same room double-booking and same speaker double-scheduling
- Schedule displayed as a day-by-day timeline view

### Minor Features (4)

#### 1. Abstract Management
- Authors submit abstracts before full paper submission
- Admin approves / rejects / requests revision
- Version tracking on revisions
- Status: SUBMITTED → APPROVED / REJECTED / REVISION_REQUESTED

#### 2. Certificate Generation
- Admin issues certificates individually or in bulk to all confirmed attendees
- Certificate types: Participation, Presentation, Best Paper, Reviewer
- Auto-generated unique certificate number format: `CMS-{confId}-{TYPE}-{UUID}`
- Attendees view certificates in a styled certificate display

#### 3. Networking / Messaging
- Any authenticated user can message any other user
- Inbox and Sent views
- Messages auto-marked as read when inbox is opened
- Unread count displayed in attendee dashboard

#### 4. Feedback Collection
- Attendees rate sessions on Content Quality, Speaker Quality, and Overall (1–5 stars)
- Optional text comments
- Duplicate submission prevention (one feedback per session per attendee)
- Admin Reports page shows aggregated ratings per session
- Speakers view their own session feedback and stats

---

## User Roles & Credentials

Pre-seeded demo accounts (auto-loaded on startup):

| Username | Password | Role | Notes |
|----------|----------|------|-------|
| `admin` | `admin123` | Admin | Full system access |
| `alice` | `alice123` | Author | MIT — has 2 submitted papers |
| `bob` | `bob123` | Author | Stanford — has 1 accepted paper |
| `carol` | `carol123` | Reviewer | Cambridge — has 2 completed reviews |
| `dave` | `dave123` | Reviewer | Oxford — has 1 pending review |
| `eve` | `eve123` | Speaker | Google Research — has 2 sessions |
| `frank` | `frank123` | Speaker | Microsoft Research — has 2 sessions |
| `grace` | `grace123` | Attendee | IIT Bombay — registered & confirmed |
| `henry` | `henry123` | Attendee | IISc Bangalore — registered & confirmed |

---

## Setup & Run

### Prerequisites
- Java 17+
- Maven 3.8+

### Steps

```bash
# 1. Extract the ZIP
unzip conference-management-system.zip
cd cms

# 2. Build
mvn clean package -DskipTests

# 3. Run
mvn spring-boot:run

# OR run the JAR directly:
java -jar target/conference-management-system-1.0.0.jar
```

### Access
- **Application:** http://localhost:8080
- **H2 Console:** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:cmsdb`
  - Username: `sa` | Password: *(empty)*

### Switch to MySQL (Production)
In `application.properties`, replace H2 config with:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cmsdb
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
```
And uncomment the MySQL dependency in `pom.xml`.

---

## Database

### Entity Relationship Summary

```
User ──────────────────────────────────────────────────────────┐
 │                                                              │
 ├── Paper (author)                                            │
 │    └── Review (paper ←→ reviewer)                          │
 │                                                             │
 ├── Abstract (author)                                         │
 │                                                             │
 ├── Registration (attendee ←→ conference)                     │
 │                                                             │
 ├── Session (speaker)                                         │
 │    ├── Session ←→ Paper (linked accepted paper)             │
 │    └── SessionFeedback (session ←→ attendee)               │
 │                                                             │
 ├── Message (sender ←→ receiver)                             │
 │                                                             │
 └── Certificate (recipient ←→ conference)                    │
                                                              │
Conference ────────────────────────────────────────────────────┘
 └── Registration[]
```

All tables are auto-created by Hibernate on startup (`ddl-auto=create-drop`) and seeded by `DataInitializerService`.

---

## UML Notes

### Use Case Diagram (Summary)
- **Admin:** Create conference, Manage papers, Assign reviewers, Create sessions, Issue certificates, View reports
- **Author:** Submit paper, Submit abstract, View reviews, Revise paper
- **Reviewer:** View assigned papers, Submit review
- **Speaker:** View sessions, View feedback
- **Attendee:** Register for conference, View schedule, Submit feedback, View certificates
- **All:** Send/receive messages, View schedule

### Key State Transitions

**Paper States:**
```
SUBMITTED → UNDER_REVIEW → ACCEPTED
                         → REJECTED
                         → REVISION_REQUIRED → REVISED → (back to UNDER_REVIEW)
```

**Registration States:**
```
[Register] → CONFIRMED (if capacity available)
           → WAITLISTED (if full)
CONFIRMED → CANCELLED
```

**Review States:**
```
ASSIGNED → IN_PROGRESS → SUBMITTED
```

**Abstract States:**
```
SUBMITTED → APPROVED
          → REJECTED
          → REVISION_REQUESTED → SUBMITTED (revised)
```
