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

package com.liferay.app.builder.web.internal.portlet;

import com.liferay.app.builder.web.internal.constants.AppBuilderWebKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.HashMapDictionary;

import java.io.IOException;

import java.util.Dictionary;
import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author Gabriel Albuquerque
 */
public class AppPortlet extends MVCPortlet {

	public static Dictionary<String, Object> getProperties(
		String appName, String portletName,
		Map<String, Object> customProperties) {

		HashMapDictionary<String, Object> properties =
			new HashMapDictionary<String, Object>() {
				{
					put("com.liferay.portlet.add-default-resource", true);
					put(
						"com.liferay.portlet.display-category",
						"category.hidden");
					put(
						"com.liferay.portlet.header-portlet-css",
						"/css/main.css");
					put("com.liferay.portlet.use-default-template", true);
					put("javax.portlet.display-name", appName);
					put("javax.portlet.name", portletName);
					put(
						"javax.portlet.init-param.template-path",
						"/META-INF/resources/");
					put(
						"javax.portlet.init-param.view-template",
						"/view_entries.jsp");
					put(
						"javax.portlet.security-role-ref",
						"administrator,guest,power-user,user");
				}
			};

		properties.putAll(customProperties);

		return properties;
	}

	public AppPortlet(long appId) {
		this(appId, true, true);
	}

	public AppPortlet(long appId, boolean showFormView, boolean showTableView) {
		_appId = appId;
		_showFormView = showFormView;
		_showTableView = showTableView;
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(AppBuilderWebKeys.APP_ID, _appId);
		renderRequest.setAttribute(
			AppBuilderWebKeys.SHOW_FORM_VIEW, _showFormView);
		renderRequest.setAttribute(
			AppBuilderWebKeys.SHOW_TABLE_VIEW, _showTableView);

		super.render(renderRequest, renderResponse);
	}

	private final long _appId;
	private final boolean _showFormView;
	private final boolean _showTableView;

}