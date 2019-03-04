package com.plambis.jmeter.amqp.client;

public interface MessageData {
    byte[] getBody();
    int getMessageCount();
    String getHeaders();
}
