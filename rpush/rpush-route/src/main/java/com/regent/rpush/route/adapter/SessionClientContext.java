package com.regent.rpush.route.adapter;

import com.regent.rpush.route.api.query.ClientContext;
import com.regent.rpush.route.utils.infrastructure.session.SessionUtils;
import org.springframework.stereotype.Component;

@Component
public class SessionClientContext implements ClientContext {
    @Override
    public String getClientId() {
        return SessionUtils.getClientId();
    }
}
