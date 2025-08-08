package com.regent.rpush.server.socket.session.factory;

import com.regent.rpush.server.socket.RpushClient;
import com.regent.rpush.server.socket.session.SocketSession;

/**
 * Factory per la creazione di istanze di SocketSession.
 */
public interface SessionFactory {
    /**
     * Crea una nuova sessione associata al client.
     *
     * @param client il client socket
     * @return la nuova istanza di SocketSession
     */
    SocketSession create(RpushClient client);
}


