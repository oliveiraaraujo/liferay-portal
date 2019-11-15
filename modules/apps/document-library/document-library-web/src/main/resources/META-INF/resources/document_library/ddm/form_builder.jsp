<%--
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
--%>

<%@ include file="/document_library/init.jsp" %>

<%
DLEditFileEntryTypeDisplayContext dlEditFileEntryTypeDisplayContext = (DLEditFileEntryTypeDisplayContext)request.getAttribute(DLWebKeys.DOCUMENT_LIBRARY_EDIT_EDIT_FILE_ENTRY_TYPE_DISPLAY_CONTEXT);

String portletResourceNamespace = renderResponse.getNamespace();

DDMStructure ddmStructure = (DDMStructure)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_DYNAMIC_DATA_MAPPING_STRUCTURE);

long ddmStructureId = BeanParamUtil.getLong(ddmStructure, request, "structureId");

String script = BeanParamUtil.getString(ddmStructure, request, "definition");

JSONArray fieldsJSONArray = DDMUtil.getDDMFormFieldsJSONArray(DDMStructureLocalServiceUtil.fetchDDMStructure(ddmStructureId), script);

String fieldsJSONArrayString = (fieldsJSONArray != null) ? fieldsJSONArray.toString() : StringPool.BLANK;

String scopeAvailableFields = dlEditFileEntryTypeDisplayContext.getAvailableFields();

Portlet portlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), portletDisplay.getId());
%>

<liferay-util:html-top>
	<link href="<%= PortalUtil.getStaticResourceURL(request, application.getContextPath() + "/css/main.css", portlet.getTimestamp()) %>" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<%
String defaultLanguageId = LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault());

Locale[] availableLocales = {LocaleUtil.getSiteDefault()};

boolean changeableDefaultLanguage = false;

if (Validator.isNotNull(script)) {
	try {
		DDMForm ddmForm = DDMUtil.getDDMForm(script);

		String ddmFormDefaultLanguageId = LocaleUtil.toLanguageId(ddmForm.getDefaultLocale());

		if (!Objects.equals(defaultLanguageId, ddmFormDefaultLanguageId)) {
			changeableDefaultLanguage = true;
		}

		defaultLanguageId = ddmFormDefaultLanguageId;

		Set<Locale> ddmFormAvailableLocales = ddmForm.getAvailableLocales();

		availableLocales = ddmFormAvailableLocales.toArray(new Locale[0]);
	}
	catch (Exception e) {
	}
}
%>

<div class="separator"><!-- --></div>

<div class="alert alert-danger hide lfr-message-response" id="<portlet:namespace />messageContainer"></div>

<liferay-ui:tabs
	names='<%= LanguageUtil.get(request, "view[action]") + "," + LanguageUtil.get(request, "source") %>'
	refresh="<%= false %>"
>
	<liferay-ui:section>
		<div id="<portlet:namespace />formBuilderTab">
			<aui:translation-manager availableLocales="<%= availableLocales %>" changeableDefaultLanguage="<%= changeableDefaultLanguage %>" defaultLanguageId="<%= defaultLanguageId %>" id="translationManager" initialize="<%= false %>" readOnly="<%= false %>" />

			<div class="diagram-builder form-builder" id="<portlet:namespace />formBuilder">
				<div class="diagram-builder-content" id="<portlet:namespace />formBuilderContent">
					<div class="tabbable">
						<div class="tabbable-content">
							<ul class="lfr-nav nav nav-tabs nav-tabs-default">
								<li class="active">
									<a href="javascript:;">
										<liferay-ui:message key="fields" />
									</a>
								</li>
								<li class="disabled">
									<a href="javascript:;">
										<liferay-ui:message key="settings" />
									</a>
								</li>
							</ul>

							<div class="tab-content">
								<div class="tab-pane"></div>
								<div class="tab-pane"></div>
							</div>
						</div>
					</div>

					<div class="diagram-builder-content-container">
						<div class="diagram-builder-canvas">
							<div class="diagram-builder-drop-container"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</liferay-ui:section>

	<liferay-ui:section>
		<div class="form-builder-source-wrapper" id="<portlet:namespace />formBuilderSourceWrapper">
			<div class="form-builder-source" id="<portlet:namespace />formBuilderEditor"></div>
		</div>
	</liferay-ui:section>
</liferay-ui:tabs>

<%
JSONArray availableLocalesJSONArray = JSONFactoryUtil.createJSONArray();

for (int i = 0; i < availableLocales.length; i++) {
	availableLocalesJSONArray.put(LanguageUtil.getLanguageId(availableLocales[i]));
}

JSONObject localesMapJSONObject = JSONFactoryUtil.createJSONObject();

for (Locale availableLocale : LanguageUtil.getAvailableLocales(themeDisplay.getSiteGroupId())) {
	localesMapJSONObject.put(LocaleUtil.toLanguageId(availableLocale), availableLocale.getDisplayName(locale));
}
%>

<aui:script use="aui-ace-editor,aui-datepicker-deprecated,aui-tabview,event-custom-base,json,liferay-portlet-dynamic-data-lists,liferay-portlet-dynamic-data-mapping,liferay-portlet-dynamic-data-mapping-custom-fields,liferay-xml-formatter">
	var Lang = A.Lang;

	var STR_VALUE = 'value';

	var availableFields;

	var displayWarning = function(message) {
		new Liferay.Notification({
			closeable: true,
			delay: {
				hide: 5000,
				show: 0
			},
			duration: 500,
			message: message,
			title: Liferay.Language.get('warning'),
			type: 'warning'
		}).render('body');
	};

	var formEditor;

	var getContentValue = function() {
		var content;

		if (formEditor && !isViewTabActive()) {
			content = formEditor.get(STR_VALUE);
		} else {
			content = formBuilder.getContent();
		}

		return content;
	};

	var getFormEditor = function() {
		if (!formEditor) {
			formEditor = new A.AceEditor({
				boundingBox: '#<portlet:namespace />formBuilderEditor',
				height: 600,
				mode: 'xml',
				tabSize: 4,
				width: 600
			}).render();
		}

		return formEditor;
	};

	var isViewTabActive = function() {
		var formBuilderTab = A.one('#<portlet:namespace />formBuilderTab');

		if (!formBuilderTab) {
			return false;
		}

		var ancestor = formBuilderTab.ancestor();

		return !ancestor.hasClass('hide');
	};

	var reloadFormBuilderData = function(content) {
		if (!Lang.isValue(content)) {
			content = window.<portlet:namespace />getContentDefinition();
		}

		content = content.replace(/nestedFields/g, 'fields');

		if (
			content.indexOf('availableLanguageIds') === -1 ||
			content.indexOf('defaultLanguageId') === -1 ||
			content.indexOf('fields') === -1
		) {
			displayWarning(
				'<%= UnicodeLanguageUtil.get(resourceBundle, "you-cannot-remove-default-attributes") %>'
			);
		} else {
			try {
				content = JSON.parse(content);
			} catch (e) {
				displayWarning(
					'<%= UnicodeLanguageUtil.get(resourceBundle, "you-have-entered-invalid-json") %>'
				);

				return;
			}

			formBuilder.translationManager.set(
				'availableLocales',
				content.availableLanguageIds
			);

			content = formBuilder.deserializeDefinitionFields(content);

			formBuilder.set('fields', content);
		}
	};

	var setEditorSize = function() {
		if (!isViewTabActive()) {
			getFormEditor().set(
				'width',
				A.one('#<portlet:namespace />formBuilderSourceWrapper').get(
					'clientWidth'
				)
			);
		}
	};

	var switchToSource = function() {
		setEditorSize();

		var content = formBuilder.getContent();

		getFormEditor().set(STR_VALUE, content);
	};

	var switchToView = function() {
		if (formEditor) {
			reloadFormBuilderData(formEditor.get(STR_VALUE));
		} else if (formBuilder) {
			reloadFormBuilderData(formBuilder.getContent());
		}
	};

	<c:if test="<%= Validator.isNotNull(scopeAvailableFields) %>">
		availableFields = A.Object.getValue(
			window,
			'<%= HtmlUtil.escapeJS(scopeAvailableFields) %>'.split('.')
		);

		if (A.Lang.isFunction(availableFields)) {
			availableFields = availableFields(A, Liferay.FormBuilder);
		}
	</c:if>

	var formBuilder = new Liferay.FormBuilder({
		allowRemoveRequiredFields: true,
		availableFields: availableFields,
		boundingBox: '#<portlet:namespace />formBuilder',
		enableEditing: false,
		fieldNameEditionDisabled: <%= (ddmStructure != null) && (DDMStorageLinkLocalServiceUtil.getStructureStorageLinksCount(ddmStructure.getStructureId()) > 0) %>,

		<c:if test="<%= Validator.isNotNull(fieldsJSONArrayString) %>">
			fields: <%= fieldsJSONArrayString %>,
		</c:if>

		portletNamespace: '<portlet:namespace />',
		portletResourceNamespace:
			'<%= HtmlUtil.escapeJS(portletResourceNamespace) %>',
		readOnly: <%= ParamUtil.getBoolean(request, "formBuilderReadOnly") %>,
		srcNode: '#<portlet:namespace />formBuilderContent',
		translationManager: {
			<c:if test="<%= availableLocalesJSONArray.length() > 0 %>">
				availableLocales: <%= availableLocalesJSONArray.toString() %>,
			</c:if>

			boundingBox: '#<portlet:namespace />translationManager',
			changeableDefaultLanguage: <%= changeableDefaultLanguage %>,
			defaultLocale: '<%= HtmlUtil.escapeJS(defaultLanguageId) %>',
			localesMap: <%= localesMapJSONObject.toString() %>,
			srcNode:
				'#<portlet:namespace />translationManager .lfr-translation-manager-content'
		}
	}).render();

	var dialog = Liferay.Util.getWindow();

	if (dialog) {
		dialog.after('widthChange', setEditorSize);
	}

	var afterShowTab = function(event) {
		if (isViewTabActive()) {
			switchToView();
		} else {
			switchToSource();
		}
	};

	Liferay.after('showTab', afterShowTab);

	var onDestroyPortlet = function(event) {
		if (event.portletId === '<%= portletDisplay.getRootPortletId() %>') {
			Liferay.detach('showTab', afterShowTab);
			Liferay.detach('destroyPortlet', onDestroyPortlet);
			Liferay.detach('<portlet:namespace />saveTemplate');

			var propertyList = formBuilder.propertyList;

			if (propertyList) {
				propertyList.get('data').each(function(model) {
					var editor = model.get('editor');

					if (editor) {
						editor.destroy();
					}
				});
			}

			formBuilder.destroy();
		}
	};

	Liferay.on('destroyPortlet', onDestroyPortlet);

	window[
		'<%= HtmlUtil.escapeJS(portletResourceNamespace) %>formBuilder'
	] = formBuilder;

	window[
		'<%= HtmlUtil.escapeJS(portletResourceNamespace) %>getContentValue'
	] = getContentValue;

	Liferay.on('<portlet:namespace />saveTemplate', function(event) {
		A.one('#<portlet:namespace />scriptContent').val(getContentValue());
	});

	Liferay.fire('<portlet:namespace />formBuilderLoaded', {
		formBuilder: formBuilder
	});
</aui:script>