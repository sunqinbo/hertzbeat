/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hertzbeat.manager.component.alerter.impl;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hertzbeat.common.entity.alerter.Alert;
import org.dromara.hertzbeat.common.entity.manager.NoticeReceiver;
import org.dromara.hertzbeat.manager.support.exception.AlertNoticeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author sunqinbo
 * @since 2023/3/31
 */
@Component
@RequiredArgsConstructor
@Slf4j
final class CatAlertNotifyHandlerImpl extends AbstractAlertNotifyHandlerImpl {
    private static final String SYSTEM_NOTICE = "System";
    private static final String TRANSACTION_NOTICE_TYPE = "Transaction";
    private static final String catNotifyUrl ="http://10.10.250.243:8080/api/v1/weixin?group=hertzbeat";

    @Override
    public void send(NoticeReceiver receiver, Alert alert) {
        try {
            WeCharNotifyDTO content = WeCharNotifyDTO.builder().text(renderContent(alert)).build();
            String url = "";
            // 告警级别 0:高-emergency-紧急告警-红色 1:中-critical-严重告警-橙色 2:低-warning-警告告警-黄色
            if (alert.getPriority() == 0) {
                // 紧急警告
                url = String.format("%s&type=%s&receivers=%s&content=%s", catNotifyUrl, SYSTEM_NOTICE, receiver.getName(), content.getText());
            } else {
                // 警告告警,严重告警
                url = String.format("%s&type=%s&receivers=%s&content=%s", catNotifyUrl, TRANSACTION_NOTICE_TYPE, receiver.getName(), content.getText());
            }
            ResponseEntity responseEntity = restTemplate.postForEntity(url, content, String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
                String body = responseEntity.getBody().toString();
                if (Objects.equals(HttpStatus.OK.value(), Integer.valueOf(body))) {
                    log.debug("Send CatAlertNotify Success; \n{}" , url);
                } else {
                    log.warn("Send CatAlertNotify Failed: {}", body);
                    throw new AlertNoticeException(body);
                }
            } else {
                log.warn("Send CatAlertNotify Failed {}", responseEntity.getBody());
                throw new AlertNoticeException("Http StatusCode " + responseEntity.getStatusCode());
            }

        } catch (Exception e) {
            throw new AlertNoticeException("[CatAlertNotify Error] " + e.getMessage());
        }
    }

    @Override
    public byte type() {
        return 99;
    }

    @Override
    protected String templateName() {
        // 模板名  src/main/resources/templates/alertCatNotify.txt
        return "alertCatNotify";
    }

    @Data
    @Builder
    private static class WeCharNotifyDTO {
        private String text;
    }
}
