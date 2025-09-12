package com.example.authservice.application.auth;

import com.example.authservice.application.port.TokenService;
import com.example.authservice.domain.auth.RefreshToken;
import com.example.authservice.domain.auth.RefreshTokenRepository;
import com.example.authservice.domain.auth.ExpiresAt;
import com.example.authservice.domain.auth.TokenHash;
import com.example.authservice.domain.user.User;
import com.example.authservice.infrastructure.config.JwtProperties;
import com.example.authservice.interfaces.rest.dto.auth.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenApplicationService {

    private final RefreshTokenRepository repository;
    private final TokenService tokenService;
    private final JwtProperties props;
    private final SecureRandom random = new SecureRandom();

    private String generateRawRefreshToken() {
        byte[] bytes = new byte[64]; 
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Transactional
    public String issueFor(User user) {
        String raw = generateRawRefreshToken();
        TokenHash hash = TokenHash.ofPlainText(raw);
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.getRefresTtlSeconds());
        RefreshToken token = RefreshToken.issue(user, hash, ExpiresAt.at(exp), now);
        repository.save(token);
        return raw;
    }

    @Transactional
    public TokenResponse refresh(String rawRefreshToken) {
        var hash = TokenHash.ofPlainText(rawRefreshToken).getValue();
        var tokenOpt = repository.findActiveByHash(hash);
        if (tokenOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido ou expirado");
        }
        RefreshToken stored = tokenOpt.get();
        if (!stored.isActive(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido ou expirado");
        }

        TokenService.TokenPair accessPair = tokenService.issue(stored.getUser());

        repository.revoke(stored.getId());
        String newRefresh = issueFor(stored.getUser());

        return new TokenResponse(accessPair.accessToken(), newRefresh, accessPair.expiresInSeconds());
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        var hash = TokenHash.ofPlainText(rawRefreshToken).getValue();
        var tokenOpt = repository.findActiveByHash(hash);
        if (tokenOpt.isPresent()) {
            repository.revoke(tokenOpt.get().getId());
        }
    }
}