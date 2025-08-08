package com.regent.rpush.server.socket.session.registry;

import com.regent.rpush.server.socket.RpushClient;
import com.regent.rpush.server.socket.session.SocketSession;

public interface SessionRegistry {

    SocketSession getSession(RpushClient client);

    SocketSession getSession(Long registrationId);

    void register(Long registrationId, SocketSession session);

    void removeSession(RpushClient client);
}
