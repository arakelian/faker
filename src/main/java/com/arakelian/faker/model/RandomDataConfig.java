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

import java.time.Month;
import java.time.ZonedDateTime;
import java.util.Random;

import org.immutables.value.Value;

import com.arakelian.core.utils.DateUtils;

@Value.Immutable
public interface RandomDataConfig {
    @Value.Default
    @Value.Auxiliary
    public default ZonedDateTime getFromBirthday() {
        return DateUtils.toZonedDateTimeUtc(1950, Month.JANUARY, 1);
    }

    @Value.Default
    @Value.Auxiliary
    public default Random getRandom() {
        return new Random(0);
    }

    @Value.Default
    @Value.Auxiliary
    public default ZonedDateTime getToBirthday() {
        return DateUtils.toZonedDateTimeUtc(2004, Month.DECEMBER, 31);
    }
}
