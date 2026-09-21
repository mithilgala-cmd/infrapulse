package com.infrapulse.repository;

import com.infrapulse.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long> {

    Optional<Server> findByHostname(String hostname);

    boolean existsByHostname(String hostname);
}