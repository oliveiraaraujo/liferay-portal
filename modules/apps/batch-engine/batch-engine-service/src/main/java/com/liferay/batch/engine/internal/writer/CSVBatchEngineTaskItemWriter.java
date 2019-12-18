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

import com.liferay.petra.io.unsync.UnsyncPrintWriter;
import com.liferay.petra.string.StringUtil;

import java.io.IOException;
import java.io.OutputStream;

import java.lang.reflect.Field;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivica cardic
 */
public class CSVBatchEngineTaskItemWriter implements BatchEngineTaskItemWriter {

	public CSVBatchEngineTaskItemWriter(
		String delimiter, Map<String, Field> fieldMap, List<String> fieldNames,
		OutputStream outputStream) {

		_delimiter = delimiter;
		_fieldMap = fieldMap;
		_fieldNames = new HashSet<>(fieldNames);

		_unsyncPrintWriter = new UnsyncPrintWriter(outputStream);

		_unsyncPrintWriter.println(StringUtil.merge(fieldNames, delimiter));
	}

	@Override
	public void close() throws IOException {
		_unsyncPrintWriter.close();
	}

	@Override
	public void write(Collection<?> items) throws Exception {
		DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSSZ");

		for (Object item : items) {
			_write(
				dateFormat,
				ColumnValuesExtractUtil.extractValues(
					_fieldMap, _fieldNames, item));
		}
	}

	private void _write(DateFormat dateFormat, Collection<?> values) {
		if (values.isEmpty()) {
			return;
		}

		_unsyncPrintWriter.println(
			StringUtil.merge(
				values,
				value -> {
					if (value instanceof Date) {
						return dateFormat.format(value);
					}

					return String.valueOf(value);
				},
				_delimiter));
	}

	private final String _delimiter;
	private final Map<String, Field> _fieldMap;
	private final Set<String> _fieldNames;
	private final UnsyncPrintWriter _unsyncPrintWriter;

}