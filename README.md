# PatchMaker

**A backend platform that matches open-source maintainers with contributors based on real skill compatibility — not just keyword search.**

---

## Overview

Open-source projects struggle to find the right contributors, and developers struggle to find projects that actually match their skill level and interests. PatchMaker is a Spring Boot backend that solves this matching problem with a combination of structured search, an algorithmic recommendation engine, and LLM-powered skill analysis — built as a clean, layered **modular monolith**.

## Problem Statement

- Maintainers post project needs across scattered channels (Twitter, Discord, GitHub issues labeled "help wanted") with no structured way to reach relevant contributors.
- Developers, especially beginners, don't know which repositories match their current skill level, or what to work on first once they find one.
- Existing "good first issue" aggregators surface issues, not skill compatibility or contribution roadmaps.

## Solution

PatchMaker gives maintainers a structured way to list projects with required tech stacks, difficulty levels, and open roles — and gives developers profile-based search, algorithmic skill-overlap recommendations, and AI-generated compatibility analysis and contribution roadmaps, so they know not just *what* to work on, but *how* to start.

## Features

- **JWT + GitHub OAuth2 authentication** with role-based access control (`DEVELOPER` / `MAINTAINER`)
- **Developer profiles** — skills, languages, experience level, interests
- **Project listings** — maintainers post projects with tech stack, difficulty, required roles
- **Application workflow** — apply, accept, reject, with ownership-validated authorization
- **Bookmarks & reviews** — save projects, review after verified contribution
- **AI-powered features (Groq API)** — skill-match analysis and contribution guide generation
- **Algorithmic recommendation engine** — deterministic skill-overlap scoring, no external dependency
- **Dynamic search** — keyword, technology, difficulty, role, and status filters with pagination and sorting

## Architecture

PatchMaker is built as a **modular monolith** — a single deployable Spring Boot application with strict internal package boundaries (controller → service → repository) rather than a distributed microservice system.

**Why modular monolith over microservices:** At this project's current scale, splitting services would introduce network calls, data-consistency challenges, and infrastructure overhead (service discovery, message brokers) without a corresponding scaling or team-ownership need to justify it. A modular monolith delivers the same separation-of-concerns benefit at the code level, while keeping deployment, debugging, and transactions simple. Service boundaries are enforced through packages and interfaces, so extracting a module into its own service later — if genuinely needed — would be a scoped, well-understood change rather than a rewrite.

```
Client
  ↓
JWT Authentication Filter (Spring Security)
  ↓
Controller Layer (HTTP only)
  ↓
Service Layer (business logic, authorization rules)
  ↓
Repository Layer (Spring Data JPA)
  ↓
MySQL
```

## Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3 |
| Security | Spring Security, JWT, OAuth2 Client (GitHub) |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL |
| AI | Groq API (Llama models, OpenAI-compatible chat completions) |
| Build | Maven |
| Utilities | Lombok, Bean Validation (Jakarta) |

## Database Design Overview

- `User` — core identity; `role` (`DEVELOPER`/`MAINTAINER`) and `authProvider` (`LOCAL`/`GITHUB`) stored as enums
- `User` ↔ `DeveloperProfile` — `@OneToOne`, one profile per developer
- `User` → `Project` — `@ManyToOne`, one maintainer owns many projects
- `Application` — join entity between `User` (developer) and `Project`, tracks `PENDING`/`ACCEPTED`/`REJECTED` state
- `Bookmark`, `Review` — join entities following the same pattern, deliberately modeled as explicit entities rather than raw `@ManyToMany`, to support timestamps, future extensibility, and independent querying
- Value collections (`skills`, `techStack`, `requiredRoles`, etc.) use `@ElementCollection`, generating dedicated join tables per field

## Authentication Flow

**Local:** Register (BCrypt-hashed password) → Login → credentials validated by `AuthenticationManager` → JWT issued containing `userId`, `email`, `role` → client sends `Authorization: Bearer <token>` on every subsequent request → `JwtAuthenticationFilter` validates the token and populates Spring's `SecurityContext` per request.

**GitHub OAuth2:** User redirects to GitHub → GitHub returns profile data → backend creates a `User` if one doesn't exist (`authProvider = GITHUB`) → same JWT issuance logic runs as local login, so the rest of the system never needs to know which login path was used.

Only JWT access tokens are used — no refresh token flow — keeping the authentication surface intentionally simple for this stage of the project.

## AI Integration

Two Groq-powered features, using prompt-engineered requests to an LLM rather than a custom-trained model:

- **Skill Match Analyzer** — compares a developer's profile against a project's requirements, returns a match percentage, matching/missing skills, and a short natural-language recommendation
- **Contribution Guide Generator** — given a project's description and tech stack, generates a beginner-friendly, step-by-step contribution roadmap

The service layer builds structured prompts requesting JSON-only responses, parses them into typed DTOs, and never persists raw AI output — results are generated fresh per request since profile/project data can change between calls.

## Search & Recommendation System

- **Search:** Spring Data JPA **Specifications** build dynamic, composable `WHERE` clauses for keyword, technology, difficulty, role, and status filters — chosen over static JPQL methods because filter combinations are open-ended and would otherwise require a combinatorial explosion of query methods
- **Pagination & sorting:** `Pageable`/`Page<T>` ensures the database returns only the requested page, with a custom `CASE`-based ordering for difficulty level (since alphabetical enum order doesn't reflect real difficulty progression)
- **Recommendation engine (non-AI):** deterministic skill-overlap scoring — `matched required skills ÷ total required skills × 100` — for instant, dependency-free bulk ranking across all open projects, complementing the deeper, narrative AI analysis available per-project

## Project Structure

```
com.patchmaker.coreservice
├── controller       → HTTP layer only, delegates to services
├── service           → business logic interfaces
│   └── impl          → business logic implementations
├── repository        → Spring Data JPA interfaces + Specifications
├── entity             → JPA-mapped domain models
├── dto
│   ├── request        → validated inbound payloads
│   └── response        → controlled outbound payloads
├── security           → JWT filter, JWT service, UserPrincipal, OAuth2 handling
├── config             → Security config, OpenAPI config, bean definitions
├── exception          → custom exceptions + centralized handler
└── client             → external API clients (Groq)
```

## How to Run

1. Clone the repository
2. Create a MySQL database: `CREATE DATABASE patchmaker_db;`
3. Set the required environment variables (see below)
4. Run `CoreServiceApplication` from your IDE, or `mvnw spring-boot:run`
5. API available at `http://localhost:8080`

## Environment Variables Required

| Variable | Description |
|---|---|
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `DB_PORT` | MySQL port (default `3306`) |
| `JWT_SECRET` | Base64-encoded signing key for JWT |
| `JWT_EXPIRATION` | Token validity in ms (default `86400000`) |
| `GITHUB_CLIENT_ID` | GitHub OAuth App client ID |
| `GITHUB_CLIENT_SECRET` | GitHub OAuth App client secret |
| `GROQ_API_KEY` | Groq API key for AI features |

See `application-example.properties` for the full template.

## API Overview

| Module | Base Path |
|---|---|
| Authentication | `/api/auth` |
| Developer Profile | `/api/developers/profile` |
| Projects | `/api/projects` |
| Applications | `/api/applications` |
| Bookmarks | `/api/bookmarks` |
| Reviews | `/api/reviews` |
| AI | `/api/ai` |
| Search | `/api/search` |
| Recommendations | `/api/recommendations` |

## Future Enhancements

- Notification system for application status changes
- Caching layer for frequently searched project queries
- Full-text search (MySQL `FULLTEXT` or Elasticsearch) to replace `LIKE`-based keyword search
- Refresh token support for longer-lived sessions
- Rate limiting on AI endpoints

## Contributing

This is currently a personal learning project and not open for external contributions yet. Feel free to fork and adapt.

## License

This project is licensed under the MIT License. See the LICENSE file for details.
## Author

**Ashish Pawar**

- **GitHub:** [AshishPawar24](https://github.com/AshishPawar24)
- **LinkedIn:** [Ashish Pawar](https://www.linkedin.com/in/ashish-pawar-24ash/)
- **Email:** vishnoiashish67@gmail.com

