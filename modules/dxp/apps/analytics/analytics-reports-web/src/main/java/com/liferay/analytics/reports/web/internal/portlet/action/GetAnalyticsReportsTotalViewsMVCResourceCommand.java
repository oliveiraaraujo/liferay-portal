/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.analytics.reports.web.internal.portlet.action;

import com.liferay.analytics.reports.web.internal.constants.AnalyticsReportsPortletKeys;
import com.liferay.analytics.reports.web.internal.data.provider.AnalyticsReportsDataProvider;
import com.liferay.analytics.reports.web.internal.layout.seo.CanonicalURLProvider;
import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ResourceBundle;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sarai Díaz
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + AnalyticsReportsPortletKeys.ANALYTICS_REPORTS,
		"mvc.command.name=/analytics_reports/get_total_views"
	},
	service = MVCResourceCommand.class
)
public class GetAnalyticsReportsTotalViewsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		AnalyticsReportsDataProvider analyticsReportsDataProvider =
			new AnalyticsReportsDataProvider(_http);

		CanonicalURLProvider canonicalURLProvider = new CanonicalURLProvider(
			_portal.getHttpServletRequest(resourceRequest), _language,
			_layoutSEOLinkManager, _portal);

		try {
			JSONObject jsonObject = JSONUtil.put(
				"analyticsReportsTotalViews",
				analyticsReportsDataProvider.getTotalViews(
					_portal.getCompanyId(resourceRequest),
					canonicalURLProvider.getCanonicalURL()));

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse, jsonObject);
		}
		catch (Exception exception) {
			if (_log.isInfoEnabled()) {
				_log.info(exception, exception);
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)resourceRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				themeDisplay.getLocale(), getClass());

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"error",
					ResourceBundleUtil.getString(
						resourceBundle, "an-unexpected-error-occurred")));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetAnalyticsReportsTotalViewsMVCResourceCommand.class);

	@Reference
	private Http _http;

	@Reference
	private Language _language;

	@Reference
	private LayoutSEOLinkManager _layoutSEOLinkManager;

	@Reference
	private Portal _portal;

}