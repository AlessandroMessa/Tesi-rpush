package com.regent.rpush.route.service.template.api;

import com.regent.rpush.route.model.RpushTemplateReceiver;

import java.util.List;

public interface IRpushTemplateReceiverService {
    void updateReceiver(RpushTemplateReceiver receiver);
    void deleteReceiver(Long id);
    List<RpushTemplateReceiver> listReceiversByClient(String clientId);
    List<RpushTemplateReceiver> listReceiversByGroup(Long groupId);
}
