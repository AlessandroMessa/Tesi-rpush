package com.regent.rpush.route.service.template.api.impl;


import com.regent.rpush.route.utils.infrastructure.persistance.Qw;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.regent.rpush.route.model.RpushTemplateReceiver;
import com.regent.rpush.route.service.template.api.IRpushTemplateReceiverService;
import com.regent.rpush.route.service.template.crud.IRpushTemplateReceiverCrudService;

import java.util.List;

@Service
public class RpushTemplateReceiverServiceImpl implements IRpushTemplateReceiverService {

    private final IRpushTemplateReceiverCrudService crudService;

    @Autowired
    public RpushTemplateReceiverServiceImpl(IRpushTemplateReceiverCrudService crudService) {
        this.crudService = crudService;
    }

    @Override
    public void updateReceiver(RpushTemplateReceiver receiver) {
        crudService.updateReceiver(receiver);
    }

    @Override
    public void deleteReceiver(Long id) {
        crudService.delete(id);
    }
    @Override
    public List<RpushTemplateReceiver> listReceiversByClient(String clientId) {
        // qui puoi riutilizzare Qw o spostare anche Qw dentro il CRUD se preferisci
        return crudService.list(
                Qw.newInstance(RpushTemplateReceiver.class)
                        .eq("client_id", clientId)
        );
    }
    @Override
    public List<RpushTemplateReceiver> listReceiversByGroup(Long groupId) {
        return crudService.list(
                Qw.newInstance(RpushTemplateReceiver.class)
                        .eq("group_id", groupId)
        );
    }
}
