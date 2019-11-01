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

package com.liferay.site.util;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.usersadmin.search.GroupSearch;
import com.liferay.portlet.usersadmin.search.GroupSearchTerms;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 */
@Component(immediate = true, service = GroupSearchProvider.class)
public class GroupSearchProvider {

	public GroupSearch getGroupSearch(
			PortletRequest portletRequest, PortletURL portletURL)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		GroupSearch groupSearch = new GroupSearch(portletRequest, portletURL);

		GroupSearchTerms searchTerms =
			(GroupSearchTerms)groupSearch.getSearchTerms();

		long parentGroupId = getParentGroupId(portletRequest);

		Company company = themeDisplay.getCompany();

		List results = null;

		if (!searchTerms.hasSearchTerms() &&
			isFilterManageableGroups(portletRequest) && (parentGroupId <= 0)) {

			List<Group> groups = ListUtil.sort(
				getAllGroups(portletRequest),
				groupSearch.getOrderByComparator());

			groupSearch.setTotal(groups.size());

			results = ListUtil.subList(
				groups, groupSearch.getStart(), groupSearch.getEnd());
		}
		else if (searchTerms.hasSearchTerms()) {
			int total = _groupLocalService.searchCount(
				company.getCompanyId(), _classNameIds,
				searchTerms.getKeywords(),
				getGroupParams(portletRequest, searchTerms, parentGroupId));

			groupSearch.setTotal(total);

			results = _groupLocalService.search(
				company.getCompanyId(), _classNameIds,
				searchTerms.getKeywords(),
				getGroupParams(portletRequest, searchTerms, parentGroupId),
				groupSearch.getStart(), groupSearch.getEnd(),
				groupSearch.getOrderByComparator());
		}
		else {
			long groupId = ParamUtil.getLong(
				portletRequest, "groupId",
				GroupConstants.DEFAULT_PARENT_GROUP_ID);

			int total = _groupLocalService.searchCount(
				company.getCompanyId(), _classNameIds, groupId,
				searchTerms.getKeywords(),
				getGroupParams(portletRequest, searchTerms, parentGroupId));

			groupSearch.setTotal(total);

			results = _groupLocalService.search(
				company.getCompanyId(), _classNameIds, groupId,
				searchTerms.getKeywords(),
				getGroupParams(portletRequest, searchTerms, parentGroupId),
				groupSearch.getStart(), groupSearch.getEnd(),
				groupSearch.getOrderByComparator());
		}

		groupSearch.setResults(results);

		return groupSearch;
	}

	protected List<Group> getAllGroups(PortletRequest portletRequest)
		throws PortalException {

		List<Group> groups = new ArrayList<>();

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		groups = user.getMySiteGroups(
			new String[] {
				Company.class.getName(), Group.class.getName(),
				Organization.class.getName()
			},
			QueryUtil.ALL_POS);

		long groupId = ParamUtil.getLong(
			portletRequest, "groupId", GroupConstants.DEFAULT_PARENT_GROUP_ID);

		if (groupId != GroupConstants.DEFAULT_PARENT_GROUP_ID) {
			groups.clear();

			groups.add(_groupLocalService.getGroup(groupId));
		}

		return groups;
	}

	protected LinkedHashMap<String, Object> getGroupParams(
			PortletRequest portletRequest, GroupSearchTerms searchTerms,
			long parentGroupId)
		throws PortalException {

		LinkedHashMap<String, Object> groupParams =
			LinkedHashMapBuilder.<String, Object>put(
				"site", Boolean.TRUE
			).build();

		if (searchTerms.hasSearchTerms()) {
			if (isFilterManageableGroups(portletRequest)) {
				groupParams.put("groupsTree", getAllGroups(portletRequest));
			}
			else if (parentGroupId > 0) {
				List<Group> groupsTree = new ArrayList<>();

				Group parentGroup = _groupLocalService.getGroup(parentGroupId);

				groupsTree.add(parentGroup);

				groupParams.put("groupsTree", groupsTree);
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			PermissionChecker permissionChecker =
				themeDisplay.getPermissionChecker();

			if (!permissionChecker.isCompanyAdmin() &&
				!GroupPermissionUtil.contains(
					permissionChecker, ActionKeys.VIEW)) {

				User user = themeDisplay.getUser();

				groupParams.put("usersGroups", Long.valueOf(user.getUserId()));
			}
		}

		return groupParams;
	}

	protected long getParentGroupId(PortletRequest portletRequest) {
		Group group = null;

		long groupId = ParamUtil.getLong(
			portletRequest, "groupId", GroupConstants.DEFAULT_PARENT_GROUP_ID);

		if (groupId > 0) {
			group = _groupLocalService.fetchGroup(groupId);
		}

		if (group != null) {
			return group.getGroupId();
		}

		if (isFilterManageableGroups(portletRequest)) {
			return GroupConstants.ANY_PARENT_GROUP_ID;
		}

		return GroupConstants.DEFAULT_PARENT_GROUP_ID;
	}

	protected boolean isFilterManageableGroups(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (permissionChecker.isCompanyAdmin()) {
			return false;
		}

		if (GroupPermissionUtil.contains(permissionChecker, ActionKeys.VIEW)) {
			return false;
		}

		return true;
	}

	@Reference(unbind = "-")
	protected void setGroupLocalService(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
	protected void setModuleServiceLifecycle(
		ModuleServiceLifecycle moduleServiceLifecycle) {

		_classNameIds = new long[] {
			PortalUtil.getClassNameId(Company.class),
			PortalUtil.getClassNameId(Group.class),
			PortalUtil.getClassNameId(Organization.class)
		};
	}

	private long[] _classNameIds;
	private GroupLocalService _groupLocalService;

}