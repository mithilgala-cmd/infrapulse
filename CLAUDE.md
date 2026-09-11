# InfraPulse — Claude Code Project Instructions

## Project Identity

**Project:** InfraPulse — Enterprise Infrastructure Monitoring & Troubleshooting Platform

InfraPulse is a locally runnable infrastructure monitoring and troubleshooting platform designed to demonstrate practical understanding of Linux systems, networking, backend development, databases, monitoring, diagnostics, incident management, and containerization.

The project must be genuinely implemented and testable. Do not create fake functionality, placeholder implementations presented as complete features, or resume-oriented mock behavior.

---

## Core Objective

The central workflow is:

```text
Infrastructure/System
        ↓
Monitoring
        ↓
Metric Collection
        ↓
Anomaly Detection
        ↓
Diagnosis
        ↓
Incident Creation
        ↓
Troubleshooting
        ↓
Remediation
        ↓
Resolution Verification
```

The project should demonstrate:

**metric → symptom → diagnosis → probable root cause → remediation → verification**

This is more important than simply producing a visually attractive monitoring dashboard.

---

## Target Stack

### Monitoring Agent
- Python
- psutil

### Backend
- Java
- Spring Boot
- REST APIs

### Database
- PostgreSQL

### Alert / State Management
- Redis

### Frontend
- React

### Infrastructure
- Linux
- Docker
- Docker Compose

### Version Control
- Git
- GitHub

Do not introduce additional major technologies without first explaining the reason and getting approval.

---

## Functional Requirements

### 1. Monitoring Agent

Collect, where practical:

- CPU utilization
- Memory utilization
- Swap usage
- Disk utilization
- Disk I/O
- Network throughput
- Network errors
- Running processes
- System uptime
- Load average
- Service status
- Relevant recent system logs

The agent should send structured metric data to the backend through the defined API.

The agent must handle temporary backend/network failures gracefully.

---

### 2. Spring Boot Backend

Provide REST APIs for:

- receiving metrics
- listing servers
- retrieving server details
- retrieving historical metrics
- alerts
- incidents
- diagnostics
- troubleshooting recommendations

Use clear controller/service/repository boundaries.

Use DTOs where appropriate rather than exposing persistence models directly.

Implement appropriate validation and error handling.

---

### 3. PostgreSQL

PostgreSQL stores persistent application data and historical metrics.

The schema should support at minimum:

- servers
- metric observations
- incidents
- incident status history where useful
- diagnostic results
- troubleshooting/playbook information where appropriate

Database design should be documented before becoming unnecessarily complex.

---

### 4. Redis

Redis is used for low-latency state such as:

- active alert state
- deduplication state
- cooldown/debounce information
- other short-lived monitoring state where appropriate

Redis must not become the source of truth for historical data.

---

## Diagnostic Engine

The diagnostic engine must initially use deterministic, explainable rules.

Do not introduce ML simply to make the project appear more advanced.

Initial rules:

### High CPU

```text
IF CPU > 90%
AND condition is sustained
THEN
    severity = HIGH
    diagnosis = High CPU utilization
    evidence = CPU threshold + process information
    action = Inspect top CPU-consuming processes
```

### Memory Pressure

```text
IF memory > 90%
AND swap usage exceeds threshold
THEN
    diagnosis = Memory pressure
    action = Inspect memory-consuming processes
```

### Disk Capacity

```text
IF disk utilization > 90%
THEN
    diagnosis = Low disk capacity
    action = Identify large files and temporary data
```

### Network Degradation

```text
IF packet loss > threshold
OR latency > threshold
THEN
    diagnosis = Network connectivity degradation
    action = Check interface, gateway and connectivity
```

### Service Failure

```text
IF service status = DOWN
THEN
    diagnosis = Service unavailable
    action = Inspect service logs and service status
```

Rules must be configurable where practical.

A diagnostic result should contain enough evidence to explain why the rule fired.

---

## Incident Management

Implement the lifecycle:

```text
NEW
 ↓
ACKNOWLEDGED
 ↓
INVESTIGATING
 ↓
MITIGATION_APPLIED
 ↓
RESOLVED
 ↓
CLOSED
```

Incidents should contain appropriate information such as:

- incident ID
- server
- timestamp
- severity
- symptom
- detected metric
- probable cause
- evidence
- recommended action
- technician notes
- status
- resolution time

Prevent invalid status transitions.

---

## L1 → L2 Escalation

Support escalation when L1 troubleshooting cannot resolve an incident.

Capture:

- escalation reason
- troubleshooting already performed
- relevant logs/metrics/evidence
- recommended next step
- escalation timestamp

The system should preserve the incident history.

---

## Fault Injection

Provide controlled development-only simulations for:

- CPU stress
- memory stress
- disk pressure
- network delay/degradation where safely possible

The purpose is to demonstrate:

```text
FAULT
 ↓
DETECTION
 ↓
DIAGNOSIS
 ↓
INCIDENT
 ↓
TROUBLESHOOTING
 ↓
REMEDIATION
 ↓
RESOLUTION
```

### Safety Requirements

Fault injection must:

- only operate in the intended local development environment
- be explicitly triggered
- have bounded resource usage
- have a clear stop/cleanup mechanism
- never delete user files
- never corrupt the host
- never modify unrelated system configuration
- never contain uncontrolled resource exhaustion
- never execute arbitrary user-provided shell commands

Do not provide destructive production-style remediation commands.

---

## Troubleshooting Playbooks

Create structured playbooks for major conditions.

Each playbook should include:

1. Symptoms
2. Checks
3. Possible causes
4. Recommended actions
5. Escalation conditions

Example: High CPU

```text
Symptoms:
- CPU > 90%
- elevated load average

Checks:
1. Identify top processes
2. Check application logs
3. Check process duration
4. Determine whether utilization is sustained

Possible causes:
- runaway process
- application overload
- insufficient CPU resources

Recommended actions:
- inspect process
- review logs
- restart service only when authorized
- continue monitoring

Escalate when:
- application/process remains unhealthy
- remediation is unsuccessful
- evidence suggests an underlying infrastructure issue
```

---

## React Dashboard

The dashboard should eventually provide:

- server overview
- server health
- CPU / memory / disk / network metrics
- active alerts
- active incidents
- incident details
- diagnosis
- evidence
- troubleshooting recommendations
- incident status transitions
- escalation information

Prioritize clarity and operational usefulness over visual complexity.

---

## Linux Troubleshooting

The agent/application may use safe read-only Linux information sources such as:

```text
top
ps
df
du
free
uptime
systemctl
journalctl
ping
ip
ss
```

Only use commands that are appropriate for the development environment.

Do not assume enterprise HPE hardware is available.

Do not claim HPE hardware, firmware, iLO, ProLiant, storage arrays, or other proprietary infrastructure was actually tested unless it is genuinely tested.

---

## Engineering Standards

### General

- Build incrementally.
- Keep modules separated.
- Use clear names.
- Prefer simple, explainable solutions.
- Avoid unnecessary abstraction.
- Avoid premature optimization.
- Do not implement unrelated features.
- Do not silently change architectural decisions.

### Configuration

- Never hardcode credentials.
- Use environment variables.
- Do not commit `.env` files containing secrets.
- Provide `.env.example` when needed.

### APIs

- Validate input.
- Return meaningful HTTP status codes.
- Use consistent response/error structures.
- Handle unavailable dependencies gracefully.
- Document important endpoints.

### Database

- Use migrations or another reproducible schema mechanism.
- Avoid destructive schema changes during normal development.
- Add indexes based on actual query requirements.

### Testing

Important functionality must have tests.

At minimum, progressively cover:

- monitoring collectors
- diagnostic rules
- incident transitions
- API behavior
- persistence
- fault-injection safety/behavior
- important frontend behavior

Tests must be real and executable.

### Git

Use meaningful commits.

Prefer commits such as:

```text
feat(agent): add system metric collectors
feat(api): add metric ingestion endpoint
feat(diagnostics): add CPU saturation rule
feat(incidents): implement incident lifecycle
feat(ui): add server health dashboard
test(diagnostics): add diagnostic rule coverage
docs: add architecture documentation
```

Do not commit generated secrets, local databases, IDE metadata, build output, or unnecessary binaries.

---

## Development Workflow

For every milestone:

1. Inspect the current repository.
2. Read relevant project documentation.
3. Explain the intended change briefly.
4. Implement the smallest coherent increment.
5. Run appropriate tests.
6. Run formatting/linting/build validation where available.
7. Inspect the resulting changes.
8. Report what changed and what was verified.
9. Stop before expanding into unrelated work.

Do not rebuild working components unnecessarily.

Before modifying an existing architectural component, understand how it currently works.

---

## Current Development Order

Follow this broad sequence:

1. Repository foundation and project documentation
2. Monitoring agent foundation
3. Metric ingestion API
4. PostgreSQL persistence
5. Server/metric retrieval APIs
6. Diagnostic engine
7. Alert state with Redis
8. Incident management
9. L1 → L2 escalation
10. Troubleshooting playbooks
11. React dashboard
12. Fault injection
13. End-to-end detection workflow
14. Docker Compose
15. Automated testing and quality improvements
16. Documentation and GitHub polish

Do not jump directly to the final architecture without completing and validating the underlying increments.

---

## Definition of Done

A feature is not considered complete merely because source code exists.

A feature should be considered complete only when:

- implementation exists
- relevant tests exist where appropriate
- tests/build/validation pass
- error handling is considered
- documentation is updated when necessary
- the feature can be demonstrated locally
- no unsupported claims are made

---

## Public GitHub Standard

This repository is intended to be public.

Never commit:

- passwords
- API keys
- tokens
- private credentials
- `.env` secrets
- personal machine-specific paths
- unnecessary personal information

The README must eventually allow another developer to clone the repository and understand how to run it.

---

## Important Honesty Constraint

InfraPulse is an infrastructure simulation/monitoring project.

Never represent it as:

- an HPE server monitoring system
- a system tested on HPE ProLiant hardware
- an HPE iLO integration
- enterprise HPE hardware diagnostics

unless those things are genuinely implemented and tested.

Use accurate terminology such as:

- Linux infrastructure monitoring
- server health monitoring
- infrastructure troubleshooting
- simulated infrastructure failures
- fault injection
- explainable diagnostic rules

---

## Current Repository State

The repository is being initialized from a clean GitHub repository.

Do not assume application components already exist.

Before implementation, inspect the repository and local environment.

When in doubt, preserve existing working code and ask for approval before making a large architectural change.
