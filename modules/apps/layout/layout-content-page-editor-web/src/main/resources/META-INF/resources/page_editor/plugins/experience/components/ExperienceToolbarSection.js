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

import React, {useContext, useMemo} from 'react';

import {ConfigContext} from '../../../app/config/index';
import {StoreContext} from '../../../app/store/index';
import API, {APIContext} from '../API';
import ExperienceSelector from './ExperienceSelector';

// TODO: show how to colocate CSS with plugins (may use loaders)
export default function ExperienceToolbarSection({selectId}) {
	const {availableSegmentsExperiences, segmentsExperienceId} = useContext(
		StoreContext
	);
	const {
		availableSegmentsEntries,
		classNameId,
		classPK,
		editSegmentsEntryURL
	} = useContext(ConfigContext);

	const experiences = useMemo(
		() =>
			Object.values(availableSegmentsExperiences).sort(
				(a, b) => b.priority - a.priority
			),
		[availableSegmentsExperiences]
	);
	const segments = useMemo(() => Object.values(availableSegmentsEntries), [
		availableSegmentsEntries
	]);

	const APIService = useMemo(() => {
		return API({
			addSegmentsExperience: '/',
			classNameId,
			classPK,
			editSegmentsExperiencePriorityURL: '/',
			editSegmentsExperienceURL: '/',
			removeSegmentsExperienceURL: '/'
		});
	}, [classNameId, classPK]);

	const selectedExperience =
		availableSegmentsExperiences[segmentsExperienceId];

	return (
		<APIContext.Provider value={APIService}>
			<div className="mr-2 page-editor-toolbar-experience">
				<label className="mr-2" htmlFor={selectId}>
					{Liferay.Language.get('experience')}
				</label>

				<ExperienceSelector
					editSegmentsEntryURL={editSegmentsEntryURL}
					experiences={experiences}
					segments={segments}
					selectId={selectId}
					selectedExperience={selectedExperience}
				/>
			</div>
		</APIContext.Provider>
	);
}
