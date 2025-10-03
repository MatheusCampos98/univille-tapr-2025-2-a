package com.example.authservice.infrastructure.persistence;

import com.example.authservice.domain.auth.MagicLink;
import com.example.authservice.domain.auth.MagicLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaMagicLinkRepository implements MagicLinkRepository {

    private final SpringDataMagicLinkJpa jpa;

    @Override
    public MagicLink save(MagicLink magicLink) {
        return jpa.save(magicLink);
    }

    @Override
    public Optional<MagicLink> findByToken(String token) {
        return jpa.findByToken(token);
    }
}