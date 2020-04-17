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

package com.liferay.data.engine.rest.internal.dto.v2_0.util;

import com.liferay.data.engine.field.type.util.LocalizedValueUtil;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinitionField;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesTracker;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

import org.powermock.api.mockito.PowerMockito;

/**
 * @author Mateus Santana
 */
@RunWith(MockitoJUnitRunner.class)
public class DataDefinitionUtilTest extends PowerMockito {

	@Test
	public void testToDDMFormEquals() {
		DataDefinition dataDefinition = _createDataDefinition();

		Assert.assertEquals(
			new DDMForm() {
				{
					setAvailableLocales(
						SetUtil.fromArray(new Locale[] {LocaleUtil.US}));
					setDDMFormFields(
						ListUtil.fromArray(
							new DDMFormField() {
								{
									setIndexType(null);
									setLabel(
										LocalizedValueUtil.toLocalizedValue(
											HashMapBuilder.<String, Object>put(
												"en_US", "label"
											).build()));
									setLocalizable(false);
									setName("Name");
									setPredefinedValue(null);
									setReadOnly(false);
									setRepeatable(false);
									setRequired(false);
									setShowLabel(false);
									setTip(
										LocalizedValueUtil.toLocalizedValue(
											HashMapBuilder.<String, Object>put(
												"en_US", "tip"
											).build()));
									setType("text");
								}
							}));
					setDefaultLocale(LocaleUtil.US);
				}
			},
			DataDefinitionUtil.toDDMForm(
				dataDefinition, _ddmFormFieldTypeServicesTracker));
	}

	@Test
	public void testToDDMFormWithEmptyDataDefinition() {
		DDMForm ddmForm = DataDefinitionUtil.toDDMForm(
			new DataDefinition(), _ddmFormFieldTypeServicesTracker);

		Assert.assertTrue(SetUtil.isEmpty(ddmForm.getAvailableLocales()));

		Assert.assertTrue(ListUtil.isEmpty(ddmForm.getDDMFormFields()));

		Assert.assertEquals(
			"en_US", LocaleUtil.toLanguageId(ddmForm.getDefaultLocale()));
	}

	@Test
	public void testToDDMFormWithNullDataDefinition() {
		Assert.assertEquals(
			new DDMForm(),
			DataDefinitionUtil.toDDMForm(
				null, _ddmFormFieldTypeServicesTracker));
	}

	private DataDefinition _createDataDefinition() {
		DataDefinition dataDefinition = new DataDefinition() {
			{
				availableLanguageIds = new String[] {"en_US"};
				dataDefinitionFields = new DataDefinitionField[] {
					new DataDefinitionField() {
						{
							description = HashMapBuilder.<String, Object>put(
								"en_US", "Description"
							).build();
							fieldType = "text";
							label = HashMapBuilder.<String, Object>put(
								"en_US", "label"
							).build();
							name = "Name";
							tip = HashMapBuilder.<String, Object>put(
								"en_us", "tip"
							).build();
						}
					}
				};
				defaultLanguageId = "en_US";
			}
		};

		dataDefinition.setDescription(
			HashMapBuilder.<String, Object>put(
				"en_US", RandomTestUtil.randomString()
			).build());
		dataDefinition.setName(
			HashMapBuilder.<String, Object>put(
				"en_US", RandomTestUtil.randomString()
			).build());

		return dataDefinition;
	}

	private DDMFormFieldTypeServicesTracker _ddmFormFieldTypeServicesTracker;

}