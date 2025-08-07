package com.regent.rpush.route.service.template.crud;

import com.baomidou.mybatisplus.extension.service.IService;
import com.regent.rpush.route.model.RpushTemplateReceiver;

public interface IRpushTemplateReceiverCrudService
        extends IService<RpushTemplateReceiver> {
    void updateReceiver(RpushTemplateReceiver receiver);
    void delete(Long id);
}