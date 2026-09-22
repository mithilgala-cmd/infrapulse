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
        # active/inactive/buffers/cached are Unix-only in psutil;
        # fall back to 0 where the platform does not expose them.
        active_bytes=getattr(mem, "active", 0) or 0,
        inactive_bytes=getattr(mem, "inactive", 0) or 0,
        buffers_bytes=getattr(mem, "buffers", 0) or 0,
        cached_bytes=getattr(mem, "cached", 0) or 0,
        swap_total_bytes=swap.total,
        swap_used_bytes=swap.used,
        swap_free_bytes=swap.free,
        percent_used=mem.percent,
        swap_percent_used=swap.percent,
    )