package com.regent.rpush.route.spi;

import com.regent.rpush.dto.enumration.MessageType;

public interface IMessageHandlerDescriptor {
    MessageType messageType();
    Class<?> payloadType();
}
