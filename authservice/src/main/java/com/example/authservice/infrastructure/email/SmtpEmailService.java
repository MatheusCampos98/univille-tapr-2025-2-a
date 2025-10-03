package com.example.authservice.infrastructure.email;

import com.example.authservice.application.port.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendMagicLink(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Seu Link Mágico de Acesso");
        message.setText("Olá! Use o link a seguir para fazer login de forma segura:\n\n"
                + "http://localhost:8080/auth/login/magic/verify?token=" + token
                + "\n\nEste link é válido por 5 minutos. Se você não solicitou este acesso, pode ignorar este e-mail.");
        mailSender.send(message);
    }
}