package com.example.authservice.application.auth;

import com.example.authservice.application.port.PasswordHasher;
import com.example.authservice.application.port.TokenService;
import com.example.authservice.domain.user.User;
import com.example.authservice.domain.user.UserRepository;
import com.example.authservice.domain.user.vo.Email;
import com.example.authservice.interfaces.rest.dto.auth.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PasswordLoginHandler {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;
    private final RefreshTokenApplicationService refreshTokens;

    public TokenResponse handle(String email, String rawPassword) {
        var userOpt = userRepository.findByEmail(new Email(email).getValue());
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais estão invalidas");
        }
        User user = userOpt.get();

        if (!passwordHasher.matches(rawPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais estão invalidas");
        }

        var pair = tokenService.issue(user);
        String refresh = refreshTokens.issueFor(user);

        return new TokenResponse(pair.accessToken(), refresh, pair.expiresInSeconds());
    }
}
