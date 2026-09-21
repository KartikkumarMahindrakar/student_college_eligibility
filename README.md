# Course Eligibility Portal

A full-stack web application that determines a student's eligibility for college admission
based on their demographic details, 12th-grade marks, and JEE/NEET qualification status —
built for the "Front-End / Full-Stack Assessment: Course Eligibility Portal" brief.

- **Frontend**: Angular 21 (standalone components, signals, zoneless, Angular Material)
- **Backend**: Spring Boot 2.7.18 / Java 11
- **Database**: MySQL 8

## Contents

- [Quick start (Docker)](#quick-start-docker)
- [Manual local setup](#manual-local-setup)
- [Default credentials](#default-credentials)
- [API overview](#api-overview)
- [Documented assumptions](#documented-assumptions)
- [Bonus features implemented](#bonus-features-implemented)
- [Project structure](#project-structure)

## Quick start (Docker)

Requires Docker + Docker Compose.

```bash
docker compose up --build
```

- Frontend: http://localhost
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- MySQL: localhost:3307 (`course_eligibility_db` / root / root) — mapped off the default 3306 in case a local MySQL install already occupies it

The backend seeds one admin user on startup (see [Default credentials](#default-credentials))
and runs its Flyway migrations automatically against the `mysql` service.

## Manual local setup

### Backend

Requires Java 11 and Maven. Start MySQL first (e.g. `docker compose up mysql -d`), then:

```bash
cd backend
mvn spring-boot:run
```

Runs on `http://localhost:8080` against `localhost:3306` by default (see
`src/main/resources/application.yml` for the environment variables that override host,
credentials, JWT secret, admin credentials, and allowed CORS origins).

### Frontend

Requires Node.js `^20.19.0 || ^22.12.0 || >=24.0.0` (Angular 21's minimum). If your local Node
is older, run the Angular CLI through Docker instead, e.g.:

```bash
docker run --rm -v "$(pwd)/frontend:/workspace" -w /workspace node:22-alpine sh -c "npm install && npx ng serve --host 0.0.0.0"
```

Or, with a compatible local Node:

```bash
cd frontend
npm install
npm start
```

Runs on `http://localhost:4200`, proxying `/students`, `/courses`, `/subjects`, and `/auth`
to the backend at `localhost:8080` (see `proxy.conf.json`).

## Default credentials

The staff login (gates Submission History, Statistics, and Excel/PDF export) is seeded on
first backend startup:

| Username | Password |
|---|---|
| `admin` | `Admin@123` |

Override via the `ADMIN_USERNAME` / `ADMIN_PASSWORD` environment variables (see
`docker-compose.yml`). There is no self-registration screen — this matches the assessment's
screen list, which doesn't include one.

## API overview

All paths match the assessment brief literally (no `/api` prefix).

| Method & path | Auth | Purpose |
|---|---|---|
| `POST /students/check-eligibility` | public | Submit the form, get an eligibility decision |
| `GET /students/check-eligibility/{id}` | public | Re-fetch a result (Result screen refresh/deep-link) |
| `GET /students/history` | staff | Search/filter submissions (`name`, `course`, `status`, `from`, `to`) |
| `GET /students/history/{id}` | staff | Full submission detail |
| `GET /students/history/export/excel` \| `/export/pdf` | staff | Download the (filtered) history table |
| `GET /students/statistics` | staff | Aggregate counts for the charts screen |
| `GET /courses` | public | Course catalog grouped by stream (subjects/cutoff/exam requirement) |
| `GET /subjects` | public | The 13-subject master list |
| `POST /auth/login` | public | Staff login, returns a JWT |

Full request/response schemas and try-it-out: **Swagger UI** at `/swagger-ui.html` once the
backend is running.

## Documented assumptions

The assessment brief leaves a few things unspecified. Rather than guess silently, here's what
was decided and why:

1. **Minimum passing mark of 40 per required subject**, applied everywhere. The brief gives
   numeric cutoffs only for Engineering/Medicine (as an *average* of 3 subjects) and none at all
   for Commerce/Humanities. A 40-mark floor (the standard Indian secondary-education pass mark)
   is applied per required subject across all four streams — as the sole eligibility bar for
   Commerce/Humanities, and as an additional floor under Engineering/Medicine's own aggregate
   cutoffs (so a single very low subject can't be masked by a high average).
2. **Recommendation ordering**: when rejected, alternatives are ranked same-stream-as-desired
   first, then by catalog order (Engineering → Medicine → Commerce → Humanities, matching the
   brief's own listing order), capped at 3.
3. **Gender values** are matched exactly as `Male` / `Female` / `Other` (the brief's own casing)
   — no silent case coercion, consistent with "backend must not trust the frontend."
4. **Public single-result lookup**: the brief's sample API only shows `POST
   /students/check-eligibility`, but the Result screen needs to survive a refresh/deep link.
   `GET /students/check-eligibility/{id}` was added as a public endpoint scoped to the same
   resource the POST already creates — distinct from the staff-only `GET /students/history/{id}`
   used for admin drill-down from the protected history table.
5. **The 6 subject-mark entries** are picked from a fixed master list of 13 subjects (the union
   of every subject named anywhere in the brief's eligibility rules), not free text, and must be
   6 *distinct* subjects — enforced client-side and server-side.

## Bonus features implemented

| Feature | Where |
|---|---|
| JWT Authentication | Spring Security + JWT filter chain; gates History/Statistics/Export |
| Dockerized deployment | `backend/Dockerfile`, `frontend/Dockerfile`, root `docker-compose.yml` |
| Unit tests | Backend: JUnit5/Mockito on the eligibility engine, MockMvc on controllers, a full-stack security smoke test |
| Swagger / OpenAPI | `/swagger-ui.html`, bearer-auth scheme wired for protected endpoints |
| Export to Excel/PDF | Apache POI / OpenPDF, reusing the same history filters as the on-screen table |
| Dark mode toggle | Material M3 theming, persisted in `localStorage` |
| Charts | Statistics screen: eligible-vs-not doughnut + per-course bar chart (Chart.js via ng2-charts) |

## Project structure

```
student_management_system/
├── backend/    Spring Boot 2.7.18 / Java 11 (Maven)
├── frontend/   Angular 21 (standalone components, signals, Material)
├── docker-compose.yml
└── screenshots/
```
