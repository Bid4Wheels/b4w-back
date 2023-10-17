package com.b4w.b4wback.service.interfaces;

public interface MailService {
   void sendMail(String to, String subject, String text);
   void sendQuestionMail(String to, String subject, String title, String text);
}
