package com.regent.rpush.server.socket.session.service.disconnect.impl;

import com.regent.rpush.server.socket.RpushClient;
import com.regent.rpush.server.socket.session.SocketSession;
import com.regent.rpush.server.socket.session.lifecycle.SessionLifecycleService;
import com.regent.rpush.server.socket.session.registry.SessionRegistry;
import com.regent.rpush.server.socket.session.service.disconnect.SessionDisconnectionService;
import com.regent.rpush.server.socket.session.service.retrieval.SessionRetrievalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionDisconnectionServiceImpl implements SessionDisconnectionService {
    private final SessionRetrievalService retrieval;
    private final SessionRegistry registry;
    private final SessionLifecycleService lifecycle;
    private static final Logger LOG = LoggerFactory.getLogger(SessionDisconnectionServiceImpl.class);

    @Autowired
    public SessionDisconnectionServiceImpl(SessionRetrievalService ret,
                                           SessionRegistry reg,
                                           SessionLifecycleService life) {
        this.retrieval = ret;
        this.registry = reg;
        this.lifecycle = life;
    }

    @Override
    public long offlineByClient(RpushClient client) {
        SocketSession sess = retrieval.getOrCreate(client);
        if (sess == null) return -1;
        Long id = sess.getRegistrationId();
        lifecycle.offline(sess);
        registry.removeSession(client);
        try {
            client.close();
        } catch (Exception e) {
            LOG.error("Errore chiusura client [{}]", id, e);
        }
        return id != null ? id : -1;
    }

    @Override
    public long offlineByRegistrationId(Long registrationId) {
        SocketSession sess = retrieval.getByRegistrationId(registrationId);
        return sess != null
                ? offlineByClient(sess.getClient())
                : -1;
    }
}