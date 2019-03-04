package com.plambis.jmeter.amqp.client.conf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MessagePublisherConfigurationImpl  implements  MessagePublisherConfiguration{

    private final String contentType;

    private final String correlationId;

    private final String replyToQueue;

    private final String messageType;

    private final String messageId;

    private final Map<String, String> headers = new HashMap<>();

    private final boolean persistent;

    public MessagePublisherConfigurationImpl(String contentType, String correlationId, String replyToQueue, String messageType, String messageId, boolean persistent) {
        this.contentType = contentType;
        this.correlationId = correlationId;
        this.replyToQueue = replyToQueue;
        this.messageType = messageType;
        this.messageId = messageId;
        this.persistent = persistent;
    }

    public String getContentType() {
        return contentType;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getReplyToQueue() {
        return replyToQueue;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getMessageId() {
        return messageId;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void addHeaders(Map<String, String> msgHeaders){
        this.headers.putAll(msgHeaders);
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}
