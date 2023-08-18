package com.b4w.b4wback.service;

public interface MailService {
    static void sendMail(String to, String subject, String text);
}
