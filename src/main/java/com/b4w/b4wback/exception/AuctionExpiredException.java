package com.b4w.b4wback.exception;

public class AuctionExpiredException extends RuntimeException{
    public AuctionExpiredException(String message){
        super(message);
    }
}
