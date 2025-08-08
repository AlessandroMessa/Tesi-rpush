package com.regent.rpush.server.socket.session.service;

import com.regent.rpush.server.socket.RpushClient;
import com.regent.rpush.server.socket.session.SocketSession;
import com.regent.rpush.server.socket.session.lifecycle.SessionLifecycleService;
import com.regent.rpush.server.socket.session.registry.SessionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servizio di livello superiore per operazioni su SocketSession:
 * creazione, login e offline.
 */
@Service
public class SocketSessionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketSessionService.class);

    private final SessionRegistry sessionRegistry;
    private final SessionLifecycleService lifecycleService;

    @Autowired
    public SocketSessionService(SessionRegistry sessionRegistry,
                                SessionLifecycleService lifecycleService) {
        this.sessionRegistry = sessionRegistry;
        this.lifecycleService = lifecycleService;
    }

    /**
     * Recupera o crea una sessione per il client.
     */
    public SocketSession getOrCreate(RpushClient client) {
        return sessionRegistry.getSession(client);
    }

    /**
     * Effettua il login per il client: registra, imposta stato e persiste.
     */
    public void login(Long registrationId, RpushClient client) {
        SocketSession session = getOrCreate(client);
        sessionRegistry.register(registrationId, session);
        lifecycleService.login(registrationId, session);
    }

    public long offlineByClient(RpushClient client) {
        SocketSession session = sessionRegistry.getSession(client);
        if (session != null) {
            Long regId = session.getRegistrationId();
            lifecycleService.offline(session);
            sessionRegistry.removeSession(client);
            try {
                client.close();
            } catch (Exception e) {
                LOGGER.error("Errore chiusura client [{}]", regId, e);
            }
            return regId != null ? regId : -1;
        }
        return -1;
    }

    /**
     * Effettua l'offline basandosi sul registrationId e restituisce l'id.
     *
     * @param registrationId l'id di registrazione
     * @return il registrationId disconnesso, oppure -1 se non esiste sessione
     */
    public long offlineByRegistrationId(Long registrationId) {
        SocketSession session = sessionRegistry.getSession(registrationId);
        if (session != null) {
            return offlineByClient(session.getClient());
        }
        return -1;
    }
    public SocketSession getSessionByRegistrationId(Long registrationId) {
        return sessionRegistry.getSession(registrationId);
    }
}