package com.regent.rpush.route.service.config.query.default_.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.regent.rpush.dto.enumration.MessagePlatformEnum;
import com.regent.rpush.dto.message.base.BaseMessage;
import com.regent.rpush.dto.message.config.Config;
import com.regent.rpush.route.mapper.RpushPlatformConfigMapper;
import com.regent.rpush.route.model.RpushPlatformConfig;
import com.regent.rpush.route.service.config.query.batch.IRpushConfigBatchQueryService;
import com.regent.rpush.route.service.config.query.default_.IRpushConfigDefaultQueryService;
import com.regent.rpush.route.utils.application.message.MessageHandlerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
@Service
public class IRpushConfigDefaultQueryServiceImpl extends ServiceImpl<RpushPlatformConfigMapper, RpushPlatformConfig> implements IRpushConfigDefaultQueryService {

    @Autowired
    private IRpushConfigBatchQueryService iRpushConfigBatchQueryService;

    @Override
    public <T> List<T> queryConfigOrDefault(String clientId, List<Long> configIds, Class<T> configType, MessagePlatformEnum platform) {
        if (configIds == null || configIds.size() <= 0) {
            // 查一个默认配置出来用
            QueryWrapper<RpushPlatformConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("client_id", clientId);
            queryWrapper.eq("platform", platform.name());
            queryWrapper.eq("default_flag", true);
            RpushPlatformConfig config = getOne(queryWrapper, false);
            if (config == null) {
                return new ArrayList<>();
            }
            configIds = Collections.singletonList(config.getId());
        }
        Map<Long, Map<String, Object>> configMap = iRpushConfigBatchQueryService.queryConfig(clientId, configIds); // 键为配置id，值为：具体的配置键值
        List<Config> configs = MessageHandlerUtils.convertConfig(configType, configMap); // 转成具体的配置实体类
        //noinspection unchecked
        return (List<T>) configs;
    }

    @Override
    public <T> List<T> queryConfigOrDefault(BaseMessage message, Class<T> configType, MessagePlatformEnum platform) {
        return queryConfigOrDefault(message.getClientId(), message.getConfigIds(), configType, platform);
    }
}
