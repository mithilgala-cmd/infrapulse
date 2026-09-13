package com.infrapulse.service;

import com.infrapulse.dto.ServerResponse;
import com.infrapulse.model.Server;
import com.infrapulse.repository.ServerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ServerService {

    private final ServerRepository serverRepository;

    public ServerService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    public List<ServerResponse> getAllServers() {
        return serverRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ServerResponse getServerById(Long id) {
        return toResponse(findByIdOrThrow(id));
    }

    public ServerResponse getServerByHostname(String hostname) {
        Server server = serverRepository.findByHostname(hostname)
                .orElseThrow(() -> new EntityNotFoundException("Server not found: " + hostname));
        return toResponse(server);
    }

    private Server findByIdOrThrow(Long id) {
        return serverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Server not found with id: " + id));
    }

    private ServerResponse toResponse(Server server) {
        return ServerResponse.builder()
                .id(server.getId())
                .hostname(server.getHostname())
                .agentId(server.getAgentId())
                .agentVersion(server.getAgentVersion())
                .ipAddresses(server.getIpAddresses())
                .createdAt(server.getCreatedAt())
                .updatedAt(server.getUpdatedAt())
                .build();
    }
}