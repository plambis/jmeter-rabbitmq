package com.plambis.jmeter.amqp.client.conf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChannelConfigurationImpl implements ChannelConfiguration{

    private final String virtualHost;

    private final List<String> hosts = new ArrayList<>();

    private final int port;

    private final String username;

    private final String password;

    private int timeout;

    private boolean useSslProtocol;

    private final boolean useTx;

    private QueueConfiguration queueConfiguration;

    private ExchangeConfiguration exchangeConfiguration;

    private String routingKey;

    public ChannelConfigurationImpl(String virtualHost, List<String> hosts, int port, String username, String password, boolean useTx) {
        this.virtualHost = virtualHost;
        this.hosts.addAll(hosts);
        this.port = port;
        this.username = username;
        this.password = password;
        this.useTx = useTx;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public List<String> getHosts() {
        return Collections.unmodifiableList(hosts);
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean useSslProtocol() {
        return useSslProtocol;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setUseSslProtocol(boolean useSslProtocol) {
        this.useSslProtocol = useSslProtocol;
    }

    public boolean useTx() {
        return useTx;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    @Override
    public QueueConfiguration getQueueConfiguration() {
        return queueConfiguration;
    }

    public void setQueueConfiguration(QueueConfiguration queueConfiguration) {
        this.queueConfiguration = queueConfiguration;
    }

    @Override
    public ExchangeConfiguration getExchangeConfiguration() {
        return exchangeConfiguration;
    }

    public void setExchangeConfiguration(ExchangeConfiguration exchangeConfiguration) {
        this.exchangeConfiguration = exchangeConfiguration;
    }

    @Override
    public String toString() {
        return "ChannelConfigurationImpl{" +
                "virtualHost='" + virtualHost + '\'' +
                ", hosts=" + hosts +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", timeout=" + timeout +
                ", useSslProtocol=" + useSslProtocol +
                ", useTx=" + useTx +
                ", queueConfiguration=" + queueConfiguration +
                ", exchangeConfiguration=" + exchangeConfiguration +
                ", routingKey='" + routingKey + '\'' +
                '}';
    }
}
