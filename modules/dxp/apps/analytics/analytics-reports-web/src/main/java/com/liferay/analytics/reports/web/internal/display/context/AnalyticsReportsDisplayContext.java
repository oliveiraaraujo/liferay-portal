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

package com.liferay.analytics.reports.web.internal.display.context;

import com.liferay.analytics.reports.info.item.AnalyticsReportsInfoItem;
import com.liferay.analytics.reports.web.internal.constants.AnalyticsReportsPortletKeys;
import com.liferay.analytics.reports.web.internal.data.provider.AnalyticsReportsDataProvider;
import com.liferay.analytics.reports.web.internal.model.TimeSpan;
import com.liferay.analytics.reports.web.internal.model.TrafficSource;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import javax.portlet.RenderResponse;
import javax.portlet.ResourceURL;

/**
 * @author David Arques
 * @author Sarai Díaz
 */
public class AnalyticsReportsDisplayContext {

	public AnalyticsReportsDisplayContext(
		AnalyticsReportsDataProvider analyticsReportsDataProvider,
		AnalyticsReportsInfoItem analyticsReportsInfoItem,
		Object analyticsReportsInfoItemObject, String canonicalURL,
		Portal portal, RenderResponse renderResponse,
		ResourceBundle resourceBundle, ThemeDisplay themeDisplay) {

		_analyticsReportsDataProvider = analyticsReportsDataProvider;
		_analyticsReportsInfoItem = analyticsReportsInfoItem;
		_analyticsReportsInfoItemObject = analyticsReportsInfoItemObject;
		_canonicalURL = canonicalURL;
		_portal = portal;
		_renderResponse = renderResponse;
		_resourceBundle = resourceBundle;

		_themeDisplay = themeDisplay;

		_validAnalyticsConnection =
			_analyticsReportsDataProvider.isValidAnalyticsConnection(
				_themeDisplay.getCompanyId());
	}

	public Map<String, Object> getData() {
		if (_data != null) {
			return _data;
		}

		_data = HashMapBuilder.<String, Object>put(
			"context", _getContext()
		).put(
			"props", getProps()
		).build();

		return _data;
	}

	public String getLiferayAnalyticsURL(long companyId) {
		return PrefsPropsUtil.getString(companyId, "liferayAnalyticsURL");
	}

	protected Map<String, Object> getProps() {
		return HashMapBuilder.<String, Object>put(
			"authorName",
			_analyticsReportsInfoItem.getAuthorName(
				_analyticsReportsInfoItemObject)
		).put(
			"publishDate",
			() -> {
				Date publishDate = _analyticsReportsInfoItem.getPublishDate(
					_analyticsReportsInfoItemObject);

				Layout layout = _themeDisplay.getLayout();

				if (DateUtil.compareTo(publishDate, layout.getPublishDate()) >
						0) {

					return publishDate;
				}

				return layout.getPublishDate();
			}
		).put(
			"title",
			_analyticsReportsInfoItem.getTitle(
				_analyticsReportsInfoItemObject, _themeDisplay.getLocale())
		).put(
			"trafficSources", _getTrafficSourcesJSONArray()
		).build();
	}

	private Map<String, Object> _getContext() {
		return HashMapBuilder.<String, Object>put(
			"defaultTimeSpanKey", TimeSpan.defaultTimeSpanKey()
		).put(
			"endpoints",
			HashMapBuilder.<String, Object>put(
				"getAnalyticsReportsHistoricalReadsURL",
				() -> {
					ResourceURL resourceURL =
						_renderResponse.createResourceURL();

					resourceURL.setResourceID(
						"/analytics_reports/get_historical_reads");

					return resourceURL.toString();
				}
			).put(
				"getAnalyticsReportsHistoricalViewsURL",
				() -> {
					ResourceURL resourceURL =
						_renderResponse.createResourceURL();

					resourceURL.setResourceID(
						"/analytics_reports/get_historical_views");

					return resourceURL.toString();
				}
			).put(
				"getAnalyticsReportsTotalReadsURL",
				() -> {
					ResourceURL resourceURL =
						_renderResponse.createResourceURL();

					resourceURL.setResourceID(
						"/analytics_reports/get_total_reads");

					return resourceURL.toString();
				}
			).put(
				"getAnalyticsReportsTotalViewsURL",
				() -> {
					ResourceURL resourceURL =
						_renderResponse.createResourceURL();

					resourceURL.setResourceID(
						"/analytics_reports/get_total_views");

					return resourceURL.toString();
				}
			).build()
		).put(
			"languageTag",
			() -> {
				Locale locale = _themeDisplay.getLocale();

				return locale.toLanguageTag();
			}
		).put(
			"namespace",
			_portal.getPortletNamespace(
				AnalyticsReportsPortletKeys.ANALYTICS_REPORTS)
		).put(
			"page",
			HashMapBuilder.<String, Object>put(
				"plid",
				() -> {
					Layout layout = _themeDisplay.getLayout();

					return layout.getPlid();
				}
			).build()
		).put(
			"timeSpans", _getTimeSpansJSONArray()
		).put(
			"validAnalyticsConnection", _validAnalyticsConnection
		).build();
	}

	private JSONArray _getTimeSpansJSONArray() {
		JSONArray timeSpansJSONArray = JSONFactoryUtil.createJSONArray();

		Stream<TimeSpan> stream = Arrays.stream(TimeSpan.values());

		stream.sorted(
			Comparator.comparingInt(TimeSpan::getDays)
		).forEach(
			timeSpan -> timeSpansJSONArray.put(
				JSONUtil.put(
					"key", timeSpan.getKey()
				).put(
					"label",
					ResourceBundleUtil.getString(
						_resourceBundle, timeSpan.getKey())
				))
		);

		return timeSpansJSONArray;
	}

	private JSONArray _getTrafficSourcesJSONArray() {
		JSONArray trafficSourcesJSONArray = JSONFactoryUtil.createJSONArray();

		String helpMessage = ResourceBundleUtil.getString(
			_resourceBundle,
			"this-number-refers-to-the-volume-of-people-that-find-your-page-" +
				"through-a-search-engine");

		Map<String, String> titleMap = HashMapBuilder.put(
			"organic", ResourceBundleUtil.getString(_resourceBundle, "organic")
		).put(
			"paid", ResourceBundleUtil.getString(_resourceBundle, "paid")
		).build();

		try {
			List<TrafficSource> trafficSources =
				_analyticsReportsDataProvider.getTrafficSources(
					_themeDisplay.getCompanyId(), _canonicalURL);

			trafficSources.forEach(
				trafficSource -> trafficSourcesJSONArray.put(
					trafficSource.toJSONObject(helpMessage, titleMap)));
		}
		catch (PortalException portalException) {
			_log.error(portalException, portalException);
		}

		return trafficSourcesJSONArray;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsReportsDisplayContext.class);

	private final AnalyticsReportsDataProvider _analyticsReportsDataProvider;
	private final AnalyticsReportsInfoItem _analyticsReportsInfoItem;
	private final Object _analyticsReportsInfoItemObject;
	private final String _canonicalURL;
	private Map<String, Object> _data;
	private final Portal _portal;
	private final RenderResponse _renderResponse;
	private final ResourceBundle _resourceBundle;
	private final ThemeDisplay _themeDisplay;
	private final boolean _validAnalyticsConnection;

}