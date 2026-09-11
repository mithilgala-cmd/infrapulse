"""
Data models for InfraPulse monitoring agent.

Defines the structure for metric snapshots that will be sent to the backend.
"""

from dataclasses import dataclass, field
from datetime import datetime
from typing import List, Dict, Any, Optional


@dataclass
class ServerIdentity:
    """Identity information for the monitored server."""
    hostname: str
    ip_addresses: List[str] = field(default_factory=list)
    agent_version: str = "0.1.0"
    agent_id: str = ""


@dataclass
class CpuMetrics:
    """CPU utilization metrics."""
    percent_total: float
    percent_user: float
    percent_system: float
    percent_idle: float
    percent_iowait: float
    load_average_1m: float
    load_average_5m: float
    load_average_15m: float
    num_cpu_cores: int
    num_logical_cpus: int


@dataclass
class MemoryMetrics:
    """Memory utilization metrics."""
    total_bytes: float
    available_bytes: float
    used_bytes: float
    free_bytes: float
    active_bytes: float
    inactive_bytes: float
    buffers_bytes: float
    cached_bytes: float
    swap_total_bytes: float
    swap_used_bytes: float
    swap_free_bytes: float
    percent_used: float
    swap_percent_used: float


@dataclass
class DiskMetrics:
    """Disk utilization metrics."""
    total_bytes: float
    used_bytes: float
    free_bytes: float
    percent_used: float
    io_read_bytes: int
    io_write_bytes: int
    io_read_count: int
    io_write_count: int


@dataclass
class NetworkMetrics:
    """Network throughput and error metrics."""
    bytes_sent: int
    bytes_recv: int
    packets_sent: int
    packets_recv: int
    errin: int
    errout: int
    dropin: int
    dropout: int


@dataclass
class ProcessInfo:
    """Information about a running process."""
    pid: int
    name: str
    username: str
    cpu_percent: float
    memory_percent: float
    memory_rss_mb: float
    memory_vms_mb: float
    num_threads: int
    num_fds: int
    create_time: float


@dataclass
class ProcessMetrics:
    """Process list metrics."""
    processes: List[ProcessInfo] = field(default_factory=list)


@dataclass
class SystemMetrics:
    """System-level metrics."""
    uptime_seconds: float
    boot_time: float


@dataclass
class MetricSnapshot:
    """
    Complete metric snapshot collected by the agent.

    This is the primary payload sent to the backend API.
    """
    server: ServerIdentity
    timestamp: datetime
    cpu: CpuMetrics
    memory: MemoryMetrics
    disk: DiskMetrics
    network: NetworkMetrics
    processes: Optional[ProcessMetrics] = None
    system: Optional[SystemMetrics] = None
    raw_data: Dict[str, Any] = field(default_factory=dict)

    def to_dict(self) -> Dict[str, Any]:
        """Convert the snapshot to a dictionary for JSON serialization."""
        return {
            "server": {
                "hostname": self.server.hostname,
                "ip_addresses": self.server.ip_addresses,
                "agent_version": self.server.agent_version,
                "agent_id": self.server.agent_id,
            },
            "timestamp": self.timestamp.isoformat(),
            "cpu": {
                "percent_total": self.cpu.percent_total,
                "percent_user": self.cpu.percent_user,
                "percent_system": self.cpu.percent_system,
                "percent_idle": self.cpu.percent_idle,
                "percent_iowait": self.cpu.percent_iowait,
                "load_average_1m": self.cpu.load_average_1m,
                "load_average_5m": self.cpu.load_average_5m,
                "load_average_15m": self.cpu.load_average_15m,
                "num_cpu_cores": self.cpu.num_cpu_cores,
                "num_logical_cpus": self.cpu.num_logical_cpus,
            },
            "memory": {
                "total_bytes": self.memory.total_bytes,
                "available_bytes": self.memory.available_bytes,
                "used_bytes": self.memory.used_bytes,
                "free_bytes": self.memory.free_bytes,
                "active_bytes": self.memory.active_bytes,
                "inactive_bytes": self.memory.inactive_bytes,
                "buffers_bytes": self.memory.buffers_bytes,
                "cached_bytes": self.memory.cached_bytes,
                "swap_total_bytes": self.memory.swap_total_bytes,
                "swap_used_bytes": self.memory.swap_used_bytes,
                "swap_free_bytes": self.memory.swap_free_bytes,
                "percent_used": self.memory.percent_used,
                "swap_percent_used": self.memory.swap_percent_used,
            },
            "disk": {
                "total_bytes": self.disk.total_bytes,
                "used_bytes": self.disk.used_bytes,
                "free_bytes": self.disk.free_bytes,
                "percent_used": self.disk.percent_used,
                "io_read_bytes": self.disk.io_read_bytes,
                "io_write_bytes": self.disk.io_write_bytes,
                "io_read_count": self.disk.io_read_count,
                "io_write_count": self.disk.io_write_count,
            },
            "network": {
                "bytes_sent": self.network.bytes_sent,
                "bytes_recv": self.network.bytes_recv,
                "packets_sent": self.network.packets_sent,
                "packets_recv": self.network.packets_recv,
                "errin": self.network.errin,
                "errout": self.network.errout,
                "dropin": self.network.dropin,
                "dropout": self.network.dropout,
            },
        }