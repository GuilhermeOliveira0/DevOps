package br.com.examplefatec.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import br.com.examplefatec.entity.Usuario;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String from;

    public EmailService(JavaMailSender mailSender, @Value("${app.mail.from:no-reply@fatecads.local}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public void sendPasswordResetEmail(Usuario usuario, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(usuario.getEmailUsuario());
        message.setSubject("Redefinicao de senha - FatecADS");
        message.setText("""
                Ola,

                Recebemos uma solicitacao para redefinir sua senha no FatecADS.
                Acesse o link abaixo para criar uma nova senha:

                %s

                Se voce nao solicitou essa alteracao, ignore este email.
                """.formatted(resetLink));
        sendSafely(message);
    }

    public void sendPasswordChangedEmail(Usuario usuario) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(usuario.getEmailUsuario());
        message.setSubject("Senha alterada - FatecADS");
        message.setText("""
                Ola,

                Sua senha do FatecADS foi alterada com sucesso.

                Se voce nao fez essa alteracao, solicite uma nova redefinicao imediatamente.
                """);
        sendSafely(message);
    }

    private void sendSafely(SimpleMailMessage message) {
        try {
            mailSender.send(message);
        } catch (MailException exception) {
            System.err.println("Falha ao enviar email transacional: " + exception.getMessage());
        }
    }
}
