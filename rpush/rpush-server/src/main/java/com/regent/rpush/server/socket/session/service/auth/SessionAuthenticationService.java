package com.regent.rpush.server.socket.session.service.auth;

import com.regent.rpush.server.socket.RpushClient;

public interface SessionAuthenticationService {
    void login(Long registrationId, RpushClient client);
}
