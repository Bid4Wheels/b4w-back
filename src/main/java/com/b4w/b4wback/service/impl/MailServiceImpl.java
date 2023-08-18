package com.b4w.b4wback.service.impl;

import com.b4w.b4wback.service.MailService;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class MailServiceImpl implements MailService {

        private MailSender mailSender;
        private SimpleMailMessage templateMessage;

        public void setMailSender(MailSender mailSender) {
            this.mailSender = mailSender;
        }

        public void setTemplateMessage(SimpleMailMessage templateMessage) {
            this.templateMessage = templateMessage;
        }

    @Override
    public void sendMail(String to, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        this.mailSender.send(msg);

    }
}


