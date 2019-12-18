/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.batch.engine.internal.writer;

import com.liferay.petra.string.StringPool;

import java.lang.reflect.Field;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * @author Ivica cardic
 */
public class ColumnValueWriter {

	public void write(
			Consumer<Collection<?>> consumer, Map<String, Field> fieldMap,
			List<String> fieldNames, Object item)
		throws IllegalAccessException {

		Map<String, Object> columnNameValueMap = _getColumnNameValueMap(
			item, fieldMap, fieldNames);

		if (!_firstLineWritten) {
			consumer.accept(fieldNames);

			_firstLineWritten = true;
		}

		consumer.accept(columnNameValueMap.values());
	}

	private Map<String, Object> _getColumnNameValueMap(
			Object item, Map<String, Field> fieldMap, List<String> fieldNames)
		throws IllegalAccessException {

		Map<String, Object> columnNameValueMap = new TreeMap<>();

		for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
			String key = entry.getKey();

			Field field = entry.getValue();

			Object value = field.get(item);

			if (value instanceof Map) {
				Map<String, Object> valueMap = (Map<String, Object>)value;

				if (valueMap.isEmpty()) {
					continue;
				}

				key = key.concat(StringPool.UNDERLINE);

				for (Map.Entry<String, Object> valueEntry :
						valueMap.entrySet()) {

					Object innerValue = valueEntry.getValue();

					if (innerValue == null) {
						innerValue = StringPool.BLANK;
					}

					String name = key.concat(valueEntry.getKey());

					if (fieldNames.contains(name)) {
						columnNameValueMap.put(name, innerValue);
					}
				}
			}
			else {
				if (value == null) {
					value = StringPool.BLANK;
				}

				if (fieldNames.contains(key)) {
					columnNameValueMap.put(key, value);
				}
			}
		}

		return columnNameValueMap;
	}

	private boolean _firstLineWritten;

}