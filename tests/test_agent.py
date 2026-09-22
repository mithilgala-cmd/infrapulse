"""
Baseline tests for the InfraPulse monitoring agent.

Covers collector output structure, agent configuration,
and metric snapshot serialization using only the standard library.
"""

import json
import os
import unittest

from agent.collectors import (
    collect_cpu_metrics,
    collect_disk_metrics,
    collect_memory_metrics,
    collect_network_metrics,
    collect_process_metrics,
    collect_system_metrics,
)
from agent.config import AgentConfig
from agent.main import collect_snapshot
from agent.models import MetricSnapshot


class TestCpuCollector(unittest.TestCase):
    def test_returns_sane_percentages(self):
        cpu = collect_cpu_metrics()
        for value in (
            cpu.percent_total,
            cpu.percent_user,
            cpu.percent_system,
            cpu.percent_idle,
            cpu.percent_iowait,
        ):
            self.assertGreaterEqual(value, 0.0)
            self.assertLessEqual(value, 100.0)

    def test_reports_cpu_topology(self):
        cpu = collect_cpu_metrics()
        self.assertGreaterEqual(cpu.num_cpu_cores, 1)
        self.assertGreaterEqual(cpu.num_logical_cpus, cpu.num_cpu_cores)
        for value in (
            cpu.load_average_1m,
            cpu.load_average_5m,
            cpu.load_average_15m,
        ):
            self.assertGreaterEqual(value, 0.0)


class TestMemoryCollector(unittest.TestCase):
    def test_reports_positive_totals(self):
        memory = collect_memory_metrics()
        self.assertGreater(memory.total_bytes, 0)
        self.assertGreaterEqual(memory.available_bytes, 0)
        self.assertGreaterEqual(memory.free_bytes, 0)
        self.assertGreaterEqual(memory.percent_used, 0.0)
        self.assertLessEqual(memory.percent_used, 100.0)

    def test_swap_fields_present(self):
        memory = collect_memory_metrics()
        self.assertGreaterEqual(memory.swap_total_bytes, 0)
        self.assertGreaterEqual(memory.swap_used_bytes, 0)
        self.assertGreaterEqual(memory.swap_percent_used, 0.0)


class TestDiskCollector(unittest.TestCase):
    def test_reports_usage(self):
        disk = collect_disk_metrics()
        self.assertGreater(disk.total_bytes, 0)
        self.assertGreaterEqual(disk.used_bytes, 0)
        self.assertGreaterEqual(disk.free_bytes, 0)
        self.assertGreaterEqual(disk.percent_used, 0.0)
        self.assertLessEqual(disk.percent_used, 100.0)

    def test_io_counters_non_negative(self):
        disk = collect_disk_metrics()
        for value in (
            disk.io_read_bytes,
            disk.io_write_bytes,
            disk.io_read_count,
            disk.io_write_count,
        ):
            self.assertGreaterEqual(value, 0)


class TestNetworkCollector(unittest.TestCase):
    def test_counters_non_negative(self):
        network = collect_network_metrics()
        for value in (
            network.bytes_sent,
            network.bytes_recv,
            network.packets_sent,
            network.packets_recv,
            network.errin,
            network.errout,
            network.dropin,
            network.dropout,
        ):
            self.assertGreaterEqual(value, 0)


class TestProcessCollector(unittest.TestCase):
    def test_respects_max_processes(self):
        config = AgentConfig(max_processes=5)
        metrics = collect_process_metrics(config)
        self.assertLessEqual(len(metrics.processes), 5)

    def test_process_entries_have_identity(self):
        config = AgentConfig(max_processes=10)
        metrics = collect_process_metrics(config)
        self.assertGreater(len(metrics.processes), 0)
        # PID 0 is the Windows System Idle Process; real processes have pid > 0.
        identified = [p for p in metrics.processes if p.pid > 0 and p.name]
        self.assertGreater(len(identified), 0)
        for proc in metrics.processes:
            self.assertGreaterEqual(proc.pid, 0)
            self.assertGreaterEqual(proc.cpu_percent, 0.0)
            self.assertGreaterEqual(proc.memory_percent, 0.0)


class TestSystemCollector(unittest.TestCase):
    def test_uptime_positive(self):
        system = collect_system_metrics()
        self.assertGreater(system.uptime_seconds, 0)
        self.assertGreater(system.boot_time, 0)


class TestAgentConfig(unittest.TestCase):
    def test_defaults(self):
        config = AgentConfig.from_env()
        self.assertTrue(config.hostname)
        self.assertTrue(config.agent_id)
        self.assertTrue(config.collect_cpu)
        self.assertEqual(config.collection_interval_seconds, 30)

    def test_env_override(self):
        os.environ["INFRAPULSE_HOSTNAME"] = "test-host"
        try:
            config = AgentConfig.from_env()
            self.assertEqual(config.hostname, "test-host")
        finally:
            del os.environ["INFRAPULSE_HOSTNAME"]


class TestMetricSnapshot(unittest.TestCase):
    def test_snapshot_serializes_to_json(self):
        config = AgentConfig(max_processes=5)
        snapshot = collect_snapshot(config)
        self.assertIsInstance(snapshot, MetricSnapshot)
        payload = snapshot.to_dict()
        for section in ("server", "timestamp", "cpu", "memory", "disk", "network"):
            self.assertIn(section, payload)
        # Must survive a JSON round trip (backend receives JSON).
        parsed = json.loads(json.dumps(payload, default=str))
        self.assertEqual(parsed["server"]["hostname"], config.hostname)
        self.assertIn("percent_total", parsed["cpu"])
        self.assertIn("total_bytes", parsed["memory"])


if __name__ == "__main__":
    unittest.main()
