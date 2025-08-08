package com.regent.rpush.server.socket.session.service.retrieval.impl;

import com.regent.rpush.server.socket.RpushClient;
import com.regent.rpush.server.socket.session.SocketSession;
import com.regent.rpush.server.socket.session.registry.SessionRegistry;
import com.regent.rpush.server.socket.session.service.retrieval.SessionRetrievalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionRetrievalServiceImpl implements SessionRetrievalService {
    private final SessionRegistry registry;
    @Autowired
    public SessionRetrievalServiceImpl(SessionRegistry r) { this.registry = r; }
    @Override public SocketSession getOrCreate(RpushClient c) { return registry.getSession(c); }
    @Override public SocketSession getByRegistrationId(Long id) { return registry.getSession(id); }

}
