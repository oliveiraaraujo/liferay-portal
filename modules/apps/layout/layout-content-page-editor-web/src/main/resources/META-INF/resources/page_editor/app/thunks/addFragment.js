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

import {fetch} from 'frontend-js-web';

import addFragmentEntryLinkAndItem from '../actions/addFragmentEntryLinkAndItem';
import {LAYOUT_DATA_ITEM_TYPES} from '../config/constants/layoutDataItemTypes';

export default function addFragment({
	config,
	fragmentGroupId,
	fragmentKey,
	parentId,
	position,
	store
}) {
	return dispatch => {
		const {portletNamespace} = config;

		const {
			addFragmentEntryLinkURL,
			classNameId,
			classPK,
			segmentsExperienceId
		} = store;

		const formData = new FormData();
		formData.append(`${portletNamespace}fragmentKey`, fragmentKey);
		formData.append(`${portletNamespace}classNameId`, classNameId);
		formData.append(`${portletNamespace}classPK`, classPK);
		formData.append(`${portletNamespace}groupId`, fragmentGroupId);
		formData.append(
			`${portletNamespace}segmentsExperienceId`,
			segmentsExperienceId
		);

		fetch(addFragmentEntryLinkURL, {
			body: formData,
			method: 'POST'
		})
			.then(response => response.json())
			.then(fragmentEntryLink => {
				// TODO: This is a temporary "hack"
				//       until the backend is consitent
				//       between both "metal+soy" and "react" versions
				const {content} = fragmentEntryLink;

				fragmentEntryLink.content = {
					value: {
						content
					}
				};

				dispatch(
					addFragmentEntryLinkAndItem({
						fragmentEntryLink,
						itemId: `thing-${Date.now()}`,
						itemType: LAYOUT_DATA_ITEM_TYPES.fragment,
						parentId,
						position
					})
				);
			});
	};
}
