"""
Process collector for InfraPulse monitoring agent.
"""

import psutil
from agent.models import ProcessInfo, ProcessMetrics
from agent.config import AgentConfig


def collect_process_metrics(config: AgentConfig = None) -> ProcessMetrics:
    """Collect running process information using psutil."""
    if config is None:
        config = AgentConfig.from_env()

    max_procs = config.max_processes
    processes: list[ProcessInfo] = []

    for proc in psutil.process_iter(['pid', 'name', 'username', 'cpu_percent',
                                      'memory_percent', 'memory_info',
                                      'num_threads', 'create_time']):
        try:
            info = proc.info
            if len(processes) >= max_procs:
                break
            # num_fds is Unix-only in psutil; resolve defensively per process.
            try:
                num_fds = proc.num_fds()
            except Exception:
                num_fds = 0
            processes.append(ProcessInfo(
                pid=info['pid'],
                name=info['name'],
                username=info['username'] or 'unknown',
                cpu_percent=info['cpu_percent'] or 0.0,
                memory_percent=info['memory_percent'] or 0.0,
                memory_rss_mb=(info['memory_info'].rss / 1024 / 1024) if info['memory_info'] else 0.0,
                memory_vms_mb=(info['memory_info'].vms / 1024 / 1024) if info['memory_info'] else 0.0,
                num_threads=info['num_threads'] or 0,
                num_fds=num_fds or 0,
                create_time=info['create_time'] or 0.0,
            ))
        except (psutil.NoSuchProcess, psutil.AccessDenied, psutil.ZombieProcess):
            continue

    return ProcessMetrics(processes=processes)