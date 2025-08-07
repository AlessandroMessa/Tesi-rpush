package com.regent.rpush.route.adapter;

import com.regent.rpush.dto.enumration.MessagePlatformEnum;
import com.regent.rpush.dto.route.config.query.field.ConfigFieldVO;
import com.regent.rpush.route.api.query.ConfigFieldProvider;
import com.regent.rpush.route.utils.application.message.MessageHandlerUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageHandlerFieldProvider implements ConfigFieldProvider {
    @Override
    public List<ConfigFieldVO> listFields(MessagePlatformEnum platform) {
        return MessageHandlerUtils.listConfigFieldName(platform);
    }
}
