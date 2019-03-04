package com.plambis.jmeter.amqp.client.conf;

import java.util.function.Function;

public enum SupportedQueueAttribute {

    X_MESSAGE_TTL("x-message-ttl",Integer::valueOf),
    X_EXPIRES("x-expires",Integer::valueOf),
    X_MAX_LENGTH("x-max-length",Integer::valueOf),
    X_MAX_LENGTH_BYTES("x-max-length-bytes",Integer::valueOf),
    X_OVERFLOW("x-overflow",Function.identity()),
    X_DEAD_LETTER_EXCHANGE("x-dead-letter-exchange",Function.identity()),
    X_DEAD_LETTER_ROUTING_KEY("x-dead-letter-routing-key",Function.identity()),
    X_MAX_PRIORITY("x-max-priority",Integer::valueOf),
    X_QUEUE_MODE("x-queue-mode",Function.identity()),
    X_QUEUE_MASTER_LOCATOR("x-queue-master-locator",Function.identity()),
    NOT_SUPPORTED_ATTRIBUTE("-1",Function.identity());

    private final String attributeType;
    private final Function<String,?> converter;

    SupportedQueueAttribute(String attributeType, Function<String,?> converter){
        this.attributeType=attributeType;
        this.converter=converter;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public Object convert(String value) {
        return converter.apply(value);
    }

    public static SupportedQueueAttribute valueByAttribute(String value){
        for(SupportedQueueAttribute element : SupportedQueueAttribute.values()){
            if(element.getAttributeType().equals(value)){
                return element;
            }
        }
        return NOT_SUPPORTED_ATTRIBUTE;
    }

}
