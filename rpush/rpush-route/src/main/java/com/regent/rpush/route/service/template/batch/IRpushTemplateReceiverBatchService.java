package com.regent.rpush.route.service.template.batch;

import com.regent.rpush.dto.enumration.MessagePlatformEnum;
import com.regent.rpush.route.dto.ReceiverBatchInsertDTO;

import java.util.List;

public interface IRpushTemplateReceiverBatchService {
    /**
     * 批量插入
     */
    void batchInsert(MessagePlatformEnum platform, List<ReceiverBatchInsertDTO> receivers);
}
