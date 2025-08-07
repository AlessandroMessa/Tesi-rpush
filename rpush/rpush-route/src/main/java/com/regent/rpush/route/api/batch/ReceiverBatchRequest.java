package com.regent.rpush.route.api.batch;


import com.regent.rpush.dto.enumration.MessagePlatformEnum;
import com.regent.rpush.route.dto.ReceiverBatchInsertDTO;
import lombok.Getter;

import java.util.List;
@Getter
public class ReceiverBatchRequest {
    private MessagePlatformEnum platform;
    private List<ReceiverBatchInsertDTO> receivers;

    public ReceiverBatchRequest() {}

    public ReceiverBatchRequest(MessagePlatformEnum platform, List<ReceiverBatchInsertDTO> receivers) {
        this.platform = platform;
        this.receivers = receivers;
    }
}
