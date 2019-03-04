package com.plambis.jmeter.amqp.client;

import java.io.IOException;

public interface MessagePublisher {
    void publish(String exchangeName, String messageRoutingKey, byte[] messageBytes) throws IOException;
    void commitTransaction() throws IOException;
}
