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

import com.arakelian.faker.model.Gender;
import com.arakelian.faker.model.ImmutablePerson;
import com.arakelian.faker.model.Person;
import com.google.common.base.Preconditions;

public class RandomPerson extends AbstractRandomService<Person> {
    private static RandomPerson INSTANCE = new RandomPerson();

    public static RandomPerson get() {
        return INSTANCE;
    }

    private final RandomData randomData;

    public RandomPerson() {
        this(RandomData.get());
    }

    public RandomPerson(final RandomData randomData) {
        this.randomData = Preconditions.checkNotNull(randomData);
    }

    @Override
    public Person next() {
        final Gender gender = randomData.next(Gender.class);

        final String firstName;
        final String spouseName;
        switch (gender) {
        case FEMALE:
            // TODO: don't assume gender roles
            firstName = randomData.nextString("name.female");
            spouseName = randomData.nextString("name.male");
            break;
        case MALE:
            firstName = randomData.nextString("name.male");
            spouseName = randomData.nextString("name.female");
            break;
        default:
            throw new IllegalStateException("Unknown gender: " + gender);
        }

        final Person person = ImmutablePerson.builder() //
                .firstName(firstName) //
                .lastName(randomData.nextString("name.surname")) //
                .gender(gender) //
                .title(randomData.nextString("job.title")) //
                .birthdate(randomData.nextDate("birthday")) //
                .comments(firstName + " is married to " + spouseName) //
                .build();
        return person;
    }
}
