"""
Disk metrics collector for InfraPulse monitoring agent.
"""

import psutil
from agent.models import DiskMetrics


def collect_disk_metrics() -> DiskMetrics:
    """Collect disk utilization and I/O metrics using psutil."""
    disk = psutil.disk_usage("/")

    # Get disk I/O counters
    io_counters = psutil.disk_io_counters()

    return DiskMetrics(
        total_bytes=disk.total,
        used_bytes=disk.used,
        free_bytes=disk.free,
        percent_used=disk.percent,
        io_read_bytes=io_counters.read_bytes if io_counters else 0,
        io_write_bytes=io_counters.write_bytes if io_counters else 0,
        io_read_count=io_counters.read_count if io_counters else 0,
        io_write_count=io_counters.write_count if io_counters else 0,
    )