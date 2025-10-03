package com.example.authservice.application.auth;

import com.example.authservice.application.port.EmailService;
import com.example.authservice.application.port.TokenService;
import com.example.authservice.domain.auth.MagicLink;
import com.example.authservice.domain.auth.MagicLinkRepository;
import com.example.authservice.domain.user.UserRepository;
import com.example.authservice.interfaces.rest.dto.auth.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class MagicLinkService {
    private final UserRepository userRepository;
    private final MagicLinkRepository magicLinkRepository;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final RefreshTokenApplicationService refreshTokens;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public void sendMagicLink(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        String rawToken = generateRawMagicLinkToken();
        var magicLink = new MagicLink(user, rawToken, Instant.now().plus(5, ChronoUnit.MINUTES));
        magicLinkRepository.save(magicLink);

        emailService.sendMagicLink(user.getEmail().getValue(), rawToken);
    }

    @Transactional
    public TokenResponse verifyMagicLink(String token) {
        var magicLink = magicLinkRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de acesso inválido."));

        if (magicLink.isUsed() || magicLink.isExpired()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de acesso expirado ou já utilizado.");
        }

        magicLink.markAsUsed();
        magicLinkRepository.save(magicLink);

        var user = magicLink.getUser();
        var pair = tokenService.issue(user);
        String refresh = refreshTokens.issueFor(user);

        return new TokenResponse(pair.accessToken(), refresh, pair.expiresInSeconds());
    }

    private String generateRawMagicLinkToken() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}