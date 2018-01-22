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

import static com.arakelian.faker.reader.TextReader.Column.Type.DOUBLE;
import static com.arakelian.faker.reader.TextReader.Column.Type.INT;
import static com.arakelian.faker.reader.TextReader.Column.Type.STRING;

import java.io.IOException;

import org.immutables.value.Value;
import org.junit.Assert;
import org.junit.Test;

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
        final TextReader<CensusData> reader = new TextReader<>("/faker/name/female", CensusData.class);
        reader.read();

        // verify properties
        Assert.assertNotNull(reader.getProperty("source"));
        Assert.assertNotNull(reader.getProperty("url"));

        // verify column names and ordering
        Assert.assertEquals(4, reader.getColumnCount());
        final String[] columnNames = reader.getColumnNames();
        Assert.assertEquals("name", columnNames[0]);
        Assert.assertEquals("frequency", columnNames[1]);
        Assert.assertEquals("cumulativeFrequency", columnNames[2]);
        Assert.assertEquals("rank", columnNames[3]);

        // verify column types
        Assert.assertEquals(STRING, reader.getColumn("name").getType());
        Assert.assertEquals(DOUBLE, reader.getColumn("frequency").getType());
        Assert.assertEquals(DOUBLE, reader.getColumn("cumulativeFrequency").getType());
        Assert.assertEquals(INT, reader.getColumn("rank").getType());

        // verify raw data
        Assert.assertEquals("MARY", reader.getString(0, 0));
        Assert.assertEquals(Double.valueOf(2.629), reader.getDouble(0, 1));
        Assert.assertEquals(Double.valueOf(2.629), reader.getDouble(0, 2));
        Assert.assertEquals(Integer.valueOf(1), reader.getInt(0, 3));

        // verify conversion to Java bean
        final CensusData row = reader.getRow(0);
        Assert.assertEquals("MARY", row.getName());
        Assert.assertEquals(Double.valueOf(2.629), row.getFrequency());
        Assert.assertEquals(Double.valueOf(2.629), row.getCumulativeFrequency());
        Assert.assertEquals(Integer.valueOf(1), row.getRank());
    }
}
