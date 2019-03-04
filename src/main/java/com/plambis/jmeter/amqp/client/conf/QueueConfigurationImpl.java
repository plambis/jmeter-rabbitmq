package com.plambis.jmeter.amqp.client.conf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QueueConfigurationImpl implements  QueueConfiguration {

    private final String queueName;

    private final boolean durable;

    private final boolean exclusive;

    private final boolean autoDelete;

    private final Map<String, Object> queueArguments = new HashMap<>();

    private boolean redeclare;

    public QueueConfigurationImpl(String queueName, boolean durable, boolean exclusive, boolean autoDelete) {
        this.queueName = queueName;
        this.durable = durable;
        this.exclusive = exclusive;
        this.autoDelete = autoDelete;
    }

    public String getQueueName() {
        return queueName;
    }

    public boolean isDurable() {
        return durable;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public Map<String, Object> getQueueArguments() {
        return Collections.unmodifiableMap(queueArguments);
    }

    public void addQueueArguments( Map<String, Object> queueArgs) {
        queueArguments.putAll(queueArgs);
    }

    public boolean redeclare() {
        return redeclare;
    }

    public void setRedeclare(boolean redeclare) {
        this.redeclare = redeclare;
    }

    @Override
    public String toString() {
        return "QueueConfigurationImpl{" +
                "queueName='" + queueName + '\'' +
                ", durable=" + durable +
                ", exclusive=" + exclusive +
                ", autoDelete=" + autoDelete +
                ", queueArguments=" + queueArguments +
                ", redeclare=" + redeclare +
                '}';
    }
}
