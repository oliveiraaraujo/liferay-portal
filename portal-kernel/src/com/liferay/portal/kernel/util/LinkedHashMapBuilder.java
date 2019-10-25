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

package com.liferay.portal.kernel.util;

import java.util.LinkedHashMap;

/**
 * @author Hugo Huijser
 */
public class LinkedHashMapBuilder<K, V> {

	public static <K, V> LinkedHashMapWrapper<K, V> put(K key, V value) {
		return new LinkedHashMapWrapper<>(key, value);
	}

	public static final class LinkedHashMapWrapper<K, V> {

		public LinkedHashMapWrapper(K key, V value) {
			_linkedHashMap.put(key, value);
		}

		public LinkedHashMap<K, V> build() {
			return _linkedHashMap;
		}

		public LinkedHashMapWrapper<K, V> put(K key, V value) {
			_linkedHashMap.put(key, value);

			return this;
		}

		private final LinkedHashMap<K, V> _linkedHashMap =
			new LinkedHashMap<>();

	}

}