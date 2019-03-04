package com.plambis.jmeter.amqp;

import java.io.IOException;

import com.plambis.jmeter.amqp.client.MessageConsumer;
import com.plambis.jmeter.amqp.client.MessageConsumerImpl;
import com.plambis.jmeter.amqp.client.MessageData;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

public class AMQPConsumer extends AMQPSampler implements Interruptible, TestStateListener {
    private static final int DEFAULT_PREFETCH_COUNT = 0; // unlimited

    public static final boolean DEFAULT_READ_RESPONSE = true;
    public static final String DEFAULT_PREFETCH_COUNT_STRING = Integer.toString(DEFAULT_PREFETCH_COUNT);

    private static final long serialVersionUID = 7480863561320459091L;

    private static final Logger log = LoggerFactory.getLogger(AMQPConsumer.class);

    // ++ These are JMX names, and must not be changed
    private static final String PREFETCH_COUNT = "AMQPConsumer.PrefetchCount";
    private static final String READ_RESPONSE = "AMQPConsumer.ReadResponse";
    private static final String PURGE_QUEUE = "AMQPConsumer.PurgeQueue";
    private static final String AUTO_ACK = "AMQPConsumer.AutoAck";
    private static final String RECEIVE_TIMEOUT = "AMQPConsumer.ReceiveTimeout";

    public static boolean DEFAULT_USE_TX = false;
    private static final String USE_TX = "AMQPConsumer.UseTx";

    private transient String consumerTag;

    /**
     * {@inheritDoc}
     */
    @Override
    public SampleResult sample(Entry entry) {
        SampleResult result = new SampleResult();
        result.sampleStart();
        result.setSampleLabel(getName());
        result.setSuccessful(false);
        result.setResponseCode("500");

        try {
            initClient();
        } catch (Exception ex) {
            log.error("Failed to initialize channel : ", ex);
            result.setResponseMessage(ex.toString());
            return result;
        }

        try {
            MessageConsumer consumer = new MessageConsumerImpl( getMessageClient());
            MessageData get = consumer.consumeMessages(getQueue(), getPrefetchCountAsInt(), autoAck());
            if (getReadResponseAsBoolean()) {
                String response = new String(get.getBody(), "UTF-8");
                result.setSamplerData(String.valueOf(get.getMessageCount()));
                result.setResponseData(response, "UTF-8");
                result.setResponseMessage("OK");
            } else {
                result.setSamplerData("Read response is false.");
            }

            result.setDataType(SampleResult.TEXT);
            result.setResponseHeaders(get.getHeaders());
            result.setResponseCodeOK();
            result.setSuccessful(true);

            getMessageClient().commitTransaction();
        } catch (ShutdownSignalException e) {
            log.warn("AMQP consumer failed to ShutdownSignalException", e);
            result.setResponseCode("400");
            result.setResponseMessage(e.getMessage());
            interrupt();
        } catch (ConsumerCancelledException e) {
            log.warn("AMQP consumer failed to ConsumerCancelledException", e);
            result.setResponseCode("300");
            result.setResponseMessage(e.getMessage());
            interrupt();
        } catch (IOException e) {
            log.warn("AMQP consumer failed to IOException", e);
            result.setResponseCode("100");
            result.setResponseMessage(e.getMessage());
        } finally {
            result.sampleEnd(); // End timimg
        }
        return result;
    }

    /**
     * @return the whether or not to purge the queue
     */
    public String getPurgeQueue() {
        return getPropertyAsString(PURGE_QUEUE);
    }

    public void setPurgeQueue(String content) {
        setProperty(PURGE_QUEUE, content);
    }

    public void setPurgeQueue(Boolean purgeQueue) {
        setProperty(PURGE_QUEUE, purgeQueue.toString());
    }

    public boolean purgeQueue() {
        return Boolean.parseBoolean(getPurgeQueue());
    }

    /**
     * @return the whether or not to auto ack
     */
    public String getAutoAck() {
        return getPropertyAsString(AUTO_ACK);
    }

    public void setAutoAck(String content) {
        setProperty(AUTO_ACK, content);
    }

    public void setAutoAck(Boolean autoAck) {
        setProperty(AUTO_ACK, autoAck.toString());
    }

    public boolean autoAck() {
        return getPropertyAsBoolean(AUTO_ACK);
    }

    protected int getReceiveTimeoutAsInt() {
        if (getPropertyAsInt(RECEIVE_TIMEOUT) < 1) {
            return DEFAULT_TIMEOUT;
        }
        return getPropertyAsInt(RECEIVE_TIMEOUT);
    }

    public String getReceiveTimeout() {
        return getPropertyAsString(RECEIVE_TIMEOUT, DEFAULT_TIMEOUT_STRING);
    }

    public void setReceiveTimeout(String s) {
        setProperty(RECEIVE_TIMEOUT, s);
    }

    public String getPrefetchCount() {
        return getPropertyAsString(PREFETCH_COUNT, DEFAULT_PREFETCH_COUNT_STRING);
    }

    public void setPrefetchCount(String prefetchCount) {
        setProperty(PREFETCH_COUNT, prefetchCount);
    }

    public int getPrefetchCountAsInt() {
        return getPropertyAsInt(PREFETCH_COUNT);
    }

    public Boolean getUseTx() {
        return getPropertyAsBoolean(USE_TX, DEFAULT_USE_TX);
    }

    public void setUseTx(Boolean tx) {
        setProperty(USE_TX, tx);
    }

    /**
     * set whether the sampler should read the response or not
     *
     * @param read whether the sampler should read the response or not
     */
    public void setReadResponse(Boolean read) {
        setProperty(READ_RESPONSE, read);
    }

    /**
     * return whether the sampler should read the response
     *
     * @return whether the sampler should read the response
     */
    public String getReadResponse() {
        return getPropertyAsString(READ_RESPONSE);
    }

    /**
     * return whether the sampler should read the response as a boolean value
     *
     * @return whether the sampler should read the response as a boolean value
     */
    public boolean getReadResponseAsBoolean() {
        return getPropertyAsBoolean(READ_RESPONSE);
    }

    @Override
    public boolean interrupt() {
        testEnded();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void testEnded() {

        if (purgeQueue()) {
            log.info("Purging queue {}", getQueue());
            try {
                getMessageClient().queuePurge();
            } catch (IOException e) {
                log.error("Failed to purge queue " + getQueue(), e);
            }
        }
    }

    @Override
    public void testStarted() {
        log.info("testStarted");

    }

    @Override
    public void testStarted(String host) {
        log.info("testStarted  {}", host);

    }

    @Override
    public void testEnded(String host) {
        log.info("testEnded  {}", host);

    }
}
