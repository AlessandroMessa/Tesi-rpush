package com.regent.rpush.route.service.template.batch;

import com.regent.rpush.route.api.batch.ReceiverBatchRequest;

import java.util.List;

public interface IRpushTemplateReceiverBatchService {
    /**
     * 批量插入
     */
    void batchInsert(ReceiverBatchRequest receiverBatchRequest);
}
