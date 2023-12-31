package com.b4w.b4wback.service.interfaces;

import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Bid;
import jakarta.mail.MessagingException;

public interface MailService {
   void sendMail(String to, String subject, String text);
   void sendQuestionMail(String to, String subject, String title, String text) throws MessagingException;
   void endOfAuctionMails(Auction auction, Bid highestBid, String[] losers);

}
