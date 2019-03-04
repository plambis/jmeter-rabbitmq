package com.plambis.jmeter.amqp.client.conf;

import java.util.List;

public interface ChannelConfiguration {
    String getVirtualHost();

    List<String> getHosts();

    int getPort();

    String getUsername();

    String getPassword();

    int getTimeout();

    boolean useSslProtocol();

    QueueConfiguration getQueueConfiguration();

    ExchangeConfiguration getExchangeConfiguration();

    String getRoutingKey();

    boolean useTx();
}
