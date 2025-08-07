package com.regent.rpush.route.adapter;

import com.regent.rpush.route.service.config.query.api.ClientContext;
import com.regent.rpush.route.utils.infrastructure.session.SessionUtils;
import org.springframework.stereotype.Component;

@Component
public class SessionClientContext implements ClientContext {
    @Override
    public String getClientId() {
        return SessionUtils.getClientId();
    }
}
