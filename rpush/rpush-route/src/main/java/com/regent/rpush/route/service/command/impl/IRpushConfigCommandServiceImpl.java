package com.regent.rpush.route.service.command.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.regent.rpush.dto.enumration.MessagePlatformEnum;
import com.regent.rpush.dto.route.config.UpdateConfigDTO;
import com.regent.rpush.route.mapper.RpushPlatformConfigMapper;
import com.regent.rpush.route.model.RpushPlatformConfig;
import com.regent.rpush.route.model.RpushPlatformConfigValue;
import com.regent.rpush.route.service.IRpushPlatformConfigValueService;
import com.regent.rpush.route.service.command.IRpushConfigCommandService;
import com.regent.rpush.route.utils.Qw;
import com.regent.rpush.route.utils.SessionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
public class IRpushConfigCommandServiceImpl extends ServiceImpl<RpushPlatformConfigMapper, RpushPlatformConfig> implements IRpushConfigCommandService {
    @Autowired
    private IRpushPlatformConfigValueService rpushPlatformConfigValueService;

    @Transactional
    @Override
    public void updateConfig(UpdateConfigDTO updateConfigDTO) {
        Map<String, String> config = updateConfigDTO.getConfig();
        if (config != null) {
            config.remove("configId");
            config.remove("configName");
            config.remove("defaultFlag");
        }
        Long configId = updateConfigDTO.getConfigId();
        configId = configId != null && configId <= 0L ? null : configId;
        MessagePlatformEnum platform = updateConfigDTO.getPlatform();
        String configName = updateConfigDTO.getConfigName();
        String clientId = SessionUtils.getClientId();

        boolean isUpdate = configId != null;
        boolean isAdd = configId == null; // 有传配置id认为是更新，没有传id认为是新增

        if (isAdd) {
            Assert.isTrue(config != null && config.size() > 0, "配置参数不全");
            Assert.isTrue(StringUtils.isNotBlank(configName), "新增配置名称不能为空");
        }

        if (StringUtils.isNotBlank(configName)) {
            // 名称判重
            QueryWrapper<RpushPlatformConfig> configNameQw = Qw.newInstance(RpushPlatformConfig.class)
                    .eq("client_id", clientId)
                    .eq("platform", platform)
                    .eq("config_name", configName);
            if (configId != null) {
                configNameQw.ne("id", configId);
            }
            RpushPlatformConfig existsConfig = getOne(configNameQw);
            Assert.isTrue(existsConfig == null, "配置名称重复");
        }

        RpushPlatformConfig rpushPlatformConfig;
        // 主表入库
        if (isUpdate) {
            rpushPlatformConfig = getById(configId);
            rpushPlatformConfig.setConfigName(configName);
            updateById(rpushPlatformConfig);
        } else {
            rpushPlatformConfig = new RpushPlatformConfig();
            rpushPlatformConfig.setClientId(clientId);
            rpushPlatformConfig.setConfigName(configName);
            rpushPlatformConfig.setPlatform(platform.name());
            save(rpushPlatformConfig);
        }

        if (config == null) {
            // 只更新主表
            return;
        }

        if ((config.size() <= 0)) {
            // 只更新主表
            return;
        }

        configId = rpushPlatformConfig.getId(); // 重新拿id

        // 先把子表所有值删掉，然后重建
        rpushPlatformConfigValueService.remove(Qw.newInstance(RpushPlatformConfigValue.class).eq("config_id", configId));

        List<RpushPlatformConfigValue> configValues = new ArrayList<>();
        for (Map.Entry<String, String> entry : config.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            RpushPlatformConfigValue configValue = new RpushPlatformConfigValue();
            configValue.setConfigId(configId);
            configValue.setValue(value);
            configValue.setKey(key);
            configValue.setClientId(clientId);
            configValues.add(configValue);
        }
        rpushPlatformConfigValueService.saveBatch(configValues);
    }

    @Override
    public void setDefault(String configId, boolean defaultFlag) {
        if (StringUtils.isBlank(configId)) {
            return;
        }

        String clientId = SessionUtils.getClientId();
        RpushPlatformConfig rpushPlatformConfig = getOne(Qw.newInstance(RpushPlatformConfig.class).eq("id", configId).eq("client_id", clientId));
        if (rpushPlatformConfig == null) {
            return;
        }
        // 维护默认设置
        if (defaultFlag) {
            UpdateWrapper<RpushPlatformConfig> defaultFlagUw = new UpdateWrapper<>();
            defaultFlagUw.set("default_flag", false)
                    .eq("client_id", clientId)
                    .eq("platform", rpushPlatformConfig.getPlatform())
                    .eq("default_flag", true)
                    .ne("id", configId);
            update(defaultFlagUw);
        }
        rpushPlatformConfig.setDefaultFlag(defaultFlag);
        updateById(rpushPlatformConfig);
    }

    @Transactional
    @Override
    public void delete(Long configId) {
        if (configId == null) {
            return;
        }
        String clientId = SessionUtils.getClientId();
        remove(Qw.newInstance(RpushPlatformConfig.class).eq("id", configId).eq("client_id", clientId));
        rpushPlatformConfigValueService.remove(Qw.newInstance(RpushPlatformConfigValue.class).eq("config_id", configId).eq("client_id", clientId));
    }
}
