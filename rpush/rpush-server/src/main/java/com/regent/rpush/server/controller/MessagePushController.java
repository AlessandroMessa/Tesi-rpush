package com.regent.rpush.server.controller;

import com.regent.rpush.api.server.MessagePushService;
import com.regent.rpush.dto.ApiResult;
import com.regent.rpush.dto.StatusCode;
import com.regent.rpush.dto.message.NormalMessageDTO;
import com.regent.rpush.server.socket.RpushClient;
import com.regent.rpush.server.socket.session.SocketSession;
import com.regent.rpush.server.socket.session.service.retrieval.SessionRetrievalService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class MessagePushController implements MessagePushService {

    @Autowired
    private SessionRetrievalService sessionRetrievalService;

    @ApiOperation(value = "发消息")
    public ApiResult<String> push(@Valid NormalMessageDTO message) {
        SocketSession socketSession = sessionRetrievalService.getByRegistrationId(message.getSendTo());
        if (socketSession == null) {
            return ApiResult.of(StatusCode.FAILURE, "该设备未上线" + message.getSendTo());
        }

        RpushClient client = socketSession.getClient();
        if (client == null) {
            return ApiResult.of(StatusCode.FAILURE, "该设备未上线" + message.getSendTo());
        }
        client.pushMessage(message);
        return ApiResult.of("消息发送成功");
    }

}
