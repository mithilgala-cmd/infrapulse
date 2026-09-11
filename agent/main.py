"""
InfraPulse Monitoring Agent - Main Entry Point.

Collects system metrics and sends them to the backend API.

The agent can also produce a structured snapshot locally
without the backend (for testing and debugging).
"""

import json
import sys
import time
import logging
from datetime import datetime

from agent.config import AgentConfig
from agent.collectors import (
    collect_cpu_metrics,
    collect_memory_metrics,
    collect_disk_metrics,
    collect_network_metrics,
    collect_process_metrics,
    collect_system_metrics,
)
from agent.models import MetricSnapshot, ServerIdentity

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S",
)
logger = logging.getLogger("infrapulse-agent")


def collect_snapshot(config: AgentConfig = None) -> MetricSnapshot:
    """
    Collect all enabled metrics and return a snapshot.

    Args:
        config: Agent configuration. If None, loads from environment.

    Returns:
        A complete MetricSnapshot containing all collected metrics.
    """
    if config is None:
        config = AgentConfig.from_env()

    logger.info("Collecting metrics for server: %s", config.hostname)

    # Collect metrics based on configuration
    cpu = None
    memory = None
    disk = None
    network = None
    processes = None
    system = None

    if config.collect_cpu:
        logger.info("Collecting CPU metrics...")
        cpu = collect_cpu_metrics()
    else:
        logger.info("CPU collector disabled by configuration")

    if config.collect_memory:
        logger.info("Collecting memory metrics...")
        memory = collect_memory_metrics()
    else:
        logger.info("Memory collector disabled by configuration")

    if config.collect_disk:
        logger.info("Collecting disk metrics...")
        disk = collect_disk_metrics()
    else:
        logger.info("Disk collector disabled by configuration")

    if config.collect_network:
        logger.info("Collecting network metrics...")
        network = collect_network_metrics()
    else:
        logger.info("Network collector disabled by configuration")

    if config.collect_processes:
        logger.info("Collecting process metrics...")
        processes = collect_process_metrics(config)
    else:
        logger.info("Process collector disabled by configuration")

    if config.collect_system:
        logger.info("Collecting system metrics...")
        system = collect_system_metrics()
    else:
        logger.info("System collector disabled by configuration")

    # Build server identity
    server = ServerIdentity(
        hostname=config.hostname,
        agent_version=config.agent_version,
        agent_id=config.agent_id,
    )

    # Build snapshot
    snapshot = MetricSnapshot(
        server=server,
        timestamp=datetime.utcnow(),
        cpu=cpu,
        memory=memory,
        disk=disk,
        network=network,
        processes=processes,
        system=system,
    )

    logger.info("Metric collection complete")
    return snapshot


def print_snapshot(snapshot: MetricSnapshot) -> None:
    """Print the snapshot as formatted JSON for local inspection."""
    print(json.dumps(snapshot.to_dict(), indent=2, default=str))


def main() -> None:
    """Main entry point for the agent."""
    config = AgentConfig.from_env()
    logger.info("InfraPulse Agent v%s starting", config.agent_version)
    logger.info("Agent ID: %s", config.agent_id)
    logger.info("Target API: %s", config.api_base_url)
    logger.info("Collection interval: %ds", config.collection_interval_seconds)

    # Collect and print snapshot locally
    snapshot = collect_snapshot(config)
    print_snapshot(snapshot)


if __name__ == "__main__":
    main()