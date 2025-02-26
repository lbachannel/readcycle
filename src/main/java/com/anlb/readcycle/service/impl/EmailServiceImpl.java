package com.anlb.readcycle.service.impl;

import java.nio.charset.StandardCharsets;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.anlb.readcycle.domain.User;
import com.anlb.readcycle.service.IEmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    /**
     * Sends an email synchronously using the configured JavaMailSender.
     *
     * @param to         The recipient's email address.
     * @param subject    The subject of the email.
     * @param content    The content/body of the email.
     * @param isMultipart Indicates whether the email supports multipart content.
     * @param isHtml     Indicates whether the email content is in HTML format.
     */
    @Override
    public void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            log.error("ERROR SEND EMAIL: {}", e);
        }
    }

    /**
     * Asynchronously sends an email to the specified user using a predefined email template.
     *
     * @param user         The recipient user, whose email address and verification token are used.
     * @param subject      The subject of the email.
     * @param templateName The name of the email template to be processed.
     */
    @Async
    @Override
    public void sendEmailFromTemplateSync(User user, String subject, String templateName) {
        Context context = new Context();
        context.setVariable("password", user.getPassword());
        context.setVariable("email", user.getEmail());
        String verifyUrl = "http://localhost:8080/api/v1/auth/verify-email?token=" + user.getVerificationEmailToken();
        context.setVariable("verifyEmailToken", verifyUrl);
        String content = this.templateEngine.process(templateName, context);
        sendEmailSync(user.getEmail(), subject, content, false, true);
    }
}
