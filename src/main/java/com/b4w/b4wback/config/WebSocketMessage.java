package com.b4w.b4wback.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

public class WebSocketMessage implements Message {

    @Override
    public Object getPayload() {
        return "Bad Response";
    }

    @Override
    public MessageHeaders getHeaders() {
        return null;
    }
}
