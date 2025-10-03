package com.example.authservice.interfaces.rest;

import com.example.authservice.application.auth.MagicLinkService;
import com.example.authservice.application.auth.PasswordLoginHandler;
import com.example.authservice.application.auth.RefreshTokenApplicationService;
import com.example.authservice.interfaces.rest.dto.auth.LogoutRequest;
import com.example.authservice.interfaces.rest.dto.auth.MagicLoginRequest;
import com.example.authservice.interfaces.rest.dto.auth.PasswordLoginRequest;
import com.example.authservice.interfaces.rest.dto.auth.RefreshRequest;
import com.example.authservice.interfaces.rest.dto.auth.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth")
public class AuthController {
    private final PasswordLoginHandler passwordLoginHandler;
    private final RefreshTokenApplicationService refreshService;
    private final MagicLinkService magicLinkService;

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

    @PostMapping("/login/magic")
    @Operation(summary = "Envia link mágico para login por email", description = "Inicia o processo de login sem senha enviando um link para o e-mail do usuário.")
    public ResponseEntity<Void> sendMagicLink(@Valid @RequestBody MagicLoginRequest body) {
        magicLinkService.sendMagicLink(body.email());
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/login/magic/verify")
    @Operation(summary = "Verifica o token do link mágico e efetua o login", description = "Valida o token recebido por e-mail e retorna os tokens de acesso e refresh.")
    public ResponseEntity<TokenResponse> verifyMagicLink(@RequestParam String token) {
        return ResponseEntity.ok(magicLinkService.verifyMagicLink(token));
    }
}