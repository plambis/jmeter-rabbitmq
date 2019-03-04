package com.plambis.jmeter.amqp.client;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;

import java.io.IOException;
import java.util.Map;

public class MessageConsumerImpl implements MessageConsumer{

    private final AMQPClient client;

    public MessageConsumerImpl(AMQPClient client) {
        this.client = client;
    }


    @Override
    public MessageData consumeMessages(String queueName, int prefetchCount, boolean autoAck) throws IOException {
        client.getChannel().basicQos(prefetchCount);

        GetResponse response = client.getChannel().basicGet(queueName, autoAck);

        if (!autoAck){
            client.getChannel().basicAck(response.getEnvelope().getDeliveryTag(), false);
        }
        return new MessageDataImpl(response);
    }

    private static class MessageDataImpl implements  MessageData {
        private static final String TIMESTAMP_PARAMETER = "Timestamp";
        private static final String EXCHANGE_PARAMETER = "Exchange";
        private static final String ROUTING_KEY_PARAMETER = "Routing Key";
        private static final String DELIVERY_TAG_PARAMETER = "Delivery Tag";

        private final GetResponse response;

        private MessageDataImpl(GetResponse response) {
            this.response = response;
        }

        @Override
        public byte[] getBody() {
            return response.getBody();
        }

        @Override
        public int getMessageCount() {
            return  response.getMessageCount();
        }

        @Override
        public String getHeaders() {
            return formatHeaders(response.getEnvelope(),response.getProps());
        }

        private String formatHeaders(Envelope envelope, AMQP.BasicProperties properties) {
            Map<String, Object> headers = properties.getHeaders();
            StringBuilder sb = new StringBuilder();
            sb.append(TIMESTAMP_PARAMETER).append(": ")
                    .append(properties.getTimestamp() != null ? properties.getTimestamp().getTime() : "").append("\n");
            sb.append(EXCHANGE_PARAMETER).append(": ").append(envelope.getExchange()).append("\n");
            sb.append(ROUTING_KEY_PARAMETER).append(": ").append(envelope.getRoutingKey()).append("\n");
            sb.append(DELIVERY_TAG_PARAMETER).append(": ").append(envelope.getDeliveryTag()).append("\n");
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(headers.get(entry.getKey())).append("\n");
            }
            return sb.toString();
        }
    }
}
