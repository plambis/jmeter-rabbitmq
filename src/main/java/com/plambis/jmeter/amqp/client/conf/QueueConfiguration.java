package com.plambis.jmeter.amqp.client.conf;

import java.util.Map;

public interface QueueConfiguration {
    String getQueueName();

    boolean isDurable();

    boolean isExclusive();

    boolean isAutoDelete();

    Map<String, Object> getQueueArguments();

    boolean redeclare();
}
