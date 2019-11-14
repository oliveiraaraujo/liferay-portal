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

import Component from 'metal-component';
import Soy, {Config} from 'metal-soy';

import '../ExperimentsLabel.es';

import 'clay-button';

import 'clay-icon';

import templates from './ExperienceItem.soy';

class ExperienceItem extends Component {
	_handleExperienceDelete() {
		const experienceId = this.experience.segmentsExperienceId;

		this.onExperienceDelete(experienceId);
	}

	_handleExperienceEdit() {
		const {name, segmentsEntryId, segmentsExperienceId} = this.experience;

		this.onExperienceEdit({name, segmentsEntryId, segmentsExperienceId});
	}

	_handleExperienceSelect() {
		const experienceId = this.experience.segmentsExperienceId;

		this.onExperienceSelect(experienceId);
	}

	_handleExperienceNavigation(event) {
		event.preventDefault();

		const url = event.delegateTarget.href;

		this.onExperimentNavigation(url);
	}

	_handlePriorityDecrease() {
		const {priority, segmentsExperienceId} = this.experience;

		const {element: priorityButton} = this.refs[
			`buttonPriorityDown${segmentsExperienceId}`
		];

		const selectExperienceBtn = this.refs[
			`selectExperienceButton${segmentsExperienceId}`
		];

		this.onPriorityDecrease({
			priority,
			priorityButton,
			segmentsExperienceId,
			selectExperienceBtn
		});
	}

	_handlePriorityIncrease() {
		const {priority, segmentsExperienceId} = this.experience;

		const {element: priorityButton} = this.refs[
			`buttonPriorityUp${segmentsExperienceId}`
		];

		const selectExperienceBtn = this.refs[
			`selectExperienceButton${segmentsExperienceId}`
		];

		this.onPriorityIncrease({
			priority,
			priorityButton,
			segmentsExperienceId,
			selectExperienceBtn
		});
	}
}

ExperienceItem.STATE = {
	/**
	 * Function to execute when clicking Delete Experience Button
	 *
	 * @memberOf ExperienceItem
	 * @review
	 * @type {function}
	 * @param {string} experienceId
	 */
	onExperienceDelete: Config.func().required(),

	/**
	 * Function to execute when clicking Edit Experience Button
	 *
	 * @memberOf ExperienceItem
	 * @review
	 * @type {function}
	 * @param {object} experienceData
	 * @param {string} experienceData.name
	 * @param {string} experienceData.segmentsEntryId
	 * @param {string} experienceData.segmentsExperienceId
	 */
	onExperienceEdit: Config.func().required(),

	/**
	 * Function to execute when clicking Select Experience Button
	 *
	 * @memberOf ExperienceItem
	 * @review
	 * @type {function}
	 * @param {string} experienceId
	 */
	onExperienceSelect: Config.func().required(),

	/**
	 * Function to execute when clicking Experiment Navigation Button
	 *
	 * @memberOf ExperienceItem
	 * @review
	 * @type {function}
	 * @param {string} url
	 */
	onExperimentNavigation: Config.func().required(),

	/**
	 * Function to execute when clicking Decrease Priority Button
	 *
	 * @memberOf ExperienceItem
	 * @review
	 *
	 * @type {function}
	 * @param {object} experienceData
	 * @param {number} experienceData.priority
	 * @param {object} experienceData.priorityButton
	 * @param {string} experienceData.segmentsExperienceId
	 * @param {object} experienceData.selectExperienceBtn
	 */
	onPriorityDecrease: Config.func().required(),

	/**
	 * Function to execute when clicking Increase Priority Button
	 *
	 * @memberOf ExperienceItem
	 * @review
	 *
	 * @type {function}
	 * @param {object} experienceData
	 * @param {number} experienceData.priority
	 * @param {object} experienceData.priorityButton
	 * @param {string} experienceData.segmentsExperienceId
	 * @param {object} experienceData.selectExperienceBtn
	 */

	onPriorityIncrease: Config.func().required()
};

Soy.register(ExperienceItem, templates);

export {ExperienceItem};
export default ExperienceItem;
