package com.plambis.jmeter.amqp.client;

import com.plambis.jmeter.amqp.client.conf.ChannelConfiguration;
import com.plambis.jmeter.amqp.client.conf.ExchangeConfiguration;
import com.plambis.jmeter.amqp.client.conf.QueueConfiguration;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class AMQPClient {
    private static final Logger log = LoggerFactory.getLogger(AMQPClient.class);
    private static final int DEFAULT_HEARTBEAT = 1;

    private final transient ChannelConfiguration channelConf;
    private transient Connection connection;
    private transient Channel channel;

    public AMQPClient(ChannelConfiguration channelConf) throws KeyManagementException, TimeoutException, NoSuchAlgorithmException,
            IOException {
        log.debug("Channel configuration: {}", channelConf);
        this.channelConf = channelConf;
        initChannel();
    }

    public Channel getChannel() {
        return channel;
    }

    private boolean initChannel() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        if (channel != null && channel.isOpen()) {
            log.info("Reuse existing channel {}", getChannel().getChannelNumber());
            return true;
        }

        if (channel != null && !channel.isOpen()) {
            log.warn("channel " + getChannel().getChannelNumber()
                    + " closed unexpectedly: ", getChannel().getCloseReason());
            channel = null; // so we re-open it below
        }

        if (getChannel() == null) {
            channel = createChannel();

            declareQueue();

            declareExchange();

            declareBindings(channelConf.getQueueConfiguration().getQueueName(), channelConf.getExchangeConfiguration().getExchangeName(),
                    channelConf.getRoutingKey());
        }

        if (channelConf.useTx()) {
            channel.txSelect();
        }
        return true;
    }

    private void declareBindings(String queueName, String exchangeName, String routingKey) throws IOException {
        if (StringUtils.isBlank(queueName) || StringUtils.isBlank(exchangeName)) {
            log.warn("There no queue or exchange configured!");
            return;
        }
        channel.queueBind(queueName, exchangeName, routingKey);
        log.info("bound queue: {} to exchange {} routing key {} ", queueName, exchangeName,
                routingKey
        );
    }

    private void declareExchange() throws IOException {
        ExchangeConfiguration exchangeConf = channelConf.getExchangeConfiguration();
        if (StringUtils.isBlank(exchangeConf.getExchangeName())) {
            log.warn("There no exchange configured!");
            return;
        }
        //Use a named exchange
        if (exchangeConf.redeclare()) {
            deleteExchange();
        }

        channel.exchangeDeclare(exchangeConf.getExchangeName(), exchangeConf.getType(), exchangeConf.isDurable(),
                exchangeConf.isAutoDelete(), exchangeConf.getExchangeArguments());
        log.info("Declared exchange: {}", exchangeConf.getExchangeName());
    }

    private void declareQueue() throws IOException {
        QueueConfiguration queueConf = channelConf.getQueueConfiguration();
        if (StringUtils.isBlank(queueConf.getQueueName())) {
            log.warn("There no queue configured!");
            return;
        }
        if (queueConf.redeclare()) {
            deleteQueue();
        }

        channel.queueDeclare(queueConf.getQueueName(), queueConf.isDurable(), queueConf.isExclusive(), queueConf.isAutoDelete(),
                queueConf.getQueueArguments());
        log.info("Declared queue: {}", queueConf.getQueueName());
    }

    public void cleanup() {
        if (connection == null || !connection.isOpen()) {
            return;
        }
        try {
            connection.close();
        } catch (IOException e) {
            log.error("Failed to close connection", e);
        }
    }

    public void commitTransaction() throws IOException {
        if (!channelConf.useTx()) {
            log.warn("We do not use transactions at all!");
            return;
        }
        channel.txCommit();
    }

    private Channel createChannel() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        log.info("Creating channel " + channelConf.getVirtualHost() + ":" + channelConf.getPort());

        if (connection == null || !connection.isOpen()) {
            createConnection();
        }

        Channel channel = connection.createChannel();
        if (!channel.isOpen()) {
            log.error("Failed to open channel: {}", channel.getCloseReason().getLocalizedMessage());
        }
        return channel;
    }

    private void createConnection() throws NoSuchAlgorithmException, KeyManagementException, IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setRequestedHeartbeat(DEFAULT_HEARTBEAT);
        factory.setConnectionTimeout(channelConf.getTimeout());
        factory.setVirtualHost(channelConf.getVirtualHost());
        factory.setUsername(channelConf.getUsername());
        factory.setPassword(channelConf.getPassword());
        if (channelConf.useSslProtocol()) {
            factory.useSslProtocol("TLS");
        }

        log.info("RabbitMQ ConnectionFactory using:"
                + "\n\t virtual host: " + channelConf.getVirtualHost()
                + "\n\t host: " + channelConf.getHosts()
                + "\n\t port: " + channelConf.getPort()
                + "\n\t username: " + channelConf.getUsername()
                + "\n\t password: " + channelConf.getPassword()
                + "\n\t timeout: " + channelConf.getTimeout()
                + "\n\t heartbeat: " + factory.getRequestedHeartbeat()
                + "\nin " + this
        );

        List<Address> addresses = channelConf.getHosts().stream().map(host -> new Address(host, channelConf.getPort())).collect(
                Collectors.toList());
        log.info("Using hosts: " + channelConf.getHosts() + " addresses: " + addresses);

        connection = factory.newConnection(addresses);
    }

    private void deleteQueue() {
        // use a different channel since channel closes on exception.
        try (Channel channel = createChannel()) {
            log.info("Deleting queue " + channelConf.getQueueConfiguration().getQueueName());
            channel.queueDelete(channelConf.getQueueConfiguration().getQueueName());
        } catch (Exception ex) {
            log.debug(ex.getMessage(), ex);
            // ignore it.
        }
    }

    private void deleteExchange() {
        // use a different channel since channel closes on exception.
        try (Channel channel = createChannel()) {
            log.info("Deleting exchange " + channelConf.getExchangeConfiguration().getExchangeName());
            channel.exchangeDelete(channelConf.getExchangeConfiguration().getExchangeName());
        } catch (Exception ex) {
            log.debug(ex.getMessage(), ex);
        }
    }

    public void queuePurge() throws IOException {
        getChannel().queuePurge(channelConf.getQueueConfiguration().getQueueName());
    }
}
