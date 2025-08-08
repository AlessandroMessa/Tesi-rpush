package com.regent.rpush.server.socket.session.factory;

import com.regent.rpush.server.socket.RpushClient;
import com.regent.rpush.server.socket.session.SocketSession;
import com.regent.rpush.server.socket.session.SocketSessionImpl;
import org.springframework.stereotype.Component;

/**
 * Implementazione di default della SessionFactory.
 */
@Component
public class DefaultSessionFactory implements SessionFactory {

    @Override
    public SocketSession create(RpushClient client) {
        return new SocketSessionImpl(client);
    }
}
