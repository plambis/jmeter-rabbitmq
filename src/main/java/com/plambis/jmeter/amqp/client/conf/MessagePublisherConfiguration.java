package com.plambis.jmeter.amqp.client.conf;

import java.util.Map;

public interface MessagePublisherConfiguration {
    boolean isPersistent();

    String getContentType();

    String getCorrelationId();

    String getReplyToQueue();

    String getMessageType();

    String getMessageId();

    Map<String, String> getHeaders();

}
