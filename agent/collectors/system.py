"""
System metrics collector for InfraPulse monitoring agent.
"""

import psutil
import time
from agent.models import SystemMetrics


def collect_system_metrics() -> SystemMetrics:
    """Collect system-level metrics including uptime and boot time."""
    # System uptime
    boot_time = psutil.boot_time()
    uptime_seconds = time.time() - boot_time

    return SystemMetrics(
        uptime_seconds=uptime_seconds,
        boot_time=boot_time,
    )