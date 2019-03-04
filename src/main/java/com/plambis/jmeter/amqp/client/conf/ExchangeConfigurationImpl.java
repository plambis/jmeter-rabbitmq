package com.plambis.jmeter.amqp.client.conf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author plambis
 */
public class ExchangeConfigurationImpl implements ExchangeConfiguration {
    private final String exchangeName;

    private final String type;

    private final boolean durable;

    private final boolean autoDelete;

    private Map<String, Object> exchangeArguments = new HashMap<>();

    private boolean redeclare;

    public ExchangeConfigurationImpl(String exchangeName, String type, boolean durable, boolean autoDelete) {
        this.exchangeName = exchangeName;
        this.type = type;
        this.durable = durable;
        this.autoDelete = autoDelete;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getType() {
        return type;
    }

    public boolean isDurable() {
        return durable;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public Map<String, Object> getExchangeArguments() {
        return Collections.unmodifiableMap(exchangeArguments);
    }

    public void addExchangeArguments(Map<String, Object> exchangeArgs) {
        this.exchangeArguments.putAll(exchangeArgs);
    }

    public boolean redeclare() {
        return redeclare;
    }

    public void setRedeclare(boolean redeclare) {
        this.redeclare = redeclare;
    }

    @Override
    public String toString() {
        return "ExchangeConfigurationImpl{" +
                "exchangeName='" + exchangeName + '\'' +
                ", type='" + type + '\'' +
                ", durable=" + durable +
                ", autoDelete=" + autoDelete +
                ", exchangeArguments=" + exchangeArguments +
                ", redeclare=" + redeclare +
                '}';
    }
}
