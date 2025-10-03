package com.example.authservice.infrastructure.persistence;

import com.example.authservice.domain.auth.MagicLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataMagicLinkJpa extends JpaRepository<MagicLink, UUID> {
    Optional<MagicLink> findByToken(String token);
}