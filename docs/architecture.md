# InfraPulse Architecture

## 1. Overview

InfraPulse is an enterprise-style infrastructure monitoring and troubleshooting platform designed to demonstrate the complete operational flow from system telemetry to diagnosis and incident resolution.

The high-level architecture is:

```text
┌──────────────────────────────┐
│      Linux Development       │
│          System(s)           │
│                              │
│ CPU / RAM / Disk / Network   │
│ Processes / Services / Logs  │
└──────────────┬───────────────┘
               │
               │ Metrics
               ▼
┌──────────────────────────────┐
│       Python Monitoring      │
│            Agent             │
│                              │
│            psutil            │
└──────────────┬───────────────┘
               │
               │ HTTP REST
               ▼
┌──────────────────────────────┐
│       Spring Boot API        │
│            Java              │
│                              │
│ Controllers                  │
│ Services                     │
│ Diagnostics                  │
│ Incident Management          │
└────────────┬───────┬─────────┘
             │       │
             │       │
             ▼       ▼
┌────────────────┐  ┌────────────────┐
│   PostgreSQL   │  │     Redis      │
│                │  │                │
│ Historical     │  │ Alert /        │
│ Metrics        │  │ transient state│
│ Incidents      │  │ Deduplication  │
└────────────────┘  └────────────────┘
             │
             ▼
┌──────────────────────────────┐
│        React Dashboard       │
│                              │
│ Servers / Metrics / Alerts   │
│ Incidents / Diagnostics      │
│ Troubleshooting / Escalation │
└──────────────────────────────┘
```

---

## 2. Architectural Principles

### Separation of Concerns

Each major responsibility should remain isolated:

- Python agent: telemetry collection
- Spring Boot: API, orchestration, business logic
- PostgreSQL: durable persistence
- Redis: short-lived operational state
- Diagnostic engine: explainable detection and diagnosis
- React: presentation and operator interaction

### Explainability

Infrastructure diagnosis should be traceable.

A diagnostic result should be explainable through:

```text
Observed evidence
       ↓
Rule evaluated
       ↓
Condition matched
       ↓
Probable cause
       ↓
Recommended action
```

### Incremental Development

The system should be developed as independently testable components before end-to-end integration.

### Safe Simulation

Fault injection is a development/testing mechanism. It must be bounded and isolated from destructive host operations.

---

## 3. Monitoring Agent

### Responsibility

The Python agent collects local system telemetry and sends it to the backend.

### Initial collectors

- CPU
- Memory
- Swap
- Disk
- Disk I/O
- Network
- Processes
- Uptime
- Load average
- Service state where available
- Relevant recent logs where safely accessible

### Agent flow

```text
Start
 ↓
Load configuration
 ↓
Identify agent/server
 ↓
Collect metrics
 ↓
Validate payload
 ↓
POST metrics to API
 ↓
Handle response/failure
 ↓
Wait for collection interval
 ↓
Repeat
```

### Reliability

The agent should tolerate:

- temporary API outage
- network failure
- malformed response
- individual collector failure

One failed collector should not unnecessarily terminate the entire agent.

---

## 4. Backend Architecture

Use conventional Spring Boot layering:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

Supporting components:

```text
Controller
   ↓
Service
   ├── Diagnostic Engine
   ├── Incident Service
   ├── Alert Service
   └── Playbook Service
```

DTOs should be used where appropriate.

---

## 5. Metric Ingestion

The agent sends structured telemetry to the backend.

Conceptually:

```text
POST /api/metrics
```

The payload should identify:

- server
- timestamp
- CPU
- memory
- swap
- disk
- network
- process information
- other supported measurements

The backend validates and persists appropriate information.

---

## 6. PostgreSQL Responsibilities

PostgreSQL is the durable source of truth for historical application data.

Conceptual entities:

```text
Server
Metric
Incident
IncidentStatusHistory
DiagnosticResult
TroubleshootingPlaybook
Escalation
```

Relationships should be designed around actual application queries rather than creating unnecessary normalization.

Historical metrics should be indexed appropriately for:

- server lookup
- timestamp ranges
- recent observations

---

## 7. Redis Responsibilities

Redis should store short-lived state.

Examples:

- active alert state
- alert deduplication
- cooldown state
- recent evaluation state

Redis should not replace PostgreSQL for durable historical data.

---

## 8. Diagnostic Engine

The first version uses deterministic rules.

Example:

```text
Metric
  ↓
Rule evaluation
  ↓
Condition matched?
  ├── No → continue
  └── Yes
        ↓
Severity
        ↓
Diagnosis
        ↓
Evidence
        ↓
Recommended action
```

Initial rules:

| Condition | Diagnosis | Example Action |
|---|---|---|
| Sustained CPU > 90% | High CPU utilization | Inspect top processes |
| Memory > 90% + high swap | Memory pressure | Inspect memory consumers |
| Disk > 90% | Low disk capacity | Identify large files |
| High latency/packet loss | Network degradation | Check interface/gateway |
| Service DOWN | Service unavailable | Inspect logs/service state |

The rule engine should avoid generating duplicate incidents for the same continuing condition.

---

## 9. Incident Architecture

Incident state machine:

```text
NEW
 │
 ▼
ACKNOWLEDGED
 │
 ▼
INVESTIGATING
 │
 ▼
MITIGATION_APPLIED
 │
 ▼
RESOLVED
 │
 ▼
CLOSED
```

Escalation can branch from an active investigation:

```text
INVESTIGATING
      │
      ▼
   Escalate
      │
      ▼
     L2
```

The system should preserve the history of important state changes.

---

## 10. Troubleshooting Playbooks

A diagnostic result should connect to an appropriate playbook.

Example:

```text
High CPU
   ↓
Symptoms
   ↓
Checks
   ↓
Possible causes
   ↓
Recommended actions
   ↓
Escalation criteria
```

Playbooks should provide operational guidance rather than pretending to automatically fix every infrastructure issue.

---

## 11. Dashboard Architecture

The React application consumes backend APIs.

Primary views:

```text
Dashboard
├── Server Overview
├── Server Detail
├── Alerts
├── Incidents
│   └── Incident Detail
├── Diagnostics
└── Troubleshooting
```

The dashboard should prioritize:

- current state
- trend visibility
- active problems
- diagnostic evidence
- recommended next action
- incident ownership/status

---

## 12. Fault Injection Architecture

Fault injection is deliberately separated from normal monitoring.

Conceptually:

```text
Dashboard / Development API
          ↓
   Simulation Controller
          ↓
   Safe Fault Mechanism
          ↓
       Local Host
          ↓
      Metric Change
          ↓
     Agent Detection
```

Supported scenarios should be bounded and reversible.

Examples:

- CPU stress with fixed duration
- memory allocation with strict limits
- temporary disk pressure in a controlled project directory
- simulated network degradation where practical

Never use destructive host-wide operations.

---

## 13. End-to-End Scenario

The primary demonstration scenario should be:

```text
Normal server
      ↓
Inject controlled CPU fault
      ↓
CPU rises above threshold
      ↓
Agent collects metric
      ↓
Backend receives metric
      ↓
Diagnostic rule evaluates condition
      ↓
Diagnostic result created
      ↓
Incident generated
      ↓
Redis tracks active alert state
      ↓
Dashboard displays HIGH incident
      ↓
Operator reviews evidence
      ↓
Troubleshooting playbook displayed
      ↓
Fault removed
      ↓
CPU returns to normal
      ↓
Incident resolved
      ↓
Incident closed
```

This scenario is the primary proof that the architecture works as an integrated system.

---

## 14. Deployment Architecture

Docker Compose should eventually orchestrate:

```text
┌───────────────────┐
│ React Frontend    │
└─────────┬─────────┘
          │
┌─────────▼─────────┐
│ Spring Boot API   │
└──────┬───────┬────┘
       │       │
┌──────▼───┐ ┌─▼──────┐
│PostgreSQL│ │ Redis  │
└──────────┘ └────────┘

Python agent runs against the development environment
and communicates with the API.
```

The exact container boundaries can evolve during implementation.

---

## 15. Security Considerations

Even though InfraPulse is primarily a local portfolio project:

- credentials must use environment variables
- secrets must not be committed
- API inputs must be validated
- fault-injection endpoints must be development-safe
- arbitrary shell execution must not be exposed
- error responses must not leak sensitive configuration
- production deployment should not be implied

---

## 16. Technology Boundaries

### Python

Responsible for:

- host telemetry
- local system inspection
- agent scheduling
- metric payload generation
- safe local simulation helpers where appropriate

### Java/Spring Boot

Responsible for:

- API
- persistence orchestration
- business logic
- diagnostic rules
- incidents
- alerts
- playbooks
- escalation

### PostgreSQL

Responsible for:

- durable data
- historical metrics
- incident history

### Redis

Responsible for:

- transient alert/state management
- deduplication/cooldown

### React

Responsible for:

- visualization
- operator workflows
- incident interaction

### Docker

Responsible for:

- reproducible local infrastructure

---

## 17. Architecture Evolution

The initial architecture is intentionally simple enough for a student project but structured enough to discuss in technical interviews.

Future enhancements may include:

- authentication
- role-based access
- WebSocket/live updates
- more diagnostic rules
- richer log correlation
- distributed agents
- metrics aggregation
- observability for InfraPulse itself

These should not be implemented until the core workflow is stable.
