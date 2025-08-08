package com.regent.rpush.server.socket.session.service.retrieval;

import com.regent.rpush.server.socket.RpushClient;
import com.regent.rpush.server.socket.session.SocketSession;

public interface SessionRetrievalService {
    SocketSession getOrCreate(RpushClient client);
    SocketSession getByRegistrationId(Long registrationId);
}
