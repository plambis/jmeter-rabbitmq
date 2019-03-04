package com.plambis.jmeter.amqp.client.conf;

import java.util.Map;

public interface ExchangeConfiguration {
    String getExchangeName();

    String getType();

    boolean isDurable();

    boolean isAutoDelete();

    Map<String, Object> getExchangeArguments();

    boolean redeclare();
}
