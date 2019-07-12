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

/* eslint no-unused-vars: "warn" */

import PropTypes from 'prop-types';
import React from 'react';

import {FRAGMENTS_EDITOR_ITEM_TYPES} from '../../../utils/constants';
import {getConnectedReactComponent} from '../../../store/ConnectedComponent.es';
import {AddCommentForm} from './AddCommentForm.es';

const FragmentComments = props => (
	<div
		data-fragments-editor-item-id={props.fragmentEntryLinkId}
		data-fragments-editor-item-type={FRAGMENTS_EDITOR_ITEM_TYPES.fragment}
	>
		<h2 className='mb-2 sidebar-dt text-secondary'>
			{props.fragmentEntryLinkName}
		</h2>

		<AddCommentForm />
	</div>
);

FragmentComments.propTypes = {
	fragmentEntryLinkId: PropTypes.string.isRequired,
	fragmentEntryLinkName: PropTypes.string
};

const ConnectedFragmentComments = getConnectedReactComponent(
	(state, ownProps) => ({
		fragmentEntryLinkName:
			state.fragmentEntryLinks[ownProps.fragmentEntryLinkId].name
	}),
	() => ({})
)(FragmentComments);

export {ConnectedFragmentComments, FragmentComments};
export default ConnectedFragmentComments;
