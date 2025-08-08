package com.regent.rpush.server.socket.session.service.disconnect;

import com.regent.rpush.server.socket.RpushClient;

public interface SessionDisconnectionService {
    long offlineByClient(RpushClient client);
    long offlineByRegistrationId(Long registrationId);
}
