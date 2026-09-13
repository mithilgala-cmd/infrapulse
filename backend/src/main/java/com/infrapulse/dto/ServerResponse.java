package com.infrapulse.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServerResponse {

    private Long id;
    private String hostname;
    private String agentId;
    private String agentVersion;
    private List<String> ipAddresses;
    private Instant createdAt;
    private Instant updatedAt;
}