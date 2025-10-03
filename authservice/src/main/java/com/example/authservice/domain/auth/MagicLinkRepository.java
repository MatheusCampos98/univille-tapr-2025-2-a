package com.example.authservice.domain.auth;

import java.util.Optional;

public interface MagicLinkRepository {
    MagicLink save(MagicLink magicLink);
    Optional<MagicLink> findByToken(String token);
}