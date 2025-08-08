package com.regent.rpush.server.socket.session.service.auth.impl;

import com.regent.rpush.server.socket.RpushClient;
import com.regent.rpush.server.socket.session.SocketSession;
import com.regent.rpush.server.socket.session.lifecycle.SessionLifecycleService;
import com.regent.rpush.server.socket.session.registry.SessionRegistry;
import com.regent.rpush.server.socket.session.service.auth.SessionAuthenticationService;
import com.regent.rpush.server.socket.session.service.retrieval.SessionRetrievalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionAuthenticationServiceImpl implements SessionAuthenticationService {
    @Autowired
    private SessionRetrievalService retrieval;

    @Autowired private SessionRegistry registry;
    @Autowired private SessionLifecycleService lifecycle;

    @Override
    public void login(Long registrationId, RpushClient client) {
        SocketSession sess = retrieval.getOrCreate(client);
        registry.register(registrationId, sess);
        lifecycle.login(registrationId, sess);
    }
}
