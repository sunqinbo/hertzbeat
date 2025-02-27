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

package org.dromara.hertzbeat.collector.collect.common.cache;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;

/**
 * 缓存key唯一标识符
 * @author tomsun28
 * @date 2021/12/1 21:30
 */
@Data
@Builder
public class CacheIdentifier {

    private String ip;

    private String port;

    private String username;

    private String password;

    @Override
    public String toString() {
        return "CacheIdentifier {" +
                "ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", username+password=>hash='" + Objects.hash(username, password) + '\'' +
                '}';
    }
}
