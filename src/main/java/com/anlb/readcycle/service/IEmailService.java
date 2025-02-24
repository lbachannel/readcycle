package com.anlb.readcycle.service;

import com.anlb.readcycle.domain.User;

public interface IEmailService {
    void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml);
    void sendEmailFromTemplateSync(User user, String subject, String templateName);
}
