"""
Configuration for the InfraPulse monitoring agent.

All configuration values are read from environment variables
or defaults defined here.
"""

import os
from dataclasses import dataclass, field
from typing import List


@dataclass
class AgentConfig:
    """Agent configuration loaded from environment variables."""
    # Agent identity
    agent_id: str = field(default_factory=lambda: os.environ.get("INFRAPULSE_AGENT_ID", "agent-localhost"))
    agent_version: str = field(default_factory=lambda: os.environ.get("INFRAPULSE_AGENT_VERSION", "0.1.0"))
    hostname: str = field(default_factory=lambda: os.environ.get("INFRAPULSE_HOSTNAME", os.environ.get("HOSTNAME", "localhost")))

    # Backend API
    api_base_url: str = field(default_factory=lambda: os.environ.get("INFRAPULSE_API_URL", "http://localhost:8080/api"))
    metrics_endpoint: str = field(default_factory=lambda: os.environ.get("INFRAPULSE_API_METRICS", "/api/metrics"))

    # Collection settings
    collection_interval_seconds: int = field(default_factory=lambda: int(os.environ.get("INFRAPULSE_INTERVAL", "30")))
    max_retries: int = field(default_factory=lambda: int(os.environ.get("INFRAPULSE_MAX_RETRIES", "3")))
    retry_delay_seconds: int = field(default_factory=lambda: int(os.environ.get("INFRAPULSE_RETRY_DELAY", "5")))

    # Collectors toggle
    collect_cpu: bool = field(default_factory=lambda: os.environ.get("INFRAPULSE_COLLECT_CPU", "true").lower() == "true")
    collect_memory: bool = field(default_factory=lambda: os.environ.get("INFRAPULSE_COLLECT_MEMORY", "true").lower() == "true")
    collect_disk: bool = field(default_factory=lambda: os.environ.get("INFRAPULSE_COLLECT_DISK", "true").lower() == "true")
    collect_network: bool = field(default_factory=lambda: os.environ.get("INFRAPULSE_COLLECT_NETWORK", "true").lower() == "true")
    collect_processes: bool = field(default_factory=lambda: os.environ.get("INFRAPULSE_COLLECT_PROCESSES", "true").lower() == "true")
    collect_system: bool = field(default_factory=lambda: os.environ.get("INFRAPULSE_COLLECT_SYSTEM", "true").lower() == "true")

    # Delivery toggle (default off: collect-and-print locally)
    send_metrics: bool = field(default_factory=lambda: os.environ.get("INFRAPULSE_SEND_METRICS", "false").lower() == "true")

    # Process collector settings
    max_processes: int = field(default_factory=lambda: int(os.environ.get("INFRAPULSE_MAX_PROCESSES", "50")))

    # Timeout settings
    request_timeout_seconds: int = field(default_factory=lambda: int(os.environ.get("INFRAPULSE_TIMEOUT", "10")))

    @classmethod
    def from_env(cls) -> "AgentConfig":
        """Load configuration from environment variables."""
        return cls()

    def to_dict(self) -> dict:
        """Convert config to dictionary."""
        return {
            "agent_id": self.agent_id,
            "agent_version": self.agent_version,
            "hostname": self.hostname,
            "api_base_url": self.api_base_url,
            "collection_interval_seconds": self.collection_interval_seconds,
            "collectors": {
                "cpu": self.collect_cpu,
                "memory": self.collect_memory,
                "disk": self.collect_disk,
                "network": self.collect_network,
                "processes": self.collect_processes,
                "system": self.collect_system,
            },
        }