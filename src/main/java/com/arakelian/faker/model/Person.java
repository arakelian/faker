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

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.core.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutablePerson.class)
@JsonDeserialize(builder = ImmutablePerson.Builder.class)
@JsonPropertyOrder({ "id", "firstName", "lastName", "title", "gender", "birthdate", "age", "comments",
        "created", "updated" })
public abstract class Person extends AbstractModel {
    @Nullable
    @Value.Default
    public Integer getAge() {
        final ZonedDateTime dob = getBirthdate();
        if (dob == null) {
            return null;
        }
        final long years = DateUtils.timeBetween(DateUtils.nowWithZoneUtc(), dob, ChronoUnit.YEARS);
        return (int) years;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another)
            return true;
        return another instanceof Person && equalTo((Person) another);
    }

    private boolean equalTo(Person another) {
        return Objects.equals(getAge(), another.getAge())
                && Objects.equals(getBirthdate(), another.getBirthdate())
                && Objects.equals(getComments(), another.getComments())
                && Objects.equals(getFirstName(), another.getFirstName())
                && Objects.equals(getGender(), another.getGender())
                && Objects.equals(getLastName(), another.getLastName())
                && Objects.equals(getTitle(), another.getTitle())
                && Objects.equals(getCreated(), another.getCreated())
                && Objects.equals(getId(), another.getId())
                && Objects.equals(getUpdated(), another.getUpdated());
    }

    @Nullable
    public abstract ZonedDateTime getBirthdate();

    @Nullable
    public abstract String getComments();

    public abstract String getFirstName();

    @Nullable
    public abstract Gender getGender();

    public abstract String getLastName();

    @Nullable
    public abstract String getTitle();
}
