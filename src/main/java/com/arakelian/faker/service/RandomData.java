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

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.StringUtils;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.faker.model.ImmutableRandomDataConfig;
import com.arakelian.faker.model.RandomDataConfig;
import com.arakelian.faker.reader.TextReader;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class RandomData {
    public enum Capitalization {
        LOWER, UPPER, TITLE;
    }

    private static RandomData INSTANCE = new RandomData(ImmutableRandomDataConfig.builder().build());

    public static RandomData get() {
        return INSTANCE;
    }

    /**
     * Data cache
     */
    private LoadingCache<String, TextReader<?>> cache;

    private final RandomDataConfig config;

    public RandomData(final RandomDataConfig config) {
        this.config = Preconditions.checkNotNull(config);

        final CacheLoader<String, TextReader<?>> loader = new CacheLoader<String, TextReader<?>>() {
            @Override
            public TextReader<?> load(final String key) throws IOException {
                final TextReader<?> reader = new TextReader<>(key, Object[].class);
                reader.read();
                return reader;
            }
        };

        cache = CacheBuilder.newBuilder().build(loader);
    }

    public TextReader<Object[]> get(final String name) {
        return get(name, Object[].class);
    }

    public <T extends Enum> T next(final Class<T> enumClass) {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        return next(enumClass, random);
    }

    public <T extends Enum> T next(final Class<T> enumClass, final Random random) {
        final T[] values = enumClass.getEnumConstants();
        final int size = values.length;
        final int index = random.nextInt(size);
        return values[index];
    }

    public Object[] next(final String name) {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        return next(name, random);
    }

    public <T> T next(final String name, final Class<T> clazz) {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        return next(name, random, clazz);
    }

    public Object[] next(final String name, final Random random) {
        final TextReader<?> reader = get(name);
        final int size = reader.getRowCount();
        final int index = random.nextInt(size);
        return reader.getRowAsArray(index);
    }

    public <T> T next(final String name, final Random random, final Class<T> clazz) {
        final TextReader<T> reader = get(name, clazz);
        final int size = reader.getRowCount();
        final int index = random.nextInt(size);
        return reader.getRow(index);
    }

    public ZonedDateTime nextDate(@SuppressWarnings("unused") final String name) {
        final ZonedDateTime from = config.getFromBirthday();
        final ZonedDateTime to = config.getToBirthday();
        final ZonedDateTime data = DateUtils.randomZonedDateTimeUtc(from, to);
        return data;
    }

    public int nextInt(final int min, final int maxInclusive) {
        return min + random().nextInt(maxInclusive - min + 1);
    }

    public String nextParagraphs(final String name, final int min, final int max) {
        final StringBuilder buf = new StringBuilder();

        final int paragraphs = nextInt(min, max);
        for (int p = 0; p < paragraphs; p++) {
            if (p != 0) {
                buf.append('\n');
            }
            final int sentences = nextInt(2, 6);
            for (int s = 0; s < sentences; s++) {
                if (s != 0) {
                    buf.append("  ");
                }
                buf.append(nextWord(name, Capitalization.TITLE));
                final int words = nextInt(2, 20);
                for (int w = 0; w < words; w++) {
                    buf.append(' ').append(nextWord(name, Capitalization.LOWER));
                }
                buf.append(".");
            }
        }
        return buf.toString();
    }

    public String nextString(final String name) {
        final Object[] data = next(name);
        return Objects.toString(data[0], null);
    }

    public String nextWord(final String name, final Capitalization capitalization) {
        final String word = nextString(name);

        switch (Preconditions.checkNotNull(capitalization, "capitalization must be non-null")) {
        case TITLE:
            return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
        case UPPER:
            return word.toUpperCase();
        case LOWER:
        default:
            return word.toLowerCase();
        }
    }

    public Random random() {
        return ThreadLocalRandom.current();
    }

    @SuppressWarnings("unchecked")
    private <T> TextReader<T> get(final String name, final Class<T> clazz) {
        String resourceName = StringUtils.replace(name, ".", "/");
        if (!StringUtils.startsWith(resourceName, "/")) {
            resourceName = "/com/arakelian/faker/" + resourceName;
        }

        final TextReader<?> reader = cache.getUnchecked(resourceName);
        final Class<?> dataClass = reader.getDataClass();
        Preconditions.checkState(clazz.isAssignableFrom(dataClass));
        return (TextReader<T>) reader;
    }
}
