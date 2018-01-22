/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arakelian.faker.model;

import java.io.Serializable;
import java.time.ZonedDateTime;

import org.immutables.value.Value;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.core.utils.MoreStringUtils;
import com.arakelian.dao.feature.HasId;
import com.arakelian.dao.feature.HasTimestamp;

public abstract class AbstractModel implements HasId, HasTimestamp, Comparable<HasId>, Serializable {
    @Override
    public int compareTo(final HasId o) {
        if (o == null) {
            return +1;
        }
        final String id = getId();
        if (id == null) {
            if (o.getId() == null) {
                return 0;
            }
            return -1;
        }
        return id.compareTo(o.getId());
    }

    @Override
    @Value.Default
    public ZonedDateTime getCreated() {
        return DateUtils.nowWithZoneUtc();
    }

    @Override
    @Value.Default
    public String getId() {
        return MoreStringUtils.shortUuid();
    }

    @Override
    @Value.Default
    public ZonedDateTime getUpdated() {
        return getCreated();
    }
}
