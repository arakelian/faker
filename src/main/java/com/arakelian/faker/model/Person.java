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
import java.util.Map;
import java.util.Objects;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.core.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;

@Value.Immutable
@JsonSerialize(as = ImmutablePerson.class)
@JsonDeserialize(builder = ImmutablePerson.Builder.class)
@JsonPropertyOrder({ "id", "firstName", "lastName", "title", "gender", "birthdate", "age", "comments",
        "created", "updated" })
public abstract class Person extends AbstractModel {
    @Override
    public boolean equals(final Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof Person && equalTo((Person) another);
    }

    private boolean equalTo(final Person another) {
        return Objects.equals(getId(), another.getId())
                && Objects.equals(getFirstName(), another.getFirstName())
                && Objects.equals(getLastName(), another.getLastName())
                && Objects.equals(getGender(), another.getGender())
                && Objects.equals(getBirthdate(), another.getBirthdate())
                && Objects.equals(getAge(), another.getAge())
                && Objects.equals(getTitle(), another.getTitle())
                && Objects.equals(getComments(), another.getComments())
                && Objects.equals(getCreated(), another.getCreated())
                && Objects.equals(getUpdated(), another.getUpdated());
    }

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

    @Nullable
    public abstract ZonedDateTime getBirthdate();

    @Nullable
    public abstract String getComments();

    public abstract String getFirstName();

    @Nullable
    public abstract Gender getGender();

    public abstract String getLastName();

    @JsonAnyGetter
    @Value.Default
    public Map<String, Object> getProperties() {
        return ImmutableMap.of();
    }

    @Nullable
    public abstract String getTitle();

    /**
     * Computes a hash code from attributes: {@code age}, {@code birthdate}, {@code comments},
     * {@code firstName}, {@code gender}, {@code lastName}, {@code title}, {@code created},
     * {@code id}, {@code updated}.
     *
     * @return hashCode value
     */
    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(getId());
        h += (h << 5) + Objects.hashCode(getFirstName());
        h += (h << 5) + Objects.hashCode(getLastName());
        h += (h << 5) + Objects.hashCode(getGender());
        h += (h << 5) + Objects.hashCode(getBirthdate());
        h += (h << 5) + Objects.hashCode(getAge());
        h += (h << 5) + Objects.hashCode(getTitle());
        h += (h << 5) + Objects.hashCode(getComments());
        h += (h << 5) + Objects.hashCode(getCreated());
        h += (h << 5) + Objects.hashCode(getUpdated());
        return h;
    }
}
