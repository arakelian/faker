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

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.jackson.model.GeoPoint;
import com.arakelian.jackson.utils.JacksonTestUtils;

public class PersonTest {
    public static final Person SAMPLE = ImmutablePerson.builder() //
            .firstName("Greg") //
            .lastName("Arakelian") //
            .putProperty("favoriteColor", "blue") //
            .putProperty("favoritePlace", GeoPoint.of("37.773972,-122.431297")) //
            .build();

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(SAMPLE, Person.class);
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(SAMPLE, Person.class);
    }
}
