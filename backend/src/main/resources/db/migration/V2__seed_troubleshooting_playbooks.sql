INSERT INTO troubleshooting_playbooks (
    condition_code,
    title,
    symptoms,
    checks,
    possible_causes,
    recommended_actions,
    escalation_conditions
) VALUES
(
    'HIGH_CPU',
    'High CPU Utilization',
    ARRAY['CPU usage is above 90%', 'Load average is elevated', 'Applications may respond slowly']::TEXT[],
    ARRAY['Identify top CPU-consuming processes', 'Check whether the load is sustained', 'Review recent application or batch jobs', 'Compare CPU usage with process count and load average']::TEXT[],
    ARRAY['Runaway process', 'Application overload', 'Scheduled job spike', 'Insufficient CPU capacity']::TEXT[],
    ARRAY['Inspect top processes', 'Review application logs', 'Stop or restart a process only when authorized', 'Continue monitoring after mitigation']::TEXT[],
    ARRAY['CPU remains above threshold after mitigation', 'Business service remains degraded', 'Evidence suggests capacity or infrastructure limits']::TEXT[]
),
(
    'MEMORY_PRESSURE',
    'Memory Pressure',
    ARRAY['Memory usage is above 90%', 'Swap usage is elevated', 'System may be slow or unresponsive']::TEXT[],
    ARRAY['Identify memory-consuming processes', 'Check swap activity', 'Review recent deployments or workload changes', 'Look for leak-like memory growth over time']::TEXT[],
    ARRAY['Memory leak', 'Application workload growth', 'Too many concurrent processes', 'Insufficient memory allocation']::TEXT[],
    ARRAY['Inspect process memory usage', 'Reduce nonessential workload where safe', 'Restart a leaking service only when authorized', 'Monitor memory recovery after action']::TEXT[],
    ARRAY['Swap continues increasing', 'Critical services are being killed or degraded', 'Memory does not recover after safe mitigation']::TEXT[]
),
(
    'DISK_CAPACITY',
    'Low Disk Capacity',
    ARRAY['Disk utilization is above 90%', 'Writes may fail', 'Applications may report storage errors']::TEXT[],
    ARRAY['Check filesystem usage', 'Identify large directories and files', 'Review log growth', 'Confirm temporary data locations']::TEXT[],
    ARRAY['Log accumulation', 'Temporary files not cleaned up', 'Unexpected data growth', 'Insufficient disk allocation']::TEXT[],
    ARRAY['Remove only verified temporary data', 'Rotate or archive logs according to policy', 'Expand storage if capacity is genuinely insufficient', 'Verify disk usage after cleanup']::TEXT[],
    ARRAY['Disk usage remains above threshold', 'Unknown files are consuming space', 'Application data requires owner review before cleanup']::TEXT[]
),
(
    'NETWORK_DEGRADATION',
    'Network Connectivity Degradation',
    ARRAY['Network errors or packet drops are present', 'Requests may timeout', 'Service connectivity may be intermittent']::TEXT[],
    ARRAY['Check interface error and drop counters', 'Verify gateway and DNS reachability', 'Check recent network changes', 'Compare affected services and hosts']::TEXT[],
    ARRAY['Interface or driver issue', 'Gateway or routing problem', 'DNS issue', 'Network congestion or packet loss']::TEXT[],
    ARRAY['Check interface status', 'Validate gateway connectivity', 'Collect error counter evidence', 'Escalate with affected host and timestamp details if needed']::TEXT[],
    ARRAY['Errors continue increasing', 'Multiple services or hosts are affected', 'Connectivity fails outside local host troubleshooting scope']::TEXT[]
),
(
    'SERVICE_UNAVAILABLE',
    'Service Unavailable',
    ARRAY['Service status is DOWN', 'Health checks fail', 'Dependent applications may be unavailable']::TEXT[],
    ARRAY['Check service status', 'Inspect recent service logs', 'Verify required ports and dependencies', 'Review recent configuration or deployment changes']::TEXT[],
    ARRAY['Service crash', 'Bad configuration', 'Missing dependency', 'Port conflict or resource exhaustion']::TEXT[],
    ARRAY['Inspect logs before restart', 'Restart only when authorized', 'Verify service health after action', 'Record evidence and action taken']::TEXT[],
    ARRAY['Service fails repeatedly after restart', 'Configuration root cause is unclear', 'Dependency outage is outside L1 scope']::TEXT[]
)
ON CONFLICT (condition_code) DO UPDATE SET
    title = EXCLUDED.title,
    symptoms = EXCLUDED.symptoms,
    checks = EXCLUDED.checks,
    possible_causes = EXCLUDED.possible_causes,
    recommended_actions = EXCLUDED.recommended_actions,
    escalation_conditions = EXCLUDED.escalation_conditions;
