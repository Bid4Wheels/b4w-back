package com.b4w.b4wback.service;

import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Bid;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.service.interfaces.MailService;
import com.b4w.b4wback.util.MailFormat;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Validated
public class MailServiceImp implements MailService {

    @Value("${sendMail.Boolean.Value}")
    private boolean sendMail;
    private final JavaMailSender mailSender;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public MailServiceImp(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendMail(String to, String subject, String text) {
        if (!sendMail) return;
        executorService.execute(()->{
            try {
                sendMailNotThread(to, subject, text);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void endOfAuctionMails(Auction auction, Bid highestBid, String[] losers){
        if (!sendMail) return;
        executorService.execute(()-> {
            String subject = "End of auction: " + auction.getTitle();
            try {
                endOfAuctionMailOwner(subject, auction, highestBid);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            if (highestBid == null) return;
            try {
                endOfAuctionSendMailWinner(subject, auction, highestBid);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            try {
                endOfAuctionSendMailLosers(subject, auction, losers);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private void endOfAuctionMailOwner(String subject, Auction auction, Bid highestBid) throws MessagingException {
        String message;
        if (highestBid == null){
            message = String.format("""
                Hello from b4w,
                The auction has finished without exit, no customer has performed a bid in the auction: %s.
                We wish you better luck in your next auctions""", auction.getTitle());
        }else {
            User bidder = highestBid.getBidder();
            message = String.format("""
                Hello from b4w,
                The auction has finished with bid of the amount: %s, performed by one of our customers.
                The name of the winner is the following: %s.
                You can contact the customer bia mail sending the message to the following direction: %s .
                """,
                    highestBid.getAmount(), bidder.getName() + " " + bidder.getLastName() ,bidder.getEmail());
        }
        sendMailNotThread(auction.getUser().getEmail(), subject,
                MailFormat.createWithDefaultHtml("End of auction: " + auction.getTitle(),
                        message));
    }

    private void endOfAuctionSendMailWinner(String subject, Auction auction, Bid highestBid) throws MessagingException {
        User owner = auction.getUser();
        String text=String.format("""
                    Hello from b4w,
                    The auction has finished with exit, we like to inform you that you won the auction by the amount
                    of %s. The owner of the auction was: %s. If you would like to contact him you can send him a email
                    to the following direction: %s .
                    Have a nice day.
                    """, highestBid.getAmount(), owner.getName() + " " + owner.getLastName(), owner.getEmail());
        sendMailNotThread(highestBid.getBidder().getEmail(),
                subject, MailFormat.createWithDefaultHtml("You are the winner: " + auction.getTitle(), text));
    }

    private void endOfAuctionSendMailLosers(String subject, Auction auction, String[] losers) throws MessagingException {
        if (losers.length < 1) return;
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(losers);
        helper.setSubject(subject);
        ClassPathResource classPathResource=  new ClassPathResource("/logo/B4W_logo.png");
        helper.addInline("img1", classPathResource);
        String text=String.format("""
            Hello from b4w,
            The auction: %s has finished, but unluckily you didn't won.
            We wish you better luck next time.
            Have a nice day.""", auction.getTitle());
        helper.setText(MailFormat.createWithDefaultHtml("You are not the winner: " + auction.getTitle(), text), true);

        mailSender.send(message);
    }

    private void sendMailNotThread(String to, String subject, String text) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        ClassPathResource classPathResource=  new ClassPathResource("/logo/B4W_logo.png");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        helper.addInline("img1", classPathResource);
        mailSender.send(message);
    }

    @Override
    public void sendQuestionMail(String to, String subject, String title, String text) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("Question about your auction" + title);
        String str=String.format("""
            Hello from b4w,
            You have received a question about your auction: %s.
            The question is the following: %s.
            Have a nice day.""", title, text);
        helper.setText(MailFormat.createWithDefaultHtml("Question about your auction: " + title, str), true);
        ClassPathResource classPathResource=  new ClassPathResource("/logo/B4W_logo.png");
        helper.addInline("img1", classPathResource);
        mailSender.send(message);
    }


}
