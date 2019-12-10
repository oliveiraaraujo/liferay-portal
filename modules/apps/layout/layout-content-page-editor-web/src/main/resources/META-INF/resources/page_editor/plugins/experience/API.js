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

import React from 'react';

export const APIContext = React.createContext({});

export default function API({
	addSegmentsExperienceURL: _addSegmentsExperienceURL,
	classNameId: _classNameId,
	classPK: _classPK,
	editSegmentsExperiencePriorityURL: _editSegmentsExperiencePriorityURL,
	editSegmentsExperienceURL: _editSegmentsExperienceURL,
	removeSegmentsExperienceURL: _removeSegmentsExperienceURL
}) {
	function createExperience({name, segmentsEntryId}) {
		// TODO actual call to server

		return new Promise(resolve => {
			setTimeout(
				() =>
					resolve({
						active: true,
						name,
						priority: 10,
						segmentsEntryId,
						segmentsExperienceId: Math.random().toString()
					}),
				1000
			);
		});
	}

	function removeExperience(
		segmentsExperienceId,
		_fragmentEntryLinkIds = []
	) {
		// TODO actual call to server
		return new Promise(resolve => {
			setTimeout(() => resolve(), 1000);
		});
	}

	function updateExperiencePriority({
		newPriority: _newPriority,
		segmentsExperienceId: _segmentsExperienceId
	}) {
		return new Promise(resolve => {
			setTimeout(() => resolve({}), 1000);
		});
	}

	function updateExperience({
		active,
		name,
		segmentsEntryId,
		segmentsExperienceId
	}) {
		// TODO actual call to server
		return new Promise(resolve => {
			setTimeout(
				() =>
					resolve({
						active,
						name,
						segmentsEntryId,
						segmentsExperienceId
					}),
				1500
			);
		});
	}
	return {
		createExperience,
		removeExperience,
		updateExperience,
		updateExperiencePriority
	};
}
