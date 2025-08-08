
package com.regent.rpush.server.socket.session.registry;

import com.regent.rpush.server.socket.RpushClient;
import com.regent.rpush.server.socket.session.SocketSession;
import com.regent.rpush.server.socket.session.factory.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementazione in-memory di SessionRegistry.
 */
@Service
public class InMemorySessionRegistry implements SessionRegistry {

    private final Map<RpushClient, SocketSession> byClient = new ConcurrentHashMap<>();
    private final Map<Long, SocketSession> byRegistration = new ConcurrentHashMap<>();
    private final SessionFactory sessionFactory;

    @Autowired
    public InMemorySessionRegistry(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public SocketSession getSession(RpushClient client) {
        return byClient.computeIfAbsent(client, sessionFactory::create);
    }

    @Override
    public SocketSession getSession(Long registrationId) {
        return byRegistration.get(registrationId);
    }

    @Override
    public void register(Long registrationId, SocketSession session) {
        byRegistration.put(registrationId, session);
    }

    @Override
    public void removeSession(RpushClient client) {
        SocketSession sess = byClient.remove(client);
        if (sess != null && sess.getRegistrationId() != null) {
            byRegistration.remove(sess.getRegistrationId());
        }
    }
}
