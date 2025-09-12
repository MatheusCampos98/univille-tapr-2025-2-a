package com.example.authservice.interfaces.rest;

import com.example.authservice.application.auth.PasswordLoginHandler;
import com.example.authservice.application.auth.RefreshTokenApplicationService;
import com.example.authservice.interfaces.rest.dto.auth.LogoutRequest;
import com.example.authservice.interfaces.rest.dto.auth.PasswordLoginRequest;
import com.example.authservice.interfaces.rest.dto.auth.RefreshRequest;
import com.example.authservice.interfaces.rest.dto.auth.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth")
public class AuthController {
    private final PasswordLoginHandler passwordLoginHandler;
    private final RefreshTokenApplicationService refreshService;

    @PostMapping("/login/password")
    @Operation(summary = "Login com email/senha", description = "Retorna access token e refresh token")
    public ResponseEntity<TokenResponse> loginWithPassword(@Valid @RequestBody PasswordLoginRequest request) {
        TokenResponse response = passwordLoginHandler.handle(request.email(), request.password());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh de token", description = "Gera novo access token e rotate do refresh token")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest body) {
        return ResponseEntity.ok(refreshService.refresh(body.refreshToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Revoga o refresh token atual")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest body) {
        refreshService.logout(body.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
