package com.regent.rpush.client.client;

import com.regent.rpush.client.MsgProcessor;

import java.util.List;

public interface ClientContext {
    void reconnect();
    List<MsgProcessor> getMsgProcessors();
}