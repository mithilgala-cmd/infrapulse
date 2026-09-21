package com.infrapulse.controller;

import com.infrapulse.dto.ServerResponse;
import com.infrapulse.service.ServerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servers")
public class ServerController {

    private final ServerService serverService;

    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    @GetMapping
    public ResponseEntity<List<ServerResponse>> getAllServers() {
        return ResponseEntity.ok(serverService.getAllServers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServerResponse> getServerById(@PathVariable Long id) {
        return ResponseEntity.ok(serverService.getServerById(id));
    }

    @GetMapping("/{hostname}")
    public ResponseEntity<ServerResponse> getServerByHostname(@PathVariable String hostname) {
        return ResponseEntity.ok(serverService.getServerByHostname(hostname));
    }
}