package com.regent.rpush.route.service.config.query.batch.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.regent.rpush.route.mapper.RpushPlatformConfigMapper;
import com.regent.rpush.route.model.RpushPlatformConfig;
import com.regent.rpush.route.model.RpushPlatformConfigValue;
import com.regent.rpush.route.service.config.query.batch.IRpushConfigBatchQueryService;
import com.regent.rpush.route.service.config.value.IRpushPlatformConfigValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class IRpushConfigBatchQueryServiceImpl extends ServiceImpl<RpushPlatformConfigMapper, RpushPlatformConfig> implements IRpushConfigBatchQueryService {

    @Autowired
    private IRpushPlatformConfigValueService rpushPlatformConfigValueService;

    @Override
    public Map<Long, Map<String, Object>> queryConfig(String clientId, List<Long> configIds) {
        if (configIds == null || configIds.size() <= 0) {
            return new HashMap<>();
        }
        Collection<RpushPlatformConfig> configs = listByIds(configIds);
        QueryWrapper<RpushPlatformConfigValue> configValueQueryWrapper = new QueryWrapper<>();
        configValueQueryWrapper.in("client_id", clientId);
        configValueQueryWrapper.in("config_id", configIds);
        List<RpushPlatformConfigValue> configValues = rpushPlatformConfigValueService.list(configValueQueryWrapper);
        Map<Long, List<RpushPlatformConfigValue>> configValueMap = new HashMap<>();
        for (RpushPlatformConfigValue configValue : configValues) {
            configValueMap.computeIfAbsent(configValue.getConfigId(), k -> new ArrayList<>()).add(configValue);
        }

        Map<Long, Map<String, Object>> configMap = new HashMap<>(); // 键为配置id，值为：具体的配置键值
        for (RpushPlatformConfig config : configs) {
            Map<String, Object> valueMap = configMap.computeIfAbsent(config.getId(), k -> new HashMap<>());
            valueMap.put("defaultFlag", config.getDefaultFlag());
            valueMap.put("configName", config.getConfigName());
            List<RpushPlatformConfigValue> values = configValueMap.get(config.getId());
            if (values == null || values.size() <= 0) {
                continue;
            }
            for (RpushPlatformConfigValue value : values) {
                valueMap.put(value.getKey(), value.getValue());
            }
        }
        return configMap;
    }
}
