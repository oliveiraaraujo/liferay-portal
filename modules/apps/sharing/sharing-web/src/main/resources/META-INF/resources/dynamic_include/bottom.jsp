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

<%@ include file="/dynamic_include/init.jsp" %>

<%
PortletURL manageCollaboratorsURL = PortletProviderUtil.getPortletURL(request, SharingEntry.class.getName(), PortletProvider.Action.MANAGE);

manageCollaboratorsURL.setWindowState(LiferayWindowState.POP_UP);

PortletURL sharingURL = PortletProviderUtil.getPortletURL(request, SharingEntry.class.getName(), PortletProvider.Action.EDIT);

sharingURL.setWindowState(LiferayWindowState.POP_UP);
%>

<aui:script sandbox="<%= true %>">
	function showDialog(uri, title, namespace, refreshOnClose) {
		Liferay.Util.openWindow(
			{
				dialog: {
					centered: true,
					constrain: true,
					cssClass: 'sharing-dialog',
					destroyOnHide: true,
					modal: true,
					height: 540,
					width: 600,
					on: {
						visibleChange: function(event) {
							if (refreshOnClose && !event.newVal) {
								Liferay.Portlet.refresh('#p_p_id' + namespace);
							}
						}
					}
				},
				id: 'sharingDialog',
				title: title,
				uri: uri
			}
		);
	}

	var Sharing = {};

	Liferay.provide(
		Sharing,
		'share',
		function(classNameId, classPK, title, namespace, refreshOnClose) {
			var sharingURL = new Liferay.PortletURL.createURL('<%= sharingURL.toString() %>');

			sharingURL.setParameter('classNameId', classNameId);
			sharingURL.setParameter('classPK', classPK);

			showDialog(sharingURL, title, namespace, refreshOnClose);
		},
		['liferay-portlet-url', 'liferay-util-window']
	);

	Liferay.provide(
		Sharing,
		'manageCollaborators',
		function(classNameId, classPK, namespace, refreshOnClose) {
			var manageCollaboratorsURL = new Liferay.PortletURL.createURL('<%= manageCollaboratorsURL.toString() %>');

			manageCollaboratorsURL.setParameter('classNameId', classNameId);
			manageCollaboratorsURL.setParameter('classPK', classPK);

			showDialog(manageCollaboratorsURL, '<%= LanguageUtil.get(resourceBundle, "manage-collaborators") %>', namespace, refreshOnClose);
		},
		['liferay-portlet-url', 'liferay-util-window']
	);

	Liferay.Sharing = Sharing;
</aui:script>