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

import {Component} from '../../core/AppContext';
import {
	EDIT_SEGMENTS_EXPERIENCE,
	CREATE_SEGMENTS_EXPERIENCE,
	SELECT_SEGMENTS_EXPERIENCE,
	DELETE_SEGMENTS_EXPERIENCE
} from './actions';
import ExperienceToolbarSection from './components/ExperienceToolbarSection';

function editExperienceReducer(state, payload) {
	let nextState = state;

	const updatedExperience = payload;

	const experience =
		state.availableSegmentsExperiences[
			updatedExperience.segmentsExperienceId
		];

	if (experience) {
		nextState = {
			...nextState,
			availableSegmentsExperiences: {
				...nextState.availableSegmentsExperiences,
				[experience.segmentsExperienceId]: {
					...updatedExperience
				}
			}
		};
	}

	return nextState;
}

function createExperienceReducer(state, payload) {
	let nextState = state;

	const newExperience = payload;

	nextState = {
		...nextState,
		availableSegmentsExperiences: {
			...nextState.availableSegmentsExperiences,
			[newExperience.segmentsExperienceId]: {...newExperience}
		},
		segmentsExperienceId: newExperience.segmentsExperienceId
	};

	return nextState;
}

function selectExperienceReducer(state, payload) {
	let nextState = state;

	const newExperience = payload;

	nextState = {
		...nextState,
		segmentsExperienceId: newExperience.segmentsExperienceId
	};

	return nextState;
}

function deleteExperienceReducer(state, payload) {
	let nextState = state;
	const {defaultExperienceId, segmentsExperienceId} = payload;

	const availableSegmentsExperiences = {
		...nextState.availableSegmentsExperiences
	};

	delete availableSegmentsExperiences[segmentsExperienceId];

	nextState = {
		...nextState,
		availableSegmentsExperiences
	};

	if (nextState.segmentsExperienceId === segmentsExperienceId) {
		nextState = {
			...nextState,
			segmentsExperienceId: defaultExperienceId
		};
	}

	return nextState;
}

/**
 * Entry-point for "Experience" (toolbar drop-down) functionality.
 */
export default class Experience {
	constructor({app, toolbarPlugin}) {
		this.Actions = app.Actions;
		this.Component = Component(app);
		this.dispatch = app.dispatch;

		this.toolbarId = app.config.toolbarId;
		this.toolbarPluginId = {toolbarPlugin};
	}

	activate() {
		const reducer = (state, action) => {
			let nextState = state;

			switch (action.type) {
				case EDIT_SEGMENTS_EXPERIENCE:
					nextState = editExperienceReducer(
						nextState,
						action.payload
					);
					break;
				case CREATE_SEGMENTS_EXPERIENCE:
					nextState = createExperienceReducer(
						nextState,
						action.payload
					);
					break;
				case SELECT_SEGMENTS_EXPERIENCE:
					nextState = selectExperienceReducer(
						nextState,
						action.payload
					);
					break;
				case DELETE_SEGMENTS_EXPERIENCE:
					nextState = deleteExperienceReducer(
						nextState,
						action.payload
					);
					break;
				default:
					break;
			}

			return nextState;
		};

		this.dispatch(this.Actions.loadReducer(reducer, Experience.name));
	}

	deactivate() {
		this.dispatch(this.Actions.unloadReducer(Experience.name));
	}

	renderToolbarSection() {
		const {Component} = this;

		const selectId = `${this.toolbarId}_${this.toolbarPluginId}`;

		return (
			<Component>
				<ExperienceToolbarSection selectId={selectId} />
			</Component>
		);
	}
}
