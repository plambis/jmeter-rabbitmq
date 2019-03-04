package com.plambis.jmeter.amqp.client;

import java.io.IOException;

public interface MessageConsumer {
    MessageData consumeMessages(String queueName, int nbMessages, boolean autoAck) throws IOException;
}
