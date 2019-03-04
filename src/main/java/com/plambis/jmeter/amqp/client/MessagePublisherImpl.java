package com.plambis.jmeter.amqp.client;

import com.plambis.jmeter.amqp.client.conf.MessagePublisherConfiguration;
import com.rabbitmq.client.AMQP;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class MessagePublisherImpl implements MessagePublisher{
    private static final Logger log = LoggerFactory.getLogger(MessagePublisherImpl.class);
    private final AMQPClient client;
    private final MessagePublisherConfiguration configuration;

    public MessagePublisherImpl(AMQPClient client, MessagePublisherConfiguration configuration) {
        this.client = client;
        this.configuration=configuration;
    }

    private AMQP.BasicProperties getProperties() {
        final AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();

        final int deliveryMode = configuration.isPersistent() ? 2 : 1;
        final String contentType = StringUtils.defaultIfEmpty(configuration.getContentType(), "text/plain");

        builder.contentType(contentType)
                .deliveryMode(deliveryMode)
                .priority(0)
                .correlationId(configuration.getCorrelationId())
                .replyTo(configuration.getReplyToQueue())
                .type(configuration.getMessageType())
                .headers(prepareHeaders())
                .build();
        if (configuration.getMessageId() != null && !configuration.getMessageId().isEmpty()) {
            builder.messageId(configuration.getMessageId());
        }
        return builder.build();
    }

    private Map<String, Object> prepareHeaders() {
        return configuration.getHeaders().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void publish(String exchangeName, String messageRoutingKey, byte[] messageBytes) throws IOException {
        log.debug("Try to write message to queue: {} using routing key {}", exchangeName, messageRoutingKey);
        client.getChannel().basicPublish(exchangeName, messageRoutingKey, getProperties(), messageBytes);
        log.info("Write message to queue: {} using routing key {}", exchangeName, messageRoutingKey);
    }

    @Override
    public void commitTransaction() throws IOException {
        client.commitTransaction();
    }
}
