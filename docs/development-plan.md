# InfraPulse Development Plan

## Goal

Build InfraPulse incrementally into a fully working GitHub portfolio project.

The final system must demonstrate:

```text
Monitoring
→ Detection
→ Diagnosis
→ Incident Management
→ Troubleshooting
→ Remediation
→ Resolution Verification
```

---

# Phase 0 — Repository Foundation

## Objectives

Establish the repository and engineering documentation before application implementation.

### Deliverables

- `CLAUDE.md`
- `docs/architecture.md`
- `docs/development-plan.md`
- `.gitignore`
- initial project directory structure only where justified

### Validation

- Git status is clean before changes
- documentation is internally consistent
- no application implementation yet

---

# Phase 1 — Python Monitoring Agent

## Objectives

Build the smallest useful telemetry collector.

### Implement

- agent configuration
- server/agent identity
- CPU collector
- memory collector
- swap collector
- disk collector
- uptime collector
- load average collector
- process collector
- network collector where practical

### Design

Use modular collectors rather than one giant Python file.

Conceptually:

```text
agent/
├── collectors/
│   ├── cpu.py
│   ├── memory.py
│   ├── disk.py
│   ├── network.py
│   ├── process.py
│   └── system.py
├── config.py
├── models.py
└── main.py
```

### Tests

Test collector behavior and payload structure.

### Definition of Done

The agent can produce a valid structured snapshot locally without the backend.

---

# Phase 2 — Backend Foundation

## Objectives

Create the Spring Boot application and basic health/API structure.

### Implement

- Spring Boot project
- configuration
- health endpoint
- base package structure
- global exception handling
- DTO conventions
- validation conventions

### Validation

- application starts
- health endpoint works
- automated tests pass

---

# Phase 3 — Metric Ingestion

## Objectives

Connect the Python agent to the Spring Boot API.

### Implement

```text
POST /api/metrics
GET  /api/servers
GET  /api/servers/{id}
GET  /api/servers/{id}/metrics
```

### Work

- request DTOs
- validation
- service layer
- persistence layer
- appropriate HTTP responses
- agent HTTP client

### Definition of Done

A real metric snapshot can travel:

```text
Python Agent
→ REST API
→ Backend
→ PostgreSQL
```

---

# Phase 4 — PostgreSQL Persistence

## Objectives

Create durable storage.

### Initial entities

- Server
- Metric
- Incident
- DiagnosticResult

Additional entities can be added only when required.

### Implement

- schema/migrations
- repositories
- indexes
- timestamp handling
- query methods

### Validation

- schema initializes reproducibly
- application persists metrics
- historical queries work
- tests pass

---

# Phase 5 — Diagnostic Engine

## Objectives

Turn raw telemetry into explainable diagnoses.

### Initial rules

1. High CPU
2. Memory pressure
3. Disk capacity
4. Network degradation
5. Service unavailable

### Diagnostic output

Each result should include:

- condition
- severity
- diagnosis
- evidence
- recommended action
- timestamp
- affected server

### Tests

Create focused unit tests for every rule, including:

- threshold boundary
- below threshold
- above threshold
- missing/invalid data where relevant

### Definition of Done

A known metric condition reliably produces the expected diagnosis.

---

# Phase 6 — Alert State and Redis

## Objectives

Prevent alert/incident duplication and maintain active alert state.

### Implement

- alert keys
- deduplication
- cooldown/debounce behavior
- active condition state

### Important Rule

Redis is transient state.

PostgreSQL remains the durable source of truth.

### Validation

Repeated identical metric observations should not create unlimited duplicate incidents.

---

# Phase 7 — Incident Management

## Objectives

Implement the complete incident lifecycle.

### States

```text
NEW
→ ACKNOWLEDGED
→ INVESTIGATING
→ MITIGATION_APPLIED
→ RESOLVED
→ CLOSED
```

### Implement

- incident creation
- incident retrieval
- status transitions
- technician notes
- resolution timestamp
- status history where appropriate

### Validation

- valid transitions work
- invalid transitions fail cleanly
- incident data persists

---

# Phase 8 — L1 → L2 Escalation

## Objectives

Represent realistic technical support escalation.

### Implement

- escalation action
- escalation reason
- troubleshooting history
- evidence
- recommended next step
- escalation timestamp

### Workflow

```text
L1
 ↓
Investigate
 ↓
Resolved?
 ├── YES → Resolve
 └── NO
      ↓
    Escalate
      ↓
      L2
```

### Validation

Escalated incidents retain their previous evidence and troubleshooting history.

---

# Phase 9 — Troubleshooting Playbooks

## Objectives

Connect diagnoses to actionable support guidance.

### Initial playbooks

- High CPU
- Memory pressure
- Disk capacity
- Network degradation
- Service unavailable

### Each playbook contains

- symptoms
- checks
- possible causes
- recommended actions
- escalation criteria

### Definition of Done

A diagnostic result can produce the corresponding troubleshooting playbook.

---

# Phase 10 — React Dashboard

## Objectives

Create the operator-facing interface.

### Views

```text
Dashboard
Servers
Server Detail
Alerts
Incidents
Incident Detail
Troubleshooting
```

### Dashboard requirements

Display:

- total servers
- healthy servers
- active alerts
- active incidents
- current CPU/memory/disk/network status

### Server detail

Display:

- current metrics
- recent trends
- processes
- relevant logs
- active alerts

### Incident detail

Display:

- severity
- symptom
- evidence
- diagnosis
- recommended action
- status
- notes
- escalation information

---

# Phase 11 — Fault Injection

## Objectives

Make the project demonstrable.

### Scenarios

- CPU stress
- memory stress
- controlled disk pressure
- network delay/degradation where safely possible

### Requirements

Every simulation must be:

- bounded
- reversible
- local
- explicitly triggered
- development-only

### Primary Demo

```text
Normal
 ↓
Inject CPU fault
 ↓
CPU spike
 ↓
Agent detects
 ↓
Backend receives
 ↓
Diagnostic rule fires
 ↓
Incident created
 ↓
Dashboard alert
 ↓
Troubleshooting recommendation
 ↓
Remove fault
 ↓
CPU normalizes
 ↓
Resolve incident
```

---

# Phase 12 — End-to-End Integration

## Objectives

Verify the entire system.

### Test path

```text
Fault Injection
      ↓
Agent
      ↓
API
      ↓
PostgreSQL
      ↓
Diagnostic Engine
      ↓
Redis
      ↓
Incident
      ↓
React
      ↓
Resolution
```

### Validation

At least one complete fault scenario must work from beginning to end.

---

# Phase 13 — Docker Compose

## Objectives

Make local setup reproducible.

### Containers

At minimum:

- Spring Boot backend
- PostgreSQL
- Redis
- React frontend

The agent should have a documented local execution strategy.

### Deliverables

- `docker-compose.yml`
- Dockerfiles where required
- environment example
- startup instructions
- health checks where appropriate

### Definition of Done

A fresh local environment can start the required infrastructure using documented commands.

---

# Phase 14 — Testing and Quality

## Objectives

Improve reliability before release.

### Backend

- unit tests
- controller/API tests
- service tests
- diagnostic tests
- incident transition tests
- persistence tests where useful

### Agent

- collector tests
- payload tests
- error-handling tests

### Frontend

- important component/workflow tests where practical

### Integration

Verify:

```text
Agent → API → DB
API → Diagnostics
Diagnostics → Incident
Incident → Dashboard
Fault → Detection → Resolution
```

---

# Phase 15 — Documentation

## Objectives

Make the repository understandable to another developer.

### README should eventually contain

1. Project overview
2. Features
3. Architecture
4. Tech stack
5. Repository structure
6. Prerequisites
7. Local setup
8. Docker setup
9. Agent setup
10. API overview
11. Fault-injection demo
12. Testing
13. Screenshots
14. Troubleshooting
15. Future improvements

### Additional documentation

Potential files:

```text
docs/
├── architecture.md
├── development-plan.md
├── api.md
├── troubleshooting.md
└── demo.md
```

Only create additional documents when they contain useful information.

---

# Phase 16 — GitHub Release Polish

## Objectives

Make the public repository professional.

### Check

- README quality
- architecture diagram
- screenshots
- setup instructions
- `.gitignore`
- `.env.example`
- no secrets
- no generated build output
- no personal machine paths
- meaningful commit history
- clean project structure

### Git history

Prefer meaningful feature commits.

Example:

```text
feat(agent): add system metric collectors
feat(api): add metric ingestion endpoint
feat(db): persist historical metrics
feat(diagnostics): add infrastructure rules
feat(incidents): implement lifecycle
feat(alerts): add redis alert state
feat(ui): add monitoring dashboard
feat(simulation): add controlled fault injection
test: add end-to-end detection workflow
docs: complete project documentation
```

---

# Phase 17 — Resume and Interview Audit

Do this only after the implementation is genuinely complete.

## Resume audit

Every resume bullet must correspond to implemented functionality.

Do not claim:

- HPE hardware integration
- HPE iLO integration
- ProLiant monitoring
- enterprise hardware testing

unless genuinely implemented.

Accurate positioning:

- Linux infrastructure monitoring
- server health monitoring
- infrastructure troubleshooting
- explainable diagnostic engine
- incident management
- fault injection
- automated anomaly detection
- Dockerized monitoring platform

## Interview preparation

Be prepared to explain:

### Architecture
- Why Python agent?
- Why Spring Boot?
- Why PostgreSQL?
- Why Redis?
- Why REST?
- How does the agent communicate with the backend?

### Operating Systems
- process vs thread
- CPU utilization
- memory pressure
- swap
- virtual memory
- load average
- Linux troubleshooting commands

### Networking
- TCP vs UDP
- HTTP vs HTTPS
- latency
- packet loss
- connectivity troubleshooting

### Storage
- disk utilization
- IOPS
- latency
- HDD vs SSD
- RAID concepts

### Troubleshooting
- root-cause reasoning
- multiple simultaneous alerts
- agent failure
- escalation
- remediation verification

### Backend
- REST
- validation
- caching
- indexing
- concurrency
- exception handling
- retries

---

# Final Completion Criteria

InfraPulse is ready for portfolio use only when:

- [ ] Agent collects real local metrics
- [ ] Agent communicates with backend
- [ ] Backend exposes functional APIs
- [ ] Metrics persist in PostgreSQL
- [ ] Diagnostic engine works
- [ ] Redis alert state works
- [ ] Incidents have a lifecycle
- [ ] L1 → L2 escalation works
- [ ] Troubleshooting playbooks exist
- [ ] React dashboard works
- [ ] At least one safe fault-injection scenario works
- [ ] End-to-end detection is demonstrated
- [ ] Docker setup works
- [ ] Tests pass
- [ ] README is complete
- [ ] No secrets are committed
- [ ] Resume claims match actual implementation
- [ ] The entire project can be explained technically in an interview
