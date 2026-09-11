"""
Memory metrics collector for InfraPulse monitoring agent.
"""

import psutil
from agent.models import MemoryMetrics


def collect_memory_metrics() -> MemoryMetrics:
    """Collect memory and swap utilization metrics using psutil."""
    mem = psutil.virtual_memory()
    swap = psutil.swap_memory()

    return MemoryMetrics(
        total_bytes=mem.total,
        available_bytes=mem.available,
        used_bytes=mem.used,
        free_bytes=mem.free,
        active_bytes=mem.active,
        inactive_bytes=mem.inactive,
        buffers_bytes=mem.buffers,
        cached_bytes=mem.cached,
        swap_total_bytes=swap.total,
        swap_used_bytes=swap.used,
        swap_free_bytes=swap.free,
        percent_used=mem.percent,
        swap_percent_used=swap.percent,
    )