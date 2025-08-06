package com.regent.rpush.route.service.config.query.impl;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.regent.rpush.common.PageUtil;
import com.regent.rpush.dto.common.config.ConfigValueType;
import com.regent.rpush.dto.enumration.MessagePlatformEnum;
import com.regent.rpush.dto.message.base.BaseMessage;
import com.regent.rpush.dto.message.config.Config;
import com.regent.rpush.dto.route.config.ConfigFieldVO;
import com.regent.rpush.dto.route.config.ConfigTableDTO;
import com.regent.rpush.dto.table.Pagination;
import com.regent.rpush.route.mapper.RpushPlatformConfigMapper;
import com.regent.rpush.route.model.RpushPlatformConfig;
import com.regent.rpush.route.model.RpushPlatformConfigValue;
import com.regent.rpush.route.model.RpushTemplate;
import com.regent.rpush.route.service.config.value.IRpushPlatformConfigValueService;
import com.regent.rpush.route.service.template.IRpushTemplateService;
import com.regent.rpush.route.service.config.query.IRpushConfigQueryService;
import com.regent.rpush.route.utils.application.message.MessageHandlerUtils;
import com.regent.rpush.route.utils.infrastructure.session.SessionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Service
public class IRpushConfigQueryServiceImpl extends ServiceImpl<RpushPlatformConfigMapper, RpushPlatformConfig> implements IRpushConfigQueryService {

    @Autowired
    private IRpushPlatformConfigValueService rpushPlatformConfigValueService;
    @Autowired
    private IRpushTemplateService rpushTemplateService;

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
        Map<Long, Map<String, Object>> configMap = queryConfig(clientId, configIds); // 键为配置id，值为：具体的配置键值
        List<Config> configs = MessageHandlerUtils.convertConfig(configType, configMap); // 转成具体的配置实体类
        //noinspection unchecked
        return (List<T>) configs;
    }

    @Override
    public <T> List<T> queryConfigOrDefault(BaseMessage message, Class<T> configType, MessagePlatformEnum platform) {
        return queryConfigOrDefault(message.getClientId(), message.getConfigIds(), configType, platform);
    }

    @Override
    public ConfigTableDTO pageConfig(MessagePlatformEnum platform, Long configId, String configName, Integer pageNum, Integer pageSize) {
        // 拿到表头
        List<ConfigFieldVO> configFieldVOS = MessageHandlerUtils.listConfigFieldName(platform);
        ConfigTableDTO tableDTO = new ConfigTableDTO();
        tableDTO.setHeader(configFieldVOS);

        // 分页
        pageNum = PageUtil.getDefaultPageNum(pageNum);
        pageSize = PageUtil.getDefaultPageSize(pageSize);
        Page<RpushPlatformConfig> page = new Page<>(pageNum, pageSize);
        QueryWrapper<RpushPlatformConfig> wrapper = new QueryWrapper<>();
        wrapper.eq("client_id", SessionUtils.getClientId());
        wrapper.eq("platform", platform.name());
        wrapper.like(StringUtils.isNotBlank(configName), "config_name", configName);
        wrapper.eq(configId != null, "id", configId);
        page = (Page<RpushPlatformConfig>) page(page, wrapper);
        List<RpushPlatformConfig> configs = page.getRecords();
        List<Long> configIds = configs.stream().map(RpushPlatformConfig::getId).collect(Collectors.toList());

        // 查具体的配置值
        Map<Long, Map<String, Object>> queryConfig = queryConfig(SessionUtils.getClientId(), configIds);
        Collection<Map<String, Object>> dataList = queryConfig.values();
        for (Map.Entry<Long, Map<String, Object>> entry : queryConfig.entrySet()) {
            entry.getValue().put("configId", entry.getKey());
        }
        Pagination<Map<String, Object>> pagination = tableDTO.getPagination();
        pagination.setPageNum(pageNum);
        pagination.setDataList(new ArrayList<>(dataList));
        pagination.setTotal((int) page.getTotal());

        // 补一下模板名称
        fillTemplateName(tableDTO, configFieldVOS, dataList);

        return tableDTO;
    }

    private void fillTemplateName(ConfigTableDTO tableDTO, List<ConfigFieldVO> configFieldVOS, Collection<Map<String, Object>> dataList) {
        if (configFieldVOS == null || dataList == null || dataList.size() <= 0) {
            return;
        }
        List<ConfigFieldVO> fieldVOS = new ArrayList<>(configFieldVOS);
        String rpushTemplateKey = "";
        int size = fieldVOS.size();
        for (int i = 0; i < size; i++) {
            ConfigFieldVO fieldVO = fieldVOS.get(i);
            ConfigValueType type = fieldVO.getType();
            rpushTemplateKey = fieldVO.getKey();
            if (ConfigValueType.RPUSH_TEMPLATE.equals(type)) {
                fieldVOS.add(i, ConfigFieldVO.builder().key(rpushTemplateKey + "Name").name(fieldVO.getName() + "名称").build());
                i++;
                size++;
            }
        }
        if (StringUtils.isNotBlank(rpushTemplateKey)) {
            String finalRpushTemplateKey = rpushTemplateKey;
            List<Long> templateIds = dataList.stream().map(config -> {
                String templateIdStr = (String) config.get(finalRpushTemplateKey);
                if (!NumberUtil.isNumber(templateIdStr)) {
                    return null;
                }
                return Long.parseLong(templateIdStr);
            }).collect(Collectors.toList());
            templateIds.removeIf(Objects::isNull);
            if (templateIds.size() > 0) {
                Collection<RpushTemplate> rpushTemplates = rpushTemplateService.listByIds(templateIds);
                Map<Long, String> templateNameMap = rpushTemplates.stream().collect(Collectors.toMap(RpushTemplate::getId, RpushTemplate::getTemplateName));
                for (Map<String, Object> config : dataList) {
                    String templateIdStr = String.valueOf(config.get(rpushTemplateKey));
                    if (!NumberUtil.isNumber(templateIdStr)) {
                        continue;
                    }
                    long templateId = Long.parseLong(templateIdStr);
                    config.put(rpushTemplateKey + "Name", templateNameMap.get(templateId));
                }
            }
        }
        tableDTO.setHeader(fieldVOS);
    }


}
