package com.regent.rpush.route.service.config.query.batch;


import java.util.List;
import java.util.Map;

public interface IRpushConfigBatchQueryService {
    /**
     * 批量查询配置
     *
     * @param configIds 配置id列表，传空会返回空map
     * @return 键为配置id，值为：具体的配置键值
     */
    Map<Long, Map<String, Object>> queryConfig(String clientId, List<Long> configIds);
}
