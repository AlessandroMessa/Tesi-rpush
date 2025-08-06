package com.regent.rpush.route.service.command;

import com.baomidou.mybatisplus.extension.service.IService;
import com.regent.rpush.dto.route.config.UpdateConfigDTO;
import com.regent.rpush.route.model.RpushPlatformConfig;

public interface IRpushConfigCommandService extends IService<RpushPlatformConfig> {
    /**
     * 更新配置
     */
    void updateConfig(UpdateConfigDTO updateConfigDTO);

    /**
     * 设为默认
     *
     * @param configId    配置id
     * @param defaultFlag true 或者 false
     */
    void setDefault(String configId, boolean defaultFlag);

    /**
     * 删除某个配置
     *
     * @param configId 配置id
     */
    void delete(Long configId);
}
