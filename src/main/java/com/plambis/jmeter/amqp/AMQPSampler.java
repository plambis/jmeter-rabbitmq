package com.plambis.jmeter.amqp;

import com.plambis.jmeter.amqp.client.*;
import com.plambis.jmeter.amqp.client.conf.ChannelConfiguration;
import com.plambis.jmeter.amqp.client.conf.ChannelConfigurationImpl;
import com.plambis.jmeter.amqp.client.conf.ExchangeConfigurationImpl;
import com.plambis.jmeter.amqp.client.conf.QueueConfigurationImpl;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public abstract class AMQPSampler extends AbstractSampler implements ThreadListener {
    private static final Logger log = LoggerFactory.getLogger(AMQPSampler.class);

    public static final boolean DEFAULT_EXCHANGE_DURABLE = true;
    public static final boolean DEFAULT_EXCHANGE_AUTO_DELETE = true;
    public static final boolean DEFAULT_EXCHANGE_REDECLARE = false;
    public static final boolean DEFAULT_QUEUE_REDECLARE = false;

    public static final int DEFAULT_PORT = 5672;
    public static final String DEFAULT_PORT_STRING = Integer.toString(DEFAULT_PORT);

    public static final int DEFAULT_TIMEOUT = 1000;
    public static final String DEFAULT_TIMEOUT_STRING = Integer.toString(DEFAULT_TIMEOUT);

    public static final int DEFAULT_ITERATIONS = 1;
    public static final String DEFAULT_ITERATIONS_STRING = Integer.toString(DEFAULT_ITERATIONS);

    //++ These are JMX names, and must not be changed
    protected static final String EXCHANGE = "AMQPSampler.Exchange";
    protected static final String EXCHANGE_TYPE = "AMQPSampler.ExchangeType";
    protected static final String EXCHANGE_DURABLE = "AMQPSampler.ExchangeDurable";
    protected static final String EXCHANGE_AUTO_DELETE = "AMQPSampler.ExchangeAutoDelete";
    protected static final String EXCHANGE_REDECLARE = "AMQPSampler.ExchangeRedeclare";
    private static final String EXCHANGE_ARGUMENTS = "AMQPSampler.ExchangeArguments";

    protected static final String VIRTUAL_HOST = "AMQPSampler.VirtualHost";
    protected static final String HOST = "AMQPSampler.Host";
    protected static final String PORT = "AMQPSampler.Port";
    protected static final String SSL = "AMQPSampler.SSL";
    protected static final String USERNAME = "AMQPSampler.Username";
    protected static final String PASSWORD = "AMQPSampler.Password";
    private static final String TIMEOUT = "AMQPSampler.Timeout";
    private static final String ITERATIONS = "AMQPSampler.Iterations";

    protected static final String QUEUE = "AMQPSampler.Queue";
    private static final String QUEUE_DURABLE = "AMQPSampler.QueueDurable";
    private static final String QUEUE_REDECLARE = "AMQPSampler.Redeclare";
    private static final String QUEUE_EXCLUSIVE = "AMQPSampler.QueueExclusive";
    private static final String QUEUE_AUTO_DELETE = "AMQPSampler.QueueAutoDelete";
    protected static final String ROUTING_KEY = "AMQPSampler.RoutingKey";
    private static final String QUEUE_ARGUMENTS = "AMQPSampler.QueueArguments";

    private transient AMQPClient amqpClient;

    protected boolean initClient() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        amqpClient = new AMQPClient(createChannelConfiguration());
        return true;
    }

    private ChannelConfiguration createChannelConfiguration() {
        ChannelConfigurationImpl channelConf = new ChannelConfigurationImpl(getVirtualHost(), getHosts(), getPortAsInt(), getUsername(), getPassword(), getUseTx());
        channelConf.setTimeout(getTimeoutAsInt());
        channelConf.setRoutingKey(getRoutingKey());
        channelConf.setUseSslProtocol(connectionSSL());

        QueueConfigurationImpl queueConf = new QueueConfigurationImpl(getQueue(), queueDurable(), queueExclusive(), queueAutoDelete());
        queueConf.setRedeclare(getQueueRedeclare());
        queueConf.addQueueArguments(getQueueArguments().getArgumentsAsMap());
        channelConf.setQueueConfiguration(queueConf);

        ExchangeConfigurationImpl exchangeConf = new ExchangeConfigurationImpl(getExchange(), getExchangeType(), getExchangeDurable(), getExchangeAutoDelete());
        exchangeConf.addExchangeArguments(getExchangeArgumentsAsMap());
        exchangeConf.setRedeclare(getExchangeRedeclare());
        channelConf.setExchangeConfiguration(exchangeConf);

        return channelConf;
    }

    protected Map<String, Object> getExchangeArgumentsAsMap() {
        return getExchangeArguments().getArgumentsAsMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
    }

    /**
     * @return a string for the sampleResult Title
     */
    protected String getTitle() {
        return this.getName();
    }

    protected int getTimeoutAsInt() {
        if (getPropertyAsInt(TIMEOUT) < 1) {
            return DEFAULT_TIMEOUT;
        }
        return getPropertyAsInt(TIMEOUT);
    }

    public String getTimeout() {
        return getPropertyAsString(TIMEOUT, DEFAULT_TIMEOUT_STRING);
    }


    public void setTimeout(String s) {
        setProperty(TIMEOUT, s);
    }

    public String getIterations() {
        return getPropertyAsString(ITERATIONS, DEFAULT_ITERATIONS_STRING);
    }

    public void setIterations(String s) {
        setProperty(ITERATIONS, s);
    }

    public int getIterationsAsInt() {
        return getPropertyAsInt(ITERATIONS);
    }

    public String getExchange() {
        return getPropertyAsString(EXCHANGE);
    }

    public void setExchange(String name) {
        setProperty(EXCHANGE, name);
    }


    public boolean getExchangeDurable() {
        return getPropertyAsBoolean(EXCHANGE_DURABLE);
    }

    public void setExchangeDurable(boolean durable) {
        setProperty(EXCHANGE_DURABLE, durable);
    }

    public boolean getExchangeAutoDelete() {
        return getPropertyAsBoolean(EXCHANGE_AUTO_DELETE);
    }

    public void setExchangeAutoDelete(boolean autoDelete) {
        setProperty(EXCHANGE_AUTO_DELETE, autoDelete);
    }

    public String getExchangeType() {
        return getPropertyAsString(EXCHANGE_TYPE);
    }

    public void setExchangeType(String name) {
        setProperty(EXCHANGE_TYPE, name);
    }


    public Boolean getExchangeRedeclare() {
        return getPropertyAsBoolean(EXCHANGE_REDECLARE);
    }

    public void setExchangeRedeclare(Boolean content) {
        setProperty(EXCHANGE_REDECLARE, content);
    }

    public String getQueue() {
        return getPropertyAsString(QUEUE);
    }

    public void setQueue(String name) {
        setProperty(QUEUE, name);
    }


    public String getRoutingKey() {
        return getPropertyAsString(ROUTING_KEY);
    }

    public void setRoutingKey(String name) {
        setProperty(ROUTING_KEY, name);
    }


    public String getVirtualHost() {
        return getPropertyAsString(VIRTUAL_HOST);
    }

    public void setVirtualHost(String name) {
        setProperty(VIRTUAL_HOST, name);
    }

    private List<String> getHosts() {
        return Arrays.asList(getHost().split(","));
    }

    public String getHost() {
        return getPropertyAsString(HOST);
    }

    public void setHost(String name) {
        setProperty(HOST, name);
    }


    public String getPort() {
        return getPropertyAsString(PORT);
    }

    public void setPort(String name) {
        setProperty(PORT, name);
    }

    protected int getPortAsInt() {
        if (getPropertyAsInt(PORT) < 1) {
            return DEFAULT_PORT;
        }
        return getPropertyAsInt(PORT);
    }

    public void setConnectionSSL(String content) {
        setProperty(SSL, content);
    }

    public void setConnectionSSL(Boolean value) {
        setProperty(SSL, value.toString());
    }

    public boolean connectionSSL() {
        return getPropertyAsBoolean(SSL);
    }


    public String getUsername() {
        return getPropertyAsString(USERNAME);
    }

    public void setUsername(String name) {
        setProperty(USERNAME, name);
    }


    public String getPassword() {
        return getPropertyAsString(PASSWORD);
    }

    public void setPassword(String name) {
        setProperty(PASSWORD, name);
    }

    /**
     * @return the whether or not the queue is durable
     */
    public String getQueueDurable() {
        return getPropertyAsString(QUEUE_DURABLE);
    }

    public void setQueueDurable(String content) {
        setProperty(QUEUE_DURABLE, content);
    }

    public void setQueueDurable(Boolean value) {
        setProperty(QUEUE_DURABLE, value.toString());
    }

    public boolean queueDurable() {
        return getPropertyAsBoolean(QUEUE_DURABLE);
    }

    /**
     * @return the whether or not the queue is exclusive
     */
    public String getQueueExclusive() {
        return getPropertyAsString(QUEUE_EXCLUSIVE);
    }

    public void setQueueExclusive(String content) {
        setProperty(QUEUE_EXCLUSIVE, content);
    }

    public void setQueueExclusive(Boolean value) {
        setProperty(QUEUE_EXCLUSIVE, value.toString());
    }

    public boolean queueExclusive() {
        return getPropertyAsBoolean(QUEUE_EXCLUSIVE);
    }

    /**
     * @return the whether or not the queue should auto delete
     */
    public String getQueueAutoDelete() {
        return getPropertyAsString(QUEUE_AUTO_DELETE);
    }

    public void setQueueAutoDelete(String content) {
        setProperty(QUEUE_AUTO_DELETE, content);
    }

    public void setQueueAutoDelete(Boolean value) {
        setProperty(QUEUE_AUTO_DELETE, value.toString());
    }

    public boolean queueAutoDelete() {
        return getPropertyAsBoolean(QUEUE_AUTO_DELETE);
    }


    public Boolean getQueueRedeclare() {
        return getPropertyAsBoolean(QUEUE_REDECLARE);
    }

    public void setQueueRedeclare(Boolean content) {
        setProperty(QUEUE_REDECLARE, content);
    }

    public abstract Boolean getUseTx();

    protected void cleanup() {
        if (amqpClient == null) {
            return;
        }
        amqpClient.cleanup();
    }

    @Override
    public void threadFinished() {
        log.info("AMQPSampler.threadFinished called");
        cleanup();
    }

    @Override
    public void threadStarted() {

    }

    protected AMQPClient getMessageClient() {
        return amqpClient;
    }

    public void setExchangeArguments(Arguments exchangeArguments) {
        setProperty(new TestElementProperty(EXCHANGE_ARGUMENTS, exchangeArguments));
    }

    public Arguments getExchangeArguments() {
        return (Arguments) getProperty(EXCHANGE_ARGUMENTS).getObjectValue();
    }

    public void setQueueArguments(Arguments queueArguments) {
        setProperty(new TestElementProperty(QUEUE_ARGUMENTS, queueArguments));
    }

    public Arguments getQueueArguments() {
        return (Arguments) getProperty(QUEUE_ARGUMENTS).getObjectValue();
    }

}
