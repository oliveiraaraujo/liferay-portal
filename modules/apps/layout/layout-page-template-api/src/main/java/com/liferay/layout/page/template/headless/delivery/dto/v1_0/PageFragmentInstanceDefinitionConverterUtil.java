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

package com.liferay.layout.page.template.headless.delivery.dto.v1_0;

import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.fragment.contributor.FragmentCollectionContributorTracker;
import com.liferay.fragment.entry.processor.util.EditableFragmentEntryProcessorUtil;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererTracker;
import com.liferay.fragment.renderer.constants.FragmentRendererConstants;
import com.liferay.fragment.service.FragmentCollectionLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLocalServiceUtil;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.headless.delivery.dto.v1_0.Fragment;
import com.liferay.headless.delivery.dto.v1_0.FragmentField;
import com.liferay.headless.delivery.dto.v1_0.FragmentFieldBackgroundImage;
import com.liferay.headless.delivery.dto.v1_0.FragmentFieldHTML;
import com.liferay.headless.delivery.dto.v1_0.FragmentFieldImage;
import com.liferay.headless.delivery.dto.v1_0.FragmentFieldText;
import com.liferay.headless.delivery.dto.v1_0.FragmentImage;
import com.liferay.headless.delivery.dto.v1_0.FragmentInlineValue;
import com.liferay.headless.delivery.dto.v1_0.FragmentLink;
import com.liferay.headless.delivery.dto.v1_0.FragmentMappedValue;
import com.liferay.headless.delivery.dto.v1_0.Mapping;
import com.liferay.headless.delivery.dto.v1_0.PageFragmentInstanceDefinition;
import com.liferay.info.display.contributor.InfoDisplayContributor;
import com.liferay.info.display.contributor.InfoDisplayContributorTracker;
import com.liferay.info.display.contributor.InfoDisplayObjectProvider;
import com.liferay.layout.util.structure.FragmentLayoutStructureItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Rubén Pulido
 */
public class PageFragmentInstanceDefinitionConverterUtil {

	public static PageFragmentInstanceDefinition
		toPageFragmentInstanceDefinition(
			FragmentCollectionContributorTracker
				fragmentCollectionContributorTracker,
			FragmentEntryConfigurationParser fragmentEntryConfigurationParser,
			FragmentLayoutStructureItem fragmentLayoutStructureItem,
			FragmentRendererTracker fragmentRendererTracker,
			InfoDisplayContributorTracker infoDisplayContributorTracker,
			boolean saveInlineContent, boolean saveMapping) {

		return toPageFragmentInstanceDefinition(
			fragmentCollectionContributorTracker,
			fragmentEntryConfigurationParser, fragmentLayoutStructureItem,
			fragmentRendererTracker, infoDisplayContributorTracker,
			saveInlineContent, saveMapping, 0);
	}

	public static PageFragmentInstanceDefinition
		toPageFragmentInstanceDefinition(
			FragmentCollectionContributorTracker
				fragmentCollectionContributorTracker,
			FragmentEntryConfigurationParser fragmentEntryConfigurationParser,
			FragmentLayoutStructureItem fragmentLayoutStructureItem,
			FragmentRendererTracker fragmentRendererTracker,
			InfoDisplayContributorTracker infoDisplayContributorTracker,
			boolean saveInlineContent, boolean saveMapping,
			long segmentsExperienceId) {

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryLinkLocalServiceUtil.fetchFragmentEntryLink(
				fragmentLayoutStructureItem.getFragmentEntryLinkId());

		if (fragmentEntryLink == null) {
			return null;
		}

		String rendererKey = fragmentEntryLink.getRendererKey();

		FragmentEntry fragmentEntry = _getFragmentEntry(
			fragmentCollectionContributorTracker,
			fragmentEntryLink.getFragmentEntryId(), rendererKey);

		return new PageFragmentInstanceDefinition() {
			{
				fragment = new Fragment() {
					{
						collectionName = _getFragmentCollectionName(
							fragmentCollectionContributorTracker, fragmentEntry,
							fragmentRendererTracker, rendererKey);
						key = _getFragmentKey(fragmentEntry, rendererKey);
						name = _getFragmentName(
							fragmentEntry, fragmentEntryLink,
							fragmentRendererTracker, rendererKey);
					}
				};
				fragmentConfig = _getFragmentConfig(
					fragmentEntryConfigurationParser, fragmentEntryLink);
				fragmentFields = _getFragmentFields(
					fragmentEntryLink, infoDisplayContributorTracker,
					saveInlineContent, saveMapping, segmentsExperienceId);
			}
		};
	}

	public static PageFragmentInstanceDefinition
		toPageFragmentInstanceDefinition(
			FragmentCollectionContributorTracker
				fragmentCollectionContributorTracker,
			FragmentEntryConfigurationParser fragmentEntryConfigurationParser,
			FragmentLayoutStructureItem fragmentLayoutStructureItem,
			InfoDisplayContributorTracker infoDisplayContributorTracker,
			FragmentRendererTracker fragmentRendererTracker) {

		return toPageFragmentInstanceDefinition(
			fragmentCollectionContributorTracker,
			fragmentEntryConfigurationParser, fragmentLayoutStructureItem,
			fragmentRendererTracker, infoDisplayContributorTracker, true, true);
	}

	private static List<String> _getAvailableLanguageIds() {
		Set<Locale> availableLocales = LanguageUtil.getAvailableLocales();

		Stream<Locale> stream = availableLocales.stream();

		return stream.map(
			availableLocale -> LanguageUtil.getLanguageId(availableLocale)
		).collect(
			Collectors.toList()
		);
	}

	private static List<FragmentField> _getBackgroundImageFragmentFields(
		InfoDisplayContributorTracker infoDisplayContributorTracker,
		JSONObject jsonObject, boolean saveMapping, long segmentsExperienceId) {

		List<FragmentField> fragmentFields = new ArrayList<>();

		Set<String> backgroundImageIds = jsonObject.keySet();

		for (String backgroundImageId : backgroundImageIds) {
			JSONObject imageJSONObject = jsonObject.getJSONObject(
				backgroundImageId);

			Map<String, String> localeMap = _toLocaleMap(
				imageJSONObject, segmentsExperienceId);

			fragmentFields.add(
				new FragmentField() {
					{
						id = backgroundImageId;
						value = _toFragmentFieldBackgroundImage(
							infoDisplayContributorTracker, imageJSONObject,
							localeMap, saveMapping);
					}
				});
		}

		return fragmentFields;
	}

	private static String _getFragmentCollectionName(
		FragmentCollectionContributorTracker
			fragmentCollectionContributorTracker,
		FragmentEntry fragmentEntry,
		FragmentRendererTracker fragmentRendererTracker, String rendererKey) {

		if (fragmentEntry == null) {
			if (Validator.isNull(rendererKey)) {
				rendererKey =
					FragmentRendererConstants.
						FRAGMENT_ENTRY_FRAGMENT_RENDERER_KEY;
			}

			FragmentRenderer fragmentRenderer =
				fragmentRendererTracker.getFragmentRenderer(rendererKey);

			return LanguageUtil.get(
				ResourceBundleUtil.getBundle(
					LocaleUtil.getSiteDefault(), fragmentRenderer.getClass()),
				"fragment.collection.label." +
					fragmentRenderer.getCollectionKey());
		}

		FragmentCollection fragmentCollection =
			FragmentCollectionLocalServiceUtil.fetchFragmentCollection(
				fragmentEntry.getFragmentCollectionId());

		if (fragmentCollection != null) {
			return fragmentCollection.getName();
		}

		String[] parts = StringUtil.split(rendererKey, StringPool.DASH);

		if (ArrayUtil.isEmpty(parts)) {
			return null;
		}

		List<FragmentCollectionContributor> fragmentCollectionContributors =
			fragmentCollectionContributorTracker.
				getFragmentCollectionContributors();

		for (FragmentCollectionContributor fragmentCollectionContributor :
				fragmentCollectionContributors) {

			if (Objects.equals(
					fragmentCollectionContributor.getFragmentCollectionKey(),
					parts[0])) {

				return fragmentCollectionContributor.getName();
			}
		}

		return null;
	}

	private static Map<String, Object> _getFragmentConfig(
		FragmentEntryConfigurationParser fragmentEntryConfigurationParser,
		FragmentEntryLink fragmentEntryLink) {

		try {
			return new HashMap<String, Object>() {
				{
					JSONObject jsonObject =
						fragmentEntryConfigurationParser.
							getConfigurationJSONObject(
								fragmentEntryLink.getConfiguration(),
								fragmentEntryLink.getEditableValues(),
								new long[] {0L});

					Set<String> keys = jsonObject.keySet();

					Iterator<String> iterator = keys.iterator();

					while (iterator.hasNext()) {
						String key = iterator.next();

						Object value = jsonObject.get(key);

						if (value instanceof JSONObject) {
							JSONObject valueJSONObject = (JSONObject)value;

							value = valueJSONObject.getString("color");
						}

						put(key, value);
					}
				}
			};
		}
		catch (JSONException jsonException) {
			return null;
		}
	}

	private static FragmentEntry _getFragmentEntry(
		FragmentCollectionContributorTracker
			fragmentCollectionContributorTracker,
		long fragmentEntryId, String rendererKey) {

		FragmentEntry fragmentEntry =
			FragmentEntryLocalServiceUtil.fetchFragmentEntry(fragmentEntryId);

		if (fragmentEntry != null) {
			return fragmentEntry;
		}

		Map<String, FragmentEntry> fragmentEntries =
			fragmentCollectionContributorTracker.getFragmentEntries();

		return fragmentEntries.get(rendererKey);
	}

	private static FragmentField[] _getFragmentFields(
		FragmentEntryLink fragmentEntryLink,
		InfoDisplayContributorTracker infoDisplayContributorTracker,
		boolean saveInlineContent, boolean saveMapping,
		long segmentsExperienceId) {

		if (!saveInlineContent && !saveMapping) {
			return new FragmentField[0];
		}

		JSONObject editableValuesJSONObject = null;

		try {
			editableValuesJSONObject = JSONFactoryUtil.createJSONObject(
				fragmentEntryLink.getEditableValues());
		}
		catch (JSONException jsonException) {
			return null;
		}

		List<FragmentField> fragmentFields = new ArrayList<>();

		fragmentFields.addAll(
			_getBackgroundImageFragmentFields(
				infoDisplayContributorTracker,
				editableValuesJSONObject.getJSONObject(
					"com.liferay.fragment.entry.processor.background.image." +
						"BackgroundImageFragmentEntryProcessor"),
				saveMapping, segmentsExperienceId));

		Map<String, String> editableTypes =
			EditableFragmentEntryProcessorUtil.getEditableTypes(
				fragmentEntryLink.getHtml());

		fragmentFields.addAll(
			_getTextFragmentFields(
				editableTypes, infoDisplayContributorTracker,
				editableValuesJSONObject.getJSONObject(
					"com.liferay.fragment.entry.processor.editable." +
						"EditableFragmentEntryProcessor"),
				saveMapping, segmentsExperienceId));

		return fragmentFields.toArray(new FragmentField[0]);
	}

	private static String _getFragmentKey(
		FragmentEntry fragmentEntry, String rendererKey) {

		if (fragmentEntry != null) {
			return fragmentEntry.getFragmentEntryKey();
		}

		return rendererKey;
	}

	private static String _getFragmentName(
		FragmentEntry fragmentEntry, FragmentEntryLink fragmentEntryLink,
		FragmentRendererTracker fragmentRendererTracker, String rendererKey) {

		if (fragmentEntry != null) {
			return fragmentEntry.getName();
		}

		JSONObject editableValuesJSONObject = null;

		try {
			editableValuesJSONObject = JSONFactoryUtil.createJSONObject(
				fragmentEntryLink.getEditableValues());
		}
		catch (JSONException jsonException) {
			return null;
		}

		String portletId = editableValuesJSONObject.getString("portletId");

		if (Validator.isNotNull(portletId)) {
			return PortalUtil.getPortletTitle(
				portletId, LocaleUtil.getSiteDefault());
		}

		if (Validator.isNull(rendererKey)) {
			rendererKey =
				FragmentRendererConstants.FRAGMENT_ENTRY_FRAGMENT_RENDERER_KEY;
		}

		FragmentRenderer fragmentRenderer =
			fragmentRendererTracker.getFragmentRenderer(rendererKey);

		return fragmentRenderer.getLabel(LocaleUtil.getSiteDefault());
	}

	private static Function<Object, String> _getImageURLTransformerFunction() {
		return object -> {
			if (object instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject)object;

				return jsonObject.getString("url");
			}

			return StringPool.BLANK;
		};
	}

	private static List<FragmentField> _getTextFragmentFields(
		Map<String, String> editableTypes,
		InfoDisplayContributorTracker infoDisplayContributorTracker,
		JSONObject jsonObject, boolean saveMapping, long segmentsExperienceId) {

		List<FragmentField> fragmentFields = new ArrayList<>();

		Set<String> textIds = jsonObject.keySet();

		for (String textId : textIds) {
			fragmentFields.add(
				_toFragmentField(
					editableTypes, infoDisplayContributorTracker, jsonObject,
					saveMapping, segmentsExperienceId, textId));
		}

		return fragmentFields;
	}

	private static boolean _isSaveFragmentMappedValue(
		JSONObject jsonObject, boolean saveMapping) {

		if (saveMapping && jsonObject.has("classNameId") &&
			jsonObject.has("classPK") && jsonObject.has("fieldId")) {

			return true;
		}

		if (saveMapping && jsonObject.has("mappedField")) {
			return true;
		}

		return false;
	}

	private static FragmentInlineValue _toDefaultMappingValue(
		InfoDisplayContributorTracker infoDisplayContributorTracker,
		JSONObject jsonObject, Function<Object, String> transformerFunction) {

		long classNameId = jsonObject.getLong("classNameId");

		if (classNameId == 0) {
			return null;
		}

		String className = null;

		try {
			className = PortalUtil.getClassName(classNameId);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get class name for default mapping value",
					exception);
			}
		}

		if (Validator.isNull(className)) {
			return null;
		}

		InfoDisplayContributor infoDisplayContributor =
			infoDisplayContributorTracker.getInfoDisplayContributor(className);

		if (infoDisplayContributor == null) {
			return null;
		}

		long classPK = jsonObject.getLong("classPK");

		try {
			InfoDisplayObjectProvider infoDisplayObjectProvider =
				infoDisplayContributor.getInfoDisplayObjectProvider(classPK);

			Map<String, Object> fieldValues =
				infoDisplayContributor.getInfoDisplayFieldsValues(
					infoDisplayObjectProvider.getDisplayObject(),
					LocaleUtil.getMostRelevantLocale());

			Object fieldValue = fieldValues.get(
				jsonObject.getString("fieldId"));

			if (transformerFunction != null) {
				fieldValue = transformerFunction.apply(fieldValue);
			}

			String valueString = GetterUtil.getString(fieldValue);

			if (Validator.isNull(valueString)) {
				return null;
			}

			return new FragmentInlineValue() {
				{
					value = valueString;
				}
			};
		}
		catch (Exception exception) {
			_log.error("Unable to get default mapped value", exception);
		}

		return null;
	}

	private static FragmentInlineValue _toDescriptionFragmentInlineValue(
		JSONObject jsonObject) {

		JSONObject configJSONObject = jsonObject.getJSONObject("config");

		if (configJSONObject == null) {
			return null;
		}

		String alt = configJSONObject.getString("alt");

		if (Validator.isNull(alt)) {
			return null;
		}

		return new FragmentInlineValue() {
			{
				value = alt;
			}
		};
	}

	private static FragmentField _toFragmentField(
		Map<String, String> editableTypes,
		InfoDisplayContributorTracker infoDisplayContributorTracker,
		JSONObject jsonObject, boolean saveMapping, long segmentsExperienceId,
		String textId) {

		JSONObject textJSONObject = jsonObject.getJSONObject(textId);

		return new FragmentField() {
			{
				id = textId;

				setValue(
					() -> {
						String type = editableTypes.getOrDefault(
							textId, "text");

						if (Objects.equals(type, "html")) {
							return _toFragmentFieldHTML(
								infoDisplayContributorTracker, textJSONObject,
								saveMapping, segmentsExperienceId);
						}

						if (Objects.equals(type, "image")) {
							return _toFragmentFieldImage(
								infoDisplayContributorTracker, textJSONObject,
								saveMapping, segmentsExperienceId);
						}

						return _toFragmentFieldText(
							infoDisplayContributorTracker, textJSONObject,
							saveMapping, segmentsExperienceId);
					});
			}
		};
	}

	private static FragmentFieldBackgroundImage _toFragmentFieldBackgroundImage(
		InfoDisplayContributorTracker infoDisplayContributorTracker,
		JSONObject jsonObject, Map<String, String> localeMap,
		boolean saveMapping) {

		return new FragmentFieldBackgroundImage() {
			{
				backgroundImage = new FragmentImage() {
					{
						title = _toTitleFragmentInlineValue(
							jsonObject, localeMap);

						setUrl(
							() -> {
								if (_isSaveFragmentMappedValue(
										jsonObject, saveMapping)) {

									return _toFragmentMappedValue(
										_toDefaultMappingValue(
											infoDisplayContributorTracker,
											jsonObject,
											_getImageURLTransformerFunction()),
										jsonObject);
								}

								return new FragmentInlineValue() {
									{
										value_i18n = localeMap;
									}
								};
							});
					}
				};
			}
		};
	}

	private static FragmentFieldHTML _toFragmentFieldHTML(
		InfoDisplayContributorTracker infoDisplayContributorTracker,
		JSONObject jsonObject, boolean saveMapping, long segmentsExperienceId) {

		return new FragmentFieldHTML() {
			{
				setHtml(
					() -> {
						if (_isSaveFragmentMappedValue(
								jsonObject, saveMapping)) {

							return _toFragmentMappedValue(
								_toDefaultMappingValue(
									infoDisplayContributorTracker, jsonObject,
									null),
								jsonObject);
						}

						return new FragmentInlineValue() {
							{
								value_i18n = _toLocaleMap(
									jsonObject, segmentsExperienceId);
							}
						};
					});
			}
		};
	}

	private static FragmentFieldImage _toFragmentFieldImage(
		InfoDisplayContributorTracker infoDisplayContributorTracker,
		JSONObject jsonObject, boolean saveMapping, long segmentsExperienceId) {

		Map<String, String> localeMap = _toLocaleMap(
			jsonObject, segmentsExperienceId);

		return new FragmentFieldImage() {
			{
				fragmentImage = new FragmentImage() {
					{
						description = _toDescriptionFragmentInlineValue(
							jsonObject);
						title = _toTitleFragmentInlineValue(
							jsonObject, localeMap);

						setUrl(
							() -> {
								if (_isSaveFragmentMappedValue(
										jsonObject, saveMapping)) {

									return _toFragmentMappedValue(
										_toDefaultMappingValue(
											infoDisplayContributorTracker,
											jsonObject,
											_getImageURLTransformerFunction()),
										jsonObject);
								}

								return new FragmentInlineValue() {
									{
										value_i18n = localeMap;
									}
								};
							});
					}
				};
				fragmentLink = _toFragmentLink(
					infoDisplayContributorTracker, jsonObject, saveMapping);
			}
		};
	}

	private static FragmentFieldText _toFragmentFieldText(
		InfoDisplayContributorTracker infoDisplayContributorTracker,
		JSONObject jsonObject, boolean saveMapping, long segmentsExperienceId) {

		return new FragmentFieldText() {
			{
				fragmentLink = _toFragmentLink(
					infoDisplayContributorTracker, jsonObject, saveMapping);

				setText(
					() -> {
						if (_isSaveFragmentMappedValue(
								jsonObject, saveMapping)) {

							return _toFragmentMappedValue(
								_toDefaultMappingValue(
									infoDisplayContributorTracker, jsonObject,
									null),
								jsonObject);
						}

						Map<String, String> localeMap = _toLocaleMap(
							jsonObject, segmentsExperienceId);

						if (MapUtil.isEmpty(localeMap)) {
							return null;
						}

						return new FragmentInlineValue() {
							{
								value_i18n = localeMap;
							}
						};
					});
			}
		};
	}

	private static FragmentLink _toFragmentLink(
		InfoDisplayContributorTracker infoDisplayContributorTracker,
		JSONObject jsonObject, boolean saveMapping) {

		JSONObject configJSONObject = jsonObject.getJSONObject("config");

		if (configJSONObject.isNull("href") &&
			!_isSaveFragmentMappedValue(configJSONObject, saveMapping)) {

			return null;
		}

		return new FragmentLink() {
			{
				setHref(
					() -> {
						if (_isSaveFragmentMappedValue(
								configJSONObject, saveMapping)) {

							return _toFragmentMappedValue(
								_toDefaultMappingValue(
									infoDisplayContributorTracker,
									configJSONObject, null),
								configJSONObject);
						}

						return new FragmentInlineValue() {
							{
								value = configJSONObject.getString("href");
							}
						};
					});

				setTarget(
					() -> {
						String target = configJSONObject.getString("target");

						if (Validator.isNull(target)) {
							return null;
						}

						return Target.create(
							StringUtil.upperCaseFirstLetter(
								target.substring(1)));
					});
			}
		};
	}

	private static FragmentMappedValue _toFragmentMappedValue(
		FragmentInlineValue fragmentInlineValue, JSONObject jsonObject) {

		return new FragmentMappedValue() {
			{
				mapping = new Mapping() {
					{
						defaultValue = fragmentInlineValue;

						setFieldKey(
							() -> {
								String fieldId = jsonObject.getString(
									"fieldId");

								if (Validator.isNotNull(fieldId)) {
									return fieldId;
								}

								return jsonObject.getString("mappedField");
							});

						setItemKey(
							() -> {
								String classNameId = jsonObject.getString(
									"classNameId");

								if (Validator.isNull(classNameId)) {
									return null;
								}

								String classPK = jsonObject.getString(
									"classPK");

								if (Validator.isNull(classPK)) {
									return null;
								}

								return StringBundler.concat(
									classNameId, StringPool.POUND, classPK);
							});
					}
				};
			}
		};
	}

	private static Map<String, String> _toLocaleMap(
		JSONObject jsonObject, long segmentsExperienceId) {

		if (jsonObject.has("segments-experience-id-" + segmentsExperienceId)) {
			return _toLocaleMap(
				jsonObject.getJSONObject(
					"segments-experience-id-" + segmentsExperienceId),
				segmentsExperienceId);
		}

		return new HashMap<String, String>() {
			{
				List<String> availableLanguageIds = _getAvailableLanguageIds();

				Set<String> keys = jsonObject.keySet();

				Iterator<String> iterator = keys.iterator();

				while (iterator.hasNext()) {
					String key = iterator.next();

					if (availableLanguageIds.contains(key)) {
						put(key, jsonObject.getString(key));
					}
				}
			}
		};
	}

	private static FragmentInlineValue _toTitleFragmentInlineValue(
		JSONObject jsonObject, Map<String, String> map) {

		JSONObject configJSONObject = jsonObject.getJSONObject("config");

		if (configJSONObject == null) {
			return null;
		}

		String imageTitle = configJSONObject.getString("imageTitle");

		if (Validator.isNull(imageTitle) || map.containsValue(imageTitle)) {
			return null;
		}

		return new FragmentInlineValue() {
			{
				value = imageTitle;
			}
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PageFragmentInstanceDefinitionConverterUtil.class);

}