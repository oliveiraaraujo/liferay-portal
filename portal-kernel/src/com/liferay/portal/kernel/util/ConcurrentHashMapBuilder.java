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

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hugo Huijser
 */
public class ConcurrentHashMapBuilder<K, V> {

	public static <K, V> ConcurrentHashMapWrapper<K, V> put(K key, V value) {
		return new ConcurrentHashMapWrapper<>(key, value);
	}

	public static final class ConcurrentHashMapWrapper<K, V> {

		public ConcurrentHashMapWrapper(K key, V value) {
			_concurrentHashMap.put(key, value);
		}

		public ConcurrentHashMap<K, V> build() {
			return _concurrentHashMap;
		}

		public ConcurrentHashMapWrapper<K, V> put(K key, V value) {
			_concurrentHashMap.put(key, value);

			return this;
		}

		private final ConcurrentHashMap<K, V> _concurrentHashMap =
			new ConcurrentHashMap<>();

	}

}