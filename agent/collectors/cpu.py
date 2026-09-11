"""
CPU metrics collector for InfraPulse monitoring agent.
"""

import os
import psutil
from agent.models import CpuMetrics


def collect_cpu_metrics() -> CpuMetrics:
    """Collect CPU utilization metrics using psutil."""
    # Get per-CPU percentages
    cpu_percent_per_core = psutil.cpu_percent(percpu=True, interval=1)
    # Get overall percentages
    cpu_percent = psutil.cpu_percent(interval=1)
    # Get detailed CPU times
    cpu_times = psutil.cpu_times_percent(interval=1)
    # Load averages (Unix/Linux only, returns None on Windows)
    try:
        load_avg = os.getloadavg()
    except (AttributeError, OSError):
        load_avg = (0.0, 0.0, 0.0)

    return CpuMetrics(
        percent_total=cpu_percent,
        percent_user=cpu_times.user,
        percent_system=cpu_times.system,
        percent_idle=cpu_times.idle,
        percent_iowait=cpu_times.iowait if hasattr(cpu_times, 'iowait') else 0.0,
        load_average_1m=load_avg[0],
        load_average_5m=load_avg[1],
        load_average_15m=load_avg[2],
        num_cpu_cores=psutil.cpu_count(logical=False) or 1,
        num_logical_cpus=psutil.cpu_count(logical=True) or 1,
    )