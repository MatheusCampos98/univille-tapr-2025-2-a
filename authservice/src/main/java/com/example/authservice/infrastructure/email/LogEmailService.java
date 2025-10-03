package com.example.authservice.infrastructure.email;

import com.example.authservice.application.port.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!prod")
public class LogEmailService implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(LogEmailService.class);

    @Override
    public void sendMagicLink(String to, String token) {
        logger.info("==================== MODO DESENVOLVIMENTO ====================");
        logger.info(">> Enviando link mágico para: {}", to);
        logger.info(">> Link: http://localhost:8080/auth/login/magic/verify?token={}", token);
        logger.info("==============================================================");
    }
}