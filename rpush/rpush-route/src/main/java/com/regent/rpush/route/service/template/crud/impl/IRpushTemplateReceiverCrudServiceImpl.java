package com.regent.rpush.route.service.template.crud.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.regent.rpush.dto.enumration.MessagePlatformEnum;
import com.regent.rpush.route.mapper.RpushTemplateReceiverMapper;
import com.regent.rpush.route.model.RpushTemplateReceiver;
import com.regent.rpush.route.api.query.ClientContext;
import com.regent.rpush.route.service.template.crud.IRpushTemplateReceiverCrudService;
import com.regent.rpush.route.utils.infrastructure.persistance.Qw;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IRpushTemplateReceiverCrudServiceImpl extends ServiceImpl<RpushTemplateReceiverMapper, RpushTemplateReceiver> implements IRpushTemplateReceiverCrudService {
    @Autowired
    private ClientContext clientContext;

    @Override
    public void updateReceiver(RpushTemplateReceiver receiver) {
        String clientId = clientContext.getClientId();
        String receiverId = receiver.getReceiverId();
        Long id = receiver.getId();
        MessagePlatformEnum platform = MessagePlatformEnum.valueOf(receiver.getPlatform());
        platform.matcherThrow(receiverId); // 验证格式
        if (StringUtils.isNotBlank(receiverId)) {
            // id判重
            QueryWrapper<RpushTemplateReceiver> receiverNameQw = Qw.newInstance(RpushTemplateReceiver.class)
                    .eq("client_id", clientId)
                    .eq("group_id", receiver.getGroupId())
                    .eq("receiver_id", receiverId);
            if (id != null) {
                receiverNameQw.ne("id", id);
            }
            RpushTemplateReceiver existReceiver = getOne(receiverNameQw);
            Assert.isTrue(existReceiver == null, "接收人重复");
        }

        // 入库
        if (id == null) {
            receiver.setClientId(clientId);
            save(receiver);
        } else {
            updateById(receiver);
        }
    }
    @Override
    public void delete(Long id) {
        remove(Qw.newInstance(RpushTemplateReceiver.class).eq("id", id).eq("client_id", clientContext.getClientId()));
    }
}
