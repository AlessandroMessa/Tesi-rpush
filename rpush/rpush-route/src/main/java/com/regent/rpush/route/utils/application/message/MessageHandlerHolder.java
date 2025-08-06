package com.regent.rpush.route.utils.application.message;

import com.regent.rpush.dto.enumration.MessageType;
import com.regent.rpush.route.spi.IMessageHandlerDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 钟宝林
 * @since 2021/3/6/006 17:28
 **/
@Component
public class MessageHandlerHolder implements CommandLineRunner {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 系统所有的消息处理器
     */
    private static final Map<MessageType, IMessageHandlerDescriptor> HANDLER_MAP = new LinkedHashMap<>();

    public static IMessageHandlerDescriptor get(MessageType messageType) {
        return HANDLER_MAP.get(messageType);
    }

    public static Collection<IMessageHandlerDescriptor> values() {
        return HANDLER_MAP.values();
    }

    @Override
    public void run(String... args) throws Exception {
        Map<String, IMessageHandlerDescriptor> springMap = applicationContext.getBeansOfType(IMessageHandlerDescriptor.class);
        for (IMessageHandlerDescriptor messageHandler : springMap.values()) {
            MessageType messageType = messageHandler.messageType();
            if (HANDLER_MAP.containsKey(messageType)) {
                // 一种消息平台只接受一个消息处理器（如果接受多个会有处理器执行的顺序问题，会变复杂，暂时不处理这种情况）
                throw new IllegalStateException("存在重复消息处理器");
            }
            HANDLER_MAP.put(messageType, messageHandler);
        }
    }
}
