package com.regent.rpush.route.service.config.query.default_;

import com.baomidou.mybatisplus.extension.service.IService;
import com.regent.rpush.dto.enumration.MessagePlatformEnum;
import com.regent.rpush.dto.message.base.BaseMessage;
import com.regent.rpush.route.model.RpushPlatformConfig;

import java.util.List;

public interface IRpushConfigDefaultQueryService {
    /**
     * 批量查询配置，如果没有传配置id列表，会查一下默认配置，如果没有默认配置，则会返回空数组
     *
     * @param clientId   clientId
     * @param configIds  配置id列表
     * @param configType 配置DTO类型
     */
    <T> List<T> queryConfigOrDefault(String clientId, List<Long> configIds, Class<T> configType, MessagePlatformEnum platform);

    <T> List<T> queryConfigOrDefault(BaseMessage message, Class<T> configType, MessagePlatformEnum platform);
}
