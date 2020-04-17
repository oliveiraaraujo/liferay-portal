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

import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinitionField;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesTracker;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.List;
import java.util.Locale;
import java.util.Set;

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
	public void testToDDMForm() {
		DataDefinition dataDefinition = _createDataDefinition();

		DDMForm ddmForm = DataDefinitionUtil.toDDMForm(
			dataDefinition, _ddmFormFieldTypeServicesTracker);

		Set<Locale> availableLocales = ddmForm.getAvailableLocales();

		Assert.assertEquals(
			"[" + dataDefinition.getAvailableLanguageIds()[0] + "]",
			availableLocales.toString());

		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		DDMFormField ddmFormField = ddmFormFields.get(0);

		Assert.assertEquals(
			dataDefinition.getDataDefinitionFields()[0].getName(),
			ddmFormField.getName());

		Locale defaultLocale = ddmForm.getDefaultLocale();

		Assert.assertEquals(
			dataDefinition.getDefaultLanguageId(), defaultLocale.toString());
	}

	@Test
	public void testToDDMFormEmptyAvailableLanguageIds() {
		DataDefinition dataDefinition = _createDataDefinition();

		dataDefinition.setAvailableLanguageIds(new String[0]);

		DDMForm ddmForm = DataDefinitionUtil.toDDMForm(
			dataDefinition, _ddmFormFieldTypeServicesTracker);

		Set<Locale> availableLocales = ddmForm.getAvailableLocales();

		Assert.assertTrue(availableLocales.isEmpty());
	}

	@Test
	public void testToDDMFormEmptyDataDefinition() {
		DDMForm ddmForm = DataDefinitionUtil.toDDMForm(
			new DataDefinition(), _ddmFormFieldTypeServicesTracker);

		Set<Locale> availableLocales = ddmForm.getAvailableLocales();

		Assert.assertTrue(availableLocales.isEmpty());

		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		Assert.assertTrue(ddmFormFields.isEmpty());

		Locale defaultLocale = ddmForm.getDefaultLocale();

		Assert.assertEquals("en_US", defaultLocale.toString());
	}

	@Test
	public void testToDDMFormEmptyDataDefinitionFields() {
		DataDefinition dataDefinition = _createDataDefinition();

		dataDefinition.setDataDefinitionFields(new DataDefinitionField[0]);

		DDMForm ddmForm = DataDefinitionUtil.toDDMForm(
			dataDefinition, _ddmFormFieldTypeServicesTracker);

		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		Assert.assertTrue(ddmFormFields.isEmpty());
	}

	@Test
	public void testToDDMFormEmptyDefaultLanguageId() {
		DataDefinition dataDefinition = _createDataDefinition();

		dataDefinition.setDefaultLanguageId(new String());

		DDMForm ddmForm = DataDefinitionUtil.toDDMForm(
			dataDefinition, _ddmFormFieldTypeServicesTracker);

		Locale defaultLocale = ddmForm.getDefaultLocale();

		Assert.assertEquals("en_US", defaultLocale.toString());
	}

	@Test
	public void testToDDMFormNullDataDefinition() {
		DDMForm ddmForm = DataDefinitionUtil.toDDMForm(
			null, _ddmFormFieldTypeServicesTracker);

		Assert.assertTrue(ddmForm.equals(new DDMForm()));
	}

	private DataDefinition _createDataDefinition() {
		DataDefinition dataDefinition = new DataDefinition() {
			{
				availableLanguageIds = new String[] {"en_US"};
				dataDefinitionFields = new DataDefinitionField[] {
					new DataDefinitionField() {
						{
							description = HashMapBuilder.<String, Object>put(
								"en_US", RandomTestUtil.randomString()
							).build();
							fieldType = "text";
							label = HashMapBuilder.<String, Object>put(
								"label", RandomTestUtil.randomString()
							).build();
							name = RandomTestUtil.randomString();
							tip = HashMapBuilder.<String, Object>put(
								"tip", RandomTestUtil.randomString()
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