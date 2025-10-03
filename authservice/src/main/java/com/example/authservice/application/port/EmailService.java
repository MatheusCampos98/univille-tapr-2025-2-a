package com.example.authservice.application.port;

public interface EmailService {
    void sendMagicLink(String to, String token);
}