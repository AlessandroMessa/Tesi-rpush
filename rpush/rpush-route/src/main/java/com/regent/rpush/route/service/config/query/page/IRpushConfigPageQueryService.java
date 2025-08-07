package com.regent.rpush.route.service.config.query.page;

import com.regent.rpush.dto.enumration.MessagePlatformEnum;
import com.regent.rpush.dto.route.config.query.table.ConfigTableDTO;

public interface IRpushConfigPageQueryService {
    /**
     * 查询配置分页数据
     *
     * @param platform 平台
     */
    ConfigTableDTO pageConfig(MessagePlatformEnum platform, Long configId, String configName, Integer pageNum, Integer pageSize);
}
