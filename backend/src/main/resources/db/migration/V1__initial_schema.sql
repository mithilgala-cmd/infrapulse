CREATE TABLE IF NOT EXISTS servers (
    id BIGSERIAL PRIMARY KEY,
    hostname VARCHAR(255) NOT NULL UNIQUE,
    agent_id VARCHAR(255) NOT NULL,
    agent_version VARCHAR(50) NOT NULL,
    ip_addresses TEXT[] NOT NULL DEFAULT '{}',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS metric_observations (
    id BIGSERIAL PRIMARY KEY,
    server_id BIGINT NOT NULL REFERENCES servers(id) ON DELETE CASCADE,
    timestamp TIMESTAMP NOT NULL,
    cpu_percent_total DOUBLE PRECISION NOT NULL,
    cpu_percent_user DOUBLE PRECISION NOT NULL,
    cpu_percent_system DOUBLE PRECISION NOT NULL,
    cpu_percent_idle DOUBLE PRECISION NOT NULL,
    cpu_percent_iowait DOUBLE PRECISION NOT NULL,
    load_average_1m DOUBLE PRECISION NOT NULL,
    load_average_5m DOUBLE PRECISION NOT NULL,
    load_average_15m DOUBLE PRECISION NOT NULL,
    num_cpu_cores INTEGER NOT NULL,
    num_logical_cpus INTEGER NOT NULL,
    memory_total_bytes BIGINT NOT NULL,
    memory_available_bytes BIGINT NOT NULL,
    memory_used_bytes BIGINT NOT NULL,
    memory_free_bytes BIGINT NOT NULL,
    memory_active_bytes BIGINT NOT NULL,
    memory_inactive_bytes BIGINT NOT NULL,
    memory_buffers_bytes BIGINT NOT NULL,
    memory_cached_bytes BIGINT NOT NULL,
    swap_total_bytes BIGINT NOT NULL,
    swap_used_bytes BIGINT NOT NULL,
    swap_free_bytes BIGINT NOT NULL,
    swap_percent_used DOUBLE PRECISION NOT NULL,
    disk_total_bytes BIGINT NOT NULL,
    disk_used_bytes BIGINT NOT NULL,
    disk_free_bytes BIGINT NOT NULL,
    disk_percent_used DOUBLE PRECISION NOT NULL,
    disk_io_read_bytes BIGINT NOT NULL,
    disk_io_write_bytes BIGINT NOT NULL,
    disk_io_read_count BIGINT NOT NULL,
    disk_io_write_count BIGINT NOT NULL,
    network_bytes_sent BIGINT NOT NULL,
    network_bytes_recv BIGINT NOT NULL,
    network_packets_sent BIGINT NOT NULL,
    network_packets_recv BIGINT NOT NULL,
    network_errin BIGINT NOT NULL,
    network_errout BIGINT NOT NULL,
    network_dropin BIGINT NOT NULL,
    network_dropout BIGINT NOT NULL,
    process_count INTEGER NOT NULL DEFAULT 0,
    uptime_seconds DOUBLE PRECISION NOT NULL,
    boot_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS incidents (
    id BIGSERIAL PRIMARY KEY,
    server_id BIGINT NOT NULL REFERENCES servers(id) ON DELETE CASCADE,
    incident_number VARCHAR(20) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    severity VARCHAR(50) NOT NULL,
    symptom TEXT NOT NULL,
    detected_metric VARCHAR(255) NOT NULL,
    probable_cause TEXT NOT NULL,
    evidence TEXT NOT NULL,
    recommended_action TEXT NOT NULL,
    technician_notes TEXT,
    status_history TEXT[] NOT NULL DEFAULT '{}',
    resolution_time TIMESTAMP,
    escalation_reason TEXT,
    troubleshooting_history TEXT[] NOT NULL DEFAULT '{}',
    evidence_log TEXT[] NOT NULL DEFAULT '{}',
    recommended_next_step TEXT,
    escalation_timestamp TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS incident_status_history (
    id BIGSERIAL PRIMARY KEY,
    incident_id BIGINT NOT NULL REFERENCES incidents(id) ON DELETE CASCADE,
    from_status VARCHAR(50),
    to_status VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    technician_notes TEXT
);

CREATE TABLE IF NOT EXISTS diagnostic_results (
    id BIGSERIAL PRIMARY KEY,
    server_id BIGINT NOT NULL REFERENCES servers(id) ON DELETE CASCADE,
    metric_observation_id BIGINT REFERENCES metric_observations(id) ON DELETE SET NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    condition_code VARCHAR(50) NOT NULL,
    severity VARCHAR(50) NOT NULL,
    diagnosis TEXT NOT NULL,
    evidence TEXT NOT NULL,
    recommended_action TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS troubleshooting_playbooks (
    id BIGSERIAL PRIMARY KEY,
    condition_code VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    symptoms TEXT[] NOT NULL,
    checks TEXT[] NOT NULL,
    possible_causes TEXT[] NOT NULL,
    recommended_actions TEXT[] NOT NULL,
    escalation_conditions TEXT[] NOT NULL
);

CREATE TABLE IF NOT EXISTS escalations (
    id BIGSERIAL PRIMARY KEY,
    incident_id BIGINT NOT NULL REFERENCES incidents(id) ON DELETE CASCADE,
    reason TEXT NOT NULL,
    troubleshooting_performed TEXT[] NOT NULL DEFAULT '{}',
    evidence TEXT[] NOT NULL DEFAULT '{}',
    recommended_next_step TEXT NOT NULL,
    escalation_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    escalated_to VARCHAR(50) NOT NULL DEFAULT 'L2'
);

CREATE TABLE IF NOT EXISTS alert_state (
    id BIGSERIAL PRIMARY KEY,
    condition_code VARCHAR(50) NOT NULL,
    server_id BIGINT NOT NULL REFERENCES servers(id) ON DELETE CASCADE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    last_seen TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cooldown_until TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_metric_observations_server_timestamp
    ON metric_observations(server_id, timestamp);
CREATE INDEX IF NOT EXISTS idx_metric_observations_server_created_at
    ON metric_observations(server_id, created_at);
CREATE INDEX IF NOT EXISTS idx_incidents_server_id
    ON incidents(server_id);
CREATE INDEX IF NOT EXISTS idx_incidents_status
    ON incidents(status);
CREATE INDEX IF NOT EXISTS idx_diagnostic_results_server_timestamp
    ON diagnostic_results(server_id, timestamp);
CREATE INDEX IF NOT EXISTS idx_diagnostic_results_condition
    ON diagnostic_results(condition_code);
CREATE INDEX IF NOT EXISTS idx_alert_state_server_condition
    ON alert_state(server_id, condition_code);
CREATE INDEX IF NOT EXISTS idx_escalations_incident_id
    ON escalations(incident_id);