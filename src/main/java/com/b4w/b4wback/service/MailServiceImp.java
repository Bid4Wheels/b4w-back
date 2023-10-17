package com.b4w.b4wback.service;

import com.b4w.b4wback.model.Auction;
import com.b4w.b4wback.model.Bid;
import com.b4w.b4wback.model.User;
import com.b4w.b4wback.service.interfaces.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
            sendMailNotThread(to, subject, text);
        });

    }

    public void endOfAuctionMails(Auction auction, Bid highestBid, String[] losers){
        if (!sendMail) return;
        executorService.execute(()-> {
            String subject = "End of auction: " + auction.getTitle();
            endOfAuctionMailOwner(subject, auction, highestBid);
            if (highestBid == null) return;
            endOfAuctionSendMailWinner(subject, auction, highestBid);
            endOfAuctionSendMailLosers(subject, auction, losers);
        });
    }


    private void endOfAuctionMailOwner(String subject, Auction auction, Bid highestBid){
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
        sendMailNotThread(auction.getUser().getEmail(), subject, message);
    }

    private void endOfAuctionSendMailWinner(String subject, Auction auction, Bid highestBid){
        User owner = auction.getUser();
        sendMailNotThread(highestBid.getBidder().getEmail(),
                subject,
                String.format("""
                        Hello from b4w,
                        The auction has finished with exit, we like to inform you that you won the auction by the amount
                        of %s. The owner of the auction was: %s. If you would like to contact him you can send him a email
                        to the following direction: %s .
                        Have a nice day.
                        """, highestBid.getAmount(), owner.getName() + " " + owner.getLastName(), owner.getEmail()));
    }

    private void endOfAuctionSendMailLosers(String subject, Auction auction, String[] losers){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(losers);
        message.setSubject(subject);

        message.setText(String.format("""
                Hello from b4w,
                The auction: %s has finished, but unluckily you didn't won.
                We wish you better luck next time.
                Have a nice day.""", auction.getTitle()));

        mailSender.send(message);
    }

    private void sendMailNotThread(String to, String subject, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    @Override
    public void sendQuestionMail(String to, String subject, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Question about your auction" + title);
        message.setText("The user " + subject + " has made a question about your auction: " + title + "\n" + text);
        mailSender.send(message);
    }
}
