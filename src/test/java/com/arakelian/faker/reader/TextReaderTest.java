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

package com.arakelian.faker.reader;

import static com.arakelian.faker.reader.TextReader.Type.DOUBLE;
import static com.arakelian.faker.reader.TextReader.Type.INT;
import static com.arakelian.faker.reader.TextReader.Type.STRING;

import java.io.IOException;

import org.immutables.value.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class TextReaderTest {
    @Value.Immutable
    @JsonSerialize(as = ImmutableCensusData.class)
    @JsonDeserialize(builder = ImmutableCensusData.Builder.class)
    public interface CensusData {
        public Double getCumulativeFrequency();

        public Double getFrequency();

        public String getName();

        public Integer getRank();
    }

    @Test
    public void testFemaleNames() throws IOException {
        final TextReader<CensusData> reader = new TextReader<>("/com/arakelian/faker/name/female",
                CensusData.class);
        reader.read();

        // verify properties
        Assertions.assertNotNull(reader.getProperty("source"));
        Assertions.assertNotNull(reader.getProperty("url"));

        // verify column names and ordering
        Assertions.assertEquals(4, reader.getColumnCount());
        final String[] columnNames = reader.getColumnNames();
        Assertions.assertEquals("name", columnNames[0]);
        Assertions.assertEquals("frequency", columnNames[1]);
        Assertions.assertEquals("cumulativeFrequency", columnNames[2]);
        Assertions.assertEquals("rank", columnNames[3]);

        // verify column types
        Assertions.assertEquals(STRING, reader.getColumn("name").getType());
        Assertions.assertEquals(DOUBLE, reader.getColumn("frequency").getType());
        Assertions.assertEquals(DOUBLE, reader.getColumn("cumulativeFrequency").getType());
        Assertions.assertEquals(INT, reader.getColumn("rank").getType());

        // verify raw data
        Assertions.assertEquals("MARY", reader.getString(0, 0));
        Assertions.assertEquals(Double.valueOf(2.629), reader.getDouble(0, 1));
        Assertions.assertEquals(Double.valueOf(2.629), reader.getDouble(0, 2));
        Assertions.assertEquals(Integer.valueOf(1), reader.getInt(0, 3));

        // verify conversion to Java bean
        final CensusData row = reader.getRow(0);
        Assertions.assertEquals("MARY", row.getName());
        Assertions.assertEquals(Double.valueOf(2.629), row.getFrequency());
        Assertions.assertEquals(Double.valueOf(2.629), row.getCumulativeFrequency());
        Assertions.assertEquals(Integer.valueOf(1), row.getRank());
    }
}
