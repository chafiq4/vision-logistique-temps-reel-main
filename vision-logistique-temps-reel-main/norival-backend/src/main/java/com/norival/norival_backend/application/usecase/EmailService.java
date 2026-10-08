package com.norival.norival_backend.application.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendCredentialsEmail(String toEmail, String nomComplet, String password) {
        try {
            logger.info("Envoi d'e-mail de création de compte à : {}", toEmail);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("smartmind0504@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Vos identifiants de connexion Norival Logistique");
            message.setText("Bonjour " + nomComplet + ",\n\n" +
                    "Votre compte chauffeur a été créé avec succès par l'administrateur.\n\n" +
                    "Voici vos identifiants pour vous connecter à l'application :\n" +
                    "- E-mail : " + toEmail + "\n" +
                    "- Mot de passe : " + password + "\n\n" +
                    "Cordialement,\n" +
                    "L'équipe Norival Logistique.");
            
            mailSender.send(message);
            logger.info("E-mail de création de compte envoyé avec succès !");
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'e-mail de création de compte à {}: {}", toEmail, e.getMessage());
        }
    }
}
