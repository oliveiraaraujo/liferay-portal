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

<%@ include file="/admin/init.jsp" %>

<%
DDMFormViewFormInstanceRecordsDisplayContext ddmFormViewFormInstanceRecordsDisplayContext = ddmFormAdminDisplayContext.getFormViewRecordsDisplayContext();

int totalItems = ddmFormViewFormInstanceRecordsDisplayContext.getTotalItems();

String redirect = ParamUtil.getString(request, "redirect");
%>

<div class="ddm-form-summary hide">
	<div class="ddm-form-summary-header">
		<div class="container-fluid container-fluid-max-xl">
			<div class="align-items-center autofit-row">
				<span class="ddm-form-summary-title text-truncate">
					<liferay-ui:message arguments="<%= totalItems %>" key="x-entries" />
				</span>
			</div>

			<div class="align-items-center autofit-row">
				<span class="ddm-form-summary-subtitle text-truncate">
					<c:choose>
						<c:when test="<%= totalItems > 0 %>">
							<liferay-ui:message arguments="30" key="last-entry-sent-x-minutes-ago" />
						</c:when>
						<c:otherwise>
							<liferay-ui:message key="no-entry-submitted-yet" />
						</c:otherwise>
					</c:choose>
				</span>
			</div>

			<liferay-ui:tabs
				cssClass="mb-0 mb-lg-0 navbar-no-collapse navigation-bar-light pt-custom"
				names="summary,entries"
				refresh="<%= false %>"
			>
				<liferay-ui:section>

				</liferay-ui:section>

				<liferay-ui:section>
					<div class="ddm-form-summary-container">
						<liferay-util:include page="/admin/form_instance_records_search_container.jsp" servletContext="<%= application %>">
							<liferay-util:param name="redirect" value="<%= redirect %>" />
						</liferay-util:include>
					</div>
				</liferay-ui:section>
			</liferay-ui:tabs>
		</div>
	</div>
</div>