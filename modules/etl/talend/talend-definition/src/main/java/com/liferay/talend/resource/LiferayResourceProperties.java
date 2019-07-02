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

import static org.talend.daikon.properties.presentation.Widget.widget;

import com.liferay.talend.LiferayBaseComponentDefinition;
import com.liferay.talend.common.oas.OASParameter;
import com.liferay.talend.connection.LiferayConnectionProperties;
import com.liferay.talend.connection.LiferayConnectionPropertiesProvider;
import com.liferay.talend.properties.ExceptionUtils;
import com.liferay.talend.runtime.LiferaySourceOrSinkRuntime;
import com.liferay.talend.runtime.ValidatedSoSSandboxRuntime;

import java.io.IOException;

import java.net.URI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.UriBuilder;

import org.apache.avro.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.talend.components.api.component.ISchemaListener;
import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessageProvider;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResultMutable;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.StringProperty;

/**
 * @author Zoltán Takács
 */
public class LiferayResourceProperties
	extends ComponentPropertiesImpl
	implements LiferayConnectionPropertiesProvider {

	public LiferayResourceProperties(String name) {
		super(name);
	}

	public ValidationResult afterEndpoint() throws Exception {
		if (_log.isDebugEnabled()) {
			_log.debug("Endpoint: " + endpoint.getValue());
		}

		ValidatedSoSSandboxRuntime validatedSoSSandboxRuntime =
			LiferayBaseComponentDefinition.initializeSandboxedRuntime(
				getEffectiveLiferayConnectionProperties());

		ValidationResultMutable validationResultMutable =
			validatedSoSSandboxRuntime.getValidationResultMutable();

		if (validationResultMutable.getStatus() ==
				ValidationResult.Result.ERROR) {

			return validationResultMutable;
		}

		LiferaySourceOrSinkRuntime liferaySourceOrSinkRuntime =
			validatedSoSSandboxRuntime.getLiferaySourceOrSinkRuntime();

		try {
			Schema endpointSchema =
				liferaySourceOrSinkRuntime.getEndpointSchema(
					endpoint.getValue(), HttpMethod.GET);

			main.schema.setValue(endpointSchema);
		}
		catch (IOException | TalendRuntimeException e) {
			validationResultMutable.setMessage(
				i18nMessages.getMessage("error.validation.schema"));
			validationResultMutable.setStatus(ValidationResult.Result.ERROR);

			_log.error("Unable to generate schema", e);
		}

		if (validationResultMutable.getStatus() ==
				ValidationResult.Result.ERROR) {

			endpoint.setValue(null);
		}

		populateParametersTable(liferaySourceOrSinkRuntime);

		refreshLayout(getForm(Form.REFERENCE));

		return validationResultMutable;
	}

	public void afterParametersTable() {
		if (_log.isDebugEnabled()) {
			_log.debug(
				"Parameters: " + parametersTable.valueColumnName.getValue());
		}
	}

	public ValidationResult beforeEndpoint() throws Exception {
		ValidatedSoSSandboxRuntime validatedSoSSandboxRuntime =
			LiferayBaseComponentDefinition.initializeSandboxedRuntime(
				getEffectiveLiferayConnectionProperties());

		ValidationResultMutable validationResultMutable =
			validatedSoSSandboxRuntime.getValidationResultMutable();

		if (validationResultMutable.getStatus() ==
				ValidationResult.Result.ERROR) {

			return validationResultMutable;
		}

		LiferaySourceOrSinkRuntime liferaySourceOrSinkRuntime =
			validatedSoSSandboxRuntime.getLiferaySourceOrSinkRuntime();

		try {
			Set<String> endpoints = liferaySourceOrSinkRuntime.getEndpointList(
				HttpMethod.GET);

			if (endpoints.isEmpty()) {
				validationResultMutable.setMessage(
					i18nMessages.getMessage("error.validation.resources"));
				validationResultMutable.setStatus(
					ValidationResult.Result.ERROR);

				return validationResultMutable;
			}

			List<NamedThing> endpointsNamedThing = new ArrayList<>();

			endpoints.forEach(
				endpoint -> endpointsNamedThing.add(
					new SimpleNamedThing(endpoint, endpoint)));

			endpoint.setPossibleNamedThingValues(endpointsNamedThing);
		}
		catch (Exception e) {
			return ExceptionUtils.exceptionToValidationResult(e);
		}

		return null;
	}

	public URI getEndpointURI() {
		String applicationBaseHref = connection.getApplicationBaseHref();

		String endpointHref = applicationBaseHref.concat(endpoint.getValue());

		UriBuilder uriBuilder = UriBuilder.fromPath(endpointHref);

		List<String> parameterNames = parametersTable.columnName.getValue();
		List<String> parameterTypes = parametersTable.typeColumnName.getValue();
		List<String> parameterValues =
			parametersTable.valueColumnName.getValue();

		Stream<String> parameterNamesStream = parameterNames.stream();

		parameterNames = parameterNamesStream.map(
			name -> name.replace("*", "")
		).collect(
			Collectors.toList()
		);

		for (int i = 0; i < parameterNames.size(); i++) {
			uriBuilder.resolveTemplate(
				parameterNames.get(i), parameterValues.get(i));
		}

		for (int i = 0; i < parameterNames.size(); i++) {
			String typeString = parameterTypes.get(i);

			if (OASParameter.Type.PATH == OASParameter.Type.valueOf(
					typeString.toUpperCase())) {

				continue;
			}

			String parameterValue = parameterValues.get(i);

			if ((parameterValue != null) &&
				!Objects.equals(parameterValue, "")) {

				uriBuilder.queryParam(parameterNames.get(i), parameterValue);
			}
		}

		return uriBuilder.build();
	}

	@Override
	public LiferayConnectionProperties getLiferayConnectionProperties() {
		return connection;
	}

	public void setSchemaListener(ISchemaListener schemaListener) {
		this.schemaListener = schemaListener;
	}

	@Override
	public void setupLayout() {
		super.setupLayout();

		// Special property settings

		endpoint.setRequired();

		// Forms

		_setupReferenceForm();
	}

	@Override
	public void setupProperties() {
		super.setupProperties();

		endpoint.setValue(null);
	}

	public LiferayConnectionProperties connection =
		new LiferayConnectionProperties("connection");
	public StringProperty endpoint = new StringProperty("endpoint");

	public SchemaProperties main = new SchemaProperties("main") {

		@SuppressWarnings("unused")
		public void afterSchema() {
			if (schemaListener != null) {
				schemaListener.afterSchema();
			}
		}

	};

	public ParametersTable parametersTable = new ParametersTable(
		"parametersTable");
	public ISchemaListener schemaListener;

	protected LiferayConnectionProperties
		getEffectiveLiferayConnectionProperties() {

		LiferayConnectionProperties liferayConnectionProperties =
			getLiferayConnectionProperties();

		if (liferayConnectionProperties == null) {
			_log.error("LiferayConnectionProperties is null");
		}

		LiferayConnectionProperties referencedLiferayConnectionProperties =
			liferayConnectionProperties.getReferencedConnectionProperties();

		if (referencedLiferayConnectionProperties != null) {
			if (_log.isDebugEnabled()) {
				_log.debug("Using a reference connection properties");
				_log.debug(
					"API spec URL: " +
						referencedLiferayConnectionProperties.getApiSpecURL());
				_log.debug(
					"User ID: " +
						referencedLiferayConnectionProperties.getUserId());
			}

			return referencedLiferayConnectionProperties;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"API spec URL: " +
					liferayConnectionProperties.apiSpecURL.getValue());
			_log.debug("User ID: " + liferayConnectionProperties.getUserId());
		}

		return liferayConnectionProperties;
	}

	protected void populateParametersTable(
			LiferaySourceOrSinkRuntime liferaySourceOrSinkRuntime)
		throws IOException {

		List<String> parameterNames = new ArrayList<>();
		List<String> parameterValues = new ArrayList<>();
		List<String> parameterTypes = new ArrayList<>();

		List<OASParameter> oasParameters =
			liferaySourceOrSinkRuntime.getParameters(
				endpoint.getValue(), HttpMethod.GET);

		if (oasParameters.isEmpty()) {
			parametersTable.columnName.setValue(Collections.emptyList());
			parametersTable.valueColumnName.setValue(Collections.emptyList());
			parametersTable.typeColumnName.setValue(Collections.emptyList());
		}
		else {
			for (OASParameter oasParameter : oasParameters) {
				String name = oasParameter.getName();

				if (Objects.equals(name, "page") ||
					Objects.equals(name, "pageSize")) {

					continue;
				}

				if (oasParameter.isRequired() ||
					(OASParameter.Type.PATH == oasParameter.getType())) {

					name = name + "*";
				}

				parameterNames.add(name);

				OASParameter.Type type = oasParameter.getType();

				String typeString = type.toString();

				typeString = typeString.toLowerCase();

				parameterTypes.add(typeString);

				parameterValues.add("");
			}

			parametersTable.columnName.setValue(parameterNames);
			parametersTable.typeColumnName.setValue(parameterTypes);
			parametersTable.valueColumnName.setValue(parameterValues);
		}
	}

	protected static final I18nMessages i18nMessages;

	static {
		I18nMessageProvider i18nMessageProvider =
			GlobalI18N.getI18nMessageProvider();

		i18nMessages = i18nMessageProvider.getI18nMessages(
			LiferayResourceProperties.class);
	}

	private void _setupReferenceForm() {
		Form referenceForm = Form.create(this, Form.REFERENCE);

		Widget endpointReferenceWidget = Widget.widget(endpoint);

		endpointReferenceWidget.setCallAfter(true);
		endpointReferenceWidget.setLongRunning(true);
		endpointReferenceWidget.setWidgetType(
			Widget.NAME_SELECTION_REFERENCE_WIDGET_TYPE);

		referenceForm.addRow(endpointReferenceWidget);

		referenceForm.addRow(main.getForm(Form.REFERENCE));

		Widget parametersTableWidget = widget(parametersTable);

		referenceForm.addRow(
			parametersTableWidget.setWidgetType(Widget.TABLE_WIDGET_TYPE));

		refreshLayout(referenceForm);
	}

	private static final Logger _log = LoggerFactory.getLogger(
		LiferayResourceProperties.class);

	private static final long serialVersionUID = 6834821457406101745L;

}