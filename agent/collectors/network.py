"""
Network metrics collector for InfraPulse monitoring agent.
"""

import psutil
from agent.models import NetworkMetrics


def collect_network_metrics() -> NetworkMetrics:
    """Collect network throughput and error metrics using psutil."""
    net_io = psutil.net_io_counters()

    return NetworkMetrics(
        bytes_sent=net_io.bytes_sent,
        bytes_recv=net_io.bytes_recv,
        packets_sent=net_io.packets_sent,
        packets_recv=net_io.packets_recv,
        errin=net_io.errin,
        errout=net_io.errout,
        dropin=net_io.dropin,
        dropout=net_io.dropout,
    )