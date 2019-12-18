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

package com.liferay.talend.resource;

import com.liferay.talend.LiferayBaseComponentDefinition;
import com.liferay.talend.common.daikon.DaikonUtil;
import com.liferay.talend.common.oas.OASExplorer;
import com.liferay.talend.common.oas.OASSource;
import com.liferay.talend.common.oas.constants.OASConstants;
import com.liferay.talend.common.schema.SchemaBuilder;
import com.liferay.talend.common.util.StringUtil;
import com.liferay.talend.source.LiferayOASSource;

import java.net.URI;

import java.util.Set;

import javax.json.JsonObject;

import javax.ws.rs.core.UriBuilder;

import org.apache.avro.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessageProvider;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.StringProperty;

/**
 * @author Zoltán Takács
 * @author Ivica Cardic
 * @author Igor Beslic
 */
public class LiferayInputResourceProperties
	extends BaseLiferayResourceProperties {

	public LiferayInputResourceProperties(String name) {
		super(name);
	}

	public void afterParametersTable() {
		if (_logger.isDebugEnabled()) {
			_logger.debug(
				"Parameters: " + parametersTable.valueColumnName.getValue());
		}
	}

	public ValidationResult beforeEndpoint() throws Exception {
		LiferayOASSource liferayOASSource =
			LiferayBaseComponentDefinition.getLiferayOASSource(
				getEffectiveLiferayConnectionProperties());

		if (!liferayOASSource.isValid()) {
			return liferayOASSource.getValidationResult();
		}

		OASSource jsonClient = liferayOASSource.getOASSource();

		try {
			OASExplorer oasExplorer = new OASExplorer();

			Set<String> endpoints = oasExplorer.getEndpointList(
				jsonClient.getOASJsonObject(), OASConstants.OPERATION_GET);

			if (endpoints.isEmpty()) {
				return new ValidationResult(
					ValidationResult.Result.ERROR,
					_i18nMessages.getMessage("error.validation.resources"));
			}

			endpoint.setPossibleNamedThingValues(
				DaikonUtil.toNamedThings(endpoints));
		}
		catch (Exception e) {
			return new ValidationResult(
				ValidationResult.Result.ERROR, e.getMessage());
		}

		return null;
	}

	public void setupLayout() {
		super.setupLayout();

		Form referenceForm = getForm(Form.REFERENCE);

		referenceForm.addRow(includeFields);
		referenceForm.addRow(includeFieldsParameters);
	}

	public Property<String> includeFields = new StringProperty("includeFields");
	public Property<String> includeFieldsParameters = new StringProperty(
		"includeFieldsParameters");

	@Override
	protected URI buildEndpointURI(UriBuilder uriBuilder) {
		_addNestedFields(uriBuilder);
		_addNestedFieldsParameters(uriBuilder);

		return super.buildEndpointURI(uriBuilder);
	}

	@Override
	protected ValidationResult doAfterEndpoint(OASSource oasSource) {
		if (_logger.isDebugEnabled()) {
			_logger.debug("Endpoint: " + endpoint.getValue());
		}

		JsonObject oasJsonObject = oasSource.getOASJsonObject();

		try {
			SchemaBuilder schemaBuilder = new SchemaBuilder();

			Schema endpointSchema = schemaBuilder.inferSchema(
				endpoint.getValue(), OASConstants.OPERATION_GET, oasJsonObject);

			main.schema.setValue(endpointSchema);
		}
		catch (TalendRuntimeException tre) {
			_logger.error("Unable to generate schema", tre);

			return new ValidationResult(
				ValidationResult.Result.ERROR,
				_i18nMessages.getMessage("error.validation.schema"));
		}

		OASExplorer oasExplorer = new OASExplorer();

		populateParametersTable(
			oasExplorer.getParameters(
				endpoint.getValue(), OASConstants.OPERATION_GET,
				oasJsonObject));

		return ValidationResult.OK;
	}

	private void _addNestedFields(UriBuilder uriBuilder) {
		String includeFieldsValue = includeFields.getValue();

		if (!StringUtil.isEmpty(includeFieldsValue)) {
			uriBuilder.queryParam("nestedFields", includeFieldsValue);
		}
	}

	private void _addNestedFieldsParameters(UriBuilder uriBuilder) {
		String includeFieldsParametersValue =
			includeFieldsParameters.getValue();

		if (!StringUtil.isEmpty(includeFieldsParametersValue)) {
			String[] includeFieldsParameters =
				includeFieldsParametersValue.split(",");

			for (String includeFieldsParameter : includeFieldsParameters) {
				String[] parameterNameValue = includeFieldsParameter.split("=");

				uriBuilder.queryParam(
					parameterNameValue[0], parameterNameValue[1]);
			}
		}
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		LiferayInputResourceProperties.class);

	private static final I18nMessages _i18nMessages;
	private static final long serialVersionUID = 6834821457406101745L;

	static {
		I18nMessageProvider i18nMessageProvider =
			GlobalI18N.getI18nMessageProvider();

		_i18nMessages = i18nMessageProvider.getI18nMessages(
			LiferayInputResourceProperties.class);
	}

}