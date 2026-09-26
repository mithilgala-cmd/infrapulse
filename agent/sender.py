"""
HTTP sender for the InfraPulse monitoring agent.

Converts a collected MetricSnapshot into the backend ingestion contract
(POST /api/metrics) and delivers it using only the Python standard library.
"""

import json
import urllib.error
import urllib.request
from datetime import datetime, timezone
from typing import Any, Dict, Tuple

from agent.config import AgentConfig
from agent.models import MetricSnapshot


def _instant(value: datetime) -> str:
    """Format a datetime as ISO-8601 UTC for the backend Instant fields."""
    if value.tzinfo is None:
        value = value.replace(tzinfo=timezone.utc)
    return value.astimezone(timezone.utc).isoformat()


def _instant_from_epoch(value: float) -> str:
    """Convert epoch seconds (psutil) to ISO-8601 UTC."""
    return datetime.fromtimestamp(value, tz=timezone.utc).isoformat()


def build_ingestion_payload(snapshot: MetricSnapshot) -> Dict[str, Any]:
    """
    Build the POST /api/metrics JSON body from a snapshot.

    Field names match MetricIngestionRequest (camelCase); integral values
    are cast to int because the backend declares them as Long/Instant.
    """
    cpu = snapshot.cpu
    memory = snapshot.memory
    disk = snapshot.disk
    network = snapshot.network

    payload: Dict[str, Any] = {
        "hostname": snapshot.server.hostname,
        "ipAddresses": list(snapshot.server.ip_addresses),
        "timestamp": _instant(snapshot.timestamp),
        "cpu": {
            "percentTotal": cpu.percent_total,
            "percentUser": cpu.percent_user,
            "percentSystem": cpu.percent_system,
            "percentIdle": cpu.percent_idle,
            "percentIowait": cpu.percent_iowait,
            "loadAverage1m": cpu.load_average_1m,
            "loadAverage5m": cpu.load_average_5m,
            "loadAverage15m": cpu.load_average_15m,
            "numCpuCores": int(cpu.num_cpu_cores),
            "numLogicalCpus": int(cpu.num_logical_cpus),
        },
        "memory": {
            "totalBytes": int(memory.total_bytes),
            "availableBytes": int(memory.available_bytes),
            "usedBytes": int(memory.used_bytes),
            "freeBytes": int(memory.free_bytes),
            "activeBytes": int(memory.active_bytes),
            "inactiveBytes": int(memory.inactive_bytes),
            "buffersBytes": int(memory.buffers_bytes),
            "cachedBytes": int(memory.cached_bytes),
            "swapTotalBytes": int(memory.swap_total_bytes),
            "swapUsedBytes": int(memory.swap_used_bytes),
            "swapFreeBytes": int(memory.swap_free_bytes),
            "swapPercentUsed": memory.swap_percent_used,
        },
        "disk": {
            "totalBytes": int(disk.total_bytes),
            "usedBytes": int(disk.used_bytes),
            "freeBytes": int(disk.free_bytes),
            "percentUsed": disk.percent_used,
            "ioReadBytes": int(disk.io_read_bytes),
            "ioWriteBytes": int(disk.io_write_bytes),
            "ioReadCount": int(disk.io_read_count),
            "ioWriteCount": int(disk.io_write_count),
        },
        "network": {
            "bytesSent": int(network.bytes_sent),
            "bytesRecv": int(network.bytes_recv),
            "packetsSent": int(network.packets_sent),
            "packetsRecv": int(network.packets_recv),
            "errin": int(network.errin),
            "errout": int(network.errout),
            "dropin": int(network.dropin),
            "dropout": int(network.dropout),
        },
    }

    if snapshot.processes is not None:
        payload["process"] = {"processCount": len(snapshot.processes.processes)}

    if snapshot.system is not None:
        payload["system"] = {
            "uptimeSeconds": snapshot.system.uptime_seconds,
            "bootTime": _instant_from_epoch(snapshot.system.boot_time),
        }

    return payload


def resolve_metrics_url(config: AgentConfig) -> str:
    """Join base URL and metrics endpoint without doubling slashes."""
    return config.api_base_url.rstrip("/") + "/" + config.metrics_endpoint.lstrip("/")


def post_snapshot(
    snapshot: MetricSnapshot, config: AgentConfig = None
) -> Tuple[int, str]:
    """
    POST a snapshot to the backend ingestion endpoint.

    Returns (http_status, response_body). Raises RuntimeError with a clear
    message when the backend cannot be reached or rejects the payload.
    """
    if config is None:
        config = AgentConfig.from_env()

    url = resolve_metrics_url(config)
    body = json.dumps(build_ingestion_payload(snapshot)).encode("utf-8")
    request = urllib.request.Request(
        url,
        data=body,
        headers={"Content-Type": "application/json"},
        method="POST",
    )
    try:
        with urllib.request.urlopen(request, timeout=config.request_timeout_seconds) as response:
            return response.status, response.read().decode("utf-8")
    except urllib.error.HTTPError as exc:
        detail = exc.read().decode("utf-8", errors="replace")
        raise RuntimeError(f"Backend rejected metrics: HTTP {exc.code}: {detail}") from exc
    except (urllib.error.URLError, TimeoutError, OSError) as exc:
        raise RuntimeError(f"Cannot reach backend at {url}: {exc}") from exc
