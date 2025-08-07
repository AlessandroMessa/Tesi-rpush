package com.regent.rpush.route.service.template.batch.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import com.regent.rpush.dto.enumration.MessagePlatformEnum;
import com.regent.rpush.route.dto.ReceiverBatchInsertDTO;
import com.regent.rpush.route.mapper.RpushTemplateReceiverMapper;
import com.regent.rpush.route.service.template.batch.IRpushTemplateReceiverBatchService;
import com.regent.rpush.route.utils.infrastructure.session.SessionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class IRpushTemplateReceiverBatchServiceImpl implements IRpushTemplateReceiverBatchService {

    @Autowired
    private RpushTemplateReceiverMapper rpushTemplateReceiverMapper;

    @Override
    public void batchInsert(MessagePlatformEnum platform, List<ReceiverBatchInsertDTO> receivers) {
        String clientId = SessionUtils.getClientId();
        String requestNo = UUID.randomUUID().toString().replaceAll("-", "");
        try {
            StringBuilder insertSql = new StringBuilder();
            insertSql.append(" INSERT INTO import_receiver (request_no, platform, receiver_id, receiver_name, group_name, client_id) VALUES ");
            List<String> insertSqlItems = new ArrayList<>(receivers.size());
            for (ReceiverBatchInsertDTO receiver : receivers) {
                if (StringUtils.isBlank(receiver.getReceiverId()) || StringUtils.isBlank(receiver.getReceiverName())) {
                    continue;
                }

                List<String> receiverFields = new ArrayList<>();
                receiverFields.add("'" + requestNo + "'");
                receiverFields.add("'" + platform.name() + "'");
                receiverFields.add("'" + receiver.getReceiverId() + "'");
                receiverFields.add("'" + receiver.getReceiverName() + "'");
                String receiverGroupName = receiver.getReceiverGroupName();
                receiverGroupName = StringUtils.isBlank(receiverGroupName) ? "默认分组" : receiverGroupName;
                receiverFields.add("'" + receiverGroupName + "'");
                receiverFields.add("'" + clientId + "'");
                insertSqlItems.add("(" + CollUtil.join(receiverFields, ",") + ")");
            }
            if (insertSqlItems.size() <= 0) {
                throw new IllegalArgumentException("空数据Excel");
            }
            insertSql.append(CollUtil.join(insertSqlItems, ","));
            insertSql.append(" ON DUPLICATE KEY UPDATE receiver_name = receiver_name;");
            rpushTemplateReceiverMapper.execute(insertSql.toString());

            // 导入分组
            String sqlSb;
            sqlSb = " INSERT INTO rpush_template_receiver_group (platform, group_name, client_id) SELECT\n" +
                    "  *\n" +
                    " FROM\n" +
                    "  (\n" +
                    "   SELECT\n" +
                    "    ir.platform AS platform,\n" +
                    "    ir.group_name AS group_name,\n" +
                    "    ir.client_id AS client_id\n" +
                    "   FROM\n" +
                    "    import_receiver AS ir\n" +
                    "   WHERE\n" +
                    "    ir.request_no = '" + requestNo + "'\n" +
                    "   GROUP BY\n" +
                    "    ir.request_no,\n" +
                    "    ir.platform,\n" +
                    "    ir.group_name\n" +
                    "  ) AS t ON DUPLICATE KEY UPDATE platform = t.platform;\n";
            rpushTemplateReceiverMapper.execute(sqlSb);

            // 导入接收人
            sqlSb = " INSERT INTO rpush_template_receiver (\n" +
                    "  platform,\n" +
                    "  group_id,\n" +
                    "  receiver_id,\n" +
                    "  receiver_name,\n" +
                    "  client_id\n" +
                    " ) SELECT\n" +
                    "  *\n" +
                    " FROM\n" +
                    "  (\n" +
                    "   SELECT\n" +
                    "    ir.platform AS platform,\n" +
                    "    r.id AS group_id,\n" +
                    "    ir.receiver_id AS receiver_id,\n" +
                    "    ir.receiver_name AS receiver_name,\n" +
                    "    ir.client_id AS client_id\n" +
                    "   FROM\n" +
                    "    import_receiver AS ir\n" +
                    "   INNER JOIN rpush_template_receiver_group AS r ON r.platform = ir.platform\n" +
                    "   AND r.group_name = ir.group_name\n" +
                    "   WHERE\n" +
                    "    ir.request_no = '" + requestNo + "'\n" +
                    "  ) AS t ON DUPLICATE KEY UPDATE platform = t.platform;";
            rpushTemplateReceiverMapper.execute(sqlSb);
        } finally {
            rpushTemplateReceiverMapper.execute("delete from import_receiver where request_no = '" + requestNo + "'");
        }
    }
}
