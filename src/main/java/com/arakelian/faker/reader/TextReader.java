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

import static com.arakelian.faker.reader.TextReader.Column.Type.STRING;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.faker.reader.TextReader.Column.Type;
import com.arakelian.jackson.utils.JacksonUtils;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TextReader<T> {
    public enum Format {
        DELIMITED, FIXED_WIDTH;
    }

    @Value.Immutable
    interface Column {
        public enum Type {
            STRING {
                @Override
                public Object parse(final String value) {
                    return value;
                }
            },
            INT {
                @Override
                public Object parse(final String value) {
                    return value.length() != 0 ? Integer.parseInt(value) : null;
                }
            },
            LONG {
                @Override
                public Object parse(final String value) {
                    return value.length() != 0 ? Long.parseLong(value) : null;
                }
            },
            DOUBLE {
                @Override
                public Object parse(final String value) {
                    return value.length() != 0 ? Double.parseDouble(value) : null;
                }
            };

            public abstract Object parse(String value);
        }

        @Value.Default
        public default int getLength() {
            return 0;
        }

        public String getName();

        @Value.Default
        public default Type getType() {
            return Type.STRING;
        }

        @Value.Check
        public default void validate() {
            Preconditions.checkState(!StringUtils.isEmpty(getName()), "name is required");
            Preconditions.checkState(getLength() >= 0, "length must be >= 0");
        }
    }

    private static final String[] EMPTY_COLUMNS = new String[0];

    private static final Logger LOGGER = LoggerFactory.getLogger(TextReader.class);

    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z0-9_]+");

    public static final Pattern COLUMN = Pattern.compile("([a-zA-Z]+)(?:\\(([a-zA-Z]+)(?:,([0-9]+))?\\))?");

    private final URL resource;

    private final Class<T> dataClass;

    private final Map<String, String> properties = Maps.newLinkedHashMap();

    private List<Object[]> rows;

    private List<T> values;

    private Map<String, Column> columns;

    private String[] columnNames;

    private boolean haveColumnWidths;

    private Format format;

    private String delimiter;

    /** Number of lines in the file **/
    private int lineCount;

    public TextReader(final String resourceName, final Class<T> dataClass) {
        this.resource = TextReader.class.getResource(resourceName);
        Preconditions.checkArgument(resource != null, "Resource \"" + resourceName + "\" not found");
        this.dataClass = Preconditions.checkNotNull(dataClass);
    }

    public TextReader(final URL resource, final Class<T> dataClass) {
        this.resource = Preconditions.checkNotNull(resource);
        this.dataClass = Preconditions.checkNotNull(dataClass);
    }

    public Column getColumn(final int index) {
        return columns != null ? columns.get(columnNames[index]) : null;
    }

    public Column getColumn(final String name) {
        return columns != null ? columns.get(name) : null;
    }

    public int getColumnCount() {
        return columns != null ? columns.size() : 0;
    }

    public String[] getColumnNames() {
        return columnNames != null ? columnNames : EMPTY_COLUMNS;
    }

    public Class<T> getDataClass() {
        return dataClass;
    }

    public Double getDouble(final int row, final int column) {
        return getValue(row, column, Double.class);
    }

    public Integer getInt(final int row, final int column) {
        return getValue(row, column, Integer.class);
    }

    public int getLineCount() {
        return lineCount;
    }

    public Long getLong(final int row, final int column) {
        return getValue(row, column, Long.class);
    }

    public String getProperty(final String name) {
        return properties.get(name);
    }

    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    public T getRow(final int row) {
        Preconditions.checkArgument(
                row >= 0 && row < rows.size(),
                "row index '%s' is out of bounds; there are %s row(s)",
                row,
                rows.size());

        return values.get(row);
    }

    public Object[] getRowAsArray(final int row) {
        Preconditions.checkArgument(
                row >= 0 && row < rows.size(),
                "row index '%s' is out of bounds; there are %s row(s)",
                row,
                rows.size());

        return rows != null ? rows.get(row) : null;
    }

    public Map<String, Object> getRowAsMap(final int index) {
        final Object[] data = getRowAsArray(index);

        return toMap(data);
    }

    public int getRowCount() {
        return rows != null ? rows.size() : 0;
    }

    public String getString(final int row, final int column) {
        return getValue(row, column, String.class);
    }

    public <V> V getValue(final int row, final int column, final Class<V> clazz) {
        Preconditions.checkArgument(
                column >= 0 && column < getColumnCount(),
                "column index '%s' is out of bounds; there are %s column(s)",
                column,
                getColumnCount());

        final Object[] data = getRowAsArray(row);
        final Object value = data[column];
        Preconditions.checkState(
                value == null || clazz.isInstance(value),
                "Column '%s' is not %s (row index: %s)",
                columnNames[column],
                clazz.getName(),
                row);
        return clazz.cast(value);
    }

    public void read() throws IOException {
        LOGGER.debug("Reading {}", resource);

        reset();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.openConnection().getInputStream(), Charsets.UTF_8))) {
            lineCount = 0;
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                lineCount++;
                parseLine(line);
            }
        } catch (final IllegalStateException | IllegalArgumentException | IOException e) {
            throw new IOException("Unable to load resource: " + resource, e);
        } finally {
            LOGGER.debug("Loaded {} rows from {}", getRowCount(), resource);
        }
    }

    private void parseColumns(final String value) throws IOException {
        Preconditions.checkState(columns == null, "columns can only be specified once");
        Preconditions.checkState(rows == null, "columns cannot be specified after rows have been ingested");

        columns = Maps.newLinkedHashMap();
        final Matcher matcher = COLUMN.matcher(value);
        while (matcher.find()) {
            final String type = StringUtils.defaultIfEmpty(matcher.group(2), STRING.name());
            final String length = StringUtils.defaultIfEmpty(matcher.group(3), "0");
            try {
                final Column column = ImmutableColumn.builder() //
                        .name(StringUtils.defaultString(matcher.group(1))) //
                        .type(Type.valueOf(type.toUpperCase())) //
                        .length(length != null ? Integer.parseInt(length) : 0) //
                        .build();
                if (column.getLength() != 0) {
                    haveColumnWidths = true;
                }
                columns.put(column.getName(), column);
            } catch (final Exception e) {
                throw new IOException("Unable to parse column specification: " + matcher.group(), e);
            }
        }

        columnNames = columns.keySet().toArray(new String[columns.size()]);
    }

    private void parseComment(final String line) throws IOException {
        final int colon = line.indexOf(':');
        if (colon == -1) {
            return;
        }

        final String name = StringUtils.trimToEmpty(line.substring(1, colon).toLowerCase());
        final String value = StringUtils.trimToEmpty(line.substring(colon + 1));

        if ("columns".equals(name)) {
            parseColumns(value);
        } else if ("format".equals(name)) {
            format = Format.valueOf(value.toUpperCase());
        } else if ("delimiter".equals(name)) {
            delimiter = value;
        } else if (IDENTIFIER.matcher(name).matches()) {
            properties.put(name, StringUtils.trimToEmpty(value));
        }
    }

    private Object[] parseFixedWidth(final String line) {
        final int numColumns = getColumnCount();

        final Object[] data = new Object[numColumns];

        int offset = 0;
        for (int i = 0; i < numColumns; i++) {
            final Column column = getColumn(i);
            final String value;

            final int length = column.getLength();
            if (length != 0) {
                value = StringUtils.substring(line, offset, offset + length);
            } else {
                value = StringUtils.substring(line, offset);
            }

            final String trimmed = StringUtils.trimToEmpty(value);
            data[i] = column.getType().parse(trimmed);
            offset += length;
        }

        return data;
    }

    private void parseLine(final String line) throws IOException {
        if (StringUtils.isEmpty(line)) {
            // skip empty lines
            return;
        }

        if (StringUtils.startsWith(line, "#")) {
            parseComment(line);
            return;
        }

        if (rows == null) {
            preflightChecks();
            rows = Lists.newArrayList();
            values = Lists.newArrayList();
        }

        final Object[] data;
        switch (format) {
        case DELIMITED:
            // FIXME: this is a hack
            data = new Object[] { line };
            break;
        case FIXED_WIDTH:
            data = parseFixedWidth(line);
            break;
        default:
            throw new IllegalStateException("Unsupported format: " + format);
        }

        rows.add(data);
        final T value = convert(data);
        values.add(value);
    }

    private void preflightChecks() throws IOException {
        // make sure we know the format
        if (format == null) {
            if (haveColumnWidths) {
                format = Format.FIXED_WIDTH;
            } else {
                format = Format.DELIMITED;
            }
        }

        // if no columns specified, treat line as single value
        if (columns == null) {
            parseColumns("value(string)");
        }

        // check DELIMITED
        if (format == Format.DELIMITED) {
            Preconditions
                    .checkState(!haveColumnWidths, "Cannot specific column widths for delimited input files");

            if (StringUtils.isEmpty(delimiter)) {
                delimiter = "\t";
            }

            // verify delimiter
            Preconditions.checkArgument(
                    delimiter.length() == 1 || delimiter.length() == 2 && delimiter.charAt(0) == '/',
                    "delimiter must be a single character or escape sequence (\\n, \\t, etc)");
            if (delimiter.charAt(0) == '\\') {
                switch (delimiter.charAt(1)) {
                case 't':
                    delimiter = "\\t";
                    break;
                default:
                    throw new IllegalStateException("Invalid delimiter escape sequence: " + delimiter);
                }
            }
        } else if (format == Format.FIXED_WIDTH) {
            // make sure all columns (except last) have a length
            for (int i = 0, size = columnNames.length - 1; i < size; i++) {
                final Column column = getColumn(i);
                Preconditions.checkState(
                        column.getLength() > 0,
                        "Column width must be specified for \"%s\"",
                        column.getName());
            }
        }

        // logging
        LOGGER.debug("Format: {}", format);
        for (final String name : getColumnNames()) {
            final Column column = getColumn(name);
            LOGGER.debug("Column: {}", column);
        }
    }

    private void reset() {
        properties.clear();
        lineCount = 0;
        rows = null;
        values = null;
        columns = null;
        columnNames = null;
        haveColumnWidths = false;
        format = null;
        delimiter = null;
    }

    protected T convert(final Object[] data) {
        if (dataClass.isAssignableFrom(Object[].class)) {
            return dataClass.cast(data);
        }
        final Map<String, Object> map = toMap(data);
        return JacksonUtils.convertValue(map, dataClass);
    }

    protected Map<String, Object> toMap(final Object[] data) {
        final Map<String, Object> map = Maps.newLinkedHashMap();
        for (int i = 0, size = getColumnCount(); i < size; i++) {
            final Column column = getColumn(i);
            map.put(column.getName(), data[i]);
        }
        return map;
    }
}
