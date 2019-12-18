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

package com.liferay.portal.kernel.service.view.count;

import com.liferay.portal.kernel.exception.PortalException;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Preston Crary
 */
@ProviderType
public interface ViewCountService {

	public void addViewCountEntry(
		long companyId, long classNameId, long classPK);

	public long getViewCount(long companyId, Class<?> clazz, long classPK);

	public long getViewCount(long companyId, long classNameId, long classPK);

	public void incrementViewCount(
		long companyId, Class<?> clazz, long classPK);

	public void incrementViewCount(
		long companyId, Class<?> clazz, long classPK, int increment);

	public void incrementViewCount(
		long companyId, long classNameId, long classPK);

	public void incrementViewCount(
		long companyId, long classNameId, long classPK, int increment);

	public void removeViewCount(long companyId, Class<?> clazz, long classPK)
		throws PortalException;

	public void removeViewCount(long companyId, long classNameId, long classPK)
		throws PortalException;

}