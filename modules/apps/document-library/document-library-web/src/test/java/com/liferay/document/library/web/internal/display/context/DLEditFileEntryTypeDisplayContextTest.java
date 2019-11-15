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

package com.liferay.document.library.web.internal.display.context;

import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Cristina González
 */
public class DLEditFileEntryTypeDisplayContextTest {

	@Test
	public void test() {
		DLEditFileEntryTypeDisplayContext dlEditFileEntryTypeDisplayContext =
			new DLEditFileEntryTypeDisplayContext(null, null);

		Assert.assertEquals(
		"Liferay.FormBuilder.AVAILABLE_FIELDS.DDM_STRUCTURE",
			dlEditFileEntryTypeDisplayContext.getAvailableFields());
	}

}
