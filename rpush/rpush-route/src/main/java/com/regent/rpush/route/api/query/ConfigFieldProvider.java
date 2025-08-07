package com.regent.rpush.route.api.query;

import com.regent.rpush.dto.enumration.MessagePlatformEnum;
import com.regent.rpush.dto.route.config.query.field.ConfigFieldVO;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface ConfigFieldProvider {
    List<ConfigFieldVO> listFields(MessagePlatformEnum platform);
}
