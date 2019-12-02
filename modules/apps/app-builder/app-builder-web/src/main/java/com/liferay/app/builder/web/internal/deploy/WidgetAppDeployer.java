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

package com.liferay.app.builder.web.internal.deploy;

import com.liferay.app.builder.constants.AppBuilderAppConstants;
import com.liferay.app.builder.deploy.AppDeployer;
import com.liferay.app.builder.model.AppBuilderApp;
import com.liferay.app.builder.service.AppBuilderAppLocalService;
import com.liferay.app.builder.web.internal.constants.AppBuilderPortletKeys;
import com.liferay.app.builder.web.internal.portlet.AppPortlet;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;

import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.Portlet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Albuquerque
 */
@Component(
	immediate = true, property = "app.builder.deploy.type=widget",
	service = AppDeployer.class
)
public class WidgetAppDeployer implements AppDeployer {

	@Override
	public void deploy(long appId) throws Exception {
		AppBuilderApp appBuilderApp =
			_appBuilderAppLocalService.getAppBuilderApp(appId);

		StringBundler formAppNameSB = new StringBundler(5);

		formAppNameSB.append(
			appBuilderApp.getName(LocaleThreadLocal.getDefaultLocale()));
		formAppNameSB.append(StringPool.SPACE);
		formAppNameSB.append(StringPool.OPEN_PARENTHESIS);
		formAppNameSB.append("Form View");
		formAppNameSB.append(StringPool.CLOSE_PARENTHESIS);

		StringBundler formPortletNameSB = new StringBundler(4);

		formPortletNameSB.append(AppBuilderPortletKeys.WIDGET_APP);
		formPortletNameSB.append(StringPool.UNDERLINE);
		formPortletNameSB.append(appId);
		formPortletNameSB.append("form");

		StringBundler tableAppNameSB = new StringBundler(5);

		tableAppNameSB.append(
			appBuilderApp.getName(LocaleThreadLocal.getDefaultLocale()));
		tableAppNameSB.append(StringPool.SPACE);
		tableAppNameSB.append(StringPool.OPEN_PARENTHESIS);
		tableAppNameSB.append("Table View");
		tableAppNameSB.append(StringPool.CLOSE_PARENTHESIS);

		StringBundler tablePortletNameSB = new StringBundler(4);

		tablePortletNameSB.append(AppBuilderPortletKeys.WIDGET_APP);
		tablePortletNameSB.append(StringPool.UNDERLINE);
		tablePortletNameSB.append(appId);
		tablePortletNameSB.append("table");

		_serviceRegistrationsMap.computeIfAbsent(
			appId,
			key -> new ServiceRegistration[] {
				_deployPortlet(
					appId,
					appBuilderApp.getName(LocaleThreadLocal.getDefaultLocale()),
					AppBuilderPortletKeys.WIDGET_APP + "_" + appId, true, true),
				_deployPortlet(
					appId, formAppNameSB.toString(),
					formPortletNameSB.toString(), true, false),
				_deployPortlet(
					appId, tableAppNameSB.toString(),
					tablePortletNameSB.toString(), false, true)
			});

		appBuilderApp.setStatus(
			AppBuilderAppConstants.Status.DEPLOYED.getValue());

		_appBuilderAppLocalService.updateAppBuilderApp(appBuilderApp);
	}

	@Override
	public void undeploy(long appId) throws Exception {
		ServiceRegistration<?>[] serviceRegistrations =
			_serviceRegistrationsMap.remove(appId);

		if (serviceRegistrations == null) {
			return;
		}

		for (ServiceRegistration serviceRegistration : serviceRegistrations) {
			serviceRegistration.unregister();
		}

		AppBuilderApp appBuilderApp =
			_appBuilderAppLocalService.getAppBuilderApp(appId);

		appBuilderApp.setStatus(
			AppBuilderAppConstants.Status.UNDEPLOYED.getValue());

		_appBuilderAppLocalService.updateAppBuilderApp(appBuilderApp);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	private ServiceRegistration<?> _deployPortlet(
		long appId, String appName, String portletName, boolean showFormView,
		boolean showTableView) {

		return _bundleContext.registerService(
			Portlet.class, new AppPortlet(appId, showFormView, showTableView),
			AppPortlet.getProperties(
				appName, portletName,
				HashMapBuilder.<String, Object>put(
					"com.liferay.portlet.display-category",
					"category.appbuilder"
				).build()));
	}

	@Reference
	private AppBuilderAppLocalService _appBuilderAppLocalService;

	private BundleContext _bundleContext;
	private final ConcurrentHashMap<Long, ServiceRegistration<?>[]>
		_serviceRegistrationsMap = new ConcurrentHashMap<>();

}