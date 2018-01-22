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

package com.arakelian.faker.service;

import com.arakelian.faker.model.Address;
import com.arakelian.faker.model.ImmutableAddress;
import com.google.common.base.Preconditions;

public class RandomAddress extends AbstractRandomService<Address> {
    private static RandomAddress INSTANCE = new RandomAddress();

    public static RandomAddress get() {
        return INSTANCE;
    }

    private final RandomData randomData;

    public RandomAddress() {
        this(RandomData.get());
    }

    public RandomAddress(final RandomData randomData) {
        this.randomData = Preconditions.checkNotNull(randomData);
    }

    @Override
    public Address next() {
        final String streetNumber = Integer.toString(randomData.random().nextInt(1000) + 1);
        final Address address = ImmutableAddress.builder() //
                .street(streetNumber + " " + randomData.nextString("address.ca.sf.street")) //
                .city("SAN FRANCISCO") //
                .state("CA") //
                .postalCode(randomData.nextString("address.ca.sf.zip")) //
                .build();
        return address;
    }
}
